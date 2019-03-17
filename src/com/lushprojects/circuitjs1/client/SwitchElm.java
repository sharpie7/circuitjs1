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

// SPST switch
class SwitchElm extends CircuitElm {
    boolean momentary;
    // position 0 == closed, position 1 == open
    int position, posCount;
    public SwitchElm(int xx, int yy) {
	super(xx, yy);
	momentary = false;
	position = 0;
	posCount = 2;
    }
    SwitchElm(int xx, int yy, boolean mm) {
	super(xx, yy);
	position = (mm) ? 1 : 0;
	momentary = mm;
	posCount = 2;
    }
    public SwitchElm(int xa, int ya, int xb, int yb, int f,
		     StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	String str = st.nextToken();
	if (str.compareTo("true") == 0)
	    position = (this instanceof LogicInputElm) ? 0 : 1;
	else if (str.compareTo("false") == 0)
	     position = (this instanceof LogicInputElm) ? 1 : 0;
	else
	    position = new Integer(str).intValue();
	momentary = new Boolean(st.nextToken()).booleanValue();
	posCount = 2;
    }
    int getDumpType() { return 's'; }
    String dump() {
	return super.dump() + " " + position + " " + momentary;
    }

    Point ps, ps2;
    void setPoints() {
	super.setPoints();
	calcLeads(32);
	ps  = new Point();
	ps2 = new Point();
    }
    
    final int openhs = 16;
	
    void draw(Graphics g) {
	int hs1 = (position == 1) ? 0 : 2;
	int hs2 = (position == 1) ? openhs : 2;
	setBbox(point1, point2, openhs);

	draw2Leads(g);
	    
	if (position == 0)
	    doDots(g);
	    
	if (!needsHighlight())
	    g.setColor(whiteColor);
	interpPoint(lead1, lead2, ps,  0, hs1);
	interpPoint(lead1, lead2, ps2, 1, hs2);
	    
	drawThickLine(g, ps, ps2);
	drawPosts(g);
    }
    
    Rectangle getSwitchRect() {
	interpPoint(lead1, lead2, ps,  0, openhs);
	return new Rectangle(lead1).union(new Rectangle(lead2)).union(new Rectangle(ps));
    }
    
    void calculateCurrent() {
	if (position == 1)
	    current = 0;
    }
    
    void stamp() {
	if (position == 0)
	    sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
    }
    int getVoltageSourceCount() {
	return (position == 1) ? 0 : 1;
    }
    void mouseUp() {
	if (momentary)
	    toggle();
    }
    void toggle() {
	position++;
	if (position >= posCount)
	    position = 0;
    }
    void getInfo(String arr[]) {
	arr[0] = (momentary) ? "push switch (SPST)" : "switch (SPST)";
	if (position == 1) {
	    arr[1] = "open";
	    arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
	} else {
	    arr[1] = "closed";
	    arr[2] = "V = " + getVoltageText(volts[0]);
	    arr[3] = "I = " + getCurrentDText(getCurrent());
	}
    }
    boolean getConnection(int n1, int n2) { return position == 0; }
    boolean isWire() { return position == 0; }
    public EditInfo getEditInfo(int n) {
	if (n == 0) {
	    EditInfo ei = new EditInfo("", 0, -1, -1);
	    ei.checkbox = new Checkbox("Momentary Switch", momentary);
	    return ei;
	}
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    momentary = ei.checkbox.getState();
    }
    int getShortcut() { return 's'; }
}
