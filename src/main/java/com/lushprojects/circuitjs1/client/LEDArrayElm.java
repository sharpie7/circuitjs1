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

    class LEDArrayElm extends ChipElm {
	public LEDArrayElm(int xx, int yy) {
	    super(xx, yy);
	}
	public LEDArrayElm(int xa, int ya, int xb, int yb, int f,
			   StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    try {
		sizeX = Integer.parseInt(st.nextToken());
		sizeY = Integer.parseInt(st.nextToken());
	    } catch (Exception e) {}
	    allocNodes();
            setupPins();
            setPoints();
	}
	
	String dump() { return super.dump() + " " + sizeX + " " + sizeY ; }
	
	String getChipName() { return "LED array"; }
	
	void setupPins() {
	    if (sizeX == 0 || sizeY == 0) {
		sizeX = sizeY = 8;
		allocNodes();
	    }
	    pins = new Pin[sizeX+sizeY];
	    int i;
	    for (i = 0; i != sizeX; i++)
		pins[i] = new Pin(i, SIDE_S, "");
	    for (i = 0; i != sizeY; i++)
		pins[i+sizeX] = new Pin(i, SIDE_W, "");
	    brightness = new double[sizeX*sizeY];
	}
	
	Diode diodes[];
	double currents[];
	double brightness[];
	
	void stamp() {
	    super.stamp();
	    
	    // create grid of diodes
	    diodes = new Diode[sizeX*sizeY];
	    int i;
	    DiodeModel model = DiodeModel.getModelWithName("default-led");
	    for (i = 0; i != diodes.length; i++) {
		diodes[i] = new Diode(sim);
		diodes[i].setup(model);
		diodes[i].stamp(nodes[sizeX+(i / sizeX)], nodes[i % sizeX]);
	    }
	    currents = new double[diodes.length];
	} 
	void doStep() {
	    super.doStep();
	    
	    int ix, iy, i = 0;
	    for (iy = 0; iy != sizeY; iy++)
		for (ix = 0; ix != sizeX; ix++, i++)
		    diodes[i].doStep(volts[sizeX+iy]-volts[ix]);
	}
        boolean nonLinear() { return true; }
	void draw(Graphics g) {
	    drawChip(g);
	    int ix, iy;
	    for (ix = 0; ix != sizeX; ix++)
		for (iy = 0; iy != sizeY; iy++) {
		    int i = ix+iy*sizeX;
		    setColor(g, i);
		    g.fillOval(pins[ix].post.x-cspc/2, pins[iy+sizeX].post.y-cspc/2, cspc, cspc);
		}
	}
	
	void calculateCurrent() {
	    // calculate diode currents
	    int ix, iy, i = 0;
	    for (ix = 0; ix != sizeX; ix++)
		pins[ix].current = 0;
	    for (iy = 0; iy != sizeY; iy++) {
		double cur = 0;
		for (ix = 0; ix != sizeX; ix++, i++) {
		    currents[i] = diodes[i].calculateCurrent(volts[sizeX+iy]-volts[ix]);
		    cur += currents[i];
		    pins[ix].current += currents[i];
		}
		pins[iy+sizeX].current = -cur;
	    }
	}

	void stepFinished() {
	    // stop for huge currents that make simulator act weird
	    int i;
	    for (i = 0; i != currents.length; i++)
		if (Math.abs(currents[i]) > 1e12)
		    sim.stop("max current exceeded", this);
	}

	void setColor(Graphics g, int p) {
	    // 10mA current = max brightness
	    if (currents == null) {
		g.setColor(new Color(20, 0, 0));
		return;
	    }
	    double w = currents[p] / .01;
            if (w > 0)
                w = 255*(1+.2*Math.log(w));
            if (w > 255)
                w = 255;
            if (w < 20)
                w = 20;
            
            // when diode turns off, made it fade gradually to simulate persistence of vision
            w = Math.max(w, brightness[p]);
            brightness[p] = w*.99;
            
            Color cc = new Color((int) w, 0, 0);
            g.setColor(cc);
	}
	int getPostCount() { return sizeX+sizeY; }
	int getVoltageSourceCount() { return 0; }
	int getDumpType() { return 405; }
	
	// this is true but it causes strange behavior with unconnected pins so we don't do it
//	boolean getConnection(int n1, int n2) { return true; }
	
	public EditInfo getEditInfo(int n) {
            if (n == 2)
                return new EditInfo("Grid Width", sizeX).setDimensionless();
            if (n == 3)
        		return new EditInfo("Grid Height", sizeY).setDimensionless();
	    return super.getEditInfo(n);
	}
	
	public void setEditValue(int n, EditInfo ei) {
            if (n == 2 && ei.value >= 2 && ei.value <= 16) {
        		sizeX = (int)ei.value;
        		allocNodes();
                setupPins();
                setPoints();
                return;
            }
            if (n == 3 && ei.value >= 2 && ei.value <= 16) {
        		sizeY = (int)ei.value;
        		allocNodes();
                setupPins();
                setPoints();
                return;
            }
	    super.setEditValue(n, ei);
	}
	
	// default getInfo doesn't work because the pins are unlabeled
	void getInfo(String arr[]) {
	    arr[0] = getChipName();
	    return;
	}
    }
