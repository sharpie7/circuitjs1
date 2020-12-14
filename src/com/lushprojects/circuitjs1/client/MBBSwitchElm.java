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

    class MBBSwitchElm extends SwitchElm {
	int link;
	int voltSources[];
	double currents[];
	double curcounts[];
	boolean both;
	
	public MBBSwitchElm(int xx, int yy) {
	    super(xx, yy, false);
	    setup();
	}
	
	void setup() {
	    noDiagonal = true;
	    voltSources = new int[2];
	    currents = new double[2];
	    curcounts = new double[3];
	}
	
	public MBBSwitchElm(int xa, int ya, int xb, int yb, int f,
			  StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    link = new Integer(st.nextToken()).intValue();
	    setup();
	}
	
	int getDumpType() { return 416; }
	String dump() {
	    return super.dump() + " " + link;
	}

	final int openhs = 16;
	Point swposts[], swpoles[];
	void setPoints() {
	    super.setPoints();
	    calcLeads(32);
	    swposts = newPointArray(2);
	    swpoles = newPointArray(2+2);
	    int i;
	    for (i = 0; i != 2; i++) {
		int hs = -openhs*(i-(2-1)/2);
		if (i == 0)
		    hs = openhs;
		interpPoint(lead1,  lead2,  swpoles[i], 1, hs);
		interpPoint(point1, point2, swposts[i], 1, hs);
	    }
	    
	    // 4 positions (pole 1, both, pole 2, both)
	    posCount = 4;
	}
	
	void draw(Graphics g) {
	    
	    setBbox(point1, point2, openhs);
	    adjustBbox(swposts[0], swposts[1]);

	    // draw first lead
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, lead1);

	    // draw other leads
	    int i;
	    for (i = 0; i != 2; i++) {
		setVoltageColor(g, volts[i+1]);
		drawThickLine(g, swpoles[i], swposts[i]);
	    }
	    
	    // draw switch
	    if (!needsHighlight())
		g.setColor(whiteColor);
	    if (both || position == 0)
		drawThickLine(g, lead1, swpoles[0]);
	    if (both || position == 2)
		drawThickLine(g, lead1, swpoles[1]);

	    // draw current
	    for (i = 0; i != 2; i++) {
		curcounts[i] = updateDotCount(currents[i], curcounts[i]);
		drawDots(g, swpoles[i], swposts[i], curcounts[i]);
	    }
	    curcounts[2] = updateDotCount(currents[0]+currents[1], curcounts[2]);
	    drawDots(g, point1, lead1, curcounts[2]);
	    drawPosts(g);
	}
	
	double getCurrentIntoNode(int n) {
	    if (n == 0)
		return -currents[0]-currents[1];
	    return currents[n-1];
	}

	Rectangle getSwitchRect() {
	    return new Rectangle(lead1).union(new Rectangle(swpoles[0])).union(new Rectangle(swpoles[1]));
	}	

	Point getPost(int n) {
	    return (n == 0) ? point1 : swposts[n-1];
	}
	
	int getPostCount() { return 3; }
	
	void setCurrent(int vn, double c) {
	    // set current for voltage source vn to c
	    if (vn == voltSources[0])
		currents[both ? 0 : position/2] = c;
	    else if (vn == voltSources[1])
		currents[1] = c;
	}
	void calculateCurrent() {
	    // make sure current of unconnected pole is zero
	    if (!both)
		currents[1-(position/2)] = 0;
	}
	void setVoltageSource(int n, int v) {
	    voltSources[n] = v;
	}
	void stamp() {
	    int vs = 0;
	    if (both || position == 0)
		sim.stampVoltageSource(nodes[0], nodes[1], voltSources[vs++], 0);
	    if (both || position == 2)
		sim.stampVoltageSource(nodes[0], nodes[2], voltSources[vs++], 0);
	}
	
	// connection is implemented by voltage source with voltage = 0.
	// need two for both loads connected, otherwise one.
	int getVoltageSourceCount() {
	    both = (position == 1 || position == 3);
	    return (both) ? 2 : 1;
	}
	void toggle() {
	    super.toggle();
	    if (link != 0) {
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
		    Object o = sim.elmList.elementAt(i);
		    if (o instanceof MBBSwitchElm) {
			MBBSwitchElm s2 = (MBBSwitchElm) o;
			if (s2.link == link)
			    s2.position = position;
		    }
		}
	    }
	}
	boolean getConnection(int n1, int n2) {
	    if (both)
		return true;
	    return comparePair(n1, n2, 0, 1+position/2);
	}
	boolean isWire() { return true; }
	void getInfo(String arr[]) {
	    arr[0] = "switch (" + (link == 0 ? "S" : "D") + "PDT, MBB)";
	    arr[1] = "I = " + getCurrentDText(getCurrent());
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 1)
	    	return new EditInfo("Switch Group", link, 0, 100).setDimensionless();
	    return super.getEditInfo(n);
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 1) {
	    	link = (int) ei.value;
	    } else
	    	super.setEditValue(n, ei);
	}
    }
