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

class EditOptions implements Editable {
	CirSim sim;
	
	public EditOptions(CirSim s) { sim = s; }
	
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Time step size (s)", sim.timeStep, 0, 0);
		if (n == 1)
			return new EditInfo("Range for voltage color (V)",
					CircuitElm.voltageRange, 0, 0);

		return null;
	}
	
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0 && ei.value > 0) {
			sim.timeStep = ei.value;

			// if timestep changed manually, prompt before changing it again
			AudioOutputElm.okToChangeTimeStep = false;
		}
		if (n == 1 && ei.value > 0)
			CircuitElm.voltageRange = ei.value;
	}
};
