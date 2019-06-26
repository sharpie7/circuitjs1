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

// Silicon-Controlled Rectifier
// 3 nodes, 1 internal node
// 0 = anode, 1 = cathode, 2 = gate
// 0, 3 = variable resistor
// 3, 2 = diode
// 2, 1 = 50 ohm resistor

class SCRElm extends CircuitElm {
    final int anode = 0;
    final int cnode = 1;
    final int gnode = 2;
    final int inode = 3;
    final int FLAG_GATE_FIX = 1;
    Diode diode;
    
    public SCRElm(int xx, int yy) {
	super(xx, yy);
	setDefaults();
	flags |= FLAG_GATE_FIX;
	setup();
    }
    public SCRElm(int xa, int ya, int xb, int yb, int f,
		  StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	setDefaults();
	try {
	    lastvac = new Double(st.nextToken()).doubleValue();
	    lastvag = new Double(st.nextToken()).doubleValue();
	    volts[anode] = 0;
	    volts[cnode] = -lastvac;
	    volts[gnode] = -lastvag;
	    triggerI = new Double(st.nextToken()).doubleValue();
	    holdingI = new Double(st.nextToken()).doubleValue();
	    cresistance = new Double(st.nextToken()).doubleValue();
	} catch (Exception e) {
	}
	setup();
    }
    void setDefaults() {
	cresistance = 50;
	holdingI = .0082;
	triggerI = .01;
    }
    void setup() {
	diode = new Diode(sim);
	diode.setupForDefaultModel();
    }
    boolean nonLinear() { return true; }
    void reset() {
	volts[anode] = volts[cnode] = volts[gnode] = 0;
	diode.reset();
	lastvag = lastvac = curcount_a = curcount_c = curcount_g = 0;
    }
    int getDumpType() { return 177; }
    String dump() {
	return super.dump() + " " + (volts[anode]-volts[cnode]) + " " +
	    (volts[anode]-volts[gnode]) + " " + triggerI + " "+  holdingI + " " +
	    cresistance;
    }
    double ia, ic, ig, curcount_a, curcount_c, curcount_g;
    double lastvac, lastvag;
    double cresistance, triggerI, holdingI;

    final int hs = 8;
    Polygon poly;
    Point cathode[], gate[];
	
    boolean applyGateFix() { return (flags & FLAG_GATE_FIX) != 0; }
    
    void setPoints() {
	super.setPoints();
	int dir = 0;
	if (abs(dx) > abs(dy)) {
	    dir = -sign(dx)*sign(dy);
	    
	    // correct dn (length) or else calcLeads() may get confused, and also gate may be drawn weirdly.  Can't do this with old circuits or it may
	    // break them
	    if (applyGateFix())
		dn = abs(dx);
	    point2.y = point1.y;
	} else {
	    dir = sign(dy)*sign(dx);
	    if (applyGateFix())
		dn = abs(dy);
	    point2.x = point1.x;
	}
	if (dir == 0)
	    dir = 1;
	calcLeads(16);
	cathode = newPointArray(2);
	Point pa[] = newPointArray(2);
	interpPoint2(lead1, lead2, pa[0], pa[1], 0, hs);
	interpPoint2(lead1, lead2, cathode[0], cathode[1], 1, hs);
	poly = createPolygon(pa[0], pa[1], lead2);

	gate = newPointArray(2);
	double leadlen = (dn-16)/2;
	int gatelen = sim.gridSize;
	gatelen += leadlen % sim.gridSize;
	if (leadlen < gatelen) {
	    x2 = x; y2 = y;
	    return;
	}
	interpPoint(lead2, point2, gate[0], gatelen/leadlen, gatelen*dir);
	interpPoint(lead2, point2, gate[1], gatelen/leadlen, sim.gridSize*2*dir);
	gate[1].x = sim.snapGrid(gate[1].x);
	gate[1].y = sim.snapGrid(gate[1].y);
    }
	
