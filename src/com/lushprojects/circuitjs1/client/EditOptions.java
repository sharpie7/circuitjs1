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
			return new EditInfo("Time step size (s)", sim.maxTimeStep, 0, 0);
		if (n == 1)
			return new EditInfo("Range for voltage color (V)",
					CircuitElm.voltageRange, 0, 0);
		if (n == 2) {
	            EditInfo ei =  new EditInfo("Change Language", 0, -1, -1);
	            ei.choice = new Choice();
	            ei.choice.add("(no change)");
	            ei.choice.add("Čeština");
	            ei.choice.add("Dansk");
	            ei.choice.add("Deutsch");
	            ei.choice.add("English");
	            ei.choice.add("Español");
	            ei.choice.add("Français");
	            ei.choice.add("Italiano");
	            ei.choice.add("Norsk bokmål");
	            ei.choice.add("Polski");
	            ei.choice.add("Português");
	            ei.choice.add("\u0420\u0443\u0441\u0441\u043a\u0438\u0439"); // Russian 
	            ei.choice.add("\u4e2d\u6587 (\u4e2d\u56fd\u5927\u9646)"); // Chinese 
	            ei.choice.add("\u4e2d\u6587 (\u53f0\u6e7e)"); // Chinese (tw) 
	            return ei;
		}
		
		if (n == 3)
		    return new EditInfo("Positive Color", CircuitElm.positiveColor.getHexValue());
		if (n == 4)
		    return new EditInfo("Negative Color", CircuitElm.negativeColor.getHexValue());
		if (n == 5)
		    return new EditInfo("Selection Color", CircuitElm.selectColor.getHexValue());
		if (n == 6)
		    return new EditInfo("Current Color", CircuitElm.currentColor.getHexValue());
		if (n == 7) {
		    EditInfo ei = new EditInfo("", 0, -1, -1);
		    ei.checkbox = new Checkbox("Auto-Adjust Timestep", sim.adjustTimeStep);
		    return ei;
		}
		if (n == 8 && sim.adjustTimeStep)
		    return new EditInfo("Minimum time step size (s)", sim.minTimeStep, 0, 0);

		return null;
	}
	
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0 && ei.value > 0) {
			sim.maxTimeStep = ei.value;

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
		    	// Czech is csx instead of cs because we are not ready to use it automatically yet
		    	case 1: langString = "csx"; break;
		    	case 2: langString = "da"; break;
		    	case 3: langString = "de"; break;
		    	case 4: langString = "en"; break;
		    	case 5: langString = "es"; break;
		    	case 6: langString = "fr"; break;
		    	case 7: langString = "it"; break;
		    	case 8: langString = "nb"; break;
		    	case 9: langString = "pl"; break;
			case 10: langString = "pt"; break;
		    	case 11: langString = "ru"; break;
		    	case 12: langString = "zh"; break;
		    	case 13: langString = "zh-tw"; break;
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
		if (n == 3) {
		    String txt = ei.textf.getText();
		    Storage stor = Storage.getLocalStorageIfSupported();
		    if (stor != null)
			stor.setItem("positiveColor", txt);
		    CircuitElm.positiveColor = (txt.length() == 0) ? Color.green : new Color(txt);
		    CircuitElm.setColorScale();
		}
		if (n == 4) {
		    String txt = ei.textf.getText();
		    Storage stor = Storage.getLocalStorageIfSupported();
		    if (stor != null)
			stor.setItem("negativeColor", txt);
		    CircuitElm.negativeColor = (txt.length() == 0) ? Color.red : new Color(txt);
		    CircuitElm.setColorScale();
		}
		if (n == 5) {
		    String txt = ei.textf.getText();
		    Storage stor = Storage.getLocalStorageIfSupported();
		    if (stor != null)
			stor.setItem("selectColor", txt);
		    CircuitElm.selectColor = (txt.length() == 0) ? Color.cyan : new Color(txt);
		}
		if (n == 6) {
		    String txt = ei.textf.getText();
		    Storage stor = Storage.getLocalStorageIfSupported();
		    if (stor != null)
			stor.setItem("currentColor", txt);
		    CircuitElm.currentColor = (txt.length() == 0) ? Color.yellow : new Color(txt);
		}
		if (n == 7) {
		    sim.adjustTimeStep = ei.checkbox.getState();
		    ei.newDialog = true;
		}
		if (n == 8 && ei.value > 0)
			sim.minTimeStep = ei.value;
	}
};
