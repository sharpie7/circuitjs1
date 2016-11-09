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

// import java.awt.*;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.NumberFormat;

// circuit element class
public abstract class CircuitElm implements Editable {
    static double voltageRange = 5;
    static int colorScaleCount = 32;
    static Color colorScale[];
    static double currentMult, powerMult;
    static Point ps1, ps2;
    static CirSim sim;
    static Color whiteColor, selectColor, lightGrayColor;
    static Font unitsFont;

    static NumberFormat showFormat, shortFormat;//, noCommaFormat;
    static final double pi = 3.14159265358979323846;

    int x, y, x2, y2, flags, nodes[], voltSource;
    int dx, dy, dsign;
    int lastHandleGrabbed=-1;
    int numHandles=2;
    double dn, dpx1, dpy1;
    Point point1, point2, lead1, lead2;
    double volts[];
    double current, curcount;
    Rectangle boundingBox;
    boolean noDiagonal;
    public boolean selected;
    private boolean iAmMouseElm=false;
    
    int getDumpType() { return 0; }
    Class getDumpClass() { return getClass(); }
    int getDefaultFlags() { return 0; }

    static void initClass(CirSim s) {
	unitsFont = new Font("SansSerif", 0, 12);
	sim = s;
	
	colorScale = new Color[colorScaleCount];
	int i;
	for (i = 0; i != colorScaleCount; i++) {
	    double v = i*2./colorScaleCount - 1;
	    if (v < 0) {
		int n1 = (int) (128*-v)+127;
		int n2 = (int) (127*(1+v));
		colorScale[i] = new Color(n1, n2, n2);
	    } else {
		int n1 = (int) (128*v)+127;
		int n2 = (int) (127*(1-v));
		colorScale[i] = new Color(n2, n1, n2);
	    }
	}
	
	ps1 = new Point();
	ps2 = new Point();

//	showFormat = DecimalFormat.getInstance();
//	showFormat.setMaximumFractionDigits(2);
	showFormat=NumberFormat.getFormat("####.##");
//	shortFormat = DecimalFormat.getInstance();
//	shortFormat.setMaximumFractionDigits(1);
	shortFormat=NumberFormat.getFormat("####.#");
//	noCommaFormat = DecimalFormat.getInstance();
//	noCommaFormat.setMaximumFractionDigits(10);
//	noCommaFormat.setGroupingUsed(false);
    }
    
    CircuitElm(int xx, int yy) {
	x = x2 = xx;
	y = y2 = yy;
	flags = getDefaultFlags();
	allocNodes();
	initBoundingBox();
    }
    
    CircuitElm(int xa, int ya, int xb, int yb, int f) {
	x = xa; y = ya; x2 = xb; y2 = yb; flags = f;
	allocNodes();
	initBoundingBox();
    }
    
    void initBoundingBox() {
	boundingBox = new Rectangle();
	boundingBox.setBounds(min(x, x2), min(y, y2),
			      abs(x2-x)+1, abs(y2-y)+1);
    }
    
    void allocNodes() {
	int n = getPostCount() + getInternalNodeCount();
	// preserve voltages if possible
	if (nodes == null || nodes.length != n) {
	    nodes = new int[n];
	    volts = new double[n];
	}
    }
    String dump() {
	int t = getDumpType();
	return (t < 127 ? ((char)t)+" " : t+" ") + x + " " + y + " " +
	    x2 + " " + y2 + " " + flags;
    }
    void reset() {
	int i;
	for (i = 0; i != getPostCount()+getInternalNodeCount(); i++)
	    volts[i] = 0;
	curcount = 0;
    }
    void draw(Graphics g) {}
    void setCurrent(int x, double c) { current = c; }
    double getCurrent() { return current; }
    void doStep() {}
    void delete() {}
    void startIteration() {}
    double getPostVoltage(int x) { return volts[x]; }
    void setNodeVoltage(int n, double c) {
	volts[n] = c;
	calculateCurrent();
    }
    void calculateCurrent() {}
    
