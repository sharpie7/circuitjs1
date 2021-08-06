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

    class MultiplexerElm extends ChipElm {
	final int FLAG_INVERTED_OUTPUT = 1<<1;
	final int FLAG_STROBE = 1<<2;
	int selectBitCount;
	int outputCount;
	int strobe;
	int outputPin;
	
	boolean hasReset() {return false;}
	public MultiplexerElm(int xx, int yy) {
	    super(xx, yy);
	    selectBitCount = 2;
	    setupPins();
	}
	public MultiplexerElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    selectBitCount = 2;
	    try {
		selectBitCount = Integer.parseInt(st.nextToken());
	    } catch (Exception e) {}
	    setupPins();
	}
	String getChipName() { return "Multiplexer"; }
	String dump() { return super.dump() + " " + selectBitCount; }
	void setupPins() {
	    sizeX = selectBitCount+1;
	    outputCount = 1;
	    int i;
	    for (i = 0; i != selectBitCount; i++)
		outputCount <<= 1;
	    sizeY = outputCount+1;

	    pins = new Pin[getPostCount()];

	    for (i = 0; i != outputCount; i++)
		pins[i] = new Pin(i, SIDE_W, "I" + i);
	    
	    int n = outputCount;
	    for (i = 0; i != selectBitCount; i++, n++)
		pins[n] = new Pin(i+1, SIDE_S, "S" + i);

	    pins[n] = new Pin(0, SIDE_E, "Q");
	    pins[n].output=true;
	    outputPin = n;
	    if (hasFlag(FLAG_INVERTED_OUTPUT)) {
		n++;
		pins[n] = new Pin(1, SIDE_E, "Q");
		pins[n].lineOver = true;
		pins[n].output=true;
	    }
	    if (hasFlag(FLAG_STROBE)) {
		n++;
		pins[n] = new Pin(0, SIDE_S, "STR");
		strobe = n;
	    } else
		strobe = -1;
	    
	    allocNodes();

	}
	int getPostCount() {
	    return outputCount + selectBitCount + 1 + (hasFlag(FLAG_INVERTED_OUTPUT) ? 1 : 0) + (hasFlag(FLAG_STROBE) ? 1 : 0);
	}
	int getVoltageSourceCount() {return hasFlag(FLAG_INVERTED_OUTPUT) ? 2 : 1;}

	void execute() {
	    int selectedValue=0;
	    int i;
	    for (i = 0; i != selectBitCount; i++)
		if (pins[outputCount+i].value)
		    selectedValue |= 1<<i;
	    boolean val = pins[selectedValue].value;
	    if (strobe != -1 && pins[strobe].value)
		val = false;
	    pins[outputPin].value = val;
	    if (hasFlag(FLAG_INVERTED_OUTPUT))
		pins[outputPin+1].value = !val;	
	}
	
	int getDumpType() { return 184; }

        public EditInfo getChipEditInfo(int n) {
            if (n == 0)
                return new EditInfo("# of Select Bits", selectBitCount, 1, 8).
                    setDimensionless();
            if (n == 1)
        	return EditInfo.createCheckbox("Inverted Output", hasFlag(FLAG_INVERTED_OUTPUT));
            if (n == 2)
        	return EditInfo.createCheckbox("Strobe Pin", hasFlag(FLAG_STROBE));
            
            return super.getChipEditInfo(n);
        }
        
        public void setChipEditValue(int n, EditInfo ei) {
            if (n == 0 && ei.value >= 1 && ei.value <= 6) {
                selectBitCount = (int) ei.value;
                setupPins();
                setPoints();
                return;
            }
            if (n == 1) {
        	flags = ei.changeFlag(flags, FLAG_INVERTED_OUTPUT);
                setupPins();
                setPoints();
                return;
            }
            if (n == 2) {
        	flags = ei.changeFlag(flags, FLAG_STROBE);
                setupPins();
                setPoints();
                return;
            }
            super.setChipEditValue(n, ei);
        }
        
    }
