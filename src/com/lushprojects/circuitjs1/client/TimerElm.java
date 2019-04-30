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

class TimerElm extends ChipElm {
    final int FLAG_RESET = 2;
    final int FLAG_GROUND = 4;
    final int N_DIS  = 0;
    final int N_TRIG = 1;
    final int N_THRES = 2;
    final int N_VIN = 3;
    final int N_CTL = 4;
    final int N_OUT = 5;
    final int N_RST = 6;
    final int N_GND = 7;
    int getDefaultFlags() { return FLAG_RESET | FLAG_GROUND; }
    int ground;
    public TimerElm(int xx, int yy) { super(xx, yy); }
    public TimerElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	super(xa, ya, xb, yb, f, st);
    }
    String getChipName() { return "555 Timer"; }
    void setupPins() {
	sizeX = 3;
	sizeY = 5;
	pins = new Pin[8];
	pins[N_DIS] = new Pin(1, SIDE_W, "dis");
	pins[N_TRIG] = new Pin(3, SIDE_W, "tr");
	pins[N_TRIG].lineOver = true;
	pins[N_THRES] = new Pin(4, SIDE_W, "th");
	pins[N_VIN] = new Pin(1, SIDE_N, "Vin");
	pins[N_CTL] = new Pin(1, SIDE_S, "ctl");
	pins[N_OUT] = new Pin(2, SIDE_E, "out");
	pins[N_OUT].state = true;
	pins[N_RST] = new Pin(1, SIDE_E, "rst");
	pins[N_GND] = new Pin(2, SIDE_S, "gnd");
    }
    boolean nonLinear() { return true; }
    boolean hasReset() { return (flags & FLAG_RESET) != 0 || hasGroundPin(); }
    boolean hasGroundPin() { return (flags & FLAG_GROUND) != 0; }
    void stamp() {
	ground = hasGroundPin() ? nodes[N_GND] : 0;
	// stamp voltage divider to put ctl pin at 2/3 V
	sim.stampResistor(nodes[N_VIN], nodes[N_CTL],  5000);
	sim.stampResistor(nodes[N_CTL], ground,        10000);
	// discharge, output, and Vin pins change in doStep()
	sim.stampNonLinear(nodes[N_DIS]);
	sim.stampNonLinear(nodes[N_OUT]);
	sim.stampNonLinear(nodes[N_VIN]);
	if (hasGroundPin())
	    sim.stampNonLinear(nodes[N_GND]);
    }
    void calculateCurrent() {
	// need current for V, discharge, control, ground; output current is
	// calculated for us, and other pins have no current.
	pins[N_VIN].current = (volts[N_CTL]-volts[N_VIN])/5000;
	double groundVolts = hasGroundPin() ? volts[N_GND] : 0;
	pins[N_CTL].current = -(volts[N_CTL]-groundVolts)/10000 - pins[N_VIN].current;
	pins[N_DIS].current = (!out) ? -(volts[N_DIS]-groundVolts)/10 : 0;
	pins[N_OUT].current = -(volts[N_OUT]-(out ? volts[N_VIN] : groundVolts));
	if (out)
	    pins[N_VIN].current -= pins[N_OUT].current;
	if (hasGroundPin()) {
	    pins[N_GND].current = (volts[N_CTL]-groundVolts)/10000;
	    if (!out)
		pins[N_GND].current += (volts[N_DIS]-groundVolts)/10 + (volts[N_OUT]-groundVolts);
	}
    }
    boolean out;
    void startIteration() {
	out = volts[N_OUT] > volts[N_VIN]/2;
	// check comparators
	if (volts[N_THRES] > volts[N_CTL])
		out = false;
	
	// trigger overrides threshold
	if (volts[N_CTL]/2 > volts[N_TRIG])
	    out = true;
	
	double groundVolts = hasGroundPin() ? volts[N_GND] : 0;
	
	// reset overrides trigger
	if (hasReset() && volts[N_RST] < .7+groundVolts)
	    out = false;
    }
    void doStep() {
	// if output is low, discharge pin 0.  we use a small
	// resistor because it's easier, and sometimes people tie
	// the discharge pin to the trigger and threshold pins.
	if (!out)
	    sim.stampResistor(nodes[N_DIS], ground, 10);
	
	// if output is high, connect Vin to output with a small resistor.  Otherwise connect output to ground.
	sim.stampResistor(out ? nodes[N_VIN] : ground, nodes[N_OUT], 1); 
    }
    int getPostCount() { return hasGroundPin() ? 8 : hasReset() ? 7 : 6; }
    int getVoltageSourceCount() { return 0; }
    int getDumpType() { return 165; }
    public EditInfo getEditInfo(int n) {
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, 0, 0);
            ei.checkbox = new Checkbox("Ground Pin", hasGroundPin());
            return ei;
        }
        return super.getEditInfo(n);
    }
    public void setEditValue(int n, EditInfo ei) {
        if (n == 2) {
            flags = ei.changeFlag(flags, FLAG_GROUND);
            allocNodes();
            setPoints();
            return;
        }
        super.setEditValue(n, ei);
    }

}
    