    void setPoints() {
    	dx = x2-x; dy = y2-y;
    	dn = Math.sqrt(dx*dx+dy*dy);
    	dpx1 = dy/dn;
    	dpy1 = -dx/dn;
    	dsign = (dy == 0) ? sign(dx) : sign(dy);
    	point1 = new Point(x , y );
    	point2 = new Point(x2, y2);
    }
    
    void calcLeads(int len) {
	if (dn < len || len == 0) {
	    lead1 = point1;
	    lead2 = point2;
	    return;
	}
	lead1 = interpPoint(point1, point2, (dn-len)/(2*dn));
	lead2 = interpPoint(point1, point2, (dn+len)/(2*dn));
    }
    Point interpPoint(Point a, Point b, double f) {
	Point p = new Point();
	interpPoint(a, b, p, f);
	return p;
    }
    void interpPoint(Point a, Point b, Point c, double f) {
	int xpd = b.x-a.x;
	int ypd = b.y-a.y;
	/*double q = (a.x*(1-f)+b.x*f+.48);
	  System.out.println(q + " " + (int) q);*/
	c.x = (int) Math.floor(a.x*(1-f)+b.x*f+.48);
	c.y = (int) Math.floor(a.y*(1-f)+b.y*f+.48);
    }
    void interpPoint(Point a, Point b, Point c, double f, double g) {
//	int xpd = b.x-a.x;
//	int ypd = b.y-a.y;
	int gx = b.y-a.y;
	int gy = a.x-b.x;
	g /= Math.sqrt(gx*gx+gy*gy);
	c.x = (int) Math.floor(a.x*(1-f)+b.x*f+g*gx+.48);
	c.y = (int) Math.floor(a.y*(1-f)+b.y*f+g*gy+.48);
    }
    Point interpPoint(Point a, Point b, double f, double g) {
	Point p = new Point();
	interpPoint(a, b, p, f, g);
	return p;
    }
    void interpPoint2(Point a, Point b, Point c, Point d, double f, double g) {
//	int xpd = b.x-a.x;
//	int ypd = b.y-a.y;
	int gx = b.y-a.y;
	int gy = a.x-b.x;
	g /= Math.sqrt(gx*gx+gy*gy);
	c.x = (int) Math.floor(a.x*(1-f)+b.x*f+g*gx+.48);
	c.y = (int) Math.floor(a.y*(1-f)+b.y*f+g*gy+.48);
	d.x = (int) Math.floor(a.x*(1-f)+b.x*f-g*gx+.48);
	d.y = (int) Math.floor(a.y*(1-f)+b.y*f-g*gy+.48);
    }
    void draw2Leads(Graphics g) {
	// draw first lead
	setVoltageColor(g, volts[0]);
	drawThickLine(g, point1, lead1);

	// draw second lead
	setVoltageColor(g, volts[1]);
	drawThickLine(g, lead2, point2);
    }
    Point [] newPointArray(int n) {
	Point a[] = new Point[n];
	while (n > 0)
	    a[--n] = new Point();
	return a;
    }
	
    void drawDots(Graphics g, Point pa, Point pb, double pos) {
	 if ((!sim.simIsRunning()) || pos == 0 || !sim.dotsCheckItem.getState())
	    return;
	int dx = pb.x-pa.x;
	int dy = pb.y-pa.y;
	double dn = Math.sqrt(dx*dx+dy*dy);
	g.setColor(sim.conventionCheckItem.getState()?Color.yellow:Color.cyan);
	int ds = 16;
	pos %= ds;
	if (pos < 0)
	    pos += ds;
	double di = 0;
	for (di = pos; di < dn; di += ds) {
	    int x0 = (int) (pa.x+di*dx/dn);
	    int y0 = (int) (pa.y+di*dy/dn);
	    g.fillRect(x0-2, y0-2, 4, 4);
	}
    }

