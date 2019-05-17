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

// 0 = switch
// 1 = switch end 1
// 2 = switch end 2
// ...
// 3n   = coil
// 3n+1 = coil
// 3n+2 = end of coil resistor

class RelayElm extends CircuitElm {
    double inductance;
    Inductor ind;
    double r_on, r_off, onCurrent;
    Point coilPosts[], coilLeads[], swposts[][], swpoles[][], ptSwitch[];
    Point lines[];
    double coilCurrent, switchCurrent[], coilCurCount, switchCurCount[];
    double d_position, coilR;
    int i_position;
    int poleCount;
    int openhs;
    final int nSwitch0 = 0;
    final int nSwitch1 = 1;
    final int nSwitch2 = 2;
    int nCoil1, nCoil2, nCoil3;
    final int FLAG_SWAP_COIL = 1;
    
    public RelayElm(int xx, int yy) {
	super(xx, yy);
	ind = new Inductor(sim);
	inductance = .2;
	ind.setup(inductance, 0, Inductor.FLAG_BACK_EULER);
	noDiagonal = true;
	onCurrent = .02;
	r_on = .05;
	r_off = 1e6;
	coilR = 20;
	coilCurrent = coilCurCount = 0;
	poleCount = 1;
	setupPoles();
    }
    public RelayElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	poleCount = new Integer(st.nextToken()).intValue();
	inductance = new Double(st.nextToken()).doubleValue();
	coilCurrent = new Double(st.nextToken()).doubleValue();
	r_on = new Double(st.nextToken()).doubleValue();
	r_off = new Double(st.nextToken()).doubleValue();
	onCurrent = new Double(st.nextToken()).doubleValue();
	coilR = new Double(st.nextToken()).doubleValue();
	noDiagonal = true;
	ind = new Inductor(sim);
	ind.setup(inductance, coilCurrent, Inductor.FLAG_BACK_EULER);
	setupPoles();
    }
    
    void setupPoles() {
	nCoil1 = 3*poleCount;
	nCoil2 = nCoil1+1;
	nCoil3 = nCoil1+2;
	if (switchCurrent == null || switchCurrent.length != poleCount) {
	    switchCurrent = new double[poleCount];
	    switchCurCount = new double[poleCount];
	}
    }
    
    int getDumpType() { return 178; }
    
    String dump() {
	return super.dump() + " " + poleCount + " " +
	    inductance + " " + coilCurrent + " " +
	    r_on + " " + r_off + " " + onCurrent + " " + coilR;
    }
    
    void draw(Graphics g) {
	int i, p;
	for (i = 0; i != 2; i++) {
	    setVoltageColor(g, volts[nCoil1+i]);
	    drawThickLine(g, coilLeads[i], coilPosts[i]);
	}
	int x = ((flags & FLAG_SWAP_COIL) != 0) ? 1 : 0;
	setPowerColor(g, coilCurrent * (volts[nCoil1]-volts[nCoil2]));
	drawCoil(g, dsign*6, coilLeads[x], coilLeads[1-x],
		 volts[nCoil1+x], volts[nCoil2-x]);

	// draw lines
	g.setColor(Color.darkGray);
	for (i = 0; i != poleCount; i++) {
	    if (i == 0)
		interpPoint(point1, point2, lines[i*2  ], .5,
			    openhs*2+5*dsign-i*openhs*3);
	    else
		interpPoint(point1, point2, lines[i*2], .5,
			    (int) (openhs*(-i*3+3-.5+d_position))+5*dsign);
	    interpPoint(point1, point2, lines[i*2+1], .5,
			(int) (openhs*(-i*3-.5+d_position))-5*dsign);
	    g.drawLine(lines[i*2].x, lines[i*2].y, lines[i*2+1].x, lines[i*2+1].y);
	}
	
	for (p = 0; p != poleCount; p++) {
	    int po = p*3;
	    for (i = 0; i != 3; i++) {
		// draw lead
		setVoltageColor(g, volts[nSwitch0+po+i]);
		drawThickLine(g, swposts[p][i], swpoles[p][i]);
	    }
	    
	    interpPoint(swpoles[p][1], swpoles[p][2], ptSwitch[p], d_position);
	    //setVoltageColor(g, volts[nSwitch0]);
	    g.setColor(Color.lightGray);
	    drawThickLine(g, swpoles[p][0], ptSwitch[p]);
	    switchCurCount[p] = updateDotCount(switchCurrent[p],
					       switchCurCount[p]);
	    drawDots(g, swposts[p][0], swpoles[p][0], switchCurCount[p]);
	    
	    if (i_position != 2)
		drawDots(g, swpoles[p][i_position+1], swposts[p][i_position+1],
			 switchCurCount[p]);
	}
	
	coilCurCount = updateDotCount(coilCurrent, coilCurCount);
	
	drawDots(g, coilPosts[0], coilLeads[0], coilCurCount);
	drawDots(g, coilLeads[0], coilLeads[1], coilCurCount);
	drawDots(g, coilLeads[1], coilPosts[1], coilCurCount);
	    
	drawPosts(g);
	setBbox(coilPosts[0], coilLeads[1], 0);
	adjustBbox(swpoles[poleCount-1][0], swposts[poleCount-1][1]); // XXX
    }
	
    double getCurrentIntoNode(int n) {
	if (n < 3*poleCount) {
	    int p = n/3;
	    int k = n%3;
	    if (k == 0)
		return -switchCurrent[p];
	    if (k == 1+i_position)
		return switchCurrent[p];
	    return 0;
	}
	if (n == 3*poleCount)
	    return -coilCurrent;
	return coilCurrent;
    }

    void setPoints() {
	super.setPoints();
	setupPoles();
	allocNodes();
	openhs = -dsign*16;

	// switch
	calcLeads(32);
	swposts = new Point[poleCount][3];
	swpoles = new Point[poleCount][3];
	int i, j;
	for (i = 0; i != poleCount; i++) {
	    for (j = 0; j != 3; j++) {
		swposts[i][j] = new Point();
		swpoles[i][j] = new Point();
	    }
	    interpPoint(lead1,  lead2, swpoles[i][0], 0, -openhs*3*i);
	    interpPoint(lead1,  lead2, swpoles[i][1], 1, -openhs*3*i-openhs);
	    interpPoint(lead1,  lead2, swpoles[i][2], 1, -openhs*3*i+openhs);
	    interpPoint(point1, point2, swposts[i][0], 0, -openhs*3*i);
	    interpPoint(point1, point2, swposts[i][1], 1, -openhs*3*i-openhs);
	    interpPoint(point1, point2, swposts[i][2], 1, -openhs*3*i+openhs);
	}

	// coil
	coilPosts = newPointArray(2);
	coilLeads   = newPointArray(2);
	ptSwitch = newPointArray(poleCount);

	int x = ((flags & FLAG_SWAP_COIL) != 0) ? 1 : 0;
	interpPoint(point1, point2, coilPosts[0],  x, openhs*2);
	interpPoint(point1, point2, coilPosts[1],  x, openhs*3);
	interpPoint(point1, point2, coilLeads[0], .5, openhs*2);
	interpPoint(point1, point2, coilLeads[1], .5, openhs*3);

	// lines
	lines = newPointArray(poleCount*2);
    }
    Point getPost(int n) {
	if (n < 3*poleCount)
	    return swposts[n / 3][n % 3];
	return coilPosts[n-3*poleCount];
    }
    int getPostCount() { return 2+poleCount*3; }
    int getInternalNodeCount() { return 1; }
    void reset() {
	super.reset();
	ind.reset();
	coilCurrent = coilCurCount = 0;
	int i;
	for (i = 0; i != poleCount; i++)
	    switchCurrent[i] = switchCurCount[i] = 0;
    }
    double a1, a2, a3, a4;
    void stamp() {
	// inductor from coil post 1 to internal node
	ind.stamp(nodes[nCoil1], nodes[nCoil3]);
	// resistor from internal node to coil post 2
	sim.stampResistor(nodes[nCoil3], nodes[nCoil2], coilR);

	int i;
	for (i = 0; i != poleCount*3; i++)
	    sim.stampNonLinear(nodes[nSwitch0+i]);
    }
    void startIteration() {
	ind.startIteration(volts[nCoil1]-volts[nCoil3]);

	// magic value to balance operate speed with reset speed semi-realistically
	double magic = 1.3;
	double pmult = Math.sqrt(magic+1);
	double p = coilCurrent*pmult/onCurrent;
	d_position = Math.abs(p*p) - 1.3;
	if (d_position < 0)
	    d_position = 0;
	if (d_position > 1)
	    d_position = 1;
	if (d_position < .1)
	    i_position = 0;
	else if (d_position > .9)
	    i_position = 1;
	else
	    i_position = 2;
	//System.out.println("ind " + this + " " + current + " " + voltdiff);
    }
    	
    // we need this to be able to change the matrix for each step
    boolean nonLinear() { return true; }

    void doStep() {
	double voltdiff = volts[nCoil1]-volts[nCoil3];
	ind.doStep(voltdiff);
	int p;
	for (p = 0; p != poleCount*3; p += 3) {
	    sim.stampResistor(nodes[nSwitch0+p], nodes[nSwitch1+p],
			      i_position == 0 ? r_on : r_off);
	    sim.stampResistor(nodes[nSwitch0+p], nodes[nSwitch2+p],
			      i_position == 1 ? r_on : r_off);
	}
    }
    void calculateCurrent() {
	double voltdiff = volts[nCoil1]-volts[nCoil3];
	coilCurrent = ind.calculateCurrent(voltdiff);

	// actually this isn't correct, since there is a small amount
	// of current through the switch when off
	int p;
	for (p = 0; p != poleCount; p++) {
	    if (i_position == 2)
		switchCurrent[p] = 0;
	    else
		switchCurrent[p] =
		    (volts[nSwitch0+p*3]-volts[nSwitch1+p*3+i_position])/r_on;
	}
    }
    void getInfo(String arr[]) {
	arr[0] = i_position == 0 ? "relay (off)" :
	    i_position == 1 ? "relay (on)" : "relay";
	int i;
	int ln = 1;
	for (i = 0; i != poleCount; i++)
	    arr[ln++] = "I" + (i+1) + " = " + getCurrentDText(switchCurrent[i]);
	arr[ln++] = "coil I = " + getCurrentDText(coilCurrent);
	arr[ln++] = "coil Vd = " +
	    getVoltageDText(volts[nCoil1] - volts[nCoil2]);
    }
    public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo("Inductance (H)", inductance, 0, 0);
	if (n == 1)
	    return new EditInfo("On Resistance (ohms)", r_on, 0, 0);
	if (n == 2)
	    return new EditInfo("Off Resistance (ohms)", r_off, 0, 0);
	if (n == 3)
	    return new EditInfo("On Current (A)", onCurrent, 0, 0);
	if (n == 4)
	    return new EditInfo("Number of Poles", poleCount, 1, 4).
		setDimensionless();
	if (n == 5)
	    return new EditInfo("Coil Resistance (ohms)", coilR, 0, 0);
	if (n == 6) {
	    EditInfo ei = new EditInfo("", 0, -1, -1);
	    ei.checkbox = new Checkbox("Swap Coil Direction",
				       (flags & FLAG_SWAP_COIL) != 0);
	    return ei;
	}
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0 && ei.value > 0) {
	    inductance = ei.value;
	    ind.setup(inductance, coilCurrent, Inductor.FLAG_BACK_EULER);
	}
	if (n == 1 && ei.value > 0)
	    r_on = ei.value;
	if (n == 2 && ei.value > 0)
	    r_off = ei.value;
	if (n == 3 && ei.value > 0)
	    onCurrent = ei.value;
	if (n == 4 && ei.value >= 1) {
	    poleCount = (int) ei.value;
	    setPoints();
	}
	if (n == 5 && ei.value > 0)
	    coilR = ei.value;
	if (n == 6) {
	    if (ei.checkbox.getState())
		flags |= FLAG_SWAP_COIL;
	    else
		flags &= ~FLAG_SWAP_COIL;
	    setPoints();
	}
    }
    boolean getConnection(int n1, int n2) {
	return (n1 / 3 == n2 / 3);
    }
    int getShortcut() { return 'R'; }
}
    
