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

    class GroundElm extends CircuitElm {
	static int lastSymbolType = 0;
	int symbolType;
	public GroundElm(int xx, int yy) {
	    super(xx, yy);
	    symbolType = lastSymbolType;
	}
	public GroundElm(int xa, int ya, int xb, int yb, int f,
			 StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    try {
		symbolType = Integer.parseInt(st.nextToken());
	    } catch (Exception e) {}
	}
	String dump() {
	    return super.dump() + " " + symbolType;
	}
	int getDumpType() { return 'g'; }
	int getPostCount() { return 1; }
	void draw(Graphics g) {
	    setVoltageColor(g, 0);
	    drawThickLine(g, point1, point2);
	    if (symbolType == 0) {
		int i;
		for (i = 0; i != 3; i++) {
		    int a = 10-i*4;
		    int b = i*5; // -10;
		    interpPoint2(point1, point2, ps1, ps2, 1+b/dn, a);
		    drawThickLine(g, ps1, ps2);
		}
	    } else if (symbolType == 1) {
		interpPoint2(point1, point2, ps1, ps2, 1, 10);
		drawThickLine(g, ps1, ps2);
		int i;
		for (i = 0; i <= 2; i++) {
		    Point p = interpPoint(ps1, ps2, i/2.);  
		    drawThickLine(g, p.x, p.y, p.x-5, p.y+8*dy/(int)dn);
		}
	    } else {
		interpPoint2(point1, point2, ps1, ps2, 1, 10);
		drawThickLine(g, ps1, ps2);
		drawThickLine(g, ps1.x, ps1.y, point2.x, point2.y+10*dy/(int)dn);
		drawThickLine(g, ps2.x, ps2.y, point2.x, point2.y+10*dy/(int)dn);
	    }
	    interpPoint(point1, point2, ps2, 1+11./dn);
	    doDots(g);
	    setBbox(point1, ps2, 11);
	    drawPosts(g);
	}
	void setCurrent(int x, double c) { current = -c; }
	void stamp() {
	    sim.stampVoltageSource(0, nodes[0], voltSource, 0);
	}
	double getVoltageDiff() { return 0; }
	int getVoltageSourceCount() { return 1; }
	void getInfo(String arr[]) {
	    arr[0] = "ground";
	    arr[1] = "I = " + getCurrentText(getCurrent());
	}
	boolean hasGroundConnection(int n1) { return true; }
	int getShortcut() { return 'g'; }
	
	public EditInfo getEditInfo(int n) {
	    if (n == 0) {
		EditInfo ei =  new EditInfo("Symbol", 0);
		ei.choice = new Choice();
		ei.choice.add("Earth");
		ei.choice.add("Chassis");
		ei.choice.add("Signal");
		ei.choice.select(symbolType);
		return ei;
	    }
	    return null;
	}

	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0)
		lastSymbolType = symbolType = ei.choice.getSelectedIndex();
	}
	
	@Override double getCurrentIntoNode(int n) { return -current; }
    }
