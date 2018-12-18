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

import com.google.gwt.user.client.Window;

// Zener code contributed by J. Mike Rollins
// http://www.camotruck.net/rollins/simulator.html
class ZenerElm extends DiodeElm {
    static String lastZenerModelName = "default-zener";
    
    public ZenerElm(int xx, int yy) {
	super(xx, yy);
	modelName = lastZenerModelName;
	setup();
    }
    public ZenerElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	super(xa, ya, xb, yb, f, st);
	if ((f & FLAG_MODEL) == 0) {
	    double zvoltage = new Double(st.nextToken()).doubleValue();
            model = DiodeModel.getModelWithParameters(model.fwdrop, zvoltage);
            modelName = model.name;
            CirSim.console("model name wparams = " + modelName);
	}
	setup();
    }
    int getDumpType() { return 'z'; }
    
    final int hs = 8;
    Polygon poly;
    Point cathode[];
    Point wing[];
	
    void setPoints() {
	super.setPoints();
	calcLeads(16);
	cathode = newPointArray(2);
	wing = newPointArray(2);
	Point pa[] = newPointArray(2);
	interpPoint2(lead1, lead2, pa[0], pa[1], 0, hs);
	interpPoint2(lead1, lead2, cathode[0], cathode[1], 1, hs);
	interpPoint(cathode[0], cathode[1], wing[0], -0.2, -hs);
	interpPoint(cathode[1], cathode[0], wing[1], -0.2, -hs);
	poly = createPolygon(pa[0], pa[1], lead2);
    }
	
    void draw(Graphics g) {
	setBbox(point1, point2, hs);

	double v1 = volts[0];
	double v2 = volts[1];

	draw2Leads(g);

	// draw arrow thingy
	setPowerColor(g, true);
	setVoltageColor(g, v1);
	g.fillPolygon(poly);

	// draw thing arrow is pointing to
	setVoltageColor(g, v2);
	drawThickLine(g, cathode[0], cathode[1]);

	// draw wings on cathode
	drawThickLine(g, wing[0], cathode[0]);
	drawThickLine(g, wing[1], cathode[1]);
	    
	doDots(g);
	drawPosts(g);
    }
	
    final double default_zvoltage = 5.6;

    void getInfo(String arr[]) {
	super.getInfo(arr);
	arr[0] = "Zener diode";
	arr[5] = "Vz = " + getVoltageText(model.breakdownVoltage);
    }
    
    int getShortcut() { return 'z'; }
    
    void setLastModelName(String n) {
	lastZenerModelName = n;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (n == 2) {
            String val = Window.prompt(sim.LS("Breakdown Voltage"), sim.LS("5.6"));
            try {
        		double zvoltage = new Double(val).doubleValue();
        		zvoltage = Math.abs(zvoltage);
        		if (zvoltage > 0) {
        		    model = DiodeModel.getZenerModel(zvoltage);
        		    modelName = model.name;
        		    ei.newDialog = true;
        		    return;
        		}
            } catch (Exception e) {
            }
        }
        
        super.setEditValue(n, ei);
    }
    
}
