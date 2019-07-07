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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.Command;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;

class PotElm extends CircuitElm implements Command, MouseWheelHandler {
    final int FLAG_SHOW_VALUES = 1;
	double position, maxResistance, resistance1, resistance2;
	double current1, current2, current3;
	double curcount1, curcount2, curcount3;
	Scrollbar slider;
	Label label;
	String sliderText;
	public PotElm(int xx, int yy) {
		super(xx, yy);
		setup();
		maxResistance = 1000;
		position = .5;
		sliderText = "Resistance";
		flags = FLAG_SHOW_VALUES;
		createSlider();
	}
    
    public PotElm(int xa, int ya, int xb, int yb, int f,
    		StringTokenizer st) {
    	super(xa, ya, xb, yb, f);
    	maxResistance = new Double(st.nextToken()).doubleValue();
    	position = new Double(st.nextToken()).doubleValue();
    	sliderText = st.nextToken();
    	while (st.hasMoreTokens())
    		sliderText += ' ' + st.nextToken();
    	createSlider();
    }
    
    void setup() {
    }
    
    int getPostCount() { return 3; }
    int getDumpType() { return 174; }
    
    Point getPost(int n) {
	return (n == 0) ? point1 : (n == 1) ? point2 : post3;
    }
    
    String dump() { return super.dump() + " " + maxResistance + " " +
    		position + " " + sliderText; }
    
    void createSlider() {
    	sim.addWidgetToVerticalPanel(label = new Label(sliderText));
    	label.addStyleName("topSpace");
    	int value = (int) (position*100);
    	sim.addWidgetToVerticalPanel(slider = new Scrollbar(Scrollbar.HORIZONTAL, value, 1, 0, 101, this, this));
   // 	sim.verticalPanel.validate();
   // 	slider.addAdjustmentListener(this);
    }
    
    public void execute() {
	sim.analyzeFlag = true;
	setPoints();
    }
    
    void delete() {
	sim.removeWidgetFromVerticalPanel(label);
	sim.removeWidgetFromVerticalPanel(slider);
        super.delete();
    }
    
    Point post3, corner2, arrowPoint, midpoint, arrow1, arrow2;
    Point ps3, ps4;
    int bodyLen;
    
