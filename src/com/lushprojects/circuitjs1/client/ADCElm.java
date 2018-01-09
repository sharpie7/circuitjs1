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

class ADCElm extends ChipElm {
    public ADCElm(int xx, int yy) { super(xx, yy); }
    public ADCElm(int xa, int ya, int xb, int yb, int f,
		  StringTokenizer st) {
	super(xa, ya, xb, yb, f, st);
    }
    String getChipName() { return "ADC"; }
    boolean needsBits() { return true; }
    void setupPins() {
	sizeX = 2;
	sizeY = bits > 2 ? bits : 2;
	pins = new Pin[getPostCount()];
	int i;
	for (i = 0; i != bits; i++) {
	    pins[i] = new Pin(bits-1-i, SIDE_E, "D" + i);
	    pins[i].output = true;
	}
	pins[bits]   = new Pin(0, SIDE_W, "In");
	pins[bits+1] = new Pin(sizeY-1, SIDE_W, "V+");
	allocNodes();
    }
    void execute() {
	int imax = (1<<bits)-1;
	// if we round, the half-flash doesn't work
	double val = imax*volts[bits]/volts[bits+1]; // + .5;
	int ival = (int) val;
	ival = min(imax, max(0, ival));
	int i;
	for (i = 0; i != bits; i++)
	    pins[i].value = ((ival & (1<<i)) != 0);
    }
    int getVoltageSourceCount() { return bits; }
    int getPostCount() { return bits+2; }
    int getDumpType() { return 167; }
}
    
