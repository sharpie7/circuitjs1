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

class FuseElm extends CircuitElm {
	double resistance;
	double heat;
	double i2t;
	boolean blown;
	final double blownResistance = 1e9;
	public FuseElm(int xx, int yy) {
	    super(xx, yy);
	    // from https://m.littelfuse.com/~/media/electronics/datasheets/fuses/littelfuse_fuse_218_datasheet.pdf.pdf
	    i2t = 6.73;
	    resistance = .0613;
	}
	public FuseElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    resistance = new Double(st.nextToken()).doubleValue();
	    i2t = new Double(st.nextToken()).doubleValue();
	    heat = new Double(st.nextToken()).doubleValue();
	    blown = new Boolean(st.nextToken()).booleanValue();
	}
	String dump() {
	    return super.dump() + " " + resistance + " " + i2t + " " + heat + " " + blown;
	}
	int getDumpType() { return 404; }

	void reset() {
	    super.reset();
	    heat = 0;
	    blown = false;
	}
	void setPoints() {
	    super.setPoints();
	    int llen = 16;
	    calcLeads(llen);
	}

	Color getTempColor(Graphics g) {
	    Color c = getVoltageColor(g, volts[0]);
	    double temp = heat/i2t;
	    if (temp < .3333) {
		double val = temp*3;
		int x = (int) (255*val);
		if (x < 0)
		    x = 0;
		return new Color(x+(255-x)*c.getRed()/255, (255-x)*c.getGreen()/255, (255-x)*c.getBlue()/255);
	    }
	    if (temp < .6667) {
		int x = (int) ((temp-.3333)*3*255);
		if (x < 0)
		    x = 0;
		return new Color(255, x, 0);
	    }
	    if (temp < 1) {
		int x = (int) ((temp-.6666)*3*255);
		if (x < 0)
		    x = 0;
		return new Color(255, 255, x);
	    }
	    return Color.white;
	}
	
	void draw(Graphics g) {
	    int segments = 16;
	    int i;
	    int hs=6;
	    setBbox(point1, point2, hs);
	    draw2Leads(g);
	    
	    //   double segf = 1./segments;
	    double len = distance(lead1, lead2);
	    g.context.save();
	    g.context.setLineWidth(3.0);
	    g.context.transform(((double)(lead2.x-lead1.x))/len, ((double)(lead2.y-lead1.y))/len, -((double)(lead2.y-lead1.y))/len,((double)(lead2.x-lead1.x))/len,lead1.x,lead1.y);
	    g.context.setStrokeStyle(getTempColor(g).getHexValue());
	    if (!blown) {
		g.context.beginPath();
		g.context.moveTo(0,0);
		for (i = 0; i <= segments; i++)
		    g.context.lineTo(i*len/segments, hs*Math.sin(i*Math.PI*2/segments));
		g.context.stroke();
	    }

	    g.context.restore();
	    doDots(g);
	    drawPosts(g);
	}

	void calculateCurrent() {
	    current = (volts[0]-volts[1])/(blown ? blownResistance : resistance);
	}
	void stamp() {
	    sim.stampNonLinear(nodes[0]);
	    sim.stampNonLinear(nodes[1]);
	}
	boolean nonLinear() { return true; }
	void startIteration() {
	    double i = getCurrent();
	    
	    // accumulate heat
	    heat += i*i*sim.timeStep;

	    // dissipate heat.  we assume the fuse can dissipate its entire i2t in 3 seconds
	    heat -= sim.timeStep*i2t/3;
	    
	    if (heat < 0)
		heat = 0;
	    if (heat > i2t)
		blown = true;
	}
	void doStep() {
	    sim.stampResistor(nodes[0], nodes[1], blown ? blownResistance : resistance);
	}
	void getInfo(String arr[]) {
	    arr[0] = blown ? "fuse (blown)" : "fuse";
	    getBasicInfo(arr);
	    arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
	    arr[4] = "I2t = " + i2t;
	    if (!blown)
		arr[5] = ((int)(heat*100/i2t)) + "% " + sim.LS("melted");
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("I2t", i2t, 0, 0);
	    if (n == 1)
		return new EditInfo("Resistance", resistance, 0, 0);
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0 && ei.value > 0)
		i2t = ei.value;
	    if (n == 1 && ei.value > 0)
		resistance = ei.value;
	}
    }