    Polygon calcArrow(Point a, Point b, double al, double aw) {
	Polygon poly = new Polygon();
	Point p1 = new Point();
	Point p2 = new Point();
	int adx = b.x-a.x;
	int ady = b.y-a.y;
	double l = Math.sqrt(adx*adx+ady*ady);
	poly.addPoint(b.x, b.y);
	interpPoint2(a, b, p1, p2, 1-al/l, aw);
	poly.addPoint(p1.x, p1.y);
	poly.addPoint(p2.x, p2.y);
	return poly;
    }
    Polygon createPolygon(Point a, Point b, Point c) {
	Polygon p = new Polygon();
	p.addPoint(a.x, a.y);
	p.addPoint(b.x, b.y);
	p.addPoint(c.x, c.y);
	return p;
    }
    Polygon createPolygon(Point a, Point b, Point c, Point d) {
	Polygon p = new Polygon();
	p.addPoint(a.x, a.y);
	p.addPoint(b.x, b.y);
	p.addPoint(c.x, c.y);
	p.addPoint(d.x, d.y);
	return p;
    }
    Polygon createPolygon(Point a[]) {
	Polygon p = new Polygon();
	int i;
	for (i = 0; i != a.length; i++)
	    p.addPoint(a[i].x, a[i].y);
	return p;
    }
    void drag(int xx, int yy) {
	xx = sim.snapGrid(xx);
	yy = sim.snapGrid(yy);
	if (noDiagonal) {
	    if (Math.abs(x-xx) < Math.abs(y-yy)) {
		xx = x;
	    } else {
		yy = y;
	    }
	}
	x2 = xx; y2 = yy;
	setPoints();
    }
    void move(int dx, int dy) {
	x += dx; y += dy; x2 += dx; y2 += dy;
	boundingBox.translate(dx, dy);
	setPoints();
    }

    // determine if moving this element by (dx,dy) will put it on top of another element
    boolean allowMove(int dx, int dy) {
	int nx = x+dx;
	int ny = y+dy;
	int nx2 = x2+dx;
	int ny2 = y2+dy;
	int i;
	for (i = 0; i != sim.elmList.size(); i++) {
	    CircuitElm ce = sim.getElm(i);
	    if (ce.x == nx && ce.y == ny && ce.x2 == nx2 && ce.y2 == ny2)
		return false;
	    if (ce.x == nx2 && ce.y == ny2 && ce.x2 == nx && ce.y2 == ny)
		return false;
	}
	return true;
    }
    
    void movePoint(int n, int dx, int dy) {
    	// modified by IES to prevent the user dragging points to create zero sized nodes
    	// that then render improperly
    	int oldx=x;
    	int oldy=y;
    	int oldx2=x2;
    	int oldy2=y2;
    	if (n == 0) {
    		x += dx; y += dy;
    	} else {
    		x2 += dx; y2 += dy;
    	}
    	if (x==x2 && y==y2) {
    		x=oldx;
    		y=oldy;
    		x2=oldx2;
    		y2=oldy2;
    	}
    	setPoints();
    }
    
    void drawPosts(Graphics g) {
	int i;
	for (i = 0; i != getPostCount(); i++) {
	    Point p = getPost(i);
	    drawPost(g, p.x, p.y, nodes[i]);
	}
    }
    
    void drawHandles(Graphics g, Color c) {
    	g.setColor(c);
    	if (lastHandleGrabbed==-1)
    		g.fillRect(x-3, y-3, 7, 7);
    	else if (lastHandleGrabbed==0)
    		g.fillRect(x-4, y-4, 9, 9);
    	if (numHandles==2) {
    		if (lastHandleGrabbed==-1)
    			g.fillRect(x2-3, y2-3, 7, 7);
    		else if (lastHandleGrabbed==1)
    			g.fillRect(x2-4, y2-4, 9, 9);
    	}
    }
    
