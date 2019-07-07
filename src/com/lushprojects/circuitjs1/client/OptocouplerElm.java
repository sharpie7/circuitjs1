package com.lushprojects.circuitjs1.client;

public class OptocouplerElm extends CompositeElm {
    int csize, cspc, cspc2;
    int rectPointsX[], rectPointsY[];
    double curCounts[];

    private static String modelString = "DiodeElm 6 1\rCCCSElm 1 2 3 4\rNTransistorElm 3 4 5";
    private static int[] modelExternalNodes = { 6, 2, 4, 5 };

    DiodeElm diode;
    TransistorElm transistor;

    public OptocouplerElm(int xx, int yy) {
	super(xx, yy, modelString, modelExternalNodes);
	noDiagonal = true;
	initOptocoupler();
    }

    public OptocouplerElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	// pass st=null since we don't need to undump any of the sub-elements
	super(xa, ya, xb, yb, f, null, modelString, modelExternalNodes);
	noDiagonal = true;
	initOptocoupler();
    }

    public String dump() {
	return dumpWithMask(0);
    }
    
    private void initOptocoupler() {
	csize = 2;
	cspc = 8*2;
	cspc2 = cspc*2;
	diode = (DiodeElm) compElmList.get(0);
	CCCSElm cccs = (CCCSElm) compElmList.get(1);
	
	// from http://www.cel.com/pdf/appnotes/an3017.pdf
	cccs.setExpr("max(0,min(.0001, select(i-.003, (-80000000000*(i)^5+800000000*(i)^4-3000000*(i)^3+5177.2*(i)^2+.2453*(i)-.00005)*1.04/700, (9000000*(i)^5-998113*(i)^4+42174*(i)^3-861.32*(i)^2+9.0836*(i)-.0078)*.945/700)))");
	
	transistor = (TransistorElm) compElmList.get(2);
	transistor.setBeta(700);
	curCounts = new double[4];
    }

    public void reset() {
	super.reset();
	curCounts = new double[4];
    }

    public boolean getConnection(int n1, int n2) {
	return n1/2 == n2/2;
    }

    void draw(Graphics g) {
        g.setColor(needsHighlight() ? selectColor : lightGrayColor);
        drawThickPolygon(g, rectPointsX, rectPointsY, 4);
	
        // draw stubs
        int i;
        for (i = 0; i != 4; i++) {
            setVoltageColor(g, volts[i]);
            Point a = posts[i];
            Point b = stubs[i];
            drawThickLine(g, a, b);
            curCounts[i] = updateDotCount(-getCurrentIntoNode(i), curCounts[i]);
            drawDots(g, a, b, curCounts[i]);
        }
        
        diode.draw(g);
        transistor.draw(g);
        
        drawPosts(g);

        // draw little arrows
        g.setColor(lightGrayColor);
        int sx = stubs[0].x+2;
        int sy = (stubs[0].y+stubs[1].y)/2;
        for (i = 0; i != 2; i++) {
            int y = sy+i*10-5;
            Point p1 = new Point(sx,    y);
            Point p2 = new Point(sx+20, y);
            Polygon p = calcArrow(p1, p2, 5, 2);
            g.fillPolygon(p);
            g.drawLine(sx+10, y, sx+15, y);
        }
    }

    Point stubs[];
    
    void setPoints() {
	super.setPoints();
	
	// adapted from ChipElm
        int hs = cspc;
        int x0 = x+cspc2; int y0 = y;
        int xr = x0-cspc;
        int yr = y0-cspc/2;
        int sizeX = 2;
        int sizeY = 2;
        int xs = sizeX*cspc2;
        int ys = sizeY*cspc2-cspc;
        rectPointsX = new int[] { xr, xr+xs, xr+xs, xr };
        rectPointsY = new int[] { yr, yr, yr+ys, yr+ys };
        setBbox(xr, yr, rectPointsX[2], rectPointsY[2]);
        stubs = new Point[4];
//        setPin(0, x0, y0, 1, 0, 0, -1, 0, 0);
//        setPin(1, x0, y0, 1, 0, 0,  1, 0, ys-cspc2);
//        setPin(2, x0, y0, 1, 0, 0, -1, 0, 0);
//        setPin(3, x0, y0, 1, 0, 0,  1, 0, ys-cspc2);
        setPin(0, x0, y0, 0, 1, -1, 0, 0, 0);
        setPin(1, x0, y0, 0, 1, -1, 0, 0, 0);
        setPin(2, x0, y0, 0, 1, 1, 0, xs-cspc2, 0);
        setPin(3, x0, y0, 0, 1, 1, 0, xs-cspc2, 0);
        diode.setPosition(posts[0].x+32, posts[0].y, posts[1].x+32, posts[1].y);
        stubs[0] = diode.getPost(0);
        stubs[1] = diode.getPost(1);
        
        int midp = (posts[2].y+posts[3].y)/2;
        transistor.setPosition(posts[2].x-40, midp, posts[2].x-24, midp);
        stubs[2] = transistor.getPost(1);
        stubs[3] = transistor.getPost(2);
    }

    void setPin(int n, int px, int py, int dx, int dy, int dax, int day, int sx, int sy) {
	int pos = n % 2; 
//		(n < 2) ? 0 : 1;
        int xa = px+cspc2*dx*pos+sx;
        int ya = py+cspc2*dy*pos+sy;
        setPost(n, new Point(xa+dax*cspc2, ya+day*cspc2));
        stubs[n] = new Point(xa+dax*cspc , ya+day*cspc );
    }
    
    @Override
    public int getDumpType() {
	return 407;
    }

    void getInfo(String arr[]) {
	arr[0] = "optocoupler";
	arr[1] = "Iin = " + getCurrentText(getCurrentIntoNode(0));
	arr[2] = "Iout = " + getCurrentText(getCurrentIntoNode(2));
    }
}