    void draw(Graphics g) {
	setBbox(point1, point2, hs);
	adjustBbox(gate[0], gate[1]);

	double v1 = volts[anode];
	double v2 = volts[cnode];

	draw2Leads(g);

	// draw arrow thingy
	setVoltageColor(g, v1);
	setPowerColor(g, true);
	g.fillPolygon(poly);

	setVoltageColor(g, volts[gnode]);
	drawThickLine(g, lead2,   gate[0]);
	drawThickLine(g, gate[0], gate[1]);
	
	// draw thing arrow is pointing to
	setVoltageColor(g, v2);
	setPowerColor(g, true);
	drawThickLine(g, cathode[0], cathode[1]);
	
	curcount_a = updateDotCount(ia, curcount_a);
	curcount_c = updateDotCount(ic, curcount_c);
	curcount_g = updateDotCount(ig, curcount_g);
	if (sim.dragElm != this) {
	    drawDots(g, point1, lead2, curcount_a);
	    drawDots(g, point2, lead2, curcount_c);
	    drawDots(g, gate[1], gate[0], curcount_g);
	    drawDots(g, gate[0], lead2, curcount_g+distance(gate[1], gate[0]));
	}
	
	if ((needsHighlight() || sim.dragElm == this) && point1.x == point2.x && point2.y > point1.y) {
	    g.setColor(Color.white);
	    int ds = sign(dx);
	    g.drawString("C", lead2.x+((ds < 0) ? 5 : -15), lead2.y+12);
	    g.drawString("A", lead1.x+5, lead1.y-4); // x+6 if ds=1, -12 if -1
	    g.drawString("G", gate[0].x, gate[0].y+12);
	}
	
	drawPosts(g);
    }
	
    double getCurrentIntoNode(int n) {
	if (n == anode)
	    return -ia;
	if (n == cnode)
	    return -ic;
	return -ig;
    }

    
    Point getPost(int n) {
	return (n == 0) ? point1 : (n == 1) ? point2 : gate[1];
    }
	
    int getPostCount() { return 3; }
    int getInternalNodeCount() { return 1; }
    double getPower() {
	return (volts[anode]-volts[gnode])*ia + (volts[cnode]-volts[gnode])*ic;
    }

    double aresistance;
    void stamp() {
	sim.stampNonLinear(nodes[anode]);
	sim.stampNonLinear(nodes[cnode]);
	sim.stampNonLinear(nodes[gnode]);
	sim.stampNonLinear(nodes[inode]);
	sim.stampResistor(nodes[gnode], nodes[cnode], cresistance);
	diode.stamp(nodes[inode], nodes[gnode]);
    }

    void doStep() {
	double vac = volts[anode]-volts[cnode]; // typically negative
	double vag = volts[anode]-volts[gnode]; // typically positive
	if (Math.abs(vac-lastvac) > .01 ||
	    Math.abs(vag-lastvag) > .01)
	    sim.converged = false;
	lastvac = vac;
	lastvag = vag;
	diode.doStep(volts[inode]-volts[gnode]);
	double icmult = 1/triggerI;
	double iamult = 1/holdingI - icmult;
	//System.out.println(icmult + " " + iamult);
	aresistance = (-icmult*ic + ia*iamult > 1) ? .0105 : 10e5;
	//System.out.println(vac + " " + vag + " " + sim.converged + " " + ic + " " + ia + " " + aresistance + " " + volts[inode] + " " + volts[gnode] + " " + volts[anode]);
	sim.stampResistor(nodes[anode], nodes[inode], aresistance);
    }
    void getInfo(String arr[]) {
	arr[0] = "SCR";
	double vac = volts[anode]-volts[cnode];
	double vag = volts[anode]-volts[gnode];
	double vgc = volts[gnode]-volts[cnode];
	arr[1] = "Ia = " + getCurrentText(ia);
	arr[2] = "Ig = " + getCurrentText(ig);
	arr[3] = "Vac = " + getVoltageText(vac);
	arr[4] = "Vag = " + getVoltageText(vag);
	arr[5] = "Vgc = " + getVoltageText(vgc);
        arr[6] = "P = " + getUnitText(getPower(), "W");
    }
    void calculateCurrent() {
	ic = (volts[cnode]-volts[gnode])/cresistance;
	ia = (volts[anode]-volts[inode])/aresistance;
	ig = -ic-ia;
    }
    public EditInfo getEditInfo(int n) {
	// ohmString doesn't work here on linux
	if (n == 0)
	    return new EditInfo("Trigger Current (A)", triggerI, 0, 0);
	if (n == 1)
	    return new EditInfo("Holding Current (A)", holdingI, 0, 0);
	if (n == 2)
	    return new EditInfo("Gate-Cathode Resistance (ohms)", cresistance, 0, 0);
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0 && ei.value > 0)
	    triggerI = ei.value;
	if (n == 1 && ei.value > 0)
	    holdingI = ei.value;
	if (n == 2 && ei.value > 0)
	    cresistance = ei.value;
    }
}

