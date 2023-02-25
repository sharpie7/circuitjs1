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

import com.google.gwt.canvas.dom.client.Context2d;

    class AndGateElm extends GateElm {
	public AndGateElm(int xx, int yy) { super(xx, yy); }
	public AndGateElm(int xa, int ya, int xb, int yb, int f,
			  StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	}
	
	String getGateText() { return "&"; }
	
	public final native void ellipse(Context2d g, double x, double y, double rx, double ry, double ro, double sa, double ea, boolean ccw) /*-{
	    g.ellipse(x, y, rx, ry, ro, sa, ea, ccw);
	}-*/;

	void drawGatePolygon(Graphics g) {
	    g.setLineWidth(3.0);
	    g.context.beginPath();
	    g.context.moveTo(gatePoly.xpoints[0], gatePoly.ypoints[0]);
	    double ang1 = -Math.PI/2 * sign(dx);
	    double ang2 =  Math.PI/2 * sign(dx);
	    boolean ccw = false;
	    double rx = ww;
	    double ry = hs2;
	    if (dx == 0) {
		ang1 = (dy > 0) ? 0 : Math.PI;
		ang2 = (dy > 0) ? Math.PI : 0;
		rx = hs2;
		ry = ww;
	    }
	    ellipse(g.context, gatePoly.xpoints[2], gatePoly.ypoints[2], rx, ry, 0, ang1, ang2, ccw);
	    g.context.lineTo(gatePoly.xpoints[4], gatePoly.ypoints[4]);
	    g.context.closePath();
	    g.context.stroke();
	    g.setLineWidth(1.0);
	}
	
	void setPoints() {
	    super.setPoints();
	 
	    if (useEuroGates()) {
		createEuroGatePolygon();
	    } else {
		// 0=topleft, 1 = top of curve, 2 = center, 3=bottom of curve,
		// 4 = bottom left
		Point triPoints[] = newPointArray(5);
		interpPoint2(lead1, lead2, triPoints[0], triPoints[4], 0, hs2);
		interpPoint2(lead1, lead2, triPoints[1], triPoints[3], .5, hs2);
		interpPoint(lead1, lead2, triPoints[2], .5);
		gatePoly = createPolygon(triPoints);
	    }
	    if (isInverting()) {
		pcircle = interpPoint(point1, point2, .5+(ww+4)/dn);
		lead2 = interpPoint(point1, point2, .5+(ww+8)/dn);
	    }
	}
	String getGateName() { return "AND gate"; }
	boolean calcFunction() {
	    int i;
	    boolean f = true;
	    for (i = 0; i != inputCount; i++)
		f &= getInput(i);
	    return f;
	}
	int getDumpType() { return 150; }
	int getShortcut() { return '2'; }
    }
