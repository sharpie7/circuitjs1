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
	boolean hasReset() {return false;}
	public DeMultiplexerElm(int xx, int yy) { super(xx, yy); }
	public DeMultiplexerElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	}
	String getChipName() { return "Demultiplexer"; }

	void setupPins() {
	    sizeX = 3;
	    sizeY = 5;
	    pins = new Pin[getPostCount()];

	    pins[0] = new Pin(0, SIDE_E, "Q0");
	    pins[0].output=true;
	    pins[1] = new Pin(1, SIDE_E, "Q1");
	    pins[1].output=true;
	    pins[2] = new Pin(2, SIDE_E, "Q2");
	    pins[2].output=true;
	    pins[3] = new Pin(3, SIDE_E, "Q3");
	    pins[3].output=true;
		
	    pins[4] = new Pin(0, SIDE_S, "S0");
	    pins[5] = new Pin(1, SIDE_S, "S1");

	    pins[6] = new Pin(0, SIDE_W, "Q");


	}
	int getPostCount() {
	    return 7;
	}
	int getVoltageSourceCount() {return 4;}

	void execute() {
	 int selectedvalue=0;
	 if(pins[4].value)selectedvalue++;
	 if(pins[5].value)selectedvalue+=2;
	 for(int i=0;i<4;i++)pins[i].value=false;
	 pins[selectedvalue].value=pins[6].value;

	}
	int getDumpType() { return 185; }

    }
