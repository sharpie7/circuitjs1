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

    class DecadeElm extends ChipElm {
	public DecadeElm(int xx, int yy) { super(xx, yy); }
	public DecadeElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	}
	String getChipName() { return "decade counter"; }
	boolean needsBits() { return true; }
	void setupPins() {
	    sizeX = bits > 2 ? bits : 2;
	    sizeY = 2;
	    pins = new Pin[getPostCount()];
	    pins[0] = new Pin(1, SIDE_W, "");
	    pins[0].clock = true;
	    pins[1] = new Pin(sizeX-1, SIDE_S, "R");
	    pins[1].bubble = true;
	    int i;
	    for (i = 0; i != bits; i++) {
		int ii = i+2;
		pins[ii] = new Pin(i, SIDE_N, "Q" + i);
		pins[ii].output = pins[ii].state = true;
	    }
	    allocNodes();
	}
	int getPostCount() { return bits+2; }
	int getVoltageSourceCount() { return bits; }
	void execute() {
	    int i;
	    if (pins[0].value && !lastClock) {
		for (i = 0; i != bits; i++)
		    if (pins[i+2].value)
			break;
		if (i < bits)
		    pins[i++ +2].value = false;
		i %= bits;
		pins[i+2].value = true;
	    }
	    if (!pins[1].value) {
		for (i = 1; i != bits; i++)
		    pins[i+2].value = false;
		pins[2].value = true;
	    }
	    lastClock = pins[0].value;
	}
	int getDumpType() { return 163; }
    }
