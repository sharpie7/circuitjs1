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
	boolean[] data = new boolean[0];
	int dataIndex = 0;
	boolean clockstate = false;
	boolean loadstate = false;
	
	public PisoShiftElm(int xx, int yy) {
		super(xx, yy);
		data = new boolean[bits];
	}
	public PisoShiftElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		data = new boolean[bits];
		
		int integer = 0;
		int bitIndex = Integer.MAX_VALUE;
		for (int i = 0; i < bits; i++) {
			if (bitIndex >= Integer.SIZE)
				if (st.hasMoreTokens()) {
					integer = Integer.parseInt(st.nextToken()); //Load next integer
					bitIndex = 0;
				} else
					break; //Data is absent
			
			data[i] = (integer & (1 << bitIndex)) != 0;
			bitIndex++;
		}
	}
	
	String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.dump());
		{
			int integer = 0;
			int bitIndex = 0;
			for (int i = 0; i < data.length; i++) {
				if (bitIndex >= Integer.SIZE) {
					//Flush completed integer
					sb.append(' ');
					sb.append(integer);
					integer = 0;
					bitIndex = 0;
				}
				if (i + dataIndex < data.length && data[i + dataIndex])
					integer |= 1 << bitIndex;
				bitIndex++;
			}
			if (bitIndex > 0) {
				sb.append(' ');
				sb.append(integer);
			}
		}
		return sb.toString();
	}
	
	int getDumpType() { return 186; }
	String getChipName() { return "PISO shift register"; }
	
	boolean needsBits() { return true; }
	int defaultBitCount() { return 8; }
	
	void setupPins() {
		sizeX = bits + 2;
		sizeY = 3;
		pins = new Pin[getPostCount()];
		
		pins[0] = new Pin(1, SIDE_W, "LD");
		pins[1] = new Pin(2, SIDE_W, "");
		pins[1].clock = true;
		
		pins[2] = new Pin(1, SIDE_E, "Q" + bits);
		pins[2].output = true;
		
		for (int i = 0; i < bits; i++)
			pins[3 + i] = new Pin(bits - i, SIDE_N, "D" + i);
		
		allocNodes();
	}
	int getPostCount() { return 3 + bits; }
	int getVoltageSourceCount() { return 1; }
	
	void execute() {
		//LOAD raised
		if (pins[0].value != loadstate) {
			loadstate = pins[0].value;
			if (loadstate && data.length > 0) {
				dataIndex = 0;
				for (int i = 0; i < data.length; i++)
					data[i] = pins[3 + i].value;
			}
		}
		
		//CLK raised: Rotate the circular array
		if (pins[1].value != clockstate) {
			clockstate = pins[1].value;
			if (clockstate) {
				if (dataIndex < data.length)
					pins[2].value = data[dataIndex++]; //Write then shift
				else
					pins[2].value = false; //Out of data
			}
		}
	}
	
	public EditInfo getEditInfo(int n) {
		if (n < 2)
			return super.getEditInfo(n);
		if (n == 2)
			return new EditInfo("# of Bits", bits, 1, 1).setDimensionless();
		return null;
	}
	public void setEditValue(int n, EditInfo ei) {
		if (n < 2) {
			super.setEditValue(n,  ei);
			return;
		}
		if (n == 2) {
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
