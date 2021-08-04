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

import java.util.HashMap;

class LabeledNodeElm extends CircuitElm {
    final int FLAG_ESCAPE = 4;
    final int FLAG_INTERNAL = 1;
    
    public LabeledNodeElm(int xx, int yy) {
	super(xx, yy);
	text = "label";
    }
    public LabeledNodeElm(int xa, int ya, int xb, int yb, int f,
	    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	text = st.nextToken();
	if ((flags & FLAG_ESCAPE) == 0) {
	    // old-style dump before escape/unescape
	    while (st.hasMoreTokens())
		text += ' ' + st.nextToken();
	} else {
	    // new-style dump
	    text = CustomLogicModel.unescape(text); 
	}
    }
    String dump() {
	flags |= FLAG_ESCAPE;
	return super.dump() + " " + CustomLogicModel.escape(text);
    }

    String text;
    
    class LabelEntry {
	Point point;
	int node;
    }
    
    static HashMap<String,LabelEntry> labelList;
    boolean isInternal() { return (flags & FLAG_INTERNAL) != 0; }

    public static native void console(String text)
    /*-{
	    console.log(text);
	}-*/;

    static void resetNodeList() {
	labelList = new HashMap<String,LabelEntry>();
    }
    final int circleSize = 17;
    void setPoints() {
	super.setPoints();
	lead1 = interpPoint(point1, point2, 1-circleSize/dn);
    }
    
    // get post we're connected to
    Point getConnectedPost() {
	LabelEntry le = labelList.get(text);
	if (le != null)
	    return le.point;
	
	// this is the first time calcWireClosure() encountered this label.  so save point1 and
	// return null for now, but return point1 the next time we see this label so that all nodes
	// with the same label are connected
	le = new LabelEntry();
	le.point = point1;
	labelList.put(text, le);
	return null;
    }
    
    void setNode(int p, int n) {
	super.setNode(p, n);
	
	// save node number so we can return it in getByName()
	LabelEntry le = labelList.get(text);
	if (le != null) // should never happen
	    le.node = n;
    }
    
    int getDumpType() { return 207; }
    int getPostCount() { return 1; }
    
    // this is basically a wire, since it just connects two or more nodes together
    boolean isWireEquivalent() { return true; }
    boolean isRemovableWire() { return true; }
    
    static Integer getByName(String n) {
	LabelEntry le = labelList.get(n);
	if (le == null)
	    return null;
	return le.node;
    }
    
    void draw(Graphics g) {
	setVoltageColor(g, volts[0]);
	drawThickLine(g, point1, lead1);
	g.setColor(needsHighlight() ? selectColor : whiteColor);
	setPowerColor(g, false);
	interpPoint(point1, point2, ps2, 1+11./dn);
	setBbox(point1, ps2, circleSize);
	drawLabeledNode(g, text, point1, lead1);

	curcount = updateDotCount(current, curcount);
	drawDots(g, point1, lead1, curcount);
	drawPosts(g);
    }
    double getCurrentIntoNode(int n) { return -current; }
    void setCurrent(int x, double c) { current = c; }
    double getVoltageDiff() { return volts[0]; }
    void getInfo(String arr[]) {
	arr[0] = text;
	arr[1] = "I = " + getCurrentText(getCurrent());
	arr[2] = "V = " + getVoltageText(volts[0]);
    }

    public EditInfo getEditInfo(int n) {
	if (n == 0) {
	    EditInfo ei = new EditInfo("Text", 0, -1, -1);
	    ei.text = text;
	    return ei;
	}
        if (n == 1) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new Checkbox("Internal Node", isInternal());
            return ei;
        }
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    text = ei.textf.getText();
	if (n == 1)
	    flags = ei.changeFlag(flags, FLAG_INTERNAL);
    }
    @Override String getScopeText(int v) {
	return text;
    }
}
