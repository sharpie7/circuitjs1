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

class TriodeElm extends CircuitElm {
    double mu, kg1;
    double curcountp, curcountc, curcountg, currentp, currentg, currentc;
    final double gridCurrentR = 6000;
    public TriodeElm(int xx, int yy) {
	super(xx, yy);
	mu = 93;
	kg1 = 680;
	setup();
    }
    public TriodeElm(int xa, int ya, int xb, int yb, int f,
		     StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	mu  = new Double(st.nextToken()).doubleValue();
	kg1 = new Double(st.nextToken()).doubleValue();
	setup();
    }
    void setup() {
	noDiagonal = true;
    }
    boolean nonLinear() { return true; }
    void reset() {
	volts[0] = volts[1] = volts[2] = 0;
	curcount = 0;
    }
    String dump() {
	return super.dump() + " " + mu + " " + kg1;
    }
    int getDumpType() { return 173; }
	
    Point plate[], grid[], cath[], midgrid, midcath;
    int circler;
    void setPoints() {
	super.setPoints();
	plate = newPointArray(4);
	grid  = newPointArray(8);
	cath  = newPointArray(4);
	grid[0] = point1;
	int nearw = 8;
	interpPoint(point1, point2, plate[1], 1, nearw);
	int farw  = 32;
	interpPoint(point1, point2, plate[0], 1, farw);
	int platew = 18;
	interpPoint2(point2, plate[1], plate[2], plate[3], 1, platew);
	    
	circler = 24;
	interpPoint(point1, point2, grid[1], (dn-circler)/dn, 0);
	int i;
	for (i = 0; i != 3; i++) {
	    interpPoint(grid[1], point2, grid[2+i*2], (i*3+1)/4.5, 0);
	    interpPoint(grid[1], point2, grid[3+i*2], (i*3+2)/4.5, 0);
	}
	midgrid = point2;

	int cathw = 16;
	midcath = interpPoint(point1, point2, 1, -nearw);
	interpPoint2(point2, plate[1], cath[1], cath[2], -1, cathw);
	interpPoint(point2, plate[1], cath[3], -1.2, -cathw);
	interpPoint(point2, plate[1], cath[0], -farw/(double) nearw, cathw);
    }
	
    void draw(Graphics g) {
	g.setColor(Color.gray);
	drawThickCircle(g, point2.x, point2.y, circler);
	setBbox(point1, plate[0], 16);
	adjustBbox(cath[0].x, cath[1].y, point2.x+circler, point2.y+circler);
	// draw plate
	setVoltageColor(g, volts[0]);
	setPowerColor(g, currentp*(volts[0]-volts[2]));
	drawThickLine(g, plate[0], plate[1]);
	drawThickLine(g, plate[2], plate[3]);
	// draw grid
	setVoltageColor(g, volts[1]);
	setPowerColor(g, currentg*(volts[1]-volts[2]));
	int i;
	for (i = 0; i != 8; i += 2)
	    drawThickLine(g, grid[i], grid[i+1]);
	// draw cathode
	setVoltageColor(g, volts[2]);
	setPowerColor(g, 0);
	for (i = 0; i != 3; i++)
	    drawThickLine(g, cath[i], cath[i+1]);
	// draw dots
	curcountp = updateDotCount(currentp, curcountp);
	curcountc = updateDotCount(currentc, curcountc);
	curcountg = updateDotCount(currentg, curcountg);
	if (sim.dragElm != this) {
	    drawDots(g, plate[0], midgrid, curcountp);
	    drawDots(g, midgrid,  midcath, curcountc);
	    drawDots(g, midcath,  cath[1], addCurCount(curcountc, 8));
	    drawDots(g, cath[1],  cath[0], addCurCount(curcountc, 8));
	    drawDots(g, point1, midgrid, curcountg);
	}
	drawPosts(g);
    }
    
    double getCurrentIntoNode(int n) {
	if (n == 2)
	    return currentc;
	if (n == 0)
	    return -currentp;
	return -currentg;
    }

    Point getPost(int n) {
	return (n == 0) ? plate[0] : (n == 1) ? grid[0] : cath[0];
    }
    int getPostCount() { return 3; }
    double getPower() { return (volts[0]-volts[2])*currentc + (volts[gridN]-volts[cathN])*currentg; }
    double getCurrent() { return currentc; } // for scope

