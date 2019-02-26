package com.lushprojects.circuitjs1.client;




public class ComparatorElm extends CompositeElm {
    
    private static String modelString = "OpAmpElm 1 2 3\rAnalogSwitchElm 4 5 3\rGroundElm 5";
    private static int[] modelExternalNodes = {2, 1, 4};
    final int FLAG_SMALL = 2;
    int opsize, opheight, opwidth;
	Point in1p[], in2p[], textp[];
	Polygon triangle;
	Font plusFont;
    
    public ComparatorElm(int xx, int yy) {
	super(xx, yy, modelString, modelExternalNodes);
	noDiagonal = true;
	setSize(sim.smallGridCheckItem.getState() ? 1 : 2);
    }
    

    public ComparatorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	super(xa, ya, xb, yb, f, st, modelString, modelExternalNodes);
	noDiagonal = true;
	setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
    }
    
    public int getDumpType() {
	return 401;
    }
    
	void setSize(int s) {
	    opsize = s;
	    opheight = 8*s;
	    opwidth = 13*s;
	    flags = (flags & ~FLAG_SMALL) | ((s == 1) ? FLAG_SMALL : 0);
	}
	
	public boolean getConnection(int n1, int n2) { return false; }
	
	void draw(Graphics g) {
	    setBbox(point1, point2, opheight*2);
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, in1p[0], in1p[1]);
	    setVoltageColor(g, volts[1]);
	    drawThickLine(g, in2p[0], in2p[1]);
	    g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	    setPowerColor(g, true);
	    drawThickPolygon(g, triangle);
	    g.setFont(plusFont);
	    drawCenteredText(g, "-", textp[0].x, textp[0].y-2, true);
	    drawCenteredText(g, "+", textp[1].x, textp[1].y  , true);
	    drawCenteredText(g, "\u2265?", textp[2].x, textp[2].y  , true);
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
	    if (ww > dn/2)
		ww = (int) (dn/2);
	    calcLeads(ww*2);
	    int hs = opheight*dsign;
//	    if ((flags & FLAG_SWAP) != 0)
//		hs = -hs;
	    in1p = newPointArray(2);
	    in2p = newPointArray(2);
	    textp = newPointArray(3);
	    interpPoint2(point1, point2, in1p[0],  in2p[0], 0, hs);
	    interpPoint2(lead1 , lead2,  in1p[1],  in2p[1], 0, hs);
	    interpPoint2(lead1 , lead2,  textp[0], textp[1], .2, hs);
	    interpPoint(lead1, lead2, textp[2], 0.5, 0);
	    Point tris[] = newPointArray(2);
	    interpPoint2(lead1,  lead2,  tris[0], tris[1],  0, hs*2);
	    triangle = createPolygon(tris[0], tris[1], lead2);
	    plusFont = new Font("SansSerif", 0, opsize == 2 ? 14 : 10);
		setPost(0, in1p[0]);
		setPost(1,in2p[0]);
		setPost(2,point2);
	}
	
	
    
    void getInfo(String arr[]) {
	 arr[0] = "Comparator";
	    arr[1] = "V+ = " + getVoltageText(volts[1]);
	    arr[2] = "V- = " + getVoltageText(volts[0]);
    }
}
