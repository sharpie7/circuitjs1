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

    // concrete subclass of ChipElm that can be used by other elements (like CustomCompositeElm) to draw chips.
    // CustomCompositeElm can't be a subclass of both ChipElm and CompositeElm.
    class CustomCompositeChipElm extends ChipElm {
	public CustomCompositeChipElm(int xx, int yy) {
	    super(xx, yy);
	    setSize(2);
	}
	boolean needsBits() { return false; }
	void setupPins() { }
	int getVoltageSourceCount() { return 0; }
	void setPins(Pin p[]) {
	    pins = p;
	}
	void allocPins(int n) {
	    pins = new Pin[n];
	}
	void setPin(int n, int p, int s, String t) {
	    pins[n] = new Pin(p, s, t);
	}
	int getPostCount() { return pins == null ? 1 : pins.length; }
    }

