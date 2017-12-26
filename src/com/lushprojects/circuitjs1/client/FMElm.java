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

// contributed by Edward Calver

class FMElm extends CircuitElm {
    static final int FLAG_COS = 2;
    double carrierfreq,signalfreq, maxVoltage, freqTimeZero,deviation;
    double lasttime=0;
    double funcx=0;
    public FMElm(int xx, int yy) {
	super(xx, yy);
	deviation=200;
	maxVoltage = 5;
	carrierfreq =800;
	signalfreq=40;
	reset();
    }
    public FMElm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	carrierfreq = new Double(st.nextToken()).doubleValue();
	signalfreq= new Double(st.nextToken()).doubleValue();
	maxVoltage = new Double(st.nextToken()).doubleValue();
	deviation = new Double(st.nextToken()).doubleValue();
	if ((flags & FLAG_COS) != 0) {
	    flags &= ~FLAG_COS;
	}
	reset();
    }
    int getDumpType() { return 201; }
    String dump() {
	return super.dump() + " " +carrierfreq+" " + signalfreq + " " +maxVoltage + " " +deviation;
    }
    /*void setCurrent(double c) {
      current = c;
      System.out.print("v current set to " + c + "\n");
      }*/

    void reset() {
	freqTimeZero = 0;
	curcount = 0;
    }
      int getPostCount() { return 1; }
	
     void stamp() {
	    sim.stampVoltageSource(0, nodes[0], voltSource);
    }
    void doStep() {
	    sim.updateVoltageSource(0, nodes[0], voltSource, getVoltage());
    }
    double getVoltage() {
	double deltaT=sim.t-lasttime;
	lasttime=sim.t;
	double signalamplitude=Math.sin((2*pi*(sim.t-freqTimeZero))*signalfreq);
	funcx+=deltaT*(carrierfreq+(signalamplitude*deviation));
	double w = 2*pi*funcx;
	return Math.sin(w)*maxVoltage;	
    }
    final int circleSize = 17;

    void draw(Graphics g) {
	setBbox(point1, point2, circleSize);
	setVoltageColor(g, volts[0]);
	drawThickLine(g, point1, lead1);

	    Font f = new Font("SansSerif", 0, 12);
	    g.setFont(f);
	    g.setColor(needsHighlight() ? selectColor : whiteColor);
	    setPowerColor(g, false);
	    double v = getVoltage();
	    String s = "FM";
	    drawCenteredText(g, s, x2, y2, true);
	    drawWaveform(g, point2);
	drawPosts(g);
	curcount = updateDotCount(-current, curcount);
	if (sim.dragElm != this)
	    drawDots(g, point1, lead1, curcount);
    }
	
    void drawWaveform(Graphics g, Point center) {
	g.setColor(needsHighlight() ? selectColor : Color.gray);
	setPowerColor(g, false);
	int xc = center.x; int yc = center.y;
	drawThickCircle(g, xc, yc, circleSize);
	int wl = 8;
	adjustBbox(xc-circleSize, yc-circleSize,
		   xc+circleSize, yc+circleSize);
    }


  void setPoints() {
	super.setPoints();
	lead1 = interpPoint(point1, point2, 1-circleSize/dn);
    }
    
    double getVoltageDiff() { return volts[0]; }
   
    boolean hasGroundConnection(int n1) { return true; }
	
    int getVoltageSourceCount() {
	return 1;
    }
    double getPower() { return -getVoltageDiff()*current; }
    void getInfo(String arr[]) {
	
	arr[0] = "FM Source";
	arr[1] = "I = " + getCurrentText(getCurrent());
	arr[2] = "V = " +
	    getVoltageText(getVoltageDiff());
	    arr[3] = "cf = " + getUnitText(carrierfreq, "Hz");
	    arr[4] = "sf = " + getUnitText(signalfreq, "Hz");
	    arr[5]= "dev =" + getUnitText(deviation, "Hz");
	    arr[6] = "Vmax = " + getVoltageText(maxVoltage);
    }
    public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo("Max Voltage", maxVoltage, -20, 20);
	if (n == 1)
	    return new EditInfo("Carrier Frequency (Hz)", carrierfreq, 4, 500);
	if (n == 2)
	    return new EditInfo("Signal Frequency (Hz)", signalfreq, 4, 500);
	if (n == 3)
	    return new EditInfo("Deviation (Hz)", deviation, 4, 500);
	
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    maxVoltage = ei.value;
	if (n == 1)
	    carrierfreq = ei.value;
	if (n == 2)
	    signalfreq=ei.value;
	if (n == 3)
	    deviation=ei.value;
    }
}
