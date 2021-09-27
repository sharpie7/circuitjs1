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

    class TimeDelayRelayElm extends ChipElm {
	double lastTransition;
	boolean poweredState;
	boolean onState;
	final double vinResistance = 10e3;
	double resistance;
	double onDelay, offDelay;
	double onResistance, offResistance;
	
	public TimeDelayRelayElm(int xx, int yy) {
	    super(xx, yy);
	    onDelay = 1;
	    offDelay = 0;
	    onResistance = 1;
	    offResistance = resistance = 10e6;
	}
	public TimeDelayRelayElm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    onDelay = Double.parseDouble(st.nextToken());
	    offDelay = Double.parseDouble(st.nextToken());
	    onResistance = Double.parseDouble(st.nextToken());
	    offResistance = resistance = Double.parseDouble(st.nextToken());
	}
	void reset() {
	    lastTransition = 0;
	    poweredState = onState = false;
	}
	String dump() {
	    return super.dump() + " " + onDelay + " " + offDelay + " " + onResistance + " " + offResistance;
	}
	String getChipName() { return "time delay relay"; }
	void setupPins() {
	    sizeX = 2;
	    sizeY = 2;
	    pins = new Pin[4];
	    pins[0] = new Pin(1, SIDE_W, "Vin");
	    pins[1] = new Pin(1, SIDE_E, "gnd");
	    pins[2] = new Pin(0, SIDE_W, "in");
	    pins[3] = new Pin(0, SIDE_E, "out");
	}
	
	boolean nonLinear() { return true; }
	void stamp() {
	    resistance = (onState) ? onResistance : offResistance;
	    sim.stampResistor(nodes[0], nodes[1], vinResistance);
	    sim.stampNonLinear(nodes[2]);
	    sim.stampNonLinear(nodes[3]);
	}
	
	void doStep() {
	    resistance = (onState) ? onResistance : offResistance;
	    sim.stampResistor(nodes[2], nodes[3], resistance);
	}
	
	void stepFinished() {
	    boolean oldState = poweredState;
	    poweredState = (volts[0]-volts[1] > 2.5);
	    if (oldState != poweredState)
		lastTransition = sim.t;
	    if (sim.t > lastTransition + (poweredState ? onDelay : offDelay))
		onState = poweredState;
	}
	
	void draw(Graphics g) {
	    pins[0].current = -(volts[0]-volts[1])/vinResistance;
	    pins[2].current = -(volts[2]-volts[3])/resistance;
	    pins[1].current = -pins[0].current;
	    pins[3].current = -pins[2].current;
	    drawChip(g);
	}
	int getPostCount() { return 4; }
	int getVoltageSourceCount() { return 0; }
	int getDumpType() { return 414; }
	
	    public EditInfo getChipEditInfo(int n) {
	        if (n == 0)
	            return new EditInfo("On Delay (s)", onDelay, 0, 0);
	        if (n == 1)
	            return new EditInfo("Off Delay (s)", offDelay, 0, 0);
	        if (n == 2)
	            return new EditInfo("On Resistance (ohms)", onResistance, 0, 0);
	        if (n == 3)
	            return new EditInfo("Off Resistance (ohms)", offResistance, 0, 0);
	        return null;
	    }
	    public void setChipEditValue(int n, EditInfo ei) {
	        if (n == 0)
	            onDelay = ei.value;
	        if (n == 1)
	            offDelay = ei.value;
	        if (n == 2 && ei.value > 0)
	            onResistance = ei.value;
	        if (n == 3 && ei.value > 0)
	            offResistance = ei.value;
	    }
    }

