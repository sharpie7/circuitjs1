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

class SeqGenElm extends ChipElm {
	final int FLAG_HAS_RESET = 2; //Flag for backwards compatibility
	final int FLAG_ONE_SHOT = 4;
	
	public SeqGenElm(int xx, int yy) {
		super(xx, yy);
		flags |= FLAG_HAS_RESET;
		setupPins();
		allocNodes();
	}
	public SeqGenElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		data = (short)Integer.parseInt(st.nextToken());
		if (st.hasMoreTokens()) {
			if (Boolean.parseBoolean(st.nextToken())) { //Backwards compatibility
				flags |= FLAG_ONE_SHOT;
				setupPins();
				allocNodes();
			}
			position = 8;
		}
	}
	
	short data = 0;
	byte position = 0;
	double lastchangetime = 0;
	boolean clockstate = false;
	String getChipName() { return "sequence generator"; }
	void setupPins() {
		sizeX = 2;
		sizeY = 2;
		pins = new Pin[getPostCount()];
		
		pins[0] = new Pin(0, SIDE_W, "");
		pins[0].clock = true;
		pins[1] = new Pin(1, SIDE_E, "Q");
		pins[1].output = true;
		if (hasReset())
			pins[2] = new Pin(1, SIDE_W, "R");
	}
	int getPostCount() { return hasReset() ? 3 : 2; }
	int getVoltageSourceCount() { return 1; }
	boolean hasOneShot() { return (flags & FLAG_ONE_SHOT) != 0; }
	boolean hasReset() { return (flags & FLAG_HAS_RESET) != 0; }
	
	void getNextBit() {
		pins[1].value = ((data >>> position) & 1) != 0;
		position++;
	}
	
	void execute() {
		if (hasOneShot()) {
			if (sim.t - lastchangetime > 0.005) {
				if (position <= 8)
					getNextBit();
				lastchangetime = sim.t;
			}
		}
		if (hasReset() && pins[2].value) {
			// Suspended state (RESET raised)
			position = (byte) (hasOneShot() ? 8 : 0);
			pins[1].value = false;
			clockstate = pins[0].value;
		} else {
			// Normal operation
			if (pins[0].value != clockstate) {
				// Edge transition
				clockstate = pins[0].value;
				if (clockstate) {
					// Rising-edge event
					clockstate = true;
					if (hasOneShot()) {
						position = 0;
					} else {
						getNextBit();
						if(position >= 8)
							position = 0;
					}
				}
			}
		}
	}
	int getDumpType() { return 188; }
	
	String dump(){
		return super.dump() + " " + data;
	}
	public EditInfo getEditInfo(int n) {
		if (n < 2)
			return super.getEditInfo(n);
		if (n < 10) {
			int bitIndex = n - 2;
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Bit "+(bitIndex+1)+" set", (data & (1<<bitIndex)) != 0);
			return ei;
		}
		if (n == 10) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("One shot", hasOneShot());
			return ei;
		}
		return null;
	}
	public void setEditValue(int n, EditInfo ei) {
		if (n < 2) {
			super.setEditValue(n, ei);
			return;
		}
		if (n < 10) {
			int bitIndex = n - 2;
			int bit = 1 << bitIndex;
			if (ei.checkbox.getState())
				data |= bit;
			else
				data &= ~bit;
			setPoints();
			return;
		}
		if (n == 10) {
			if (ei.checkbox.getState()) {
				flags |= FLAG_ONE_SHOT;
				position = 8;
			} else {
				flags &= ~FLAG_ONE_SHOT;
				position = 0;
			}
			return;
		}
	}
}
