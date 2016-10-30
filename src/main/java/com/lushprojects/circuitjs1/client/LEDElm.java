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

//import java.awt.*;
//import java.util.StringTokenizer;

    class LEDElm extends DiodeElm {
	double colorR, colorG, colorB;
	public LEDElm(int xx, int yy) {
	    super(xx, yy);
	    fwdrop = 2.1024259;
	    setup();
	    colorR = 1; colorG = colorB = 0;
	}
	public LEDElm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    if ((f & FLAG_FWDROP) == 0)
		fwdrop = 2.1024259;
	    setup();
	    colorR = new Double(st.nextToken()).doubleValue();
	    colorG = new Double(st.nextToken()).doubleValue();
	    colorB = new Double(st.nextToken()).doubleValue();
	}
	int getDumpType() { return 162; }
	String dump() {
	    return super.dump() + " " + colorR + " " + colorG + " " + colorB;
	}

	Point ledLead1, ledLead2, ledCenter;
	void setPoints() {
	    super.setPoints();
	    int cr = 12;
	    ledLead1  = interpPoint(point1, point2, .5-cr/dn);
	    ledLead2  = interpPoint(point1, point2, .5+cr/dn);
	    ledCenter = interpPoint(point1, point2, .5);
	}
	
	void draw(Graphics g) {
	    if (needsHighlight() || this == sim.dragElm) {
		super.draw(g);
		return;
	    }
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, ledLead1);
	    setVoltageColor(g, volts[1]);
	    drawThickLine(g, ledLead2, point2);
	    
	    g.setColor(Color.gray);
	    int cr = 12;
	    drawThickCircle(g, ledCenter.x, ledCenter.y, cr);
	    cr -= 4;
	    double w = 255*current/.01;
	    if (w > 255)
		w = 255;
	    Color cc = new Color((int) (colorR*w), (int) (colorG*w),
				 (int) (colorB*w));
	    g.setColor(cc);
	    g.fillOval(ledCenter.x-cr, ledCenter.y-cr, cr*2, cr*2);
	    setBbox(point1, point2, cr);
	    updateDotCount();
	    drawDots(g, point1, ledLead1, curcount);
	    drawDots(g, point2, ledLead2, -curcount);
	    drawPosts(g);
	}

	void getInfo(String arr[]) {
	    super.getInfo(arr);
	    arr[0] = "LED";
	}

	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return super.getEditInfo(n);
	    if (n == 1)
		return new EditInfo("Red Value (0-1)", colorR, 0, 1).
		    setDimensionless();
	    if (n == 2)
		return new EditInfo("Green Value (0-1)", colorG, 0, 1).
		    setDimensionless();
	    if (n == 3)
		return new EditInfo("Blue Value (0-1)", colorB, 0, 1).
		    setDimensionless();
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0)
		super.setEditValue(0, ei);
	    if (n == 1)
		colorR = ei.value;
	    if (n == 2)
		colorG = ei.value;
	    if (n == 3)
		colorB = ei.value;
	}
	int getShortcut() { return 'l'; }
    }
