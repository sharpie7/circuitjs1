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

    class AntennaElm extends RailElm {
	public AntennaElm(int xx, int yy) { super(xx, yy, WF_AC); }
	public AntennaElm(int xa, int ya, int xb, int yb, int f,
		       StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    waveform = WF_AC;
	}
	double fmphase;
	
	void drawRail(Graphics g) {
	    drawRailText(g, "Ant");
	}
	
	double getVoltage() {
	    fmphase += 2*pi*(2200+Math.sin(2*pi*sim.t*13)*100)*sim.timeStep;
	    double fm = 3*Math.sin(fmphase);
	    return Math.sin(2*pi*sim.t*3000)*(1.3+Math.sin(2*pi*sim.t*12))*3 +
	           Math.sin(2*pi*sim.t*2710)*(1.3+Math.sin(2*pi*sim.t*13))*3 +
		   Math.sin(2*pi*sim.t*2433)*(1.3+Math.sin(2*pi*sim.t*14))*3 + fm;
	}
	
	int getDumpType() { return 'A'; }
	int getShortcut() { return 0; }
	
	public EditInfo getEditInfo(int n) {
	    return null;
	}
    }
