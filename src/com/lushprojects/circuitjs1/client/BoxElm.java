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

//import java.awt.*;
//import java.util.StringTokenizer;
import java.util.Vector;

class BoxElm extends GraphicElm {

    public BoxElm(int xx, int yy) {
	super(xx, yy);
	x2 = xx + 16;
	y2 = yy + 16;
	setBbox(x, y, x2, y2);
    }

    public BoxElm(int xa, int ya, int xb, int yb, int f,
		   StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	x2 = xb;
	y2 = yb;
/*	if ( st.hasMoreTokens() )
		x = new Integer(st.nextToken()).intValue();
	if ( st.hasMoreTokens() )
		y = new Integer(st.nextToken()).intValue();
	if ( st.hasMoreTokens() )
		x2 = new Integer(st.nextToken()).intValue();
	if ( st.hasMoreTokens() )
		y2 = new Integer(st.nextToken()).intValue();*/
	setBbox(x, y, x2, y2);
    }

    String dump() {
	return super.dump();
    }

    int getDumpType() { return 'b'; }

    void drag(int xx, int yy) {
	x = xx;
	y = yy;
    }

    void draw(Graphics g) {
	//g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	g.setColor(needsHighlight() ? selectColor : Color.GRAY);
	setBbox(x, y, x2, y2);
	if ( x < x2 && y < y2 )
		g.fillRect(x,y, x2-x, y2-y);
	else if ( x > x2 && y < y2 )
		g.fillRect(x2,y, x-x2, y2-y);
	else if ( x < x2 && y > y2 )
		g.fillRect(x, y2, x2-x, y-y2);
	else
		g.fillRect(x2, y2, x-x2, y-y2);
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

