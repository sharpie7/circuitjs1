package com.lushprojects.circuitjs1.client;

/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
    
    TestPointElm by Bill Collis
    
*/

class TestPointElm extends CircuitElm {
    int meter;
    final int TP_VOL = 0;
    final int TP_RMS = 1;
    final int TP_MAX = 2;
    final int TP_MIN = 3;
    final int TP_P2P = 4;
    final int TP_BIN = 5;
    final int TP_FRQ = 6;
    final int TP_PER = 7;
    final int TP_PWI = 8;
    final int TP_DUT = 9; //mark to space ratio
    final int FLAG_LABEL = 1;
    int zerocount=0;
    double rmsV=0, total, count;
    double maxV=0, lastMaxV;
    double minV=0, lastMinV;
    double frequency=0;
    double period=0;
    double binaryLevel=0;//0 or 1 - double because we only pass doubles back to the web page
    double pulseWidth=0;
    double dutyCycle=0;
    double selectedValue=0;
    int lastStepCount;
    
    double voltages[];
    boolean increasingV=true, decreasingV=true;
    long periodStart, periodLength, pulseStart;//time between consecutive max values
    String label;
    
    public TestPointElm(int xx, int yy) { 
        super(xx, yy); 
        meter = TP_VOL;
	label = "TP";
    }
    public TestPointElm(int xa, int ya, int xb, int yb, int f,
             StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        meter = new Integer(st.nextToken()).intValue(); //get meter type from saved dump
	if ((flags & FLAG_LABEL) != 0)
	    label = CustomLogicModel.unescape(st.nextToken());
	else
	    label = "TP";
    }
    int getDumpType() { return 368; }
    int getPostCount() { return 1; }
    void setPoints() {
        super.setPoints();
        lead1 = new Point();
    }
    String dump() {
	boolean writeLabel = (!label.equals("TP"));
	flags = (writeLabel) ? (flags | FLAG_LABEL) : (flags & ~FLAG_LABEL);
        String str = super.dump() + " " + meter;
	if (writeLabel)
	    str += " " + CustomLogicModel.escape(label);
	return str;
    }
    String getMeter(){
        switch (meter) {
        case TP_VOL:
            return "V";
        case TP_RMS:
            return "V(rms)";
        case TP_MAX:
            return "Vmax";
        case TP_MIN:
            return "Vmin";
        case TP_P2P:
            return "Peak to peak";
        case TP_BIN:
            return "Binary";
        case TP_FRQ:
            return "Frequency";
        case TP_PER:
            return "Period";
        case TP_PWI:
            return "Pulse width";
        case TP_DUT:
            return "Duty cycle";
        }
        return "";
    }
    
    void drawText(Graphics g, String str, String str2, Point pt1, Point pt2) {
        int w1 = (int)g.context.measureText(str).getWidth();
        int w2 = (int)g.context.measureText(str2).getWidth();
        final int spacing = 14;
        int wmax = max(w1, w2);
        int h=(int)g.currentFontSize;
        g.save();
        g.context.setTextBaseline("middle");
        int x = pt2.x, y = pt2.y;
        if (pt1.y != pt2.y) {
            x -= wmax/2;
            y += sign(pt2.y-pt1.y)*h;
            if (pt2.y < pt1.y)
        	y -= spacing-4;
        } else {
            if (pt2.x > pt1.x)
                x += 4;
            else
                x -= 4+wmax;
        }
        g.drawString(str,  x+(wmax-w1)/2, y);
        g.drawString(str2, x+(wmax-w2)/2, y+spacing);
        adjustBbox(x, y-h/2, x+wmax, y+spacing+h/2);
        g.restore();
    }    
    

