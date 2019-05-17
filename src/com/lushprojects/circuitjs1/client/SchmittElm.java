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

// contributed by Edward Calver

class SchmittElm extends InvertingSchmittElm{
	public SchmittElm(int xx, int yy) {
	super(xx,yy);
	}
	public SchmittElm(int xa, int ya, int xb, int yb, int f,
			      StringTokenizer st) {
	super(xa,ya,xb,yb,f,st);
	}

	int getDumpType() { return 182; }
	void doStep() {
	    double v0 = volts[1];
	    double out;
		if(state)
		{//Output is high
			if(volts[0]>upperTrigger)//Input voltage high enough to set output high
			{
			state=false;
			out=logicOnLevel;
			}
			else
			{
			out=logicOffLevel;
			}
		}
		else
		{//Output is low
			if(volts[0]<lowerTrigger)//Input voltage low enough to set output low
			{
			state=true;
			out=logicOffLevel;
			}
			else
			{
			out=logicOnLevel;
			}
		}
	    
	    double maxStep = slewRate * sim.timeStep * 1e9;
	    out = Math.max(Math.min(v0+maxStep, out), v0-maxStep);
	    sim.updateVoltageSource(0, nodes[1], voltSource, out);
	}

	void draw(Graphics g) {
	    drawPosts(g);
	    draw2Leads(g);
	    g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	    drawThickPolygon(g, gatePoly);
	    g.setLineWidth(2);
	    drawPolygon(g, symbolPoly);
	    g.setLineWidth(1);;
	    curcount = updateDotCount(current, curcount);
	    drawDots(g, lead2, point2, curcount);
	}
	void setPoints() {
	    super.setPoints();
	    int hs = 16;
	    int ww = 16;
	    if (ww > dn/2)
		ww = (int) (dn/2);
	    lead1 = interpPoint(point1, point2, .5-ww/dn);
	    lead2 = interpPoint(point1, point2, .5+(ww-3)/dn);
	    Point triPoints[] = newPointArray(3); 
	    interpPoint2(lead1, lead2, triPoints[0], triPoints[1], 0, hs);
	    triPoints[2] = interpPoint(point1, point2, .5+(ww-5)/dn);
	    gatePoly = createPolygon(triPoints);
	}
        void getInfo(String arr[]) {
            arr[0] = "Schmitt Trigger~"; // ~ is for localization
        }

	@Override double getCurrentIntoNode(int n) {
	    if (n == 1)
		return current;
	    return 0;
	}

    }
