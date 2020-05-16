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

import com.google.gwt.user.client.Window;

class CustomTransformerElm extends CircuitElm {
	double coilCurrents[], coilInductances[], coilCurCounts[], coilCurSourceValues[], coilPolarities[];
	double nodeCurrents[], nodeCurCounts[];
	
	// node number n of first node of each coil (second node = n+1)
	int coilNodes[];
	
	int coilCount, nodeCount;
	
	// number of primary coils
	int primaryCoils;
	
	Point nodePoints[], nodeTaps[], ptCore[];
	String description;
	double inductance, couplingCoef;
	boolean needDots;
	
	Point dots[];
	int width;
	
	public CustomTransformerElm(int xx, int yy) {
	    super(xx, yy);
	    inductance = 4;
	    width = 32;
	    noDiagonal = true;
	    couplingCoef = .999;
	    description = "1,1:1";
	    parseDescription(description);
	}
	public CustomTransformerElm(int xa, int ya, int xb, int yb, int f,
			      StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    width = 32; // max(32, abs(yb-ya));
	    inductance = new Double(st.nextToken()).doubleValue();
	    couplingCoef = new Double(st.nextToken()).doubleValue();
	    String str = st.nextToken();
	    description = CustomLogicModel.unescape(str);
	    coilCount = new Integer(st.nextToken()).intValue();
	    int i;
	    coilCurrents = new double[coilCount];
	    for (i = 0; i != coilCount; i++)
		coilCurrents[i] = new Double(st.nextToken()).doubleValue();
	    noDiagonal = true;
	    parseDescription(description);
	}
	void drag(int xx, int yy) {
	    xx = sim.snapGrid(xx);
	    yy = sim.snapGrid(yy);
//	    width = max(32, abs(yy-y));
	    if (xx == x)
	        yy = y;
	    x2 = xx; y2 = yy;
	    setPoints();
	}
	int getDumpType() { return 406; }
	String dump() {
	    String s = super.dump() + " " + inductance + " " + couplingCoef + " " + CustomLogicModel.escape(description) + " " + coilCount + " ";
	    int i;
	    for (i = 0; i != coilCount; i++) {
		s += coilCurrents[i] + " ";
	    }
	    return s;
	}
	
	void parseDescription() {
	    parseDescription(description);
	}
	
	boolean parseDescription(String desc) {
	    // a number indicates a coil (number = turns ratio to base inductance coil)
	    // (negative number = reverse polarity)
	    // : separates primary and secondary
	    // , separates two coils
	    // + separates two connected coils (tapped)
	    StringTokenizer st = new StringTokenizer(desc, ",:+", true);
	    
	    // count coils/nodes
	    coilCount = nodeCount = 0;
	    while (st.hasMoreTokens()) {
		String s = st.nextToken();
		if (s == "+")
		    nodeCount--;
		if (s == "," || s == "+" || s == ":")
		    continue;
		nodeCount += 2;
		coilCount++;
	    }
	    
	    coilNodes = new int[coilCount];
	    coilInductances = new double[coilCount];
	    // save coil currents if possible (needed for undumping)
	    if (coilCurrents == null || coilCurrents.length != coilCount)
		coilCurrents = new double[coilCount];
	    coilCurCounts = new double[coilCount];
	    coilCurSourceValues = new double[coilCount];
	    coilPolarities = new double[coilCount];
	    nodePoints = newPointArray(nodeCount);
	    nodeTaps = newPointArray(nodeCount);
	    nodeCurrents = new double[nodeCount];
	    nodeCurCounts = new double[nodeCount];
	    
	    // start over
	    st = new StringTokenizer(desc, ",:+", true);
	    int nodeNum = 0;
	    int coilNum = 0;
	    primaryCoils = 0;
	    boolean secondary = false;
	    needDots = false;
	    while (true) {
		String tok = st.nextToken();
		double n = 0;
		try {
		    n = Double.parseDouble(tok);
		} catch (Exception e) { return false; }
		if (n == 0)
		    return false;
		// create new coil
		coilNodes[coilNum] = nodeNum;
		coilInductances[coilNum] = n*n*inductance;
		coilPolarities[coilNum] = 1;
		if (n < 0) {
		    coilPolarities[coilNum] = -1;
		    needDots = true;
		}
		nodeNum += 2;
		coilNum++;
		if (!secondary)
		    primaryCoils = coilNum;
		if (!st.hasMoreTokens())
		    break;
		tok = st.nextToken();
		if (tok == ",")
		    continue;
		if (tok == "+") {
		    nodeNum--;
		    continue;
		}
		if (tok == ":") {
		    // switch to secondary
		    if (secondary)
			return false;
		    secondary = true;
		    continue;
		}
		return false;
	    }
	    allocNodes();
	    setPoints();
	    return true;
	}
	
