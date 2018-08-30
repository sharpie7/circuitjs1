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

class DiodeElm extends CircuitElm {
    Diode diode;
    static final int FLAG_FWDROP = 1;
    final double defaultdrop = .805904783;
    double fwdrop, zvoltage;
    static double lastFwdrop;
    
    public DiodeElm(int xx, int yy) {
	super(xx, yy);
	diode = new Diode(sim);
	fwdrop = lastFwdrop == 0 ? defaultdrop : lastFwdrop;
	zvoltage = 0;
	setup();
    }
    public DiodeElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	diode = new Diode(sim);
	fwdrop = defaultdrop;
	zvoltage = 0;
	if ((f & FLAG_FWDROP) > 0) {
	    try {
		fwdrop = new Double(st.nextToken()).doubleValue();
	    } catch (Exception e) {
	    }
	}
	setup();
    }
    boolean nonLinear() { return true; }
    
    void setup() {
	diode.setup(fwdrop, zvoltage);
    }
    
    int getDumpType() { return 'd'; }
    String dump() {
	flags |= FLAG_FWDROP;
	return super.dump() + " " + fwdrop;
    }
    

    final int hs = 8;
    Polygon poly;
    Point cathode[];
	
    void setPoints() {
	super.setPoints();
	calcLeads(16);
	cathode = newPointArray(2);
	Point pa[] = newPointArray(2);
	interpPoint2(lead1, lead2, pa[0], pa[1], 0, hs);
	interpPoint2(lead1, lead2, cathode[0], cathode[1], 1, hs);
	poly = createPolygon(pa[0], pa[1], lead2);
    }
	
    void draw(Graphics g) {
	drawDiode(g);
	doDots(g);
	drawPosts(g);
    }
	
    void reset() {
	diode.reset();
	volts[0] = volts[1] = curcount = 0;
    }
	
    void drawDiode(Graphics g) {
	setBbox(point1, point2, hs);

	double v1 = volts[0];
	double v2 = volts[1];

	draw2Leads(g);

	// draw arrow thingy
	setPowerColor(g, true);
	setVoltageColor(g, v1);
	g.fillPolygon(poly);

	// draw thing arrow is pointing to
	setVoltageColor(g, v2);
	drawThickLine(g, cathode[0], cathode[1]);
    }
	
    void stamp() { diode.stamp(nodes[0], nodes[1]); }
    void doStep() {
	diode.doStep(volts[0]-volts[1]);
    }
    void calculateCurrent() {
	current = diode.calculateCurrent(volts[0]-volts[1]);
    }
    void getInfo(String arr[]) {
	arr[0] = "diode";
	arr[1] = "I = " + getCurrentText(getCurrent());
	arr[2] = "Vd = " + getVoltageText(getVoltageDiff());
	arr[3] = "P = " + getUnitText(getPower(), "W");
	arr[4] = "Vf = " + getVoltageText(fwdrop);
    }
    public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo("Fwd Voltage @ 1A", fwdrop, 10, 1000);
	return null;
    } 
    public void setEditValue(int n, EditInfo ei) {
	fwdrop = ei.value;
	
	// save diode drop value for next time we create a diode
	if (!(this instanceof LEDElm))
	    lastFwdrop = fwdrop;
	
	setup();
    }
    int getShortcut() { return 'd'; }
    
    void stepFinished() {
        // stop for huge currents that make simulator act weird
        if (Math.abs(current) > 1e12)
            sim.stop("max current exceeded", this);
    }


}
