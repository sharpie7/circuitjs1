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

class LineElm extends GraphicElm {

    public LineElm(int xx, int yy) {
	super(xx, yy);
	x2 = xx;
	y2 = yy;
	setBbox(x, y, x2, y2);
    }

    public LineElm(int xa, int ya, int xb, int yb, int f,
		   StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	x2 = xb;
	y2 = yb;
	setBbox(x, y, x2, y2);
    }

    String dump() {
	return super.dump();
    }

    int getDumpType() { return 423; }

    void drag(int xx, int yy) {
	x2 = xx;
	y2 = yy;
    }

    boolean creationFailed() {
	return Math.hypot(x-x2, y-y2) < 16;
    }
    
    void draw(Graphics g) {
	//g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	g.setColor(needsHighlight() ? selectColor : Color.GRAY);
	setBbox(x, y, x2, y2);
	g.drawLine(x, y, x2, y2);
    }

    public EditInfo getEditInfo(int n) {
	return null;
    }

    public void setEditValue(int n, EditInfo ei) {
    }

    void getInfo(String arr[]) {
    }

    @Override
    int getShortcut() { return 0; }
}

