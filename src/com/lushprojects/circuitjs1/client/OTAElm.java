package com.lushprojects.circuitjs1.client;

public class OTAElm extends CompositeElm {

    private static String modelString = "RailElm 4\rRailElm 10\rNTransistorElm 1 2 3\rNTransistorElm 3 1 4\rNTransistorElm 3 3 4\rNTransistorElm 5 6 2\rNTransistorElm 7 8 2\rPTransistorElm 9 6 10\rPTransistorElm 9 9 10\rPTransistorElm 6 12 9\rPTransistorElm 11 8 10\rPTransistorElm 11 11 10\rPTransistorElm 8 13 11\rNTransistorElm 14 14 4\rNTransistorElm 14 12 4\rNTransistorElm 12 13 14\rNTransistorElm 15 15 5\rNTransistorElm 15 15 7";
    private static int[] modelExternalNodes = { 7, 5, 15, 1, 13 };

//    private static String modelString="NTransistorElm 1 1 2\rNTransistorElm 1 2 3\rNTransistorElm 1 3 4\rNTransistorElm 1 4 5";
//    private static int[] modelExternalNodes = { 1, 2, 3 };
    final int FLAG_SMALL = 2;
    int opsize, opheight, opwidth;
    Point in1p[], in2p[], in3p[], in4p[],textp[];
    Polygon triangle;
    Font plusFont;

    public OTAElm(int xx, int yy) {
	super(xx, yy, modelString, modelExternalNodes);
	noDiagonal = true;
	setSize(sim.smallGridCheckItem.getState() ? 1 : 2);
	initOTA();
    }

    public OTAElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	super(xa, ya, xb, yb, f, st, modelString, modelExternalNodes);
	noDiagonal = true;
	setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
	initOTA();
    }



    private void initOTA() {
	((RailElm)compElmList.get(0)).maxVoltage = -9;
	((RailElm)compElmList.get(1)).maxVoltage = 9;
    }
    
 
    
    void setSize(int s) {
	opsize = s;
	opheight = 8 * s;
	opwidth = 13 * s;
	flags = (flags & ~FLAG_SMALL) | ((s == 1) ? FLAG_SMALL : 0);
    }

    public boolean getConnection(int n1, int n2) {
	return false;
    }

    void draw(Graphics g) {
	setBbox(point1, point2, opheight * 2);
	setVoltageColor(g, volts[0]);
	drawThickLine(g, in1p[0], in1p[1]);
	setVoltageColor(g, volts[1]);
	drawThickLine(g, in2p[0], in2p[1]);
	g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	setPowerColor(g, true);
	drawThickPolygon(g, triangle);
	g.setFont(plusFont);
	drawCenteredText(g, "-", textp[0].x, textp[0].y - 2, true);
	drawCenteredText(g, "+", textp[1].x, textp[1].y, true);
	drawCenteredText(g, "OTA", textp[2].x, textp[2].y, true);
	setVoltageColor(g, volts[2]);
	drawThickLine(g, lead2, point2);
	curcount = updateDotCount(-getCurrentIntoNode(2), curcount);
	drawDots(g, point2, lead2, curcount);
	drawPosts(g);
    }

    void setPoints() {
	super.setPoints();
	if (dn > 150 && this == sim.dragElm)
	    setSize(2);
	int ww = opwidth;
	if (ww > dn / 2)
	    ww = (int) (dn / 2);
	calcLeads(ww * 2);
	int hs = opheight * dsign;
	// if ((flags & FLAG_SWAP) != 0)
	// hs = -hs;
	in1p = newPointArray(2);
	in2p = newPointArray(2);
	in3p = newPointArray(2);
	in4p = newPointArray(2);
	textp = newPointArray(3);
	interpPoint2(point1, point2, in1p[0], in2p[0], 0, hs);
	interpPoint2(lead1, lead2, in1p[1], in2p[1], 0, hs);
	interpPoint2(lead1, lead2, textp[0], textp[1], .2, hs);
	interpPoint(lead1, lead2, textp[2], 0.5, 0);
	interpPoint(lead1, lead2, in3p[0], 0, 0);
	interpPoint(lead1, lead2, in4p[0], 0.5, hs);
	in3p[0].x=sim.snapGrid(in3p[0].x);
	in3p[0].y=sim.snapGrid(in3p[0].y);
	in4p[0].x=sim.snapGrid(in4p[0].x);
	in4p[0].y=sim.snapGrid(in4p[0].y);
	Point tris[] = newPointArray(2);
	interpPoint2(lead1, lead2, tris[0], tris[1], 0, hs * 2);
	triangle = createPolygon(tris[0], tris[1], lead2);
	plusFont = new Font("SansSerif", 0, opsize == 2 ? 14 : 10);
	setPost(0, in1p[0]);
	setPost(1, in2p[0]);
	setPost(2, in3p[0]);
	setPost(3, in4p[0]);
	setPost(4, point2);
    }

    @Override
    public int getDumpType() {
	return 402;
    }
    
    void getInfo(String arr[]) {
	arr[0] = "OTA"; // TODO
    }

}