    void draw(Graphics g) {
	g.save();
        boolean selected = needsHighlight();
        Font f = new Font("SansSerif", selected ? Font.BOLD : 0, 14);
        g.setFont(f);
        g.setColor(selected ? selectColor : whiteColor);
        //depending upon flags show voltage or TP
        
        String s = label;
        interpPoint(point1, point2, lead1, 1-((int)g.context.measureText("TP").getWidth()/2+8)/dn);
        setBbox(point1, lead1, 0);
        
        //draw selected value
        switch (meter) {
            case TP_VOL:
                s = getUnitText(volts[0],"V");
                break;
            case TP_RMS:
                s = getUnitText(rmsV,"V(rms)");
                break;
            case TP_MAX:
                s = getUnitText(lastMaxV,"Vpk");
                break;
            case TP_MIN:
                s = getUnitText(lastMinV,"Vmin");
                break;
            case TP_P2P:
                s = getUnitText(lastMaxV-lastMinV,"Vp2p");
                break;
            case TP_BIN:
                s= binaryLevel + "";
                break;
            case TP_FRQ:
                s = getUnitText(frequency, "Hz");
                break;
            case TP_PER:
//                s = "percent:"+period + " " + sim.timeStep + " " + sim.simTime + " " + sim.getIterCount();
                break;
            case TP_PWI:
                s = getUnitText(pulseWidth, "S");
                break;
            case TP_DUT:
                s = showFormat.format(dutyCycle);
                break;
        }
        drawText(g, label, s, point1, lead1);
        
        setVoltageColor(g, volts[0]);
        if (selected)
            g.setColor(selectColor);
        drawThickLine(g, point1, lead1);
        drawPosts(g);
        g.restore();
    }
    
    
    void stepFinished(){
	if (sim.timeStepCount == lastStepCount)
	    return;
	lastStepCount = sim.timeStepCount;
        count++;//how many counts are in a cycle    
        total += volts[0]*volts[0]; //sum of squares

        if (volts[0]<2.5)
            binaryLevel = 0;
        else
            binaryLevel = 1;
        
        
        //V going up, track maximum value with 
        if (volts[0]>maxV && increasingV){
            maxV = volts[0];
            increasingV = true;
            decreasingV = false;
        }
        if (volts[0]<maxV && increasingV){//change of direction V now going down - at start of waveform
            lastMaxV=maxV; //capture last maximum 
            //capture time between
            periodLength = System.currentTimeMillis() - periodStart;
            periodStart = System.currentTimeMillis();
            period = periodLength;
            pulseWidth = System.currentTimeMillis() - pulseStart;
            dutyCycle = pulseWidth / periodLength;
            minV=volts[0]; //track minimum value with V
            increasingV=false;
            decreasingV=true;
            
            //rms data
            total = total/count;
            rmsV = Math.sqrt(total);
            if (Double.isNaN(rmsV))
                rmsV=0;
            count=0;
            total=0;
            
        }
        if (volts[0]<minV && decreasingV){ //V going down, track minimum value with V
            minV=volts[0];
            increasingV=false;
            decreasingV=true;
        }

        if (volts[0]>minV && decreasingV){ //change of direction V now going up
            lastMinV=minV; //capture last minimum
            pulseStart =  System.currentTimeMillis();
            maxV = volts[0];
            increasingV = true;
            decreasingV = false;
            
            //rms data
            total = total/count;
            rmsV = Math.sqrt(total);
            if (Double.isNaN(rmsV))
                rmsV=0;
            count=0;
            total=0;

            
        }
        //need to zero the rms value if it stays at 0 for a while
        if (volts[0]==0){
            zerocount++;
            if (zerocount > 5){
                total=0;
                rmsV=0;
                maxV=0;
                minV=0;
            }
        }else{
            zerocount=0;
        }
        switch (meter) {
        case TP_VOL:
            selectedValue = volts[0];
            break;
        case TP_RMS:
            selectedValue = rmsV;
            break;
        case TP_MAX:
            selectedValue = lastMaxV;
            break;
        case TP_MIN:
            selectedValue = lastMinV;
            break;
        case TP_P2P:
            selectedValue = lastMaxV-lastMinV;
            break;
        case TP_BIN:
            selectedValue = binaryLevel;
            break;
        case TP_FRQ:
            selectedValue = frequency;
            break;
        case TP_PER:
            selectedValue = period ;
            break;
        case TP_PWI:
            selectedValue = pulseWidth;
            break;
        case TP_DUT:
            selectedValue = dutyCycle;
            break;
        }

    }
    
    //alert the user
    public static native void alert(String msg) /*-{
      $wnd.alert(msg);
    }-*/;
    
    double getScopeValue(int x){
        return selectedValue;
    }
    
    double getVoltageDiff() { return volts[0]; }
    
    void getInfo(String arr[]) {
        arr[0] = "Test Point";
        switch (meter) {
            case TP_VOL:
                arr[1] = "V = " + getUnitText(volts[0], "V");
                break;
            case TP_RMS:
                arr[1] = "V(rms) = " + getUnitText(rmsV, "V");
                break;
            case TP_MAX:
                arr[1] = "Vmax = " + getUnitText(lastMaxV, "Vpk");
                break;
            case TP_MIN:
                arr[1] = "Vmin = " + getUnitText(lastMinV, "Vmin");
                break;
            case TP_P2P:
                arr[1] = "Vp2p = " + getUnitText(lastMaxV-lastMinV, "Vp2p");
                break;
            case TP_BIN:
                arr[1] = "Binary:" + binaryLevel + "";
                break;
            case TP_FRQ:
                arr[1] = "Freq = " + getUnitText(frequency, "Hz");
                break;
            case TP_PER:
                arr[1] = "Period = " + getUnitText(period*sim.maxTimeStep/sim.getIterCount(), "S");
                break;
            case TP_PWI:
                arr[1] = "Pulse width = " + getUnitText(pulseWidth*sim.maxTimeStep*sim.getIterCount(), "S");
                break;
            case TP_DUT:
                arr[1] = "Duty cycle = " + showFormat.format(dutyCycle);
                break;
        }    
    }
        
//    void drawHandles(Graphics g, Color c) {
//        g.setColor(c);
//        g.fillRect(x-3, y-3, 7, 7);
//    }
    
    public EditInfo getEditInfo(int n) {
        if (n==0){
            EditInfo ei =  new EditInfo("Value", selectedValue, -1, -1);
            ei.choice = new Choice();
            ei.choice.add("Voltage");
            ei.choice.add("RMS Voltage");
            ei.choice.add("Max Voltage");
            ei.choice.add("Min Voltage");
            ei.choice.add("P2P Voltage");
            ei.choice.add("Binary Value");        
            //ei.choice.add("Frequency");
            //ei.choice.add("Period");
            //ei.choice.add("Pulse Width");
            //ei.choice.add("Duty Cycle");
            ei.choice.select(meter);
            return ei;
        }
        if (n == 1) {
            EditInfo ei = new EditInfo("Label", 0, -1, -1);
            ei.text = label;
            return ei;
        }

	return null;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (n==0){
            meter = ei.choice.getSelectedIndex();
        }
        if (n == 1)
            label = ei.textf.getText();
    }
    
}

