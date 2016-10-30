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

    class CounterElm extends ChipElm {
	final int FLAG_ENABLE = 2;
	boolean invertreset=false;
	public CounterElm(int xx, int yy) { super(xx, yy); }
	public CounterElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	if(st.hasMoreTokens())invertreset=new Boolean(st.nextToken()).booleanValue();
	else invertreset=true;
	pins[1].bubble = invertreset;
	}

	String dump() {
	return super.dump()+" "+invertreset;
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
	    if (hasEnable())
		pins[bits+2] = new Pin(sizeY-2, SIDE_W, "En");
	    allocNodes();
	}
	int getPostCount() {
	    if (hasEnable())
		return bits+3;
	    return bits+2;
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
		if (ei.checkbox.getState())
		    {		    
		    invertreset=true;
	            pins[1].bubble = true;
		    }
		else
		    {
		    invertreset=false;
	            pins[1].bubble = false;
		    }
		setPoints();
	    }
	}
	boolean hasEnable() { return (flags & FLAG_ENABLE) != 0; }
	int getVoltageSourceCount() { return bits; }
	void execute() {
	    boolean en = true;
	    if (hasEnable())
		en = pins[bits+2].value;
	    if (pins[0].value && !lastClock && en) {
		int i;
		for (i = bits-1; i >= 0; i--) {
		    int ii = i+2;
		    if (!pins[ii].value) {
			pins[ii].value = true;
			break;
		    }
		    pins[ii].value = false;
		}
	    }
	    if (!pins[1].value==invertreset) {
		int i;
		for (i = 0; i != bits; i++)
		    pins[i+2].value = false;
	    }
	    lastClock = pins[0].value;
	}
	int getDumpType() { return 164; }
    }
