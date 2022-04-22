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

    class DelayBufferElm extends CircuitElm {
	double delay, threshold, highVoltage;
	
	public DelayBufferElm(int xx, int yy) {
	    super(xx, yy);
	    noDiagonal = true;
	    threshold = 2.5;
	    highVoltage = 5;
	}
	public DelayBufferElm(int xa, int ya, int xb, int yb, int f,
			      StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    noDiagonal = true;
	    delay = Double.parseDouble(st.nextToken());
	    threshold = 2.5;
	    highVoltage = 5;
	    try {
		threshold = Double.parseDouble(st.nextToken());
		highVoltage = Double.parseDouble(st.nextToken());
	    } catch (Exception e) {}
	}
	String dump() {
	    return super.dump() + " " + delay + " " + threshold + " " + highVoltage;
	}
	
	int getDumpType() { return 422; }
	
	Point center;
	
	void draw(Graphics g) {
	    drawPosts(g);
	    draw2Leads(g);
	    g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	    drawThickPolygon(g, gatePoly);
	    if (GateElm.useEuroGates())
		drawCenteredText(g, "1", center.x, center.y-6, true);
	    curcount = updateDotCount(current, curcount);
	    drawDots(g, lead2, point2, curcount);
	}
	Polygon gatePoly;
	void setPoints() {
	    super.setPoints();
	    int hs = 16;
	    int ww = 16-2;
	    if (ww > dn/2)
		ww = (int) (dn/2);
	    lead1 = interpPoint(point1, point2, .5-ww/dn);
	    lead2 = interpPoint(point1, point2, .5+ww/dn);
	    
	    if (GateElm.useEuroGates()) {
		Point pts[] = newPointArray(4);
		Point l2 = interpPoint(point1, point2, .5+(ww-5)/dn);
		interpPoint2(lead1, l2, pts[0], pts[1], 0, hs);
		interpPoint2(lead1, l2, pts[3], pts[2], 1, hs);
		gatePoly = createPolygon(pts);
		center = interpPoint(lead1, l2, .5);
	    } else {
		Point triPoints[] = newPointArray(3);
		interpPoint2(lead1, lead2, triPoints[0], triPoints[1], 0, hs);
		triPoints[2] = interpPoint(point1, point2, .5+ww/dn);
		gatePoly = createPolygon(triPoints);
	    }
	    setBbox(point1, point2, hs);
	}
	int getVoltageSourceCount() { return 1; }
	void stamp() {
	    sim.stampVoltageSource(0, nodes[1], voltSource);
	}
	
	double delayEndTime;
	
	void doStep() {
	    boolean inState = volts[0] > threshold;
	    boolean outState = volts[1] > threshold;
	    if (inState != outState) {
		if (sim.t >= delayEndTime)
		    outState = inState;
	    } else
		delayEndTime = sim.t + delay;
	    sim.updateVoltageSource(0, nodes[1], voltSource, outState ? highVoltage : 0);
	}
	double getVoltageDiff() { return volts[0]; }
	void getInfo(String arr[]) {
	    arr[0] = sim.LS("buffer");
	    arr[1] = sim.LS("delay = " )+ getUnitText(delay, "s");
	    arr[2] = "Vi = " + getVoltageText(volts[0]);
	    arr[3] = "Vo = " + getVoltageText(volts[1]);
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Delay (s)", delay, 0, 0);
	    if (n == 1)
		return new EditInfo("Threshold (V)", threshold, 0, 0);
	    if (n == 2)
		return new EditInfo("High Logic Voltage", highVoltage, 0, 0);
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0)
		delay = ei.value;
	    if (n == 1)
		threshold = ei.value;
	    if (n == 2)
		highVoltage = ei.value;
	}
	// there is no current path through the inverter input, but there
	// is an indirect path through the output to ground.
	boolean getConnection(int n1, int n2) { return false; }
	boolean hasGroundConnection(int n1) {
	    return (n1 == 1);
	}
	
	@Override double getCurrentIntoNode(int n) {
	    if (n == 1)
		return current;
	    return 0;
	}

    }
