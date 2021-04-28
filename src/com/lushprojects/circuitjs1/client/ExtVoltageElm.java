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

    class ExtVoltageElm extends RailElm {
	public ExtVoltageElm(int xx, int yy) { super(xx, yy, WF_AC); name = "ext"; }
	public ExtVoltageElm(int xa, int ya, int xb, int yb, int f,
		       StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    name = CustomLogicModel.unescape(st.nextToken());
	    waveform = WF_AC;
	}
	
        String name;
        double voltage;

        String dump() { return super.dump() + " " + CustomLogicModel.escape(name); }
        
	void drawRail(Graphics g) {
	    drawRailText(g, name);
	}
	void setVoltage(double v) { if (!Double.isNaN(v)) voltage = v; }
	String getName() { return name; }
	
	double getVoltage() {
            return voltage;
	}

	int getDumpType() { return 418; }
	int getShortcut() { return 0; }

	public EditInfo getEditInfo(int n) {
	    if (n == 0) {
		EditInfo ei = new EditInfo("Name", 0, -1, -1);
		ei.text = name;
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0)
		name = ei.textf.getText();
	}

	void getInfo(String arr[]) {
	    super.getInfo(arr);
	    arr[0] = sim.LS("ext. voltage") + " (" + name + ")";
	}
    }