    int getHandleGrabbedClose(int xtest, int ytest, int deltaSq, int minSize) {
    	lastHandleGrabbed=-1;
    	if ( Graphics.distanceSq(x , y , x2, y2)>=minSize) {
    		if (Graphics.distanceSq(x, y, xtest,ytest) <= deltaSq)
    			lastHandleGrabbed=0;
    		else if (Graphics.distanceSq(x2, y2, xtest,ytest) <= deltaSq)
    			lastHandleGrabbed=1;
    	}
    	return lastHandleGrabbed;
    }
    
    void stamp() {}
    int getVoltageSourceCount() { return 0; }
    int getInternalNodeCount() { return 0; }
    void setNode(int p, int n) { nodes[p] = n; }
    void setVoltageSource(int n, int v) { voltSource = v; }
    int getVoltageSource() { return voltSource; }
    double getVoltageDiff() {
	return volts[0] - volts[1];
    }
    boolean nonLinear() { return false; }
    int getPostCount() { return 2; }
    int getNode(int n) { return nodes[n]; }
    Point getPost(int n) {
	return (n == 0) ? point1 : (n == 1) ? point2 : null;
    }
    void drawPost(Graphics g, int x0, int y0, int n) {
	if (sim.dragElm == null && !needsHighlight() &&
	    sim.getCircuitNode(n).links.size() == 2)
	    return;
	if (sim.mouseMode == CirSim.MODE_DRAG_ROW ||
	    sim.mouseMode == CirSim.MODE_DRAG_COLUMN)
	    return;
	drawPost(g, x0, y0);
    }
    void drawPost(Graphics g, int x0, int y0) {
	g.setColor(whiteColor);
	g.fillOval(x0-3, y0-3, 7, 7);
    }
    void setBbox(int x1, int y1, int x2, int y2) {
	if (x1 > x2) { int q = x1; x1 = x2; x2 = q; }
	if (y1 > y2) { int q = y1; y1 = y2; y2 = q; }
	boundingBox.setBounds(x1, y1, x2-x1+1, y2-y1+1);
    }
    void setBbox(Point p1, Point p2, double w) {
	setBbox(p1.x, p1.y, p2.x, p2.y);
	int gx = p2.y-p1.y;
	int gy = p1.x-p2.x;
	int dpx = (int) (dpx1*w);
	int dpy = (int) (dpy1*w);
	adjustBbox(p1.x+dpx, p1.y+dpy, p1.x-dpx, p1.y-dpy);
    }
    void adjustBbox(int x1, int y1, int x2, int y2) {
	if (x1 > x2) { int q = x1; x1 = x2; x2 = q; }
	if (y1 > y2) { int q = y1; y1 = y2; y2 = q; }
	x1 = min(boundingBox.x, x1);
	y1 = min(boundingBox.y, y1);
	x2 = max(boundingBox.x+boundingBox.width-1,  x2);
	y2 = max(boundingBox.y+boundingBox.height-1, y2);
	boundingBox.setBounds(x1, y1, x2-x1, y2-y1);
    }
    void adjustBbox(Point p1, Point p2) {
	adjustBbox(p1.x, p1.y, p2.x, p2.y);
    }
    boolean isCenteredText() { return false; }
	
    void drawCenteredText(Graphics g, String s, int x, int y, boolean cx) {
	// FontMetrics fm = g.getFontMetrics();
	//int w = fm.stringWidth(s);
//    	int w=0;
//	if (cx)
//	    x -= w/2;
//	g.drawString(s, x, y+fm.getAscent()/2);
//	adjustBbox(x, y-fm.getAscent()/2,
//		   x+w, y+fm.getAscent()/2+fm.getDescent());
    	int w=(int)g.context.measureText(s).getWidth();
    	int h2=(int)g.currentFontSize/2;
		g.context.save();
		g.context.setTextBaseline("middle");
		if (cx) {
			g.context.setTextAlign("center");
			adjustBbox(x-w/2,y-h2,x+w/2,y+h2);
		} else {
			adjustBbox(x,y-h2,x+w,y+h2);
		}
		
		if (cx)
			g.context.setTextAlign("center");
		g.drawString(s, x, y);
		g.context.restore();
    }
    