	boolean isTrapezoidal() { return (flags & Inductor.FLAG_BACK_EULER) == 0; }
	void draw(Graphics g) {
	    int i;
	    
	    // draw taps
	    for (i = 0; i != getPostCount(); i++) {
		setVoltageColor(g, volts[i]);
		drawThickLine(g, nodePoints[i], nodeTaps[i]);
	    }
	    
	    // draw coils
	    for (i = 0; i != coilCount; i++) {
		int n = coilNodes[i];
		setVoltageColor(g, volts[n]);
		setPowerColor(g, coilCurrents[i]*(volts[n]-volts[n+1]));
		drawCoil(g, (i >= primaryCoils ? -6 : 6), nodeTaps[n], nodeTaps[n+1], volts[n], volts[n+1]);
		if (dots != null) {
		    g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		    g.fillOval(dots[i].x-2, dots[i].y-2, 5, 5);
		}
	    }
	    g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	    
	    // draw core
	    for (i = 0; i != 2; i++) {
		drawThickLine(g, ptCore[i], ptCore[i+2]);
	    }
	    
	    // draw coil currents
	    for (i = 0; i != coilCount; i++) {
		coilCurCounts[i] = updateDotCount(coilCurrents[i], coilCurCounts[i]);
		int ni = coilNodes[i];
		drawDots(g, nodeTaps[ni], nodeTaps[ni+1], coilCurCounts[i]);
	    }
	    
	    // draw tap currents
	    for (i = 0; i != nodeCount; i++) {
		nodeCurCounts[i] = updateDotCount(nodeCurrents[i], nodeCurCounts[i]);
		drawDots(g, nodePoints[i], nodeTaps[i], nodeCurCounts[i]);
	    }
	    
	    drawPosts(g);
	    setBbox(nodePoints[0], nodePoints[nodeCount-1], 0);
	    adjustBbox(ptCore[0], ptCore[3]);
	}
	
