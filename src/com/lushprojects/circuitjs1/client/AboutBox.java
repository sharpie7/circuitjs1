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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Button;

public class AboutBox extends PopupPanel {
	
	VerticalPanel vp;
	Button okButton;
	
	AboutBox(String version) {
		super();
		vp = new VerticalPanel();
		setWidget(vp);
		vp.setWidth("400px");
		vp.add(new HTML("<p>Circuit Simulator version "+version+".</p>"+
		"<p>Original by Paul Falstad.<br><a href=\"http://www.falstad.com/\" target=\"_blank\">http://www.falstad.com/</a></p>"+
		"<p>JavaScript conversion by Iain Sharp.<br><a href=\"http://lushprojects.com/\" target=\"_blank\">http://lushprojects.com/</a></p>"+
		"<p>Thanks to: Edward Calver for 15 new components and other improvements; Rodrigo Hausen for file import/export and many other UI improvements; "+  
		"J. Mike Rollins for the Zener diode code; Julius Schmidt for the spark gap code and some examples; Dustin Soodak for help with the user interface improvements; "+
		"Jacob Calvert for the T Flip Flop; Ben Hayden for scope spectrum; " +
		"Thomas Reitinger for the German translation; " +
		"Krystian Sławiński for the Polish translation; " +
		"Usevalad Khatkevich for the Russian translation; " +
		"Lucio Sciamanna for the Italian translation; " +
		"Mauro Hemerly Gazzani for the Portuguese translation; " +
		"J. Miguel Silva for the Spanish translation; " +
		"Andre Adrian for improved emitter coupled oscillator; Felthry for many examples; " +
		"Colin Howell for code improvements.</p>"+
		"<p style=\"font-size:9px\">This program is free software: you can redistribute it and/or modify it "+
		"under the terms of the GNU General Public License as published by "+
		"the Free Software Foundation, either version 2 of the License, or "+
		"(at your option) any later version.</p>"+
		"<p style=\"font-size:9px\">This program is distributed in the hope that it will be useful,"+
		"but WITHOUT ANY WARRANTY; without even the implied warranty of "+
		"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the "+
		"GNU General Public License for more details.</p>"+
		"<p style=\"font-size:9px\">For details of licensing see <A href=\"http://www.gnu.org/licenses/\" target=\"_blank\">http://www.gnu.org/licenses/</A>.</p>"+
		"<p style=\"font-size:9px\">Source code (Paul):<A href=\"https://github.com/pfalstad/circuitjs1\" target=\"_blank\">https://github.com/pfalstad/circuitjs1</A></p>"+
	    "<p style=\"font-size:9px\">Source code (Iain):<A href=\"https://github.com/sharpie7/circuitjs1\" target=\"_blank\">https://github.com/sharpie7/circuitjs1</A></p>"));
		
		
		vp.add(okButton = new Button("OK"));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				close();
			}
		});
		center();
		show();
	}

	public void close() {
		hide();
	}
}
