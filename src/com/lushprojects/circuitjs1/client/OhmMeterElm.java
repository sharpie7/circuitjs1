package com.lushprojects.circuitjs1.client;

public class OhmMeterElm extends CurrentElm {
	public OhmMeterElm(int xx, int yy) {
	    super(xx, yy);
	}
	public OhmMeterElm(int xa, int ya, int xb, int yb, int f,
		   StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	}
	int getDumpType() { return 216; }

	void setPoints() {
	    super.setPoints();
	    calcLeads(26);
	}
	void draw(Graphics g) {
	    int cr = 12;
	    draw2Leads(g);
	    setVoltageColor(g, (volts[0]+volts[1])/2);
	    setPowerColor(g, false);
	    
	    drawThickCircle(g, center.x, center.y, cr);
	    drawCenteredText(g, sim.ohmString, center.x, center.y, true);

	    setBbox(point1, point2, cr);
	    doDots(g);
	    if (sim.showValuesCheckItem.getState() && current != 0) {
		String s = getShortUnitText(getVoltageDiff()/current, sim.ohmString);
		if (dx == 0 || dy == 0)
		    drawValues(g, s, cr);
	    }
	    drawPosts(g);
	}
	    double getScopeValue(int x) {
		return (x == Scope.VAL_R) ? getVoltageDiff()/current : super.getScopeValue(x);
	    }
	    int getScopeUnits(int x) {
		return (x == Scope.VAL_R) ? Scope.UNITS_OHMS : super.getScopeUnits(x);
	    }
	    boolean canShowValueInScope(int x) {
		return x == Scope.VAL_R;
	    }

	/*
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Current (A)", currentValue, 0, .1);
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    currentValue = ei.value;
	}
	*/
	void getInfo(String arr[]) {
	    arr[0] = "ohmmeter";
	    if (current == 0)
		arr[1] = "R = \u221e";
	    else
		arr[1] = "R = " + getUnitText(getVoltageDiff()/current, sim.ohmString);
	}
}