    void drawValues(Graphics g, String s, double hs) {
	if (s == null)
	    return;
	g.setFont(unitsFont);
	//FontMetrics fm = g.getFontMetrics();
	int w = (int)g.context.measureText(s).getWidth();;
	g.setColor(whiteColor);
	int ya = (int)g.currentFontSize/2;
	int xc, yc;
	if (this instanceof RailElm || this instanceof SweepElm) {
	    xc = x2;
	    yc = y2;
	} else {
	    xc = (x2+x)/2;
	    yc = (y2+y)/2;
	}
	int dpx = (int) (dpx1*hs);
	int dpy = (int) (dpy1*hs);
	if (dpx == 0) {
	    g.drawString(s, xc-w/2, yc-abs(dpy)-2);
	} else {
	    int xx = xc+abs(dpx)+2;
	     if (this instanceof VoltageElm || (x < x2 && y > y2))
		xx = xc-(w+abs(dpx)+2);
	    g.drawString(s, xx, yc+dpy+ya);
	}
    }
    void drawCoil(Graphics g, int hs, Point p1, Point p2,
		  double v1, double v2) {
	double len = distance(p1, p2);
	int segments = 30; // 10*(int) (len/10);
	int i;
	double segf = 1./segments;
	    
	ps1.setLocation(p1);
	for (i = 0; i != segments; i++) {
	    double cx = (((i+1)*6.*segf) % 2)-1;
	    double hsx = Math.sqrt(1-cx*cx);
	    if (hsx < 0)
		hsx = -hsx;
	    interpPoint(p1, p2, ps2, i*segf, hsx*hs);
	    double v = v1+(v2-v1)*i/segments;
	    setVoltageColor(g, v);
	    drawThickLine(g, ps1, ps2);
	    ps1.setLocation(ps2);
    }
//		GWT.log("Coil"+hs+" "+p1.x+" "+p1.y+" "+p2.x+" "+p2.y);
//		g.context.save();
//    	g.context.setLineWidth(3.0);
//    	g.context.setTransform(((double)(p2.x-p1.x))/len, ((double)(p2.y-p1.y))/len, -((double)(p2.y-p1.y))/len,((double)(p2.x-p1.x))/len,p1.x,p1.y);
//    	CanvasGradient grad = g.context.createLinearGradient(0,0,len,0);
//    	grad.addColorStop(0, getVoltageColor(g,v1).getHexValue());
//    	grad.addColorStop(1.0, getVoltageColor(g,v2).getHexValue());
//    	g.context.setStrokeStyle(grad);
//    	g.context.beginPath();
//    	g.context.arc(len*0.16667,0,len*0.16667,pi,(hs<0)?0:pi*2.0, hs<0);
//    	g.context.arc(len*0.5,0,len*0.16667,pi,pi*2.0);
//    	g.context.arc(len*0.83333,0,len*0.16667,pi,pi*2.0);
//    	g.context.stroke();
//    	g.context.restore();
//    	g.context.setTransform(1.0, 0, 0, 1.0, 0, 0);
//    	g.context.setLineWidth(1.0);
    }
    static void drawThickLine(Graphics g, int x, int y, int x2, int y2) {
//	g.drawLine(x, y, x2, y2);
//	g.drawLine(x+1, y, x2+1, y2);
//	g.drawLine(x, y+1, x2, y2+1);
//	g.drawLine(x+1, y+1, x2+1, y2+1);
    	g.setLineWidth(3.0);
    	g.drawLine(x,y,x2,y2);
    	g.setLineWidth(1.0);
    }

