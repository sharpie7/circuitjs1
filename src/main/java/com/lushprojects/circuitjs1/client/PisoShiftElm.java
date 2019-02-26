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
	boolean hasReset() {return false;}
	public PisoShiftElm(int xx, int yy) { super(xx, yy); }
	public PisoShiftElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	}
	short data=0;//Lack of unsigned types sucks
	boolean clockstate=false;
	boolean modestate=false;
	String getChipName() { return "PISO shift register"; }

	void setupPins() {
	    sizeX = 10;
	    sizeY = 3;
	    pins = new Pin[getPostCount()];

	    pins[0] = new Pin(1, SIDE_W, "L");
	    pins[1] = new Pin(2, SIDE_W, "");
	    pins[1].clock=true;

	    pins[2] = new Pin(1, SIDE_N, "I7");
	    pins[3] = new Pin(2, SIDE_N, "I6");
	    pins[4] = new Pin(3, SIDE_N, "I5");
	    pins[5] = new Pin(4, SIDE_N, "I4");
	    pins[6] = new Pin(5, SIDE_N, "I3");
	    pins[7] = new Pin(6, SIDE_N, "I2");
	    pins[8] = new Pin(7, SIDE_N, "I1");
	    pins[9] = new Pin(8, SIDE_N, "I0");

	    pins[10] = new Pin(1, SIDE_E, "Q");
	    pins[10].output=true;

	}
	int getPostCount() {
	    return 11;
	}
	int getVoltageSourceCount() {return 1;}

	void execute() {
		if(pins[0].value&&!modestate)
		{
		modestate=true;
		data=0;
		if(pins[2].value)data+=128;
		if(pins[3].value)data+=64;
		if(pins[4].value)data+=32;
		if(pins[5].value)data+=16;
		if(pins[6].value)data+=8;
		if(pins[7].value)data+=4;
		if(pins[8].value)data+=2;
		if(pins[9].value)data+=1;
		}
		else if(pins[1].value&&!clockstate)
		{
		clockstate=true;
		if((data&1)==0)pins[10].value=false;
		else pins[10].value=true;
		data=(byte)(data>>>1);
		}
		if(!pins[0].value)modestate=false;
		if(!pins[1].value)clockstate=false;
	}
	int getDumpType() { return 186; }

    }
