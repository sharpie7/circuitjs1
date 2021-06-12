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

import java.util.NoSuchElementException;
import com.google.gwt.user.client.ui.TextArea;

// contributed by Edward Calver

class SeqGenElm extends ChipElm {
	final int FLAG_NEW_VERSION = 2;
	final int FLAG_ONE_SHOT = 4;
	final int FLAG_HAS_RESET = 8;
	
	int bitPosition = 0;
	int bitCount = 0;
	byte data[];
	double lastchangetime = 0;
	boolean clockstate = false;
	
	public SeqGenElm(int xx, int yy) {
		super(xx, yy);
		bitCount = 8;
		data = new byte[] { 0 };
		flags |= FLAG_NEW_VERSION;
		//flags |= FLAG_HAS_RESET; // Uncomment this if somebody asks for a RESET pin on the SeqGen
		setupPins();
		allocNodes();
	}
	public SeqGenElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		try {
			if ((flags & FLAG_NEW_VERSION) == 0) {
				// This is an old sequence generator. Upgrade it to the new, flexible version.
				flags |= FLAG_NEW_VERSION;
				
				//The old sequence generator read bytes backwards, from right to left, and this needs to be corrected.
				byte oldData = (byte)Integer.parseInt(st.nextToken());
				byte newData = 0;
				for (int i = 0; i < Byte.SIZE; i++)
					newData |= oldData & ((~(Byte.MAX_VALUE - 1) >> i) != 0 ? (1 << i) : 0);
				
				bitCount = 8;
				data = new byte[] { newData };
				
				if (st.hasMoreTokens()) {
					if (Boolean.parseBoolean(st.nextToken())) {
						flags |= FLAG_ONE_SHOT;
						setupPins();
						allocNodes();
					}
				}
			} else {
				// Load normally
				bitCount = Integer.parseInt(st.nextToken());
				data = new byte[(int)(bitCount / Byte.SIZE) + (bitCount % Byte.SIZE != 0 ? 1 : 0)]; //Allocate enough bytes to fit the requested number of bits
				for (int i = 0; i < data.length; i++)
					data[i] = Byte.parseByte(st.nextToken());
			}
		} catch (NoSuchElementException e) {
			// Corrupted element: Data is incomplete
		}
		
		// Ensure bitCount does not exceed the amount of data we have. (This can happen if there was an error)
		if (bitCount > data.length * Byte.SIZE)
			bitCount = data.length * Byte.SIZE;
		
		if (hasOneShot())
			bitPosition = data.length * Byte.SIZE; //Set the pos to the end so that this seqgen's one-shot mode doesn't trigger immediately
	}
	
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
	
	void nextBit() {
		if (data.length > 0 && bitCount > 0) {
			if (bitPosition / Byte.SIZE >= data.length)
				bitPosition = 0;
			pins[1].value = (data[bitPosition / Byte.SIZE] & (1 << (bitPosition % Byte.SIZE))) != 0;
			bitPosition++;
		} else {
			pins[1].value = false;
		}
	}
	
	void execute() {
		if (hasReset() && pins[2].value) {
			// Suspended state (RESET raised)
			bitPosition = 0;
			pins[1].value = data.length > 0 && (data[0] & 1) != 0;
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
						CirSim.console("Setting bit position (from "+bitPosition+" to 0)");
						bitPosition = 0;
					} else
						nextBit();
				}
			}
			if (hasOneShot()) {
				// One-shot mode
				if (sim.t - lastchangetime > 0.005) {
					CirSim.console("Tick");
					if (bitPosition / Byte.SIZE < data.length)
						nextBit();
					if (sim.t - lastchangetime > 0.005 * 2.0)
						lastchangetime = sim.t;
					else
						lastchangetime += 0.005;
				}
			}
		}
	}
	int getDumpType() { return 188; }
	
	String dump(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.dump());
		sb.append(' ');
		sb.append(bitCount);
		for (int i = 0; i < data.length; i++) {
			sb.append(' ');
			sb.append(Byte.toString(data[i]));
		}
		return sb.toString();
	}
	public EditInfo getEditInfo(int n) {
		if (n < 2)
			return super.getEditInfo(n);
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("One shot", hasOneShot());
			return ei;
		}
		if (n == 3) {
			EditInfo ei = new EditInfo("Sequence", 0, -1, -1);
			ei.textArea = new TextArea();
        	ei.textArea.setVisibleLines(5);
        	StringBuilder sb = new StringBuilder(bitCount);
        	for (int i = 0; i < bitCount; i++)
        		sb.append((data[i / Byte.SIZE] & (1 << (i % Byte.SIZE))) != 0 ? '1' : '0');
        	ei.textArea.setText(sb.toString());
			return ei;
		}
		return null;
	}
	public void setEditValue(int n, EditInfo ei) {
		if (n < 2) {
			super.setEditValue(n, ei);
			return;
		}
		if (n == 2) {
			if (ei.checkbox.getState() != ((flags & FLAG_ONE_SHOT) != 0)) { //value changed
				flags = ei.changeFlag(flags, FLAG_ONE_SHOT);
				if (hasOneShot())
					bitPosition = data.length * Byte.SIZE; //Ditto
			}
			return;
		}
		if (n == 3) {
			String s = ei.textArea.getText();
			boolean wasEmpty = data.length == 0;
			
			// First count the number of bits so we can initialize the data array
			bitCount = 0;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '0' || c == '1')
					bitCount++;
			}
			data = new byte[bitCount / Byte.SIZE];
			
			// Fill the data array
			bitCount = 0;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '0' || c == '1') {
					if (c == '1')
						data[bitCount / Byte.SIZE] = (byte) (data[bitCount / Byte.SIZE] | (1 << (bitCount % Byte.SIZE)));
					bitCount++;
				}
			}
			
			if (hasOneShot() && wasEmpty)
				bitPosition = data.length * Byte.SIZE; //Ditto
			
			return;
		}
	}
}
