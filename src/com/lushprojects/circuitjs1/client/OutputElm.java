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

    class OutputElm extends CircuitElm {
	final int FLAG_VALUE = 1;
	public OutputElm(int xx, int yy) { super(xx, yy); }
	public OutputElm(int xa, int ya, int xb, int yb, int f,
			 StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	}
	int getDumpType() { return 'O'; }
	int getPostCount() { return 1; }
	void setPoints() {
	    super.setPoints();
	    lead1 = new Point();
	}
	void draw(Graphics g) {
	    boolean selected = needsHighlight();
	    Font f = new Font("SansSerif", selected ? Font.BOLD : 0, 14);
	    g.setFont(f);
	    g.setColor(selected ? selectColor : whiteColor);
	    String s = (flags & FLAG_VALUE) != 0 ? getVoltageText(volts[0]) : "out";
//	    FontMetrics fm = g.getFontMetrics();
	    if (this == sim.plotXElm)
		s = "X";
	    if (this == sim.plotYElm)
		s = "Y";
	    interpPoint(point1, point2, lead1, 1-((int)g.context.measureText(s).getWidth()/2+8)/dn);
	    setBbox(point1, lead1, 0);
	    drawCenteredText(g, s, x2, y2, true);
	    setVoltageColor(g, volts[0]);
	    if (selected)
		g.setColor(selectColor);
	    drawThickLine(g, point1, lead1);
	    drawPosts(g);
	}
	double getVoltageDiff() { return volts[0]; }
	void getInfo(String arr[]) {
	    arr[0] = "output";
	    arr[1] = "V = " + getVoltageText(volts[0]);
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Show Voltage",
					   (flags & FLAG_VALUE) != 0);
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0)
		flags = (ei.checkbox.getState()) ?
		    (flags | FLAG_VALUE) :
		    (flags & ~FLAG_VALUE);
	}
	
//    void drawHandles(Graphics g, Color c) {
//    	g.setColor(c);
//		g.fillRect(x-3, y-3, 7, 7);
//    }
    
    }