    final int gridN = 1;
    final int cathN = 2;
    final int plateN = 0;

    double lastv0, lastv1, lastv2;
    void doStep() {
	double vs[] = new double[3];
	vs[0] = volts[0];
	vs[1] = volts[1];
	vs[2] = volts[2];
	if (vs[1] > lastv1 + .5)
	    vs[1] = lastv1 + .5;
	if (vs[1] < lastv1 - .5)
	    vs[1] = lastv1 - .5;
	if (vs[2] > lastv2 + .5)
	    vs[2] = lastv2 + .5;
	if (vs[2] < lastv2 - .5)
	    vs[2] = lastv2 - .5;
	double vgk = vs[gridN] -vs[cathN];
	double vpk = vs[plateN]-vs[cathN];
	if (Math.abs(lastv0-vs[0]) > .01 ||
	    Math.abs(lastv1-vs[1]) > .01 ||
	    Math.abs(lastv2-vs[2]) > .01)
	    sim.converged = false;
	lastv0 = vs[0];
	lastv1 = vs[1];
	lastv2 = vs[2];
	double ids = 0;
	double gm = 0;
	double Gds = 0;
	double ival = vgk+vpk/mu;
	currentg = 0;
	if (vgk > .01) {
	    sim.stampResistor(nodes[gridN], nodes[cathN], gridCurrentR);
	    currentg = vgk/gridCurrentR;
	} else
	    sim.stampResistor(nodes[gridN], nodes[cathN], 1e8); // avoid singular matrix 
	if (ival < 0) {
	    // should be all zero, but that causes a singular matrix,
	    // so instead we treat it as a large resistor
	    Gds = 1e-8;
	    ids = vpk*Gds;
	} else {
	    ids = Math.pow(ival, 1.5)/kg1;
	    double q = 1.5*Math.sqrt(ival)/kg1;
	    // gm = dids/dgk;
	    // Gds = dids/dpk;
	    Gds = q;
	    gm = q/mu;
	}
	currentp = ids;
	currentc = ids+currentg;
	double rs = -ids + Gds*vpk + gm*vgk;
	sim.stampMatrix(nodes[plateN],  nodes[plateN],  Gds);
	sim.stampMatrix(nodes[plateN],  nodes[cathN],  -Gds-gm); 
	sim.stampMatrix(nodes[plateN],  nodes[gridN],   gm);
	    
	sim.stampMatrix(nodes[cathN],   nodes[plateN],  -Gds);
	sim.stampMatrix(nodes[cathN],   nodes[cathN],    Gds+gm); 
	sim.stampMatrix(nodes[cathN],   nodes[gridN],   -gm);
	    
	sim.stampRightSide(nodes[plateN],  rs);
	sim.stampRightSide(nodes[cathN ], -rs);
    }

    void stamp() {
	sim.stampNonLinear(nodes[0]);
	sim.stampNonLinear(nodes[1]);
	sim.stampNonLinear(nodes[2]);
    }
    void getInfo(String arr[]) {
	arr[0] = "triode";
	double vac = volts[plateN]-volts[cathN];
	double vgc = volts[gridN ]-volts[cathN];
	double vag = volts[plateN]-volts[gridN];
	arr[1] = "Vac = " + getVoltageText(vac);
	arr[2] = "Vgc = " + getVoltageText(vgc);
	arr[3] = "Vag = " + getVoltageText(vag);
	arr[4] = "Ic = " + getUnitText(currentc, "A");
	arr[5] = "Ig = " + getUnitText(currentg, "A");
    }
    // grid not connected to other terminals
    boolean getConnection(int n1, int n2) { return !(n1 == 1 || n2 == 1); }
    public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo("mu", mu, 0, 0).setDimensionless();
	if (n == 1)
	    return new EditInfo("kg1", kg1, 0, 0).setDimensionless();
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0 && ei.value > 0)
	    mu = ei.value;
	if (n == 1 && ei.value > 0)
	    kg1 = ei.value;
    }
    boolean canViewInScope() { return true; }
    double getVoltageDiff() { return volts[plateN] - volts[cathN]; }    
}

