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

    class InductorElm extends CircuitElm {
	Inductor ind;
	double inductance;
	public InductorElm(int xx, int yy) {
	    super(xx, yy);
	    ind = new Inductor(sim);
	    inductance = 1;
	    ind.setup(inductance, current, flags);
	}
	public InductorElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    ind = new Inductor(sim);
	    inductance = new Double(st.nextToken()).doubleValue();
	    current = new Double(st.nextToken()).doubleValue();
	    ind.setup(inductance, current, flags);
	}
	int getDumpType() { return 'l'; }
	String dump() {
	    return super.dump() + " " + inductance + " " + current;
	}
	void setPoints() {
	    super.setPoints();
	    calcLeads(32);
	}
	void draw(Graphics g) {
	    double v1 = volts[0];
	    double v2 = volts[1];
	    int i;
	    int hs = 8;
	    setBbox(point1, point2, hs);
	    draw2Leads(g);
	    setPowerColor(g, false);
	    drawCoil(g, 8, lead1, lead2, v1, v2);
	    if (sim.showValuesCheckItem.getState()) {
		String s = getShortUnitText(inductance, "H");
		drawValues(g, s, hs);
	    }
	    doDots(g);
	    drawPosts(g);
	}
	void reset() {
	    current = volts[0] = volts[1] = curcount = 0;
	    ind.reset();
	}
	void stamp() { ind.stamp(nodes[0], nodes[1]); }
	void startIteration() {
	    ind.startIteration(volts[0]-volts[1]);
	}
	boolean nonLinear() { return ind.nonLinear(); }
	void calculateCurrent() {
	    double voltdiff = volts[0]-volts[1];
	    current = ind.calculateCurrent(voltdiff);
	}
	void doStep() {
	    double voltdiff = volts[0]-volts[1];
	    ind.doStep(voltdiff);
	}
	void getInfo(String arr[]) {
	    arr[0] = "inductor";
	    getBasicInfo(arr);
	    arr[3] = "L = " + getUnitText(inductance, "H");
	    arr[4] = "P = " + getUnitText(getPower(), "W");
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Inductance (H)", inductance, 0, 0);
	    if (n == 1) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Trapezoidal Approximation",
					   ind.isTrapezoidal());
		return ei;
	    }
	    return null;
	}
	
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0 && ei.value > 0)
		inductance = ei.value;
	    if (n == 1) {
		if (ei.checkbox.getState())
		    flags &= ~Inductor.FLAG_BACK_EULER;
		else
		    flags |= Inductor.FLAG_BACK_EULER;
	    }
	    ind.setup(inductance, current, flags);
	}
	
	int getShortcut() { return 'L'; }
	
    }
