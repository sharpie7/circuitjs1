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

    class FullAdderElm extends ChipElm {
	public FullAdderElm(int xx, int yy) {
	    super(xx, yy);
	    flags |= FLAG_BITS;
	    bits = 4;
	    setupPins();
	}
	public FullAdderElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    if (!needsBits())
		bits = 1;
	    setupPins();
	}
	static final int FLAG_BITS = 2;
	
	String getChipName() { return "Adder"; }
	int carryIn, carryOut;

	void setupPins() {
	    sizeX=2;
	    sizeY=bits*2+1;
	    pins=new Pin[getPostCount()];

	    int i;
	    for (i = 0; i != bits; i++) {
		pins[i       ] = new Pin(bits-1-i, SIDE_W, "A" + i);
		pins[i+bits  ] = new Pin(bits-1-i+bits, SIDE_W, "B" + i);
		pins[i+bits*2] = new Pin(bits-1-i+2, SIDE_E, "S" + i);
		pins[i+bits*2].output=true;
	    }
	    carryIn = bits*3;
	    carryOut = bits*3+1;
	    pins[carryOut] = new Pin(0, SIDE_E, "C");
	    pins[carryOut].output=true;
	    pins[carryIn] = new Pin(bits*2, SIDE_W, "Cin");
	    allocNodes();
	}
	int getPostCount() {
	    return bits*3+2;
	}
	int getVoltageSourceCount() { return bits+1; }

	void execute() {
	    int i;
	    int c = pins[carryIn].value ? 1 : 0;
	    for (i = 0; i != bits; i++) {
		int v = (pins[i].value ? 1 : 0) + (pins[i+bits].value ? 1 : 0) + c;
		c = (v > 1) ? 1 : 0;
		writeOutput(i+bits*2, ((v & 1) == 1));
	    }
	    writeOutput(carryOut, (c == 1));
	}
	int getDumpType() { return 196; }
	boolean needsBits() { return (flags & FLAG_BITS) != 0; }

        public EditInfo getEditInfo(int n) {
            if (n == 2)
                return new EditInfo("# of Bits", bits, 1, 1).setDimensionless();
            return super.getEditInfo(n);
        }
        public void setEditValue(int n, EditInfo ei) {
            if (n == 2 && ei.value >= 2) {
                bits = (int)ei.value;
                flags |= FLAG_BITS;
                setupPins();
                setPoints();
                allocNodes();
                return;
            }
            super.setEditValue(n, ei);
        }

    }
