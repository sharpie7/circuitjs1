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
	final int FLAG_PLAY_ONCE = 4;
	final int FLAG_HAS_RESET = 8;
	
	int bitPosition = 0;
	int bitCount = 0;
	int data[];
	boolean clockstate = false;
	
	public SeqGenElm(int xx, int yy) {
		super(xx, yy);
		bitCount = 8;
		data = new int[] { 0 };
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
				for (int i = 0; i < Integer.SIZE; i++)
					newData |= oldData & ((~(Integer.MAX_VALUE - 1) >> i) != 0 ? (1 << i) : 0);
				
				bitCount = 8;
				data = new int[] { newData };
			} else {
				// Load normally
				bitCount = Integer.parseInt(st.nextToken());
				data = new int[(bitCount / Integer.SIZE) + (bitCount % Integer.SIZE != 0 ? 1 : 0)]; //Allocate enough bytes to fit the requested number of bits
				for (int i = 0; i < data.length; i++)
					data[i] = Integer.parseInt(st.nextToken());
			}
		} catch (NoSuchElementException e) {
			// Corrupted element: Data is incomplete
		}
		
		// Ensure bitCount does not exceed the amount of data we have. (This can happen if there was an error)
		if (bitCount > data.length * Integer.SIZE)
			bitCount = data.length * Integer.SIZE;
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
	double getVoltageDiff() {
		return volts[1];
	}
	int getPostCount() { return hasReset() ? 3 : 2; }
	int getVoltageSourceCount() { return 1; }
	boolean hasPlayOnce() { return (flags & FLAG_PLAY_ONCE) != 0; }
	boolean hasReset() { return (flags & FLAG_HAS_RESET) != 0; }
	
	void reset() {
	    super.reset();
	    bitPosition = 0;
	}
	
	void nextBit() {
		if (data.length > 0 && bitCount > 0) {
			if (bitPosition >= bitCount) {
				if (hasPlayOnce()) {
					pins[1].value = false;
					return; //Stopped
				}
				bitPosition = 0;
			}
			pins[1].value = (data[bitPosition / Integer.SIZE] & (1 << (bitPosition % Integer.SIZE))) != 0;
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
				if (clockstate) // Rising-edge event
					nextBit();
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
			sb.append(Integer.toString(data[i]));
		}
		return sb.toString();
	}
	public EditInfo getEditInfo(int n) {
		if (n < 2)
			return super.getEditInfo(n);
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Play Once", hasPlayOnce());
			return ei;
		}
		if (n == 3) {
			EditInfo ei = new EditInfo("Sequence", 0, -1, -1);
			ei.textArea = new TextArea();
        	ei.textArea.setVisibleLines(5);
        	StringBuilder sb = new StringBuilder(bitCount);
        	for (int i = 0; i < bitCount; i++)
        		sb.append((data[i / Integer.SIZE] & (1 << (i % Integer.SIZE))) != 0 ? '1' : '0');
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
			flags = ei.changeFlag(flags, FLAG_PLAY_ONCE);
			return;
		}
		if (n == 3) {
			String s = ei.textArea.getText();
			
			// First count the number of bits so we can initialize the data array
			bitCount = 0;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '0' || c == '1')
					bitCount++;
			}
			data = new int[bitCount / Integer.SIZE];
			
			// Fill the data array
			bitCount = 0;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '0' || c == '1') {
					if (c == '1')
						data[bitCount / Integer.SIZE] = (byte) (data[bitCount / Integer.SIZE] | (1 << (bitCount % Integer.SIZE)));
					bitCount++;
				}
			}
			
			return;
		}
	}
}