    void setPoints() {
	super.setPoints();
	int offset = 0;
	int myLen =0;
	if (abs(dx) > abs(dy)) {
	    myLen =  2 * sim.gridSize * Integer.signum(dx) * ((((Integer)Math.abs(dx))+ 2 * sim.gridSize -1) / (2 * sim.gridSize));
	    point2.x =  point1.x + myLen;
	    offset = (dx < 0) ? dy : -dy;
	    point2.y = point1.y;
	} else {
	    myLen =  2 * sim.gridSize * Integer.signum(dy) * ((((Integer)Math.abs(dy))+ 2 * sim.gridSize -1) / (2 * sim.gridSize));
	    if (dy != 0) {
		point2.y = point1.y + myLen;
		offset = (dy > 0) ? dx : -dx;
		point2.x = point1.x;
	    }
	}
//	if (abs(dx) > abs(dy)) {
//	    dx = Integer.signum(dx) * sim.snapGrid(Math.abs(dx) / 2) * 2;
//	    point2.x = x2 = point1.x + dx;
//	    offset = (dx < 0) ? dy : -dy;
//	    point2.y = point1.y;
//	} else {
//	    dy = Integer.signum(dy) * sim.snapGrid(Math.abs(dy) / 2) * 2;
//	    if (dy != 0) {
//		point2.y = y2 = point1.y + dy;
//		offset = (dy > 0) ? dx : -dx;
//		point2.x = point1.x;
//	    }
//	}
	if (offset == 0)
	    offset = sim.gridSize;
	dn = distance(point1, point2);
	int bodyLen = 32;
	calcLeads(bodyLen);
	position = slider.getValue() * .0099 + .005;
	int soff = (int) ((position - .5) * bodyLen);
	// int offset2 = offset - sign(offset)*4;
	post3 = interpPoint(point1, point2, .5, offset);
	corner2 = interpPoint(point1, point2, soff / dn + .5, offset);
	arrowPoint = interpPoint(point1, point2, soff / dn + .5, 8 * sign(offset));
	midpoint = interpPoint(point1, point2, soff / dn + .5);
	arrow1 = new Point();
	arrow2 = new Point();
	double clen = abs(offset) - 8;
	interpPoint2(corner2, arrowPoint, arrow1, arrow2, (clen - 8) / clen, 8);
	ps3 = new Point();
	ps4 = new Point();
    }
    
	
    void draw(Graphics g) {
	int segments = 16;
	int i;
	int ox = 0;
	int hs = sim.euroResistorCheckItem.getState() ? 6 : 8;
	double v1 = volts[0];
	double v2 = volts[1];
	double v3 = volts[2];
	setBbox(point1, point2, hs);
	draw2Leads(g);
	setPowerColor(g, true);
	double segf = 1./segments;
	int divide = (int) (segments*position);
	if (!sim.euroResistorCheckItem.getState()) {
	    // draw zigzag
	    for (i = 0; i != segments; i++) {
		int nx = 0;
		switch (i & 3) {
		case 0: nx = 1; break;
		case 2: nx = -1; break;
		default: nx = 0; break;
		}
		double v = v1+(v3-v1)*i/divide;
		if (i >= divide)
		    v = v3+(v2-v3)*(i-divide)/(segments-divide);
		setVoltageColor(g, v);
		interpPoint(lead1, lead2, ps1, i*segf, hs*ox);
		interpPoint(lead1, lead2, ps2, (i+1)*segf, hs*nx);
		drawThickLine(g, ps1, ps2);
		ox = nx;
	    }
	} else {
	    // draw rectangle
	    setVoltageColor(g, v1);
	    interpPoint2(lead1, lead2, ps1, ps2, 0, hs);
	    drawThickLine(g, ps1, ps2);
	    for (i = 0; i != segments; i++) {
		double v = v1+(v3-v1)*i/divide;
		if (i >= divide)
		    v = v3+(v2-v3)*(i-divide)/(segments-divide);
		setVoltageColor(g, v);
		interpPoint2(lead1, lead2, ps1, ps2, i*segf, hs);
		interpPoint2(lead1, lead2, ps3, ps4, (i+1)*segf, hs);
		drawThickLine(g, ps1, ps3);
		drawThickLine(g, ps2, ps4);
	    }
	    interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
	    drawThickLine(g, ps1, ps2);
	}
	setVoltageColor(g, v3);
	drawThickLine(g, post3, corner2);
	drawThickLine(g, corner2, arrowPoint);
	drawThickLine(g, arrow1, arrowPoint);
	drawThickLine(g, arrow2, arrowPoint);
	curcount1 = updateDotCount(current1, curcount1);
	curcount2 = updateDotCount(current2, curcount2);
	curcount3 = updateDotCount(current3, curcount3);
	if (sim.dragElm != this) {
	    drawDots(g, point1, midpoint, curcount1);
	    drawDots(g, point2, midpoint, curcount2);
	    drawDots(g, post3, corner2, curcount3);
	    drawDots(g, corner2, midpoint,
		     curcount3+distance(post3, corner2));
	}
	drawPosts(g);

	if (sim.showValuesCheckItem.getState() && resistance1 > 0 && (flags & FLAG_SHOW_VALUES) != 0) {
	    // check for vertical pot with 3rd terminal on left
	    boolean reverseY = (post3.x < lead1.x && lead1.x == lead2.x);
	    // check for horizontal pot with 3rd terminal on top
	    boolean reverseX = (post3.y < lead1.y && lead1.x != lead2.x);
	    // check if we need to swap texts (if leads are reversed, e.g. drawn right to left)
	    boolean rev = (lead1.x == lead2.x && lead1.y < lead2.y) || (lead1.y == lead2.y && lead1.x > lead2.x);
	    
	    // draw units
	    String s1 = getShortUnitText(rev ? resistance2 : resistance1, "");
	    String s2 = getShortUnitText(rev ? resistance1 : resistance2, "");
	    g.setFont(unitsFont);
	    g.setColor(whiteColor);
	    int ya = (int)g.currentFontSize/2;
	    int w;
	    w = (int)g.context.measureText(s1).getWidth();
	    
	    // vertical?
	    if (lead1.x == lead2.x)
		g.drawString(s1, !reverseY ? arrowPoint.x+2 : arrowPoint.x-2-w, Math.max(arrow1.y, arrow2.y)+5+ya);
	    else
		g.drawString(s1, Math.min(arrow1.x, arrow2.x)-2-w, !reverseX ? arrowPoint.y+4+ya : arrowPoint.y-4);
	    
	    w = (int)g.context.measureText(s2).getWidth();
	    if (lead1.x == lead2.x)
		g.drawString(s2, !reverseY ? arrowPoint.x+2 : arrowPoint.x-2-w, Math.min(arrow1.y, arrow2.y)-3);
	    else
		g.drawString(s2, Math.max(arrow1.x, arrow2.x)+2, !reverseX ? arrowPoint.y+4+ya : arrowPoint.y-4); 
	}
    }
    
