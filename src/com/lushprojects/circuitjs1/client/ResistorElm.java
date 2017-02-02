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

import com.google.gwt.canvas.dom.client.CanvasGradient;

//import java.awt.*;
//import java.util.StringTokenizer;

    class ResistorElm extends CircuitElm {
	double resistance;
	public ResistorElm(int xx, int yy) { super(xx, yy); resistance = 1000; }
	public ResistorElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    resistance = new Double(st.nextToken()).doubleValue();
	}
	int getDumpType() { return 'r'; }
	String dump() {
	    return super.dump() + " " + resistance;
	}

	Point ps3, ps4;
	void setPoints() {
	    super.setPoints();
	    calcLeads(32);
	    ps3 = new Point();
	    ps4 = new Point();
	}
	
	void draw(Graphics g) {
	    int segments = 16;
	    int i;
	    int ox = 0;
	    //int hs = sim.euroResistorCheckItem.getState() ? 6 : 8;
	    int hs=6;
	    double v1 = volts[0];
	    double v2 = volts[1];
	    setBbox(point1, point2, hs);
	    draw2Leads(g);
	    setPowerColor(g, true);
	 //   double segf = 1./segments;
	    double len = distance(lead1, lead2);
    	g.context.save();
    	g.context.setLineWidth(3.0);
    	g.context.setTransform(((double)(lead2.x-lead1.x))/len, ((double)(lead2.y-lead1.y))/len, -((double)(lead2.y-lead1.y))/len,((double)(lead2.x-lead1.x))/len,lead1.x,lead1.y);
    	CanvasGradient grad = g.context.createLinearGradient(0,0,len,0);
    	grad.addColorStop(0, getVoltageColor(g,v1).getHexValue());
    	grad.addColorStop(1.0, getVoltageColor(g,v2).getHexValue());
    	g.context.setStrokeStyle(grad);
	    if (!sim.euroResistorCheckItem.getState()) {
//		// draw zigzag
//		for (i = 0; i != segments; i++) {
//		    int nx = 0;
//		    switch (i & 3) {
//		    case 0: nx = 1; break;
//		    case 2: nx = -1; break;
//		    default: nx = 0; break;
//		    }
//		    double v = v1+(v2-v1)*i/segments;
//		    setVoltageColor(g, v);
//		    interpPoint(lead1, lead2, ps1, i*segf, hs*ox);
//		    interpPoint(lead1, lead2, ps2, (i+1)*segf, hs*nx);
//		    drawThickLine(g, ps1, ps2);
//		    ox = nx;
//		}
	    	g.context.beginPath();
	    	g.context.moveTo(0,0);
	    	for (i=0;i<4;i++){
	    		g.context.lineTo((1+4*i)*len/16, hs);
	    		g.context.lineTo((3+4*i)*len/16, -hs);
	    	}
	    	g.context.lineTo(len, 0);
	    	g.context.stroke();
	    	
	    } else    {
		// draw rectangle
//		setVoltageColor(g, v1);
//		interpPoint2(lead1, lead2, ps1, ps2, 0, hs);
//		drawThickLine(g, ps1, ps2);
//		for (i = 0; i != segments; i++) {
//		    double v = v1+(v2-v1)*i/segments;
//		    setVoltageColor(g, v);
//		    interpPoint2(lead1, lead2, ps1, ps2, i*segf, hs);
//		    interpPoint2(lead1, lead2, ps3, ps4, (i+1)*segf, hs);
//		    drawThickLine(g, ps1, ps3);
//		    drawThickLine(g, ps2, ps4);
//		}
//		interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
//		drawThickLine(g, ps1, ps2);
	    	
	    	g.context.strokeRect(0, -hs, len, 2.0*hs);
	    }
    	g.context.restore();
	    if (sim.showValuesCheckItem.getState()) {
		String s = getShortUnitText(resistance, "");
		drawValues(g, s, hs);
	    }
	    doDots(g);
	    drawPosts(g);
	}
    
	void calculateCurrent() {
	    current = (volts[0]-volts[1])/resistance;
	    //System.out.print(this + " res current set to " + current + "\n");
	}
	void stamp() {
	    sim.stampResistor(nodes[0], nodes[1], resistance);
	}
	void getInfo(String arr[]) {
	    arr[0] = "resistor";
	    getBasicInfo(arr);
	    arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
	    arr[4] = "P = " + getUnitText(getPower(), "W");
	}
	public EditInfo getEditInfo(int n) {
	    // ohmString doesn't work here on linux
	    if (n == 0)
		return new EditInfo("Resistance (ohms)", resistance, 0, 0);
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (ei.value > 0)
	        resistance = ei.value;
	}
	int getShortcut() { return 'r'; }
    }
