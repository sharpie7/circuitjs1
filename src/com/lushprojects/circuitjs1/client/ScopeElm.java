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

import java.util.Vector;

class ScopeElm extends CircuitElm {
    
    Scope elmScope;

    public ScopeElm(int xx, int yy) {
	super(xx, yy);
	noDiagonal=false;
	x2=x+128;
	y2=y+64;
	elmScope = new Scope(sim);
	setPoints();
    }
    
    public void setScopeElm(CircuitElm e) {
	elmScope.setElm(e);
	elmScope.resetGraph();
    }
    
    public ScopeElm(int xa, int ya, int xb, int yb, int f,
		   StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	noDiagonal=false;
	String sStr = st.nextToken();
	StringTokenizer sst = new StringTokenizer(sStr, "_");
	elmScope = new Scope(sim);
	elmScope.undump(sst);
	setPoints();
	elmScope.resetGraph();
    }
    
    public void setScopeRect() {
	int i1 = sim.transformX(min(x,x2));
	int i2 = sim.transformX(max(x,x2));
	int j1 = sim.transformY(min(y,y2));
	int j2 = sim.transformY(max(y,y2));
	Rectangle r = new Rectangle(i1,j1,i2-i1, j2-j1);
	if (!r.equals(elmScope.rect))
	    elmScope.setRect(r);
    }
    
    public void setPoints() {
	super.setPoints();
	setScopeRect();
    }
    
    public void setElmScope( Scope s) {
	elmScope=s;
    }
    
    
    public void stepScope() {
	elmScope.timeStep();
    }
    
    public void reset() {
	super.reset();
	elmScope.resetGraph(true);
    }
    
    public void clearElmScope() {
	elmScope = null;
    }
    
    boolean canViewInScope() { return false; }
    
    int getDumpType() { return 403; }

    public String dump() {
	String dumpStr=super.dump();
	String sStr = elmScope.dump().replace(' ', '_');
	sStr = sStr.replaceFirst("o_", ""); // remove unused prefix for embedded Scope
	return dumpStr + " " + sStr;
    }
    
    void draw(Graphics g) {
	g.setColor(needsHighlight() ? selectColor : whiteColor);
	g.context.save();
	g.context.setTransform(1, 0, 0, 1, 0, 0);
	setScopeRect();
	elmScope.draw(g);
	g.context.restore();
	setBbox(point1, point2, 0);
	drawPosts(g);

    }
    
    int getPostCount() { return 0; }
    
    
}
