package com.lushprojects.circuitjs1.client;

public class OTAElm extends CompositeElm {

    private static String modelString = "RailElm 4\rRailElm 10\rNTransistorElm 1 2 3\rNTransistorElm 3 1 4\rNTransistorElm 3 3 4\rNTransistorElm 5 6 2\rNTransistorElm 7 8 2\rPTransistorElm 9 6 10\rPTransistorElm 9 9 10\rPTransistorElm 6 12 9\rPTransistorElm 11 8 10\rPTransistorElm 11 11 10\rPTransistorElm 8 13 11\rNTransistorElm 14 14 4\rNTransistorElm 14 12 4\rNTransistorElm 12 13 14\rNTransistorElm 15 15 5\rNTransistorElm 15 15 7";
    private static int[] modelExternalNodes = { 7, 5, 15, 1, 13 };
    // private static String modelString="NTransistorElm 1 1 2\rNTransistorElm 1
    // 2 3\rNTransistorElm 1 3 4\rNTransistorElm 1 4 5";
    // private static int[] modelExternalNodes = { 1, 2, 3 , 4, 5};

    Polygon arrowPoly1, arrowPoly2;

    int opsize;
    final int opheight = 32;
    final int opwidth = 32;
    final int circDiam = 19;
    final int circOverlap = 8;
    Point in1p[], in2p[], in3p[], in4p[], textp[], bar1[], bar2[], circCent[];
    Point point2bis;
    Polygon triangle;
    Font plusFont;
    double curCount0 = 0;
    double curCount1 = 0;
    double curCount2 = 0;
    double curCount3 = 0;
    double posVolt = 9.0;
    double negVolt = -9.0;

    public OTAElm(int xx, int yy) {
	super(xx, yy, modelString, modelExternalNodes);
	noDiagonal = true;
	initOTA();
    }

