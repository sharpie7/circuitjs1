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

// contributed by Edward Calver

    class DeMultiplexerElm extends ChipElm {
	int selectBitCount;
	int outputCount;
	int qPin;
	boolean hasReset() {return false;}
	public DeMultiplexerElm(int xx, int yy) { super(xx, yy); }
	public DeMultiplexerElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    try {
		selectBitCount = Integer.parseInt(st.nextToken());
		setupPins();
		setPoints();
	    } catch (Exception e) {}
	}
	String getChipName() { return "demultiplexer"; }
	String dump() { return super.dump() + " " + selectBitCount; }

	void setupPins() {
	    if (selectBitCount == 0)
		selectBitCount = 2;
	    outputCount = 1 << selectBitCount;
	    sizeX = 1+selectBitCount;
	    sizeY = 1+outputCount;
	    pins = new Pin[getPostCount()];
	    int i;
	    for (i = 0; i != outputCount; i++) {
		pins[i] = new Pin(i, SIDE_E, "Q" + i);
		pins[i].output=true;
	    }
	    for (i = 0; i != selectBitCount; i++) {
		int ii = i+outputCount;
		pins[ii] = new Pin(i, SIDE_S, "S" + i);
	    }
	    qPin = outputCount+selectBitCount;
	    pins[qPin] = new Pin(0, SIDE_W, "Q");
	}
	int getPostCount() {
	    return qPin+1;
	}
	int getVoltageSourceCount() { return outputCount; }

	void execute() {
	    int val = 0;
	    int i;
	    for (i = 0; i != selectBitCount; i++)
		if (pins[i+outputCount].value)
		    val |= 1<<i;
	    for (i = 0; i != outputCount; i++)
		pins[i].value = false;
	    pins[val].value = pins[qPin].value;
	}

	public EditInfo getEditInfo(int n) {
            if (n < 2)
		return super.getEditInfo(n);
            if (n == 2)
                return new EditInfo("# of Select Bits", selectBitCount).setDimensionless();
            return null;
        }
        public void setEditValue(int n, EditInfo ei) {
            if (n < 2) {
                super.setEditValue(n,  ei);
                return;
            }
            if (n == 2 && ei.value >= 1 && ei.value <= 6) {
                selectBitCount = (int)ei.value;
                setupPins();
                setPoints();
            }
        }

	int getDumpType() { return 185; }

    }
