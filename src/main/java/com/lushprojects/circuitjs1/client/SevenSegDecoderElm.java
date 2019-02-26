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

    class SevenSegDecoderElm extends ChipElm {

	private static final boolean[][] symbols={
		{true,true,true,true,true,true,false},//0
		{false,true,true,false,false,false,false},//1
		{true,true,false,true,true,false,true},//2
		{true,true,true,true,false,false,true},//3
		{false,true,true,false,false,true,true},//4
		{true,false,true,true,false,true,true},//5
		{true,false,true,true,true,true,true},//6
		{true,true,true,false,false,false,false},//7
		{true,true,true,true,true,true,true},//8
		{true,true,true,false,false,true,true},//9
		{true,true,true,false,true,true,true},//A
		{false,false,true,true,true,true,true},//B
		{true,false,false,true,true,true,false},//C
		{false,true,true,true,true,false,true},//D
		{true,false,false,true,true,true,true},//E
		{true,false,false,false,true,true,true},//F
};

	boolean hasReset() {return false;}
	public SevenSegDecoderElm(int xx, int yy) { super(xx, yy); }
	public SevenSegDecoderElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	}
	String getChipName() { return "Seven Segment LED Decoder"; }

	void setupPins() {
	    sizeX = 3;
	    sizeY = 7;
	    pins = new Pin[getPostCount()];

	    pins[7] = new Pin(0, SIDE_W, "I3");
	    pins[8] = new Pin(1, SIDE_W, "I2");
	    pins[9] = new Pin(2, SIDE_W, "I1");
	    pins[10] = new Pin(3, SIDE_W, "I0");
	
	    pins[0] = new Pin(0, SIDE_E, "a");
	    pins[0].output=true;
	    pins[1] = new Pin(1, SIDE_E, "b");
	    pins[1].output=true;
	    pins[2] = new Pin(2, SIDE_E, "c");
	    pins[2].output=true;
	    pins[3] = new Pin(3, SIDE_E, "d");
	    pins[3].output=true;
	    pins[4] = new Pin(4, SIDE_E, "e");
	    pins[4].output=true;
	    pins[5] = new Pin(5, SIDE_E, "f");
	    pins[5].output=true;
	    pins[6] = new Pin(6, SIDE_E, "g");
	    pins[6].output=true;
	}

	int getPostCount() {
	    return 11;
	}
	int getVoltageSourceCount() {return 7;}

	void execute() {
	int input=0;
	if(pins[7].value)input+=8;
	if(pins[8].value)input+=4;
	if(pins[9].value)input+=2;
	if(pins[10].value)input+=1;

		for(int i=0;i<7;i++)
		{
		pins[i].value=symbols[input][i];
		}
	}
	int getDumpType() { return 197; }

    }
