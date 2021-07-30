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

class PisoShiftElm extends ChipElm {
	final int FLAG_NEW_BEHAVIOR = 2; //SER and no extra output register
	
	boolean[] data = new boolean[0];
	int dataIndex = 0;
	boolean clockState = false;
	boolean loadState = false;
	int dataPinIndex; // the register pins' starting index
	
	public PisoShiftElm(int xx, int yy) {
		super(xx, yy);
		data = new boolean[bits];
		flags |= FLAG_NEW_BEHAVIOR;
		setupPins();
	}
	public PisoShiftElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		data = new boolean[bits];
		readBits(st, data);
		setupPins();
	}
	
	String dump() {
		//Normalize the circular array before exporting
		boolean[] newData = new boolean[data.length];
		for (int i = 0; i < data.length; i++)
			newData[i] = data[(i + dataIndex) % data.length];
		dataIndex = 0;
		data = newData;
		
		return super.dump() + writeBits(data);
	}
	int getDumpType() { return 186; }
	String getChipName() { return "PISO shift register"; }
	
	boolean needsBits() { return true; }
	int defaultBitCount() { return 8; }
	
	boolean hasNewBhvr() { return (flags & FLAG_NEW_BEHAVIOR) != 0; }
	
	void reset() {
		super.reset();
		data = new boolean[bits];
	}

	void setupPins() {
		sizeX = bits + 2;
		sizeY = 3;
		pins = new Pin[getPostCount()];
		
		pins[0] = new Pin(1, SIDE_W, "LD");
		pins[1] = new Pin(2, SIDE_W, "");
		pins[1].clock = true;
		
		pins[2] = new Pin(1, SIDE_E, "Q" + (hasNewBhvr() ?  bits-1 : bits));
		pins[2].output = true;
		
		if (hasNewBhvr()) {
			pins[3] = new Pin(0, SIDE_W, "SER");
			if (data != null && data.length > 0)
				pins[2].value = data[0];
			dataPinIndex = 4;
		} else {
			dataPinIndex = 3;
		}
		
		for (int i = 0; i < bits; i++)
			pins[dataPinIndex + i] = new Pin(bits - i, SIDE_N, "D" + (bits - (i + 1)));
		
		allocNodes();
	}
	int getPostCount() { return (hasNewBhvr() ? 4 : 3) + bits; }
	int getVoltageSourceCount() { return 1; }
	
	void execute() {
		//LOAD raised
		if (pins[0].value != loadState) {
			loadState = pins[0].value;
			if (loadState && data.length > 0) {
				if (hasNewBhvr()) {
					pins[2].value = pins[dataPinIndex].value; //Set output immediately
					dataIndex = 0;
				} else {
					dataIndex = -1;
				}
				for (int i = 0; i < data.length; i++)
					data[i] = pins[dataPinIndex + i].value;
			}
		}
		
		//CLK raised: Rotate the circular array
		if (pins[1].value != clockState) {
			clockState = pins[1].value;
			if (clockState) {
				//Shift
				if (dataIndex >= 0)
					data[dataIndex] = hasNewBhvr() && pins[3].value;
				dataIndex++;
				if (dataIndex >= data.length)
					dataIndex = 0;
				
				//Write
				pins[2].value = data[dataIndex];
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
				data = new boolean[bits];
				setupPins();
				setPoints();
			}
			return;
		}
	}
}
