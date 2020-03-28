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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;

class EditInfo {
    	// mn/mx were used in the java version to create sliders in the edit dialog but we don't do that in the javascript version, so this
    	// constructor is deprecated
	EditInfo(String n, double val, double mn, double mx) {
		name = n;
		value = val;
		dimensionless = false;
	}
	
	EditInfo(String n, double val) {
		name = n;
		value = val;
		dimensionless = false;
	}
		
	EditInfo setDimensionless() { dimensionless = true; return this; }
	EditInfo disallowSliders() { noSliders = true; return this; }
	int changeFlag(int flags, int bit) {
	    if (checkbox.getState())
		return flags | bit;
	    return flags & ~bit;
	}
	
	String name, text;
	double value;
	TextBox textf;
	Choice choice;
	Checkbox checkbox;
	Button button;
	TextArea textArea;
	Widget widget;
	boolean newDialog;
	boolean dimensionless;
	boolean noSliders;
	
	// for slider dialog
	TextBox minBox, maxBox, labelBox;
	
	boolean canCreateAdjustable() {
	    return choice == null && checkbox == null && button == null && textArea == null &&
			widget == null && !noSliders;
	}
}
    