    static void drawThickLine(Graphics g, Point pa, Point pb) {
//	g.drawLine(pa.x, pa.y, pb.x, pb.y);
//	g.drawLine(pa.x+1, pa.y, pb.x+1, pb.y);
//	g.drawLine(pa.x, pa.y+1, pb.x, pb.y+1);
//	g.drawLine(pa.x+1, pa.y+1, pb.x+1, pb.y+1);
    	g.setLineWidth(3.0);
    	g.drawLine(pa.x, pa.y, pb.x, pb.y);
    	g.setLineWidth(1.0);
    }

    static void drawThickPolygon(Graphics g, int xs[], int ys[], int c) {
//	int i;
//	for (i = 0; i != c-1; i++)
//	    drawThickLine(g, xs[i], ys[i], xs[i+1], ys[i+1]);
//	drawThickLine(g, xs[i], ys[i], xs[0], ys[0]);
    	g.setLineWidth(3.0);
    	g.drawPolyline(xs, ys, c);
    	g.setLineWidth(1.0);
    }
    
    static void drawThickPolygon(Graphics g, Polygon p) {
	drawThickPolygon(g, p.xpoints, p.ypoints, p.npoints);
    }
    
    static void drawPolygon(Graphics g, Polygon p) {
    	g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
/*	int i;
	int xs[] = p.xpoints;
	int ys[] = p.ypoints;
	int np = p.npoints;
	np -= 3;
	for (i = 0; i != np-1; i++)
	    g.drawLine(xs[i], ys[i], xs[i+1], ys[i+1]);
	g.drawLine(xs[i], ys[i], xs[0], ys[0]);*/
    }
    
    static void drawThickCircle(Graphics g, int cx, int cy, int ri) {
	int a;
	double m = pi/180;
	double r = ri*.98;
	for (a = 0; a != 360; a += 20) {
	    double ax = Math.cos(a*m)*r + cx;
	    double ay = Math.sin(a*m)*r + cy;
	    double bx = Math.cos((a+20)*m)*r + cx;
	    double by = Math.sin((a+20)*m)*r + cy;
	    drawThickLine(g, (int) ax, (int) ay, (int) bx, (int) by);
	}
    }
    
    Polygon getSchmittPolygon(float gsize, float ctr) {
	Point pts[] = newPointArray(6);
	float hs = 3*gsize;
	float h1 = 3*gsize;
	float h2 = h1*2;
	double len = distance(lead1, lead2);
	pts[0] = interpPoint(lead1, lead2, ctr-h2/len, hs);
	pts[1] = interpPoint(lead1, lead2, ctr+h1/len,  hs);
	pts[2] = interpPoint(lead1, lead2, ctr+h1/len, -hs);
	pts[3] = interpPoint(lead1, lead2, ctr+h2/len, -hs);
	pts[4] = interpPoint(lead1, lead2, ctr-h1/len, -hs);
	pts[5] = interpPoint(lead1, lead2, ctr-h1/len, hs);
	return createPolygon(pts); 
    }

    static String getVoltageDText(double v) {
	return getUnitText(Math.abs(v), "V");
    }
    static String getVoltageText(double v) {
	return getUnitText(v, "V");
	
	
    }
    
    // IES - hacking
    static String getUnitText(double v, String u) {
    	return myGetUnitText(v,u, false);
    }
    static String getShortUnitText(double v, String u) {
    	return myGetUnitText(v,u, true);
    }
    
