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
    
    AmmeterElm by Bill Collis
    
*/

package com.lushprojects.circuitjs1.client;

    class AmmeterElm extends CircuitElm {
        
        int meter;
        final int AM_VOL = 0;
        final int AM_RMS = 1;
        int zerocount=0;
        double rmsI=0, total, count;
        double maxI=0, lastMaxI;
        double minI=0, lastMinI;
        double selectedValue=0;
        
        double currents[];
        boolean increasingI=true, decreasingI=true;

    public AmmeterElm(int xx, int yy) { 
        super(xx, yy); 
        flags = FLAG_SHOWCURRENT;
        }
    public AmmeterElm(int xa, int ya, int xb, int yb, int f,
               StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        meter = new Integer(st.nextToken()).intValue(); //get meter type from saved dump

    }
    String dump() {
            return super.dump() + " " + meter;
    }
    String getMeter(){
        switch (meter) {
        case AM_VOL:
            return "I";
        case AM_RMS:
            return "Irms";
        }
        return "";
    }
    void setPoints(){
        super.setPoints();
        mid = interpPoint(point1,point2,0.6);
        arrowPoly = calcArrow(point1, mid, 14, 7);
    }
    Point mid;
    static final int FLAG_SHOWCURRENT = 1;
    void stepFinished(){
        count++;//how many counts are in a cycle    
        total += current*current; //sum of squares
        if (current>maxI && increasingI){
            maxI = current;
            increasingI = true;
            decreasingI = false;
        }
        if (current<maxI && increasingI){//change of direction I now going down - at start of waveform
            lastMaxI=maxI; //capture last maximum 
            //capture time between
            minI=current; //track minimum value 
            increasingI=false;
            decreasingI=true;
            
            //rms data
            total = total/count;
            rmsI = Math.sqrt(total);
            if (Double.isNaN(rmsI))
                rmsI=0;
            count=0;
            total=0;
            
        }
        if (current<minI && decreasingI){ //I going down, track minimum value
            minI=current;
            increasingI=false;
            decreasingI=true;
        }

        if (current>minI && decreasingI){ //change of direction I now going up
            lastMinI=minI; //capture last minimum

            maxI = current;
            increasingI = true;
            decreasingI = false;
            
            //rms data
            total = total/count;
            rmsI = Math.sqrt(total);
            if (Double.isNaN(rmsI))
                rmsI=0;
            count=0;
            total=0;

            
        }
        //need to zero the rms value if it stays at 0 for a while
        if (current==0){
            zerocount++;
            if (zerocount > 5){
                total=0;
                rmsI=0;
                maxI=0;
                minI=0;
            }
        }else{
            zerocount=0;
        }
        switch (meter) {
        case AM_VOL:
            selectedValue = current;
            break;
        case AM_RMS:
            selectedValue = rmsI;
            break;
        }
    }
    
    Polygon arrowPoly;
    void draw(Graphics g) {
        super.draw(g);//BC required for highlighting
        setVoltageColor(g, volts[0]);
        drawThickLine(g, point1, point2);
        g.fillPolygon(arrowPoly);
        doDots(g);
        setBbox(point1, point2, 3);
        String s = "A";
        switch (meter) {
        case AM_VOL:
            s = myGetUnitText(getCurrent(), "A",false);
            break;
        case AM_RMS:
            s = myGetUnitText(rmsI, "A(rms)",false);
            break;
        }

            drawValues(g, s, 4);


        drawPosts(g);
    }
    int getDumpType() { return 370; }
    void stamp() {
        sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
    }
    boolean mustShowCurrent() {
        return (flags & FLAG_SHOWCURRENT) != 0;
    }
    int getVoltageSourceCount() { return 1; }
    void getInfo(String arr[]) {
        arr[0] = "Ammeter";
        switch (meter) {
            case AM_VOL:
                arr[1] = "I = " + myGetUnitText(current, "A", false);
                break;
            case AM_RMS:
                arr[1] = "Irms = " + myGetUnitText(rmsI, "A", false);
                break;
        }    
    }
    double getPower() { return 0; }
    double getVoltageDiff() { return volts[0]; }
    boolean isWire() { return true; }
    
    public EditInfo getEditInfo(int n) {
        if (n==0){
            EditInfo ei =  new EditInfo("Value", selectedValue, -1, -1);
            ei.choice = new Choice();
            ei.choice.add("Current");
            ei.choice.add("RMS Current");
            ei.choice.select(meter);
            return ei;
        }
        return null;
    }
    public void setEditValue(int n, EditInfo ei) {
        if (n==0){
            meter = ei.choice.getSelectedIndex();
        }

    }
    }


