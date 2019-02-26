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

    class CounterElm extends ChipElm {
	boolean invertreset;
	int modulus;
	final int FLAG_UP_DOWN = 4;

	public CounterElm(int xx, int yy) {
	    super(xx, yy);
	}

	public CounterElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    invertreset = true;
	    try {
	   	invertreset = Boolean.parseBoolean(st.nextToken());
		modulus = Integer.parseInt(st.nextToken());
	    } catch (Exception e) {}
	    pins[1].bubble = invertreset;
	}

	String dump() {
	    return super.dump() + " " + invertreset + " " + modulus;
	}

	boolean needsBits() { return true; }
	String getChipName() { return "Counter"; }
	void setupPins() {
	    sizeX = 2;
	    sizeY = bits > 2 ? bits : 2;
	    pins = new Pin[getPostCount()];
	    pins[0] = new Pin(0, SIDE_W, "");
	    pins[0].clock = true;
	    pins[1] = new Pin(sizeY-1, SIDE_W, "R");
	    pins[1].bubble = invertreset;
	    int i;
	    for (i = 0; i != bits; i++) {
		int ii = i+2;
		pins[ii] = new Pin(i, SIDE_E, "Q" + (bits-i-1));
		pins[ii].output = pins[ii].state = true;
	    }
	    if (hasUpDown())
	        pins[bits+2] = new Pin(sizeY-2, SIDE_W, "U/D");
	    allocNodes();
	}
	int getPostCount() {
	    return (hasUpDown()) ? bits+3 : bits+2;
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Flip X", (flags & FLAG_FLIP_X) != 0);
		return ei;
	    }
	    if (n == 1) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Flip Y", (flags & FLAG_FLIP_Y) != 0);
		return ei;
	    }
    	    if (n == 2) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Invert reset pin",invertreset);
		return ei;
	    }
            if (n == 3)
                return new EditInfo("# of Bits", bits, 1, 1).setDimensionless();
            if (n == 4)
                return new EditInfo("Modulus", modulus, 1, 1).setDimensionless();
    	    if (n == 5) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Up/Down Pin", hasUpDown());
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0) {
		if (ei.checkbox.getState())
		    flags |= FLAG_FLIP_X;
		else
		    flags &= ~FLAG_FLIP_X;
		setPoints();
	    }
	    if (n == 1) {
		if (ei.checkbox.getState())
		    flags |= FLAG_FLIP_Y;
		else
		    flags &= ~FLAG_FLIP_Y;
		setPoints();
	    }
	    if (n == 2) {
		invertreset = ei.checkbox.getState();
		setupPins();
		setPoints();
	    }
	    if (n == 3 && ei.value >= 3) {
		bits = (int)ei.value;
		setupPins();
		setPoints();
	    }
	    if (n == 4)
		modulus = (int)ei.value;
	    if (n == 5) {
		flags = ei.changeFlag(flags, FLAG_UP_DOWN);
		setupPins();
		setPoints();
	    }
	}
	boolean hasUpDown() { return (flags & FLAG_UP_DOWN) != 0; }
	int getVoltageSourceCount() { return bits; }
	void execute() {
	    if (pins[0].value && !lastClock) {
		int i;
		int value = 0;
		
		// get direction
		int dir = 1;
		if (hasUpDown() && pins[bits+2].value)
		    dir = -1;
		
		// get current value
		int lastBit = 2+bits-1;
		for (i = 0; i != bits; i++)
		    if (pins[lastBit-i].value)
			value |= 1<<i;
		
		// update value
		value += dir;
		if (modulus != 0)
		   value = (value+modulus) % modulus;
		
		// convert value to binary
		for (i = 0; i != bits; i++)
		    pins[lastBit-i].value = (value & (1<<i)) != 0;
	    }
	    if (!pins[1].value == invertreset) {
		int i;
		for (i = 0; i != bits; i++)
		    pins[i+2].value = false;
	    }
	    lastClock = pins[0].value;
	}
	int getDumpType() { return 164; }
    }