	void setPoints() {
	    super.setPoints();
	    point2.y = point1.y;
	    int i;
	    int primaryNodes = (primaryCoils == coilCount) ? nodeCount : coilNodes[primaryCoils];
	    dn = Math.abs(point1.x-point2.x);
	    double ce = .5-12/dn;
	    double cd = .5-2/dn;
	    double maxWidth = 0;
	    int step;
	    for (step = 0; step != 2; step++) {
		int c = 0;
		double offset = 0;
		for (i = 0; i != nodeCount; i++) {
		    if (i == primaryNodes)
			offset = 0;
		    if (step == 1) {
			if (i == primaryNodes-1 || i == nodeCount-1)
			    offset = maxWidth;
			interpPoint(point1, point2, nodePoints[i], i < primaryNodes ? 0 : 1,     -offset);
			interpPoint(point1, point2, nodeTaps[i]  , i < primaryNodes ? ce : 1-ce, -offset);
		    }
		    maxWidth = Math.max(maxWidth, offset); 
		    int nn = c < coilCount ? coilNodes[c] : -1;
		    if (nn == i) {
			// this is first node of a coil, make room
			c++;
			offset += width;
		    } else {
			// this is last node of a coil, make small gap
			offset += 16;
		    }
		}
	    }
	    ptCore = newPointArray(4);
	    for (i = 0; i != 4; i += 2) {
		double h = (i == 2) ? -maxWidth : 0;
		interpPoint(point1, point2, ptCore[i],   cd, h);
		interpPoint(point1, point2, ptCore[i+1], 1-cd, h);
	    }
	    
	    if (needDots) {
		dots = new Point[coilCount];
		double dotp = Math.abs(7./width);
		for (i = 0; i != coilCount; i++) {
		    int n = coilNodes[i];
		    dots[i] = interpPoint(nodeTaps[n], nodeTaps[n+1], coilPolarities[i] > 0 ? dotp : 1-dotp, i < primaryCoils ? -7 : 7);
		}
	    } else
		dots = null;
	}
	Point getPost(int n) {
	    return nodePoints[n];
	}
	int getPostCount() { return nodeCount; }
	void reset() {
	    int i;
	    for (i = 0; i != coilCount; i++)
		coilCurrents[i] = coilCurSourceValues[i] = coilCurCounts[i] = 0;
	    for (i = 0; i != nodeCount; i++)
		volts[i] = nodeCurrents[i] = nodeCurCounts[i] = 0;
	}
	double xformMatrix[][];
	
	void stamp() {
	    // equations for transformer:
	    //   v1 = L1  di1/dt + M12  di2/dt + M13 di3/dt + ...
	    //   v2 = M21 di1/dt + L2 di2/dt   + M23 di3/dt + ...
	    //   v3 = ... (one row for each coil)
	    // we invert that to get:
	    //   di1/dt = a1 v1 + a2 v2 + ...
	    //   di2/dt = a3 v1 + a4 v2 + ...
	    // integrate di1/dt using trapezoidal approx and we get:
	    //   i1(t2) = i1(t1) + dt/2 (i1(t1) + i1(t2))
	    //          = i1(t1) + a1 dt/2 v1(t1) + a2 dt/2 v2(t1) + ... +
	    //                     a1 dt/2 v1(t2) + a2 dt/2 v2(t2) + ...
	    // the norton equivalent of this for i1 is:
	    //  a. current source, I = i1(t1) + a1 dt/2 v1(t1) + a2 dt/2 v2(t1) + ...
	    //  b. resistor, G = a1 dt/2
	    //  c. current source controlled by voltage v2, G = a2 dt/2
	    // and for i2:
	    //  a. current source, I = i2(t1) + a3 dt/2 v1(t1) + a4 dt/2 v2(t1) + ...
	    //  b. resistor, G = a3 dt/2
	    //  c. current source controlled by voltage v2, G = a4 dt/2
	    //
	    // For backward euler, the current source value is just i1(t1) and we use
	    // dt instead of dt/2 for the resistor and VCCS.
	    xformMatrix = new double[coilCount][coilCount];
	    int i;
	    // fill diagonal
	    for (i = 0; i != coilCount; i++)
		xformMatrix[i][i] = coilInductances[i];
	    int j;
	    // fill off-diagonal
	    for (i = 0; i != coilCount; i++)
		for (j = 0; j != i; j++)
		    xformMatrix[i][j] = xformMatrix[j][i] = couplingCoef*Math.sqrt(coilInductances[i]*coilInductances[j])*coilPolarities[i]*coilPolarities[j];

	    CirSim.invertMatrix(xformMatrix, coilCount);
	    
	    double ts = isTrapezoidal() ? sim.timeStep/2 : sim.timeStep;
	    for (i = 0; i != coilCount; i++)
		for (j = 0; j != coilCount; j++) {
		    // multiply in dt/2 (or dt for backward euler)
		    xformMatrix[i][j] *= ts;
		    int ni = coilNodes[i];
		    int nj = coilNodes[j];
		    if (i == j)
			sim.stampConductance(nodes[ni], nodes[ni+1], xformMatrix[i][i]);
		    else
			sim.stampVCCurrentSource(nodes[ni], nodes[ni+1], nodes[nj], nodes[nj+1], xformMatrix[i][j]);
		}
	    for (i = 0; i != nodeCount; i++)
		sim.stampRightSide(nodes[i]);
	}
	