    static String myGetUnitText(double v, String u, boolean sf) {
    NumberFormat s;
    String sp = "";
    if (sf)
    	s=shortFormat;
    else {
    	s=showFormat;
    	sp = " ";
    }
	double va = Math.abs(v);
	if (va < 1e-14)
	    return sf ? null : "0 " + u;
	if (va < 1e-9)
	    return s.format(v*1e12) + sp + "p" + u;
	if (va < 1e-6)
	    return s.format(v*1e9) + sp + "n" + u;
	if (va < 1e-3)
	    return s.format(v*1e6) + sp + CirSim.muString + u;
	if (va < 1)
	    return s.format(v*1e3) + sp + "m" + u;
	if (va < 1e3)
	    return s.format(v) + sp + u;
	if (va < 1e6)
	    return s.format(v*1e-3) + sp + "k" + u;
	if (va < 1e9)
	    return s.format(v*1e-6) + sp + "M" + u;
	return s.format(v*1e-9) + sp + "G" + u;
    }
    
    /*
    static String getUnitText(double v, String u) {
	double va = Math.abs(v);
	if (va < 1e-14)
	    return "0 " + u;
	if (va < 1e-9)
	    return showFormat.format(v*1e12) + " p" + u;
	if (va < 1e-6)
	    return showFormat.format(v*1e9) + " n" + u;
	if (va < 1e-3)
	    return showFormat.format(v*1e6) + " " + CirSim.muString + u;
	if (va < 1)
	    return showFormat.format(v*1e3) + " m" + u;
	if (va < 1e3)
	    return showFormat.format(v) + " " + u;
	if (va < 1e6)
	    return showFormat.format(v*1e-3) + " k" + u;
	if (va < 1e9)
	    return showFormat.format(v*1e-6) + " M" + u;
	return showFormat.format(v*1e-9) + " G" + u;
    }
    static String getShortUnitText(double v, String u) {
	double va = Math.abs(v);
	if (va < 1e-13)
	    return null;
	if (va < 1e-9)
	    return shortFormat.format(v*1e12) + "p" + u;
	if (va < 1e-6)
	    return shortFormat.format(v*1e9) + "n" + u;
	if (va < 1e-3)
	    return shortFormat.format(v*1e6) + CirSim.muString + u;
	if (va < 1)
	    return shortFormat.format(v*1e3) + "m" + u;
	if (va < 1e3)
	    return shortFormat.format(v) + u;
	if (va < 1e6)
	    return shortFormat.format(v*1e-3) + "k" + u;
	if (va < 1e9)
	    return shortFormat.format(v*1e-6) + "M" + u;
	return shortFormat.format(v*1e-9) + "G" + u;
    }*/
    static String getCurrentText(double i) {
	return getUnitText(i, "A");
    }
    static String getCurrentDText(double i) {
	return getUnitText(Math.abs(i), "A");
    }

    void updateDotCount() {
	curcount = updateDotCount(current, curcount);
    }
    double updateDotCount(double cur, double cc) {
  
	 if (!sim.simIsRunning())
	    return cc;
	double cadd = cur*currentMult;
	/*if (cur != 0 && cadd <= .05 && cadd >= -.05)
	  cadd = (cadd < 0) ? -.05 : .05;*/
	cadd %= 8;
	/*if (cadd > 8)
	  cadd = 8;
	  if (cadd < -8)
	  cadd = -8;*/
	return cc + cadd;
    }
    void doDots(Graphics g) {
	updateDotCount();
	if (sim.dragElm != this)
	    drawDots(g, point1, point2, curcount);
    }
    void doAdjust() {}
    void setupAdjust() {}
    void getInfo(String arr[]) {
    }
    int getBasicInfo(String arr[]) {
	arr[1] = "I = " + getCurrentDText(getCurrent());
	arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
	return 3;
    }
    
    Color getVoltageColor(Graphics g, double volts) {
    	if (needsHighlight()) {
    	    return (selectColor);
    	}
    	if (!sim.voltsCheckItem.getState()) {
    	    if (!sim.powerCheckItem.getState()) // && !conductanceCheckItem.getState())
    	    	return(whiteColor);
    	    return (g.lastColor);
    	}
    	int c = (int) ((volts+voltageRange)*(colorScaleCount-1)/
    		       (voltageRange*2));
    	if (c < 0)
    	    c = 0;
    	if (c >= colorScaleCount)
    	    c = colorScaleCount-1;
    	return (colorScale[c]);
    }
    
