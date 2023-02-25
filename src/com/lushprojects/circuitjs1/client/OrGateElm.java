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

    class OrGateElm extends GateElm {
	public OrGateElm(int xx, int yy) { super(xx, yy); }
	public OrGateElm(int xa, int ya, int xb, int yb, int f,
			  StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	}
	String getGateName() { return "OR gate"; }
	
	void drawGatePolygon(Graphics g) {
	    g.setLineWidth(3.0);
            g.context.beginPath();
            g.context.moveTo(gatePoly.xpoints[0], gatePoly.ypoints[0]);
            g.context.lineTo(gatePoly.xpoints[1], gatePoly.ypoints[1]);
            g.context.bezierCurveTo(
        	    gatePoly.xpoints[2], gatePoly.ypoints[2],
        	    gatePoly.xpoints[2], gatePoly.ypoints[2],
        	    gatePoly.xpoints[3], gatePoly.ypoints[3]);
            g.context.bezierCurveTo(
        	    gatePoly.xpoints[4], gatePoly.ypoints[4],
        	    gatePoly.xpoints[4], gatePoly.ypoints[4],
        	    gatePoly.xpoints[5], gatePoly.ypoints[5]);
            g.context.lineTo(gatePoly.xpoints[6], gatePoly.ypoints[6]);
            g.context.bezierCurveTo(
        	    gatePoly.xpoints[7], gatePoly.ypoints[7],
        	    gatePoly.xpoints[7], gatePoly.ypoints[7],
        	    gatePoly.xpoints[0], gatePoly.ypoints[0]);
            g.context.closePath();
            
            if (this instanceof XorGateElm) {
                g.context.moveTo(gatePoly.xpoints[8], gatePoly.ypoints[8]);
                g.context.bezierCurveTo(
            	    gatePoly.xpoints[10], gatePoly.ypoints[10],
            	    gatePoly.xpoints[10], gatePoly.ypoints[10],
            	    gatePoly.xpoints[9], gatePoly.ypoints[9]);
            }

            g.context.stroke();
	    g.setLineWidth(1.0);
	}
	
	void setPoints() {
	    super.setPoints();

	    if (useEuroGates()) {
		createEuroGatePolygon();
		linePoints = null;
	    } else {
		// 0 - top left, 1 - start of top curve, 2 - control point for top curve
		// 3 - right, 4 - control point for bottom curve, 5 - start of bottom curve, 6 - bottom right, 7 - control point for left curve
//		if (this instanceof XorGateElm)
//		    linePoints = new Point[5];
		
		Point triPoints[] = newPointArray(11);
		interpPoint2(lead1, lead2, triPoints[0], triPoints[6], -.05, hs2);
		interpPoint2(lead1, lead2, triPoints[1], triPoints[5], .3, hs2);
		triPoints[3] = lead2;
		interpPoint2(lead1, lead2, triPoints[2], triPoints[4], .7, hs2*.81);
		interpPoint(lead1, lead2, triPoints[7], .08); // was .15
		
		if (this instanceof XorGateElm) {
		    double ww2 = (ww == 0) ? dn*2 : ww*2;
		    interpPoint2(lead1, lead2, triPoints[8], triPoints[9], -.05-5/ww2, hs2); 
		    interpPoint(lead1, lead2, triPoints[10], .08-5/ww2);
		}

		gatePoly = createPolygon(triPoints);
	    }
	    if (isInverting()) {
		pcircle = interpPoint(point1, point2, .5+(ww+4)/dn);
		lead2 = interpPoint(point1, point2, .5+(ww+8)/dn);
	    }
	}

	String getGateText() { return "\u22651"; }
	
	boolean calcFunction() {
	    int i;
	    boolean f = false;
	    for (i = 0; i != inputCount; i++)
		f |= getInput(i);
	    return f;
	}
	int getDumpType() { return 152; }
	int getShortcut() { return '3'; }
    }
