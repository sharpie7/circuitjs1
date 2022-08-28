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

import com.lushprojects.circuitjs1.client.util.Locale;

class Counter2Elm extends ChipElm {
	int modulus;

	public Counter2Elm(int xx, int yy) {
	    super(xx, yy);
	}

	public Counter2Elm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    try {
		modulus = Integer.parseInt(st.nextToken());
	    } catch (Exception e) {}
	}

	String dump() {
	    return super.dump() + " " + modulus;
	}

	boolean needsBits() { return true; }
	String getChipName() {
	    if (modulus == 0)
		return "Counter";
	    return Locale.LS("Counter") + Locale.LS(" (mod ") + modulus + ")";
	}
	
	int clk, clr, enp, ent, rco, load;
	
	void setupPins() {
	    sizeX = 2;
	    sizeY = bits+3;
	    pins = new Pin[getPostCount()];
	    int i;
	    for (i = 0; i != bits; i++) {
		pins[i] = new Pin(i+1, SIDE_E, "Q" + (bits-i-1));
		pins[i].output = pins[i].state = true;
	    }
	    for (i = 0; i != bits; i++) {
		int ii = i+bits;
		pins[ii] = new Pin(i+1, SIDE_W, "I" + (bits-i-1));
	    }
	    int p = bits*2;
	    clk = p;
	    clr = p+1;
	    enp = p+2;
	    rco = p+3;
	    load = p+4;
	    ent = p+5;
	    pins[clk] = new Pin(0, SIDE_W, "");
	    pins[clk].clock = true;
	    pins[clr] = new Pin(bits+1, SIDE_W, "CLR");
	    pins[clr].bubble = true;
	    pins[enp] = new Pin(bits+2, SIDE_W, "EnP");
	    pins[rco] = new Pin(0, SIDE_E, "RCO");
	    pins[rco].output = true;
	    pins[load] = new Pin(bits+1, SIDE_E, "LOAD");
	    pins[load].bubble = true;
	    pins[ent] = new Pin(bits+2, SIDE_E, "EnT");
	}
	int getPostCount() {
	    return bits*2+6;
	}
	public EditInfo getChipEditInfo(int n) {
            if (n == 0)
                return new EditInfo("# of Bits", bits, 1, 1).setDimensionless();
            if (n == 1)
                return new EditInfo("Modulus", modulus, 1, 1).setDimensionless();
	    return null;
	}
	public void setChipEditValue(int n, EditInfo ei) {
	    if (n == 0 && ei.value >= 2) {
		bits = (int)ei.value;
		setupPins();
		setPoints();
		allocNodes();
	    }
	    if (n == 1)
		modulus = (int)ei.value;
	}
	int getVoltageSourceCount() { return bits+1; }
	
	boolean carry;
	
	void execute() {
	    if (pins[clk].value && !lastClock) {
		if (pins[enp].value && pins[ent].value) {
		    int i;
		    int value = 0;

		    // get current value
		    int lastBit = bits-1;
		    for (i = 0; i != bits; i++)
			if (pins[lastBit-i].value)
			    value |= 1<<i;

		    // update value
		    value++;
		    int realmod = (modulus == 0) ? (1<<bits) : modulus;
		    value %= realmod;

		    // convert value to binary
		    for (i = 0; i != bits; i++)
			writeOutput(lastBit-i, (value & (1<<i)) != 0);

		    carry = (value == realmod-1);
		}
		
		if (!pins[load].value) {
		    int i;
		    for (i = 0; i != bits; i++)
			writeOutput(i, pins[i+bits].value);
		}
	    }
	    if (!pins[clr].value) {
		int i;
		for (i = 0; i != bits; i++)
		    writeOutput(i, false);
		carry = false;
	    }
	    
	    lastClock = pins[clk].value;
	    writeOutput(rco, carry && pins[ent].value);
	}
	int getDumpType() { return 421; }
    }
