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

    class StopTriggerElm extends CircuitElm {
	double triggerVoltage;
	boolean triggered, stopped;
	double delay, triggerTime;
	int type;
	
	public StopTriggerElm(int xx, int yy) {
	    super(xx, yy);
	    triggerVoltage = 1;
	}
	public StopTriggerElm(int xa, int ya, int xb, int yb, int f,
			 StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    triggerVoltage = Double.parseDouble(st.nextToken());
	    type = Integer.parseInt(st.nextToken());
	    delay = Double.parseDouble(st.nextToken());
	}
	String dump() { return super.dump() + " " + triggerVoltage + " " + type + " " + delay; }
	void reset() {
	    triggered = false;
	}
	int getDumpType() { return 408; }
	int getPostCount() { return 1; }
	void setPoints() {
	    super.setPoints();
	    lead1 = new Point();
	}
	void draw(Graphics g) {
	    boolean selected = needsHighlight() || stopped;
	    Font f = new Font("SansSerif", selected ? Font.BOLD : 0, 14);
	    g.setFont(f);
	    g.setColor(selected ? selectColor : whiteColor);
	    String s = "trigger";
	    interpPoint(point1, point2, lead1, 1-((int)g.context.measureText(s).getWidth()/2+8)/dn);
	    setBbox(point1, lead1, 0);
	    drawCenteredText(g, s, x2, y2, true);
	    setVoltageColor(g, volts[0]);
	    if (selected)
		g.setColor(selectColor);
	    drawThickLine(g, point1, lead1);
	    drawPosts(g);
	}
	void stepFinished() {
	    stopped = false;
	    if (!triggered && ((type == 0 && volts[0] >= triggerVoltage) || (type == 1 && volts[0] <= triggerVoltage))) {
		triggered = true;
		triggerTime = sim.t;
	    }
	    if (triggered && sim.t >= triggerTime+delay) {
		triggered = false;
		stopped = true;
		sim.setSimRunning(false);
	    }
	}
	
	double getVoltageDiff() { return volts[0]; }
	void getInfo(String arr[]) {
	    arr[0] = "stop trigger";
	    arr[1] = "V = " + getVoltageText(volts[0]);
	    arr[2] = "Vtrigger = " + getVoltageText(triggerVoltage);
	    arr[3] = (triggered) ? ("stopping in " + getUnitText(triggerTime+delay-sim.t, "s")) : (stopped) ? "stopped" : "waiting";
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0) {
		EditInfo ei = new EditInfo("Voltage", triggerVoltage);
		return ei;
	    }
	    if (n == 1) {
		EditInfo ei =  new EditInfo("Trigger Type", type, -1, -1);
		ei.choice = new Choice();
		ei.choice.add(">=");
		ei.choice.add("<=");
		ei.choice.select(type);
		return ei;
	    }
	    if (n == 2) {
		EditInfo ei = new EditInfo("Delay (s)", delay);
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0)
		triggerVoltage = ei.value;
	    if (n == 1)
		type = ei.choice.getSelectedIndex();
	    if (n == 2)
		delay = ei.value;
	}
    }