    void setVoltageColor(Graphics g, double volts) {
    	g.setColor(getVoltageColor(g, volts));
    }
    
    void setPowerColor(Graphics g, boolean yellow) {
	/*if (conductanceCheckItem.getState()) {
	  setConductanceColor(g, current/getVoltageDiff());
	  return;
	  }*/
	if (!sim.powerCheckItem.getState())
	    return;
	setPowerColor(g, getPower());
    }
    
    void setPowerColor(Graphics g, double w0) {
	w0 *= powerMult;
	//System.out.println(w);
	double w = (w0 < 0) ? -w0 : w0;
	if (w > 1)
	    w = 1;
	int rg = 128+(int) (w*127);
	int b  = (int) (128*(1-w));
	/*if (yellow)
	  g.setColor(new Color(rg, rg, b));
	  else */
	if (w0 > 0)
	    g.setColor(new Color(rg, b, b));
	else
	    g.setColor(new Color(b, rg, b));
    }
    void setConductanceColor(Graphics g, double w0) {
	w0 *= powerMult;
	//System.out.println(w);
	double w = (w0 < 0) ? -w0 : w0;
	if (w > 1)
	    w = 1;
	int rg = (int) (w*255);
	g.setColor(new Color(rg, rg, rg));
    }
    double getPower() { return getVoltageDiff()*current; }
    double getScopeValue(int x) {
	return (x == 1) ? getPower() : getVoltageDiff();
    }
    String getScopeUnits(int x) {
	return (x == 1) ? "W" : "V";
    }
    public EditInfo getEditInfo(int n) { return null; }
    public void setEditValue(int n, EditInfo ei) {}
    
    // get number of nodes that can be retrieved by getConnectionNode()
    int getConnectionNodeCount() { return getPostCount(); }
    
    // get nodes that can be passed to getConnection(), to test if this element connects
    // those two nodes; this is the same as getNode() for all but labeled nodes.
    int getConnectionNode(int n) { return getNode(n); }
    
    // are n1 and n2 connected by this element?  this is used to determine
    // unconnected nodes, and look for loops
    boolean getConnection(int n1, int n2) { return true; }
    
    // is n1 connected to ground somehow?
    boolean hasGroundConnection(int n1) { return false; }
    
    boolean isWire() { return false; }
    boolean canViewInScope() { return getPostCount() <= 2; }
    boolean comparePair(int x1, int x2, int y1, int y2) {
	return ((x1 == y1 && x2 == y2) || (x1 == y2 && x2 == y1));
    }
    boolean needsHighlight() { return iAmMouseElm || selected; }
    boolean isSelected() { return selected; }
    void setSelected(boolean x) { selected = x; }
    void selectRect(Rectangle r) {
	selected = r.intersects(boundingBox);
    }
    static int abs(int x) { return x < 0 ? -x : x; }
    static int sign(int x) { return (x < 0) ? -1 : (x == 0) ? 0 : 1; }
    static int min(int a, int b) { return (a < b) ? a : b; }
    static int max(int a, int b) { return (a > b) ? a : b; }
    static double distance(Point p1, Point p2) {
	double x = p1.x-p2.x;
	double y = p1.y-p2.y;
	return Math.sqrt(x*x+y*y);
    }
    Rectangle getBoundingBox() { return boundingBox; }
    boolean needsShortcut() { return getShortcut() > 0; }
    int getShortcut() { return 0; }

    boolean isGraphicElmt() { return false; }
    
    void setMouseElm(boolean v) {iAmMouseElm=v;}
    
    boolean isMouseElm() {return iAmMouseElm; }
    void updateModels() {}
    void stepFinished() {}
}
