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

class SparkGapElm extends CircuitElm {
    double resistance, onresistance, offresistance, breakdown, holdcurrent;
    boolean state;
    public SparkGapElm(int xx, int yy) {
	super(xx, yy);
	offresistance = 1e9;
	onresistance = 1e3;
	breakdown = 1e3;
	holdcurrent = 0.001;
	state = false;
    }
    public SparkGapElm(int xa, int ya, int xb, int yb, int f,
		       StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	onresistance = new Double(st.nextToken()).doubleValue();
	offresistance = new Double(st.nextToken()).doubleValue();
	breakdown = new Double(st.nextToken()).doubleValue();
	holdcurrent = new Double(st.nextToken()).doubleValue();
    }
    boolean nonLinear() {return true;}
    int getDumpType() { return 187; }
    String dump() {
	return super.dump() + " " + onresistance + " " + offresistance + " "
	    + breakdown + " " + holdcurrent;
    }
    Polygon arrow1, arrow2;
    void setPoints() {
	super.setPoints();
	int dist = 16;
	int alen = 8;
	calcLeads(dist+alen);
	Point p1 = interpPoint(point1, point2, (dn-alen)/(2*dn));
	arrow1 = calcArrow(point1, p1, alen, alen);
	p1 = interpPoint(point1, point2, (dn+alen)/(2*dn));
	arrow2 = calcArrow(point2, p1, alen, alen);
    }
    
    void draw(Graphics g) {
	int i;
	double v1 = volts[0];
	double v2 = volts[1];
	setBbox(point1, point2, 8);
	draw2Leads(g);
	setVoltageColor(g, volts[0]);
	setPowerColor(g, true);
	g.fillPolygon(arrow1);
	setVoltageColor(g, volts[1]);
	setPowerColor(g, true);
	g.fillPolygon(arrow2);
	if (state)
	    doDots(g);
	drawPosts(g);
    }
    
    void calculateCurrent() {
	double vd = volts[0] - volts[1];
	current = vd/resistance;
    }

    void reset() {
	super.reset();
	state = false;
    }

    void startIteration() {
	if (Math.abs(current) < holdcurrent)
	    state = false;
	double vd = volts[0] - volts[1];
	if (Math.abs(vd) > breakdown)
	    state = true;
    }
    
    void doStep() {
	resistance = (state) ? onresistance : offresistance;
	sim.stampResistor(nodes[0], nodes[1], resistance);
    }
    void stamp() {
	sim.stampNonLinear(nodes[0]);
	sim.stampNonLinear(nodes[1]);
    }
    void getInfo(String arr[]) {
	arr[0] = "spark gap";
	getBasicInfo(arr);
	arr[3] = state ? "on" : "off";
	arr[4] = "Ron = " + getUnitText(onresistance, sim.ohmString);
	arr[5] = "Roff = " + getUnitText(offresistance, sim.ohmString);
	arr[6] = "Vbreakdown = " + getUnitText(breakdown, "V");
    }
    public EditInfo getEditInfo(int n) {
	// ohmString doesn't work here on linux
	if (n == 0)
	    return new EditInfo("On resistance (ohms)", onresistance, 0, 0);
	if (n == 1)
	    return new EditInfo("Off resistance (ohms)", offresistance, 0, 0);
	if (n == 2)
	    return new EditInfo("Breakdown voltage", breakdown, 0, 0);
	if (n == 3)
	    return new EditInfo("Holding current (A)", holdcurrent, 0, 0);
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (ei.value > 0 && n == 0)
	    onresistance = ei.value;
	if (ei.value > 0 && n == 1)
	    offresistance = ei.value;
	if (ei.value > 0 && n == 2)
	    breakdown = ei.value;
	if (ei.value > 0 && n == 3)
	    holdcurrent = ei.value;
    }
}

