package com.lushprojects.circuitjs1.client;

// Test element to evaluate if constructing compound elements from individual transistors is feasible

// Not intended for public use/visibility without further work
// Iain Sharp, Feb 2017

import java.util.Vector;

public class DarlingtonElm extends CompositeElm {

//    private Vector<TransistorElm> transistorList;
    private Polygon rectPoly;
    private Point rect[], coll[], emit[], base;
    private static String modelString = "NTransistorElm 0 1 3\rNTransistorElm 3 1 2";
    private static int[] modelExternalNodes = {0, 1,2};

    DarlingtonElm(int xx, int yy, boolean pnpflag) {
	super(xx, yy, modelString, modelExternalNodes);
//	transistorList = new Vector<TransistorElm>();
//	createTransistors(pnpflag);
	noDiagonal = true;
	
    }

    public DarlingtonElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	super(xa, ya, xb, yb, f, st, modelString, modelExternalNodes);
	int pnp = new Integer(st.nextToken()).intValue();
//	CirSim.console("Creating xa="+xa+" ya="+ya+" st="+st.toString());
//	transistorList = new Vector<TransistorElm>();
//	
//	createTransistors(pnp == -1);
	noDiagonal = true;
//	CirSim.console("leaving");
    }

//    private void createTransistors(boolean pnpflag) {
//	TransistorElm t1;
//	//t1=new TransistorElm(0,0,pnpflag);
//	CirSim.console("made t1");
//	//transistorList.add(t1);
//	//CirSim.console("added t1");
//	for (int i = 0; i < 2; i++)
//	    transistorList.add(new TransistorElm(0, 0, pnpflag));
//
//    }
//
//    boolean nonLinear() {
//	return true;
//    }

//    void reset() {
//	for (int i = 0; i < transistorList.size(); i++)
//	    transistorList.get(i).reset();
//    }

    int getDumpType() {
	return 400;
    }

    String dump() {
	// TODO store and restore voltages
	return super.dump() + " " + 1 + " " + (volts[0]-volts[1]) + " " +
		(volts[0]-volts[2]);
    }

    void draw(Graphics g) {
	setBbox(point1, point2, 16);
	setPowerColor(g, true);
	// draw collector
	setVoltageColor(g, volts[1]);
	drawThickLine(g, coll[0], coll[1]);
	// draw emitter
	setVoltageColor(g, volts[2]);
	drawThickLine(g, emit[0], emit[1]);
	// draw base
	setVoltageColor(g, volts[0]);
	if (sim.powerCheckItem.getState())
	    g.setColor(Color.gray);
	drawThickLine(g, point1, base);
	int ds = sign(dx);
	g.drawString("B", base.x - 10 * ds, base.y - 5);
	g.drawString("C", coll[0].x - 3 + 9 * ds, coll[0].y + 4); // x+6 if
								  // ds=1, -12
								  // if -1
	g.drawString("E", emit[0].x - 3 + 9 * ds, emit[0].y + 4);

	if ((needsHighlight() || sim.dragElm == this) && dy == 0) {
	    g.setColor(Color.white);

	}
	drawPosts(g);
    }

    int getPostCount() {
	return 3;
    }

    int getInternalNodeCount() {
	return 1;
    }

//    Point getPost(int n) {
//	return (n == 0) ? point1 : (n == 1) ? coll[0] : emit[0];
//    }

//    double getPower() {
//	// TODO
//	return 0;
//    }
//
//    void stamp() {
//	for (int i = 0; i < transistorList.size(); i++)
//	    transistorList.get(i).stamp();
//    }
//
//    void doStep() {
//	for (int i = 0; i < transistorList.size(); i++)
//	    transistorList.get(i).doStep();
//    }
//
//    void stepFinished() {
//	for (int i = 0; i < transistorList.size(); i++)
//	    transistorList.get(i).stepFinished();
//    }

    void getInfo(String arr[]) {
	// TODO
	arr[0] = sim.LS("darlington");
    }

    void setPoints() {
	CirSim.console("settingpoints");
	super.setPoints();
	int hs = 16;
	int hs2 = hs * dsign;
	// calc collector, emitter posts
	coll = newPointArray(2);
	emit = newPointArray(2);
	interpPoint2(point1, point2, coll[0], emit[0], 1, hs2);
	// calc rectangle edges
	rect = newPointArray(4);
	interpPoint2(point1, point2, rect[0], rect[1], 1 - 16 / dn, hs);
	interpPoint2(point1, point2, rect[2], rect[3], 1 - 13 / dn, hs);
	// calc points where collector/emitter leads contact rectangle
	interpPoint2(point1, point2, coll[1], emit[1], 1 - 13 / dn, 6 * dsign);
	// calc point where base lead contacts rectangle
	base = new Point();
	interpPoint(point1, point2, base, 1 - 16 / dn);
	// rectangle
	rectPoly = createPolygon(rect[0], rect[2], rect[3], rect[1]);
	setPost(0, point1);
	setPost(1,coll[0]);
	setPost(2,emit[0]);

    }

//    void setNode(int p, int n) {
//	CirSim.console("settingnode");
//	// nodes[p] = n
//	super.setNode(p, n);
//	if (p == 0) // base
//	    transistorList.get(0).setNode(0, n);
//	if (p == 1) { // collector
//	    transistorList.get(0).setNode(1, n);
//	    transistorList.get(1).setNode(1, n);
//	}
//	if (p == 2) // emitter
//	    transistorList.get(1).setNode(2, n);
//	if (p == 3) { // internal
//	    transistorList.get(0).setNode(2, n);
//	    transistorList.get(1).setNode(0, n);
//	}
//    }
//
//    void setNodeVoltage(int n, double c) {
//	// volts[n] = c;
//	super.setNodeVoltage(n, c);
//	if (n == 0) // base
//	    transistorList.get(0).setNodeVoltage(0, c);
//	if (n == 1) { // collector
//	    transistorList.get(0).setNodeVoltage(1, c);
//	    transistorList.get(1).setNodeVoltage(1, c);
//	}
//	if (n == 2) // emitter
//	    transistorList.get(1).setNodeVoltage(2, c);
//	if (n == 3) { // internal
//	    transistorList.get(0).setNodeVoltage(2, c);
//	    transistorList.get(1).setNodeVoltage(0, c);
//	}
//
//    }
//
//    boolean canViewInScope() {
//	return false;
//    }
//    
//void delete() {
//    // TODO
//}
    
    

    double getCurrentIntoPoint(int xa, int ya) {
	// TODO tidy this up
	TransistorElm t0, t1;
	t0 = (TransistorElm)compElmList.get(0);
	t1 = (TransistorElm)compElmList.get(1);
	if (xa == x && ya == y)
	    return -t0.ib;
	if (xa == coll[0].x && ya == coll[0].y)
	    return -t0.ic - t1.ic;
	return -t1.ie;
    }
    

}
