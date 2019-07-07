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

// SPDT switch

    class Switch2Elm extends SwitchElm {
	int link;
	int throwCount;
	static final int FLAG_CENTER_OFF = 1;
	
	public Switch2Elm(int xx, int yy) {
	    super(xx, yy, false);
	    noDiagonal = true;
	    throwCount = 2;
	}
	Switch2Elm(int xx, int yy, boolean mm) {
	    super(xx, yy, mm);
	    noDiagonal = true;
	    throwCount = 2;
	}
	public Switch2Elm(int xa, int ya, int xb, int yb, int f,
			  StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    link = new Integer(st.nextToken()).intValue();
	    throwCount = 2;
	    try {
		throwCount = new Integer(st.nextToken()).intValue();
	    } catch (Exception e) { }
	    noDiagonal = true;
	}
	int getDumpType() { return 'S'; }
	String dump() {
	    return super.dump() + " " + link + " " + throwCount;
	}

	final int openhs = 16;
	Point swposts[], swpoles[];
	void setPoints() {
	    super.setPoints();
	    calcLeads(32);
	    swposts = newPointArray(throwCount);
	    swpoles = newPointArray(2+throwCount);
	    int i;
	    for (i = 0; i != throwCount; i++) {
		int hs = -openhs*(i-(throwCount-1)/2);
		if (throwCount == 2 && i == 0)
		    hs = openhs;
		interpPoint(lead1,  lead2,  swpoles[i], 1, hs);
		interpPoint(point1, point2, swposts[i], 1, hs);
	    }
	    swpoles[i] = lead2; // for center off
	    posCount = hasCenterOff() ? 3 : throwCount;
	}
	
	void draw(Graphics g) {
	    setBbox(point1, point2, openhs);
	    adjustBbox(swposts[0], swposts[throwCount-1]);

	    // draw first lead
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, lead1);

	    // draw other leads
	    int i;
	    for (i = 0; i != throwCount; i++) {
		setVoltageColor(g, volts[i+1]);
		drawThickLine(g, swpoles[i], swposts[i]);
	    }
	    
	    // draw switch
	    if (!needsHighlight())
		g.setColor(whiteColor);
	    drawThickLine(g, lead1, swpoles[position]);
	    
	    updateDotCount();
	    drawDots(g, point1, lead1, curcount);
	    if (position != 2)
		drawDots(g, swpoles[position], swposts[position], curcount);
	    drawPosts(g);
	}
	
	double getCurrentIntoNode(int n) {
	    if (n == 0)
		return -current;
	    if (n == position+1)
		return current;
	    return 0;
	}

	Rectangle getSwitchRect() {
	    return new Rectangle(lead1).union(new Rectangle(swpoles[0])).union(new Rectangle(swpoles[throwCount-1]));
	}	

	Point getPost(int n) {
	    return (n == 0) ? point1 : swposts[n-1];
	}
	int getPostCount() { return 1+throwCount; }
	void calculateCurrent() {
	    if (position == 2 && hasCenterOff())
		current = 0;
	}
	void stamp() {
	    if (position == 2 && hasCenterOff()) // in center?
		return;
	    sim.stampVoltageSource(nodes[0], nodes[position+1], voltSource, 0);
	}
	int getVoltageSourceCount() {
	    return (position == 2 && hasCenterOff()) ? 0 : 1;
	}
	void toggle() {
	    super.toggle();
	    if (link != 0) {
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
		    Object o = sim.elmList.elementAt(i);
		    if (o instanceof Switch2Elm) {
			Switch2Elm s2 = (Switch2Elm) o;
			if (s2.link == link)
			    s2.position = position;
		    }
		}
	    }
	}
	boolean getConnection(int n1, int n2) {
	    if (position == 2 && hasCenterOff())
		return false;
	    return comparePair(n1, n2, 0, 1+position);
	}
	void getInfo(String arr[]) {
	    arr[0] = "switch (" + (link == 0 ? "S" : "D") + "P" +
		    ((throwCount > 2) ? throwCount+"T)" : "DT)");
	    arr[1] = "I = " + getCurrentDText(getCurrent());
	}
	public EditInfo getEditInfo(int n) {
	    /*if (n == 1) {
	    	EditInfo ei = new EditInfo("", 0, -1, -1);
	    	ei.checkbox = new Checkbox("Center Off", hasCenterOff());
	    	return ei;
	    }*/
	    if (n == 1)
	    	return new EditInfo("Switch Group", link, 0, 100).setDimensionless();
	    if (n == 2)
	    	return new EditInfo("# of Throws", throwCount, 2, 10).setDimensionless();
	    return super.getEditInfo(n);
	}
	public void setEditValue(int n, EditInfo ei) {
	    /*if (n == 1) {
	    	flags &= ~FLAG_CENTER_OFF;
	    	if (ei.checkbox.getState())
	    		flags |= FLAG_CENTER_OFF;
	    	if (hasCenterOff())
	    		momentary = false;
	    	setPoints();
	    } else*/
	    if (n == 1) {
	    	link = (int) ei.value;
	    } else if (n == 2) {
		if (ei.value >= 2)
		    throwCount = (int) ei.value;
		if (throwCount > 2)
		    momentary = false;
		allocNodes();
		setPoints();
	    } else
	    	super.setEditValue(n, ei);
	}
	
	// this is for backwards compatibility only.  we only support it if throwCount = 2
	boolean hasCenterOff() { return (flags & FLAG_CENTER_OFF) != 0 && throwCount == 2; }
	
	int getShortcut() { return 'S'; }
    }
