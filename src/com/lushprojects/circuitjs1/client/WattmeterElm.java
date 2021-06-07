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

class WattmeterElm extends CircuitElm {
    int width;
    int voltSources[];
    double currents[];
    double curcounts[];

    public WattmeterElm(int xx, int yy) {
	super(xx, yy);
	setup();
    }
    public WattmeterElm(int xa, int ya, int xb, int yb, int f,
	    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	width = Integer.parseInt(st.nextToken());
	setup();
    }

    void setup() {
	voltSources = new int[2];
	currents = new double[2];
	curcounts = new double[2];
    }

    String dump() { return super.dump() + " " + width; }

    int getVoltageSourceCount() { return 2; }
    int getDumpType() { return 420; }
    int getPostCount() { return 4; }

    void drag(int xx, int yy) {
	xx = sim.snapGrid(xx);
	yy = sim.snapGrid(yy);
	int w1 = max(sim.gridSize, abs(yy-y));
	int w2 = max(sim.gridSize, abs(xx-x));
	if (w1 > w2) {
	    xx = x;
	    width = w2;
	} else {
	    yy = y;
	    width = w1;
	}
	x2 = xx; y2 = yy;
	setPoints();
    }

    Point posts[];
    Point inner[];
    int maxTextLen;

    void setPoints() {
	super.setPoints();
	int ds = (dy == 0) ? sign(dx) : -sign(dy);
	
	// get 2 more terminals
	Point p3 = interpPoint(point1, point2, 0, -width*ds);
	Point p4 = interpPoint(point1, point2, 1, -width*ds);
	
	// get stubs
	int sep = sim.gridSize;
	Point p5 = interpPoint(point1, point2,   sep/dn);
	Point p6 = interpPoint(point1, point2, 1-sep/dn);
	Point p7 = interpPoint(p3, p4,   sep/dn);
	Point p8 = interpPoint(p3, p4, 1-sep/dn);

	// we number the posts like this because we want the lower-numbered
	// points to be on the bottom, so that if some of them are unconnected
	// (which is often true) then the bottom ones will get automatically
	// attached to ground.
	posts = new Point[] { p3, p4, point1, point2 };
	inner = new Point[] { p7, p8, p5, p6 };

	// get rectangle
	Point r1 = interpPoint(point1, point2,   sep/dn, ds*sep);
	Point r2 = interpPoint(point1, point2, 1-sep/dn, ds*sep);
	Point r3 = interpPoint(point1, point2,   sep/dn, -ds*(sep+width));
	Point r4 = interpPoint(point1, point2, 1-sep/dn, -ds*(sep+width));
	rectPointsX = new int[] { r1.x, r2.x, r4.x, r3.x };
	rectPointsY = new int[] { r1.y, r2.y, r4.y, r3.y };

	center = interpPoint(r1, r4, .5);
	maxTextLen = max(abs(r1.x-r4.x)-5, 5);
    }

    int rectPointsX[], rectPointsY[];
    Point center;

    Point getPost(int n) {
	return posts[n];
    }

    void stamp() {
	// zero-valued voltage sources from 0 to 1 and 2 to 3, so we can measure current
	sim.stampVoltageSource(nodes[0], nodes[1], voltSources[0], 0);
	sim.stampVoltageSource(nodes[2], nodes[3], voltSources[1], 0);
    }

    void setVoltageSource(int j, int vs) {
	voltSources[j] = vs;
    }

    void draw(Graphics g) {
	int i;
	for (i = 0; i != 2; i++)
	    curcounts[i] = updateDotCount(currents[i], curcounts[i]);
	double flip = 1;
	for (i = 0; i != 4; i++) {
	    setVoltageColor(g, volts[i]);
	    drawThickLine(g, posts[i], inner[i]);
	    drawDots(g, posts[i], inner[i], curcounts[i/2]*flip);
	    flip *= -1;
	}
	
	g.setColor(Color.lightGray);
	drawThickPolygon(g, rectPointsX, rectPointsY, 4);
	
	setBbox(posts[0].x, posts[0].y, posts[3].x, posts[3].y);
	drawPosts(g);

	String str = getUnitText(getPower(), "W");
	g.context.save();
	int fsize = 15;
	int w;
	// adjust font size to fit
	while (true) {
	    g.setFont(new Font("SansSerif", 0, fsize));
	    w=(int)g.context.measureText(str).getWidth();
	    if (w < maxTextLen)
		break;
	    fsize--;
	}
	g.setColor(whiteColor);
	g.context.setTextBaseline("middle");
	g.drawString(str, center.x-w/2, center.y);
	g.context.restore();
    }

    double getPower() { return getVoltageDiff()*getCurrent(); }

    void setCurrent(int vn, double c) {
	currents[vn == voltSources[0] ? 0 : 1] = c;
    }

    boolean getConnection(int n1, int n2) { return (n1/2) == (n2/2); }
    boolean hasGroundConnection(int n1) { return false; }

    void getInfo(String arr[]) {
	arr[0] = "wattmeter";
	getBasicInfo(arr);
	arr[3] = "P = " + getUnitText(getPower(), "W");
    }
    boolean canViewInScope() { return true; }
    double getCurrent() { return currents[1]; }
    double getVoltageDiff() { return volts[2]-volts[0]; }
}
