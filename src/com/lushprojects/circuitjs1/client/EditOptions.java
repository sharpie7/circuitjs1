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

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;

class EditOptions implements Editable {
	CirSim sim;
	
	public EditOptions(CirSim s) { sim = s; }
	
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Time step size (s)", sim.timeStep, 0, 0);
		if (n == 1)
			return new EditInfo("Range for voltage color (V)",
					CircuitElm.voltageRange, 0, 0);
		if (n == 2) {
	            EditInfo ei =  new EditInfo("Change Language", 0, -1, -1);
	            ei.choice = new Choice();
	            ei.choice.add("(no change)");
	            ei.choice.add("Dansk");
	            ei.choice.add("Deutsch");
	            ei.choice.add("English");
	            ei.choice.add("Italiano");
	            ei.choice.add("Polski");
	            ei.choice.add("Português");
	            ei.choice.add("\u0420\u0443\u0441\u0441\u043a\u0438\u0439"); // Russian 
	            ei.choice.add("Español");
	            return ei;
		}

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
		if (n == 2) {
		    	int lang = ei.choice.getSelectedIndex();
		    	if (lang == 0)
		    	    return;
		    	String langString = null;
		    	switch (lang) {
		    	case 1: langString = "da"; break;
		    	case 2: langString = "de"; break;
		    	case 3: langString = "en"; break;
		    	case 4: langString = "it"; break;
		    	case 5: langString = "pl"; break;
			case 6: langString = "pt"; break;
		    	case 7: langString = "ru"; break;
		    	case 8: langString = "es"; break;
		    	}
		    	if (langString == null)
		    	    return;
		        Storage stor = Storage.getLocalStorageIfSupported();
		        if (stor == null) {
		            Window.alert(sim.LS("Can't set language"));
		            return;
		        }
		        stor.setItem("language", langString);
		        if (Window.confirm(sim.LS("Must restart to set language.  Restart now?")))
		            Window.Location.reload();
		}
	}
};
