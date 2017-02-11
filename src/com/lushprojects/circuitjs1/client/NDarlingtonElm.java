package com.lushprojects.circuitjs1.client;

public class NDarlingtonElm extends DarlingtonElm {



    public NDarlingtonElm(int xx, int yy) {
	super(xx, yy, false);
    }

    public NDarlingtonElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	super(xa, ya, xb, yb, f, st);
    }

    Class getDumpClass() {
	return DarlingtonElm.class;
    }
}
