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

class SipoShiftElm extends ChipElm {
	final int DATA_PIN_INDEX = 2; // the initial index of the register pins in the "pins" array
	
	boolean clockstate = false;
	
	public SipoShiftElm(int xx, int yy) {
		super(xx, yy);
	}
	public SipoShiftElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		
		boolean[] data = new boolean[bits];
		readBits(st, data);
		
		for (int i = 0; i < bits; i++)
			pins[DATA_PIN_INDEX + i].value = data[i];
	}
	
	String dump() {
		boolean[] data = new boolean[bits];
		for (int i = 0; i < bits; i++)
			data[i] = pins[DATA_PIN_INDEX + i].value;
		
		return super.dump() + writeBits(data);
	}
	int getDumpType() { return 189; }
	String getChipName() { return "SIPO shift register"; }
	
	boolean needsBits() { return true; }
	int defaultBitCount() { return 8; }
	
	void setupPins() {
		sizeX = bits + 1;
		sizeY = 3;
		pins = new Pin[getPostCount()];
		
		pins[0] = new Pin(1, SIDE_W, "D");
		pins[1] = new Pin(2, SIDE_W, "");
		pins[1].clock = true;
		
		for (int i = 0; i < bits; i++) {
			boolean value = pins[DATA_PIN_INDEX + i] != null ? pins[DATA_PIN_INDEX + i].value : false;
			Pin pin = pins[DATA_PIN_INDEX + i] = new Pin(i + 1, SIDE_N, "Q" + i);
			pin.value = value;
			pin.output = true;
		}
		allocNodes();
	}
	int getPostCount() { return 2 + bits; }
	int getVoltageSourceCount() { return bits; }
	
	void execute() {
		if (pins[1].value != clockstate) {
			clockstate = pins[1].value;
			if (clockstate && bits > 0) {
				for (int i = bits - 2; i >= 0; i--)
					pins[DATA_PIN_INDEX + i + 1].value = pins[DATA_PIN_INDEX + i].value; //Shift
				
				pins[2].value = pins[0].value; //Load
			}
		}
	}
	
	public EditInfo getChipEditInfo(int n) {
		if (n == 0)
			return new EditInfo("# of Bits", bits, 1, 1).setDimensionless();
		return null;
	}
	public void setChipEditValue(int n, EditInfo ei) {
		if (n == 0) {
			if (ei.value != bits && ei.value >= 1) {
				bits = (int)ei.value;
				setupPins();
				setPoints();
			}
			return;
		}
	}
}