    public OTAElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	super(xa, ya, xb, yb, f, st, modelString, modelExternalNodes);
	noDiagonal = true;
	negVolt = ((RailElm) compElmList.get(0)).maxVoltage;
	posVolt = ((RailElm) compElmList.get(1)).maxVoltage;
    }

    private void initOTA() {
	((RailElm) compElmList.get(0)).maxVoltage = negVolt;
	((RailElm) compElmList.get(1)).maxVoltage = posVolt;
    }

    public void reset() {
	super.reset();
	curCount0 = curCount1 = curCount2 = curCount3 = 0;
    }

    public boolean getConnection(int n1, int n2) {
	return false;
    }

    void draw(Graphics g) {
	setBbox(point1, point2, 3 * opheight / 2);
	setVoltageColor(g, volts[0]);
	drawThickLine(g, in1p[0], in1p[1]);
	setVoltageColor(g, volts[1]);
	drawThickLine(g, in2p[0], in2p[1]);
	setVoltageColor(g, volts[2]);
	drawThickLine(g, in3p[0], in3p[1]);
	setVoltageColor(g, volts[3]);
	drawThickLine(g, in4p[0], in4p[1]);
	g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	setPowerColor(g, true);
	drawThickPolygon(g, triangle);
	g.fillPolygon(arrowPoly1);
	g.fillPolygon(arrowPoly2);
	drawThickLine(g, bar1[0], bar1[1]);
	drawThickLine(g, bar2[0], bar2[1]);
	drawThickCircle(g, circCent[0].x, circCent[0].y, circDiam / 2);
	drawThickCircle(g, circCent[1].x, circCent[1].y, circDiam / 2);
	g.setFont(plusFont);
	drawCenteredText(g, "+", textp[0].x, textp[0].y - 2, true);
	drawCenteredText(g, "-", textp[1].x, textp[1].y, true);
	// setVoltageColor(g, volts[2]);
	// drawThickLine(g, lead2, point2);
	curCount0 = updateDotCount(-getCurrentIntoNode(0), curCount0);
	drawDots(g, in1p[0], in1p[1], curCount0);
	curCount1 = updateDotCount(-getCurrentIntoNode(1), curCount1);
	drawDots(g, in2p[0], in2p[1], curCount0);
	curCount2 = updateDotCount(-getCurrentIntoNode(2), curCount2);
	drawDots(g, in3p[0], in3p[1], curCount2);
	curCount3 = updateDotCount(-getCurrentIntoNode(3), curCount3);
	drawDots(g, in4p[0], in4p[1], curCount3);
	drawPosts(g);
    }

    void setPoints() {
	super.setPoints();
	int ww = opwidth;
	int wtot = ww * 2 + 2 * circDiam - circOverlap;

	if (dn > wtot) {
	    lead1 = interpPoint(point1, point2, 1.0 - wtot / dn, 0);
	    lead2 = point2;
	    point2bis = point2;
	} else {
	    lead1 = point1;
	    lead2 = interpPoint(point1, point2, wtot / dn, 0);
	    point2bis = lead2;
	}
	int hs = opheight * dsign;
	// if ((flags & FLAG_SWAP) != 0)
	// hs = -hs;
	in1p = newPointArray(2);
	in2p = newPointArray(2);
	in3p = newPointArray(2);
	in4p = newPointArray(2);
	textp = newPointArray(2);
	bar1 = newPointArray(2);
	bar2 = newPointArray(2);
	circCent = newPointArray(2);
	interpPoint2(point1, point2bis, in1p[0], in2p[0], 0, hs);
	interpPoint2(lead1, lead2, in1p[1], in2p[1], 0, hs);
	interpPoint2(lead1, lead2, textp[0], textp[1], .1, hs);
	in3p[0] = point1;
	in3p[1] = lead1;
	in4p[0] = interpPoint(lead1, lead2, 1.0 - (16.0 / wtot), 32);
	in4p[1] = interpPoint(lead1, lead2, 1.0 - (16.0 / wtot), 8);
	// in4p[0].x=sim.snapGrid(in4p[0].x);
	// in4p[0].y=sim.snapGrid(in4p[0].y);
	Point tris[] = newPointArray(3);
	interpPoint2(lead1, lead2, tris[0], tris[1], 0, 3 * hs / 2);
	tris[2] = interpPoint(lead1, lead2, (2.0 * ww) / wtot);
	triangle = createPolygon(tris[0], tris[1], tris[2]);
	circCent[0] = interpPoint(lead1, lead2, 1.0 - (circDiam / (2.0 * wtot)), 0);
	circCent[1] = interpPoint(lead1, lead2, 1.0 - (3 * circDiam / 2.0 - circOverlap) / wtot, 0);
	Point d1, d2;
	d1 = interpPoint(in3p[1], in1p[1], 0.3333);
	d2 = interpPoint(in3p[1], in1p[1], 0.6666);
	arrowPoly1 = calcArrow(d1, d2, 8, 4);
	interpPoint2(d1, d2, bar1[0], bar1[1], 1.0, 4);
	d1 = interpPoint(in3p[1], in2p[1], 0.3333);
	d2 = interpPoint(in3p[1], in2p[1], 0.6666);
	arrowPoly2 = calcArrow(d1, d2, 8, 4);
	interpPoint2(d1, d2, bar2[0], bar2[1], 1.0, 4);
	plusFont = new Font("SansSerif", 0, 14);
	setPost(0, in1p[0]);
	setPost(1, in2p[0]);
	setPost(2, in3p[0]);
	setPost(3, in4p[0]);
	setPost(4, point2bis);
    }

    @Override
    public int getDumpType() {
	return 402;
    }

    void getInfo(String arr[]) {
	arr[0] = "OTA (LM13700 style)";
	arr[1] = "Iabc = " + getCurrentText(-getCurrentIntoNode(3));
	arr[2] = "V+ - V- = " + getVoltageText(volts[0] - volts[1]);
    }

    public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo("Positive Supply Voltage (5-20V)", posVolt, 5, 20);
	if (n == 1)
	    return new EditInfo("Negative Supply Voltage (V)", negVolt, -20, -5);
	return null;
    }

    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    posVolt = ei.value;
	if (n == 1)
	    negVolt = ei.value;
	initOTA();
    }

}