    // draw component values (number of resistor ohms, etc).  hs = offset
    void drawValues(Graphics g, String s, Point pt, int hs) {
	if (s == null)
	    return;
	g.setFont(unitsFont);
	//FontMetrics fm = g.getFontMetrics();
	int w = (int)g.context.measureText(s).getWidth();
	g.setColor(whiteColor);
	int ya = (int)g.currentFontSize/2;
	int xc = pt.x;
	int yc = pt.y;
	int dpx = hs;
	int dpy = 0;
	if (lead1.x != lead2.x) {
	    dpx = 0;
	    dpy = -hs;
	}
	sim.console("dv " + dpx + " " + w);
	if (dpx == 0)
	    g.drawString(s, xc-w/2, yc-abs(dpy)-2);
	else {
	    int xx = xc+abs(dpx)+2;
	    g.drawString(s, xx, yc+dpy+ya);
	}
    }
    
    void reset() {
	curcount1 = curcount2 = curcount3 = 0;
	super.reset();
    }
    void calculateCurrent() {
	if (resistance1 == 0)
	    return; // avoid NaN
	current1 = (volts[0]-volts[2])/resistance1;
	current2 = (volts[1]-volts[2])/resistance2;
	current3 = -current1-current2;
    }
    
    @Override double getCurrentIntoNode(int n) {
	if (n == 0)
	    return -current1;
	if (n == 1)
	    return -current2;
	return -current3;
    }
    
    void stamp() {
	resistance1 = maxResistance*position;
	resistance2 = maxResistance*(1-position);
	sim.stampResistor(nodes[0], nodes[2], resistance1);
	sim.stampResistor(nodes[2], nodes[1], resistance2);
    }
    void getInfo(String arr[]) {
	arr[0] = "potentiometer";
	arr[1] = "Vd = " + getVoltageDText(getVoltageDiff());
	arr[2] = "R1 = " + getUnitText(resistance1, sim.ohmString);
	arr[3] = "R2 = " + getUnitText(resistance2, sim.ohmString);
	arr[4] = "I1 = " + getCurrentDText(current1);
	arr[5] = "I2 = " + getCurrentDText(current2);
    }
    public EditInfo getEditInfo(int n) {
	// ohmString doesn't work here on linux
	if (n == 0)
	    return new EditInfo("Resistance (ohms)", maxResistance, 0, 0);
	if (n == 1) {
	    EditInfo ei = new EditInfo("Slider Text", 0, -1, -1);
	    ei.text = sliderText;
	    return ei;
	}
	if (n == 2) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new Checkbox("Show Values", (flags & FLAG_SHOW_VALUES) != 0);
            return ei;
	}
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    maxResistance = ei.value;
	if (n == 1) {
	    sliderText = ei.textf.getText();
	    label.setText(sliderText);
	    sim.setiFrameHeight();
	}
	if (n == 2)
	    flags = ei.changeFlag(flags, FLAG_SHOW_VALUES);
    }
    void setMouseElm(boolean v) {
    	super.setMouseElm(v);
    	if (slider!=null)
    		slider.draw();
    }
    
    public void onMouseWheel(MouseWheelEvent e) {
    	if (slider!=null)
    		slider.onMouseWheel(e);
    }
}

