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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;

class UnijunctionElm extends CompositeElm {
	// node 0 = base
	// node 1 = collector
	// node 2 = emitter
	
	public UnijunctionElm(int xx, int yy) {
	    super(xx, yy);
	    setup();
	}
	public UnijunctionElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    setup();
	}
	
	private static String ujtModelString = "DiodeElm 1 4\rVoltageElm 5 4\rCCVSElm 4 5 6 0\rResistorElm 0 6\rVCCSElm 5 7 5 7 6 7 5\rCapacitorElm 5 7\rResistorElm 7 2\rResistorElm 3 5";
	private static int ujtExternalNodes[] = { 1, 2, 3 };
	private static String ujtModelDump = "2 x2n2646-emitter/0 0 0 0/2 2 1000*(a-b)/0 1000000/0 5 0.00028*(a-b)\\p0.00575*(c-d)*e/2 3.5e-11 0 0/0 38.15/0 2518";
	void setup() {
	    noDiagonal = true;
	    flags |= FLAG_ESCAPE;
	    StringTokenizer st = new StringTokenizer(ujtModelDump, "/");
	    loadComposite(st, ujtModelString, ujtExternalNodes);
	}
	
	public void reset() {
	    volts[0] = volts[1] = volts[2] = 0;
	    lastvbc = lastvbe = curcount_c = curcount_e = curcount_b = 0;
	}
	int getDumpType() { return 't'; }
	
	double ic, ie, ib, curcount_c, curcount_e, curcount_b;
	Point src[], drn[];
        Polygon gatePoly;
        Point gate[];
        double curcountg, curcounts, curcountd;
        double ids;
	
		Polygon rectPoly, arrowPoly;
	        double gateCurrent;
	        final int hs = 16;
	        
	        void draw(Graphics g) {
	            setBbox(point1, point2, hs);
	            setVoltageColor(g, volts[1]);
	            drawThickLine(g, src[0], src[1]);
	            drawThickLine(g, src[1], src[2]);
	            setVoltageColor(g, volts[2]);
	            drawThickLine(g, drn[0], drn[1]);
	            drawThickLine(g, drn[1], drn[2]);
	            setVoltageColor(g, volts[0]);
	            drawThickLine(g, gate[0], gate[1]);
	            drawThickLine(g, gate[1], gate[2]);
	            g.fillPolygon(arrowPoly);
	            setPowerColor(g, true);
	            g.fillPolygon(gatePoly);
	            curcountd = updateDotCount(-ids,            curcountd);
	            curcountg = updateDotCount(gateCurrent,     curcountg);
	            curcounts = updateDotCount(-gateCurrent-ids, curcounts);
	            if (curcountd != 0 || curcounts != 0) {
	                drawDots(g, src[0], src[1], curcounts);
	                drawDots(g, src[1], src[2], curcounts+8);
	                drawDots(g, drn[0], drn[1], -curcountd);
	                drawDots(g, drn[1], drn[2], -(curcountd+8));
	                drawDots(g, gate[0], gate[1], curcountg);
	                drawDots(g, gate[1], gate[2], curcountg);
	            }
	            drawPosts(g);
	        }
	        
	        void setPoints() {
	            super.setPoints();

	            // find the coordinates of the various points we need to draw
	            // the JFET.
	            int hs2 = hs*dsign;
	            src = newPointArray(3);
	            drn = newPointArray(3);
	            gate = newPointArray(3);
	            interpPoint2(point1, point2, src[0], drn[0], 1, -hs2);
	            interpPoint2(point1, point2, src[1], drn[1], 1, -hs2/2);
	            interpPoint2(point1, point2, src[2], drn[2], 1-10/dn, -hs2/2);

	            interpPoint(point1, point2, gate[0], 0, hs2);
	            interpPoint(point1, point2, gate[1], 1-28/dn, hs2);
	            gate[2] = interpPoint(point1, point2, 1-14/dn);

	            Point ra[] = newPointArray(4);
	            interpPoint2(point1, point2, ra[0], ra[1], 1-13/dn, hs);
	            interpPoint2(point1, point2, ra[2], ra[3], 1-10/dn, hs);
	            gatePoly = createPolygon(ra[0], ra[1], ra[3], ra[2]);
	            arrowPoly = calcArrow(gate[1], gate[2], 8, 3);
	        }
	        

	        Point getPost(int n) {
	            return (n == 0) ? gate[0] : (n == 1) ? drn[0] : src[0];
	        }
	
	int getPostCount() { return 3; }
	
	static final double leakage = 1e-13; // 1e-6;
	// Electron thermal voltage at SPICE's default temperature of 27 C (300.15 K):
	static final double vt = 0.025865;
	double vcrit;
	double lastvbc, lastvbe;
	double limitStep(double vnew, double vold) {
	    double arg;
	    double oo = vnew;
	    
	    if (vnew > vcrit && Math.abs(vnew - vold) > (vt + vt)) {
		if(vold > 0) {
		    arg = 1 + (vnew - vold) / vt;
		    if(arg > 0) {
			vnew = vold + vt * Math.log(arg);
		    } else {
			vnew = vcrit;
		    }
		} else {
		    vnew = vt *Math.log(vnew/vt);
		}
		sim.converged = false;
		//System.out.println(vnew + " " + oo + " " + vold);
	    }
	    return(vnew);
	}
	
	void getInfo(String arr[]) {
	    arr[0] = "unijunction transistor";
	    double vbc = volts[0]-volts[1];
	    double vbe = volts[0]-volts[2];
	    double vce = volts[1]-volts[2];
	    arr[1] = "Ic = " + getCurrentText(ic);
	    arr[2] = "Ib = " + getCurrentText(ib);
	    arr[3] = "Vbe = " + getVoltageText(vbe);
	    arr[4] = "Vbc = " + getVoltageText(vbc);
	    arr[5] = "Vce = " + getVoltageText(vce);
	    arr[6] = "P = " + getUnitText(getPower(), "W");
	}
	
	double getCurrentIntoNode(int n) {
	    if (n==0)
		return -ib;
	    if (n==1)
		return -ic;
	    return -ie;
	}
    }
