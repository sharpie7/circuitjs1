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

    class MosfetElm extends CircuitElm {
	int pnp;
	int FLAG_PNP = 1;
	int FLAG_SHOWVT = 2;
	int FLAG_DIGITAL = 4;
	int FLAG_FLIP = 8;
	double vt;
	// beta = 1/(RdsON*(Vgs-Vt))
	double beta;
	MosfetElm(int xx, int yy, boolean pnpflag) {
	    super(xx, yy);
	    pnp = (pnpflag) ? -1 : 1;
	    flags = (pnpflag) ? FLAG_PNP : 0;
	    noDiagonal = true;
	    beta = getDefaultBeta();
	    vt = getDefaultThreshold();
	}
	public MosfetElm(int xa, int ya, int xb, int yb, int f,
			 StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    pnp = ((f & FLAG_PNP) != 0) ? -1 : 1;
	    noDiagonal = true;
	    vt = getDefaultThreshold();
	    beta = getDefaultBeta();
	    try {
		vt = new Double(st.nextToken()).doubleValue();
		beta = new Double(st.nextToken()).doubleValue();
	    } catch (Exception e) {}
	}
	double getDefaultThreshold() { return 1.5; }
	double getDefaultBeta() { return .02; }
	boolean nonLinear() { return true; }
	boolean drawDigital() { return (flags & FLAG_DIGITAL) != 0; }
	void reset() {
	    lastv1 = lastv2 = volts[0] = volts[1] = volts[2] = curcount = 0;
	}
	String dump() {
	    return super.dump() + " " + vt + " " + beta;
	}
	int getDumpType() { return 'f'; }
	final int hs = 16;
	
	void draw(Graphics g) {
		setBbox(point1, point2, hs);
		setVoltageColor(g, volts[1]);
		drawThickLine(g, src[0], src[1]);
		setVoltageColor(g, volts[2]);
		drawThickLine(g, drn[0], drn[1]);
		int segments = 6;
		int i;
		setPowerColor(g, true);
		double segf = 1./segments;
		for (i = 0; i != segments; i++) {
			double v = volts[1]+(volts[2]-volts[1])*i/segments;
			setVoltageColor(g, v);
			interpPoint(src[1], drn[1], ps1, i*segf);
			interpPoint(src[1], drn[1], ps2, (i+1)*segf);
			drawThickLine(g, ps1, ps2);
		}
		setVoltageColor(g, volts[1]);
		drawThickLine(g, src[1], src[2]);
		setVoltageColor(g, volts[2]);
		drawThickLine(g, drn[1], drn[2]);
		if (!drawDigital()) {
			setVoltageColor(g, pnp == 1 ? volts[1] : volts[2]);
			g.fillPolygon(arrowPoly);
		}
		if (sim.powerCheckItem.getState())
			g.setColor(Color.gray);
		setVoltageColor(g, volts[0]);
		drawThickLine(g, point1, gate[1]);
		drawThickLine(g, gate[0], gate[2]);
		if (drawDigital() && pnp == -1)
			drawThickCircle(g, pcircle.x, pcircle.y, pcircler);
		if ((flags & FLAG_SHOWVT) != 0) {
			String s = "" + (vt*pnp);
			g.setColor(whiteColor);
			g.setFont(unitsFont);
			drawCenteredText(g, s, x2+2, y2, false);
		}
		if ((needsHighlight() || sim.dragElm == this) && dy == 0) {
			g.setColor(Color.white);
			g.setFont(unitsFont);
			int ds = sign(dx);
			g.drawString("G", gate[1].x-10*ds, gate[1].y-5);
			g.drawString(pnp == -1 ? "D" : "S", src[0].x-3+9*ds, src[0].y+4); // x+6 if ds=1, -12 if -1
			g.drawString(pnp == -1 ? "S" : "D", drn[0].x-3+9*ds, drn[0].y+4);
		}	    
		curcount = updateDotCount(-ids, curcount);
		drawDots(g, src[0], src[1], curcount);
		drawDots(g, src[1], drn[1], curcount);
		drawDots(g, drn[1], drn[0], curcount);
		drawPosts(g);
	}
	Point getPost(int n) {
	    return (n == 0) ? point1 : (n == 1) ? src[0] : drn[0];
	}
	double getCurrent() { return ids; }
	double getPower() { return ids*(volts[2]-volts[1]); }
	int getPostCount() { return 3; }

	int pcircler;
	Point src[], drn[], gate[], pcircle;
	Polygon arrowPoly;
	
	void setPoints() {
	    super.setPoints();

	    // find the coordinates of the various points we need to draw
	    // the MOSFET.
	    int hs2 = hs*dsign;
	    if ((flags & FLAG_FLIP) != 0)
	    	hs2 = -hs2;
	    src = newPointArray(3);
	    drn = newPointArray(3);
	    interpPoint2(point1, point2, src[0], drn[0], 1, -hs2);
	    interpPoint2(point1, point2, src[1], drn[1], 1-22/dn, -hs2);
	    interpPoint2(point1, point2, src[2], drn[2], 1-22/dn, -hs2*4/3);

	    gate = newPointArray(3);
	    interpPoint2(point1, point2, gate[0], gate[2], 1-28/dn, hs2/2); // was 1-20/dn
	    interpPoint(gate[0], gate[2], gate[1], .5);

	    if (!drawDigital()) {
		if (pnp == 1)
		    arrowPoly = calcArrow(src[1], src[0], 10, 4);
		else
		    arrowPoly = calcArrow(drn[0], drn[1], 12, 5);
	    } else if (pnp == -1) {
		interpPoint(point1, point2, gate[1], 1-36/dn);
		int dist = (dsign < 0) ? 32 : 31;
		pcircle = interpPoint(point1, point2, 1-dist/dn);
		pcircler = 3;
	    }
	}

	double lastv1, lastv2;
	double ids;
	int mode = 0;
	double gm = 0;
	
	void stamp() {
	    sim.stampNonLinear(nodes[1]);
	    sim.stampNonLinear(nodes[2]);
	}
	void doStep() {
	    double vs[] = new double[3];
	    vs[0] = volts[0];
	    vs[1] = volts[1];
	    vs[2] = volts[2];
	    if (vs[1] > lastv1 + .5)
		vs[1] = lastv1 + .5;
	    if (vs[1] < lastv1 - .5)
		vs[1] = lastv1 - .5;
	    if (vs[2] > lastv2 + .5)
		vs[2] = lastv2 + .5;
	    if (vs[2] < lastv2 - .5)
		vs[2] = lastv2 - .5;
	    int source = 1;
	    int drain = 2;
	    
	    // if source voltage > drain (for NPN), swap source and drain
	    // (opposite for PNP)
	    if (pnp*vs[1] > pnp*vs[2]) {
	    	source = 2;
	    	drain = 1;
	    }
	    int gate = 0;
	    double vgs = vs[gate ]-vs[source];
	    double vds = vs[drain]-vs[source];
	    if (Math.abs(lastv1-vs[1]) > .01 ||
		Math.abs(lastv2-vs[2]) > .01)
		sim.converged = false;
	    lastv1 = vs[1];
	    lastv2 = vs[2];
	    double realvgs = vgs;
	    double realvds = vds;
	    vgs *= pnp;
	    vds *= pnp;
	    ids = 0;
	    gm = 0;
	    double Gds = 0;
	    if (vgs > .5 && this instanceof JfetElm) {
		sim.stop("JFET is reverse biased!", this);
		return;
	    }
	    if (vgs < vt) {
		// should be all zero, but that causes a singular matrix,
		// so instead we treat it as a large resistor
		Gds = 1e-8;
		ids = vds*Gds;
		mode = 0;
	    } else if (vds < vgs-vt) {
		// linear
		ids = beta*((vgs-vt)*vds - vds*vds*.5);
		gm  = beta*vds;
		Gds = beta*(vgs-vds-vt);
		mode = 1;
	    } else {
		// saturation; Gds = 0
		gm  = beta*(vgs-vt);
		// use very small Gds to avoid nonconvergence
		Gds = 1e-8;
		ids = .5*beta*(vgs-vt)*(vgs-vt) + (vds-(vgs-vt))*Gds;
		mode = 2;
	    }
	    double rs = -pnp*ids + Gds*realvds + gm*realvgs;
	    //System.out.println("M " + vds + " " + vgs + " " + ids + " " + gm + " "+ Gds + " " + volts[0] + " " + volts[1] + " " + volts[2] + " " + source + " " + rs + " " + this);
	    sim.stampMatrix(nodes[drain],  nodes[drain],  Gds);
	    sim.stampMatrix(nodes[drain],  nodes[source], -Gds-gm); 
	    sim.stampMatrix(nodes[drain],  nodes[gate],   gm);
	    
	    sim.stampMatrix(nodes[source], nodes[drain],  -Gds);
	    sim.stampMatrix(nodes[source], nodes[source], Gds+gm); 
	    sim.stampMatrix(nodes[source], nodes[gate],  -gm);
	    
	    sim.stampRightSide(nodes[drain],  rs);
	    sim.stampRightSide(nodes[source], -rs);
	    if (source == 2 && pnp == 1 ||
		source == 1 && pnp == -1)
		ids = -ids;
	}
	void getFetInfo(String arr[], String n) {
	    arr[0] = ((pnp == -1) ? "p-" : "n-") + n;
	    arr[0] += " (Vt = " + getVoltageText(pnp*vt) + ")";
	    arr[1] = ((pnp == 1) ? "Ids = " : "Isd = ") + getCurrentText(ids);
	    arr[2] = "Vgs = " + getVoltageText(volts[0]-volts[pnp == -1 ? 2 : 1]);
	    arr[3] = ((pnp == 1) ? "Vds = " : "Vsd = ") + getVoltageText(volts[2]-volts[1]);
	    arr[4] = (mode == 0) ? "off" :
		(mode == 1) ? "linear" : "saturation";
	    arr[5] = "gm = " + getUnitText(gm, "A/V");
	}
	void getInfo(String arr[]) {
	    getFetInfo(arr, "MOSFET");
	}
	boolean canViewInScope() { return true; }
	double getVoltageDiff() { return volts[2] - volts[1]; }
	boolean getConnection(int n1, int n2) {
	    return !(n1 == 0 || n2 == 0);
	}
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Threshold Voltage", pnp*vt, .01, 5);
		if (n == 1)
			return new EditInfo("Beta", beta, .01, 5);
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Digital Symbol", drawDigital());
			return ei;
		}
		if (n == 3) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Swap D/S", (flags & FLAG_FLIP) != 0);
			return ei;
		}

		return null;
	}
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			vt = pnp*ei.value;
		if (n == 1)
			beta = ei.value;	
		if (n == 2) {
			flags = (ei.checkbox.getState()) ? (flags | FLAG_DIGITAL) :
				(flags & ~FLAG_DIGITAL);
			setPoints();
		}
		if (n == 3) {
			flags = (ei.checkbox.getState()) ? (flags | FLAG_FLIP) :
				(flags & ~FLAG_FLIP);
			setPoints();
		}
	}
    }