	void startIteration() {
	    int i;
	    for (i = 0; i != coilCount; i++) {
		double val = coilCurrents[i];
		if (isTrapezoidal()) {
		    int j;
		    for (j = 0; j != coilCount; j++) {
			int n = coilNodes[j];
			double voltdiff = volts[n]-volts[n+1];
			val += voltdiff*xformMatrix[i][j];
		    }
		}
		coilCurSourceValues[i] = val;
	    }
	}
	
	void doStep() {
	    int i;
	    for (i = 0; i != coilCount; i++) {
		int n = coilNodes[i];
		sim.stampCurrentSource(nodes[n], nodes[n+1], coilCurSourceValues[i]);
	    }
 	}
	
	void calculateCurrent() {
	    int i;
	    for (i = 0; i != nodeCount; i++)
		nodeCurrents[i] = 0;
	    for (i = 0; i != coilCount; i++) {
		double val = coilCurSourceValues[i];
		if (xformMatrix != null) {
		    int j;
		    for (j = 0; j != coilCount; j++) {
			int n = coilNodes[j];
			double voltdiff = volts[n]-volts[n+1];
			val += voltdiff*xformMatrix[i][j];
		    }
		}
		coilCurrents[i] = val;
		int ni = coilNodes[i];
		nodeCurrents[ni] += val;
		nodeCurrents[ni+1] -= val;
	    }
	}
	
	@Override double getCurrentIntoNode(int n) {
	    return -nodeCurrents[n];
	}
	
	void getInfo(String arr[]) {
	    arr[0] = "transformer (custom)";
	    arr[1] = "L = " + getUnitText(inductance, "H");
	    int i;
	    for (i = 0; i != coilCount ; i++) {
		if (2+i*2 >= arr.length)
		    break;
		int ni = coilNodes[i];
		arr[2+i*2] = "Vd" + (i+1) + " = " + getVoltageText(volts[ni]-volts[ni+1]);
		arr[3+i*2] = "I" + (i+1) + " = " + getCurrentText(coilCurrents[i]);
	    }
	}
	
	boolean getConnection(int n1, int n2) {
	    int i;
	    for (i = 0; i != coilCount; i++)
		if (comparePair(n1, n2, coilNodes[i], coilNodes[i]+1))
		    return true;
	    return false;
	}
	
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Base Inductance (H)", inductance, .01, 5);
	    if (n == 1) {
		EditInfo ei = new EditInfo("<a href=\"customtransformer.html\" target=\"_blank\">Description</a>", 0, -1, -1);
		ei.text = description;
		ei.disallowSliders();
		return ei;
	    }
	    if (n == 2)
		return new EditInfo("Coupling Coefficient", couplingCoef, 0, 1).
		    setDimensionless();
	    if (n == 3) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Trapezoidal Approximation",
					   isTrapezoidal());
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0 && ei.value > 0) {
		inductance = ei.value;
		parseDescription();
	    }
	    if (n == 1) {
		String s = ei.textf.getText();
		if (s != description) {
		    if (!parseDescription(s)) {
			parseDescription(description);
			Window.alert("Parse error in description");
		    } else
			description = s;
		    setPoints();
		}
	    }
	    if (n == 2 && ei.value > 0 && ei.value < 1) {
		couplingCoef = ei.value;
		parseDescription();
	    }
	    if (n == 3) {
		if (ei.checkbox.getState())
		    flags &= ~Inductor.FLAG_BACK_EULER;
		else
		    flags |= Inductor.FLAG_BACK_EULER;
		parseDescription();
	    }
	}
    }
