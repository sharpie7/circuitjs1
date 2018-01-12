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
*/

package com.lushprojects.circuitjs1.client;

// much of this was adapted from Bill Collis's code in TestPointElm.java

class ProbeElm extends CircuitElm {
    static final int FLAG_SHOWVOLTAGE = 1;
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
    
    public ProbeElm(int xx, int yy) { super(xx, yy);
    	meter = TP_VOL;
    	
    	// default for new elements
    	flags = FLAG_SHOWVOLTAGE;
    }
    public ProbeElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
    	meter = TP_VOL;
	try {
	    meter = new Integer(st.nextToken()).intValue(); //get meter type from saved dump
	} catch (Exception e) {}
    }
    int getDumpType() { return 'p'; }
    String dump() {
        return super.dump() + " " + meter ;
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
    
    double rmsV=0, total, count;
    double binaryLevel=0;//0 or 1 - double because we only pass doubles back to the web page
    int zerocount=0;
    double maxV=0, lastMaxV;
    double minV=0, lastMinV;
    double frequency=0;
    double period=0;
    double pulseWidth=0;
    double dutyCycle=0;
    double selectedValue=0;

    boolean increasingV=true, decreasingV=true;
    long periodStart, periodLength, pulseStart;//time between consecutive max values

    Point center;
	
	void setPoints() {
	    super.setPoints();
	    center = interpPoint(point1, point2, .5);
	}
	
	

    void draw(Graphics g) {
	int hs = 8;
	setBbox(point1, point2, hs);
	boolean selected = needsHighlight();
	double len = (selected || sim.dragElm == this || mustShowVoltage()) ? 16 : dn-32;
	calcLeads((int) len);
	setVoltageColor(g, volts[0]);
	if (selected)
	    g.setColor(selectColor);
	drawThickLine(g, point1, lead1);
	setVoltageColor(g, volts[1]);
	if (selected)
	    g.setColor(selectColor);
	drawThickLine(g, lead2, point2);
	Font f = new Font("SansSerif", Font.BOLD, 14);
	g.setFont(f);
	if (this == sim.plotXElm)
	    drawCenteredText(g, "X", center.x, center.y, true);
	if (this == sim.plotYElm)
	    drawCenteredText(g, "Y", center.x, center.y, true);
	if (mustShowVoltage()) {
	    String s = "";
	        switch (meter) {
	            case TP_VOL:
	                s = myGetUnitText(getVoltageDiff(),"V",false);
	                break;
	            case TP_RMS:
	                s = myGetUnitText(rmsV,"V(rms)",false);
	                break;
	            case TP_MAX:
	                s = myGetUnitText(lastMaxV,"Vpk",false);
	                break;
	            case TP_MIN:
	                s = myGetUnitText(lastMinV,"Vmin",false);
	                break;
	            case TP_P2P:
	                s = myGetUnitText(lastMaxV-lastMinV,"Vp2p",false);
	                break;
	            case TP_BIN:
	                s= binaryLevel + "";
	                break;
	            case TP_FRQ:
	                s = myGetUnitText(frequency, "Hz", false);
	                break;
	            case TP_PER:
//	                s = "percent:"+period + " " + sim.timeStep + " " + sim.simTime + " " + sim.getIterCount();
	                break;
	            case TP_PWI:
	                s = myGetUnitText(pulseWidth, "S", false);
	                break;
	            case TP_DUT:
	                s = showFormat.format(dutyCycle);
	                break;
	        }
	    drawValues(g, s, 4);
	}
	   g.setColor(Color.white);
           g.setFont(unitsFont);
           Point plusPoint = interpPoint(point1, point2, (dn/2-len/2-4)/dn, -10*dsign );
           if (y2 > y)
		plusPoint.y += 4;
	    if (y > y2)
		plusPoint.y += 3;
           int w = (int)g.context.measureText("+").getWidth();;
           g.drawString("+", plusPoint.x-w/2, plusPoint.y);
	drawPosts(g);
    }
	
    boolean mustShowVoltage() {
	return (flags & FLAG_SHOWVOLTAGE) != 0;
    }
    
    void stepFinished(){
        count++;//how many counts are in a cycle
        double v = getVoltageDiff();
        total += v*v; //sum of squares

        if (v<2.5)
            binaryLevel = 0;
        else
            binaryLevel = 1;
        
        
        //V going up, track maximum value with 
        if (v>maxV && increasingV){
            maxV = v;
            increasingV = true;
            decreasingV = false;
        }
        if (v<maxV && increasingV){//change of direction V now going down - at start of waveform
            lastMaxV=maxV; //capture last maximum 
            //capture time between
            periodLength = System.currentTimeMillis() - periodStart;
            periodStart = System.currentTimeMillis();
            period = periodLength;
            pulseWidth = System.currentTimeMillis() - pulseStart;
            dutyCycle = pulseWidth / periodLength;
            minV=v; //track minimum value with V
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
        if (v<minV && decreasingV){ //V going down, track minimum value with V
            minV=v;
            increasingV=false;
            decreasingV=true;
        }

        if (v>minV && decreasingV){ //change of direction V now going up
            lastMinV=minV; //capture last minimum
            pulseStart =  System.currentTimeMillis();
            maxV = v;
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
        if (v==0){
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
    }

    void getInfo(String arr[]) {
	arr[0] = "voltmeter";
	arr[1] = "Vd = " + getVoltageText(getVoltageDiff());
    }
    boolean getConnection(int n1, int n2) { return false; }

    public EditInfo getEditInfo(int n) {
	if (n == 0) {
	    EditInfo ei = new EditInfo("", 0, -1, -1);
	    ei.checkbox = new Checkbox("Show Value", mustShowVoltage());
	    return ei;
	}
        if (n==1){
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

return null;
    }

    public void setEditValue(int n, EditInfo ei) {
	if (n == 0) {
	    if (ei.checkbox.getState())
		flags = FLAG_SHOWVOLTAGE;
	    else
		flags &= ~FLAG_SHOWVOLTAGE;
	}
        if (n==1){
            meter = ei.choice.getSelectedIndex();
        }
    }
}

