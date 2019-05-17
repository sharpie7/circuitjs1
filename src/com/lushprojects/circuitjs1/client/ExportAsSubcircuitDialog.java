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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.i18n.client.DateTimeFormat;

// XXX move this if possible
class ExtListEntry {
    ExtListEntry(String s, int n) { name = s; node = n; }
    String name;
    int node;
};

// XXX get rid of this if possible
class DeviceInfo {
    Vector<ExtListEntry> extList;
    String dump;
    String nodeList;
    String models;
};

public class ExportAsSubcircuitDialog extends DialogBox {
	
	VerticalPanel vp;
	boolean error;
	
	boolean error() { return error; }
	
        static public final native String getBlobUrl(String data) 
        /*-{
                var datain=[""];
                datain[0]=data;
                var oldblob = $doc.exportBlob;
                if (oldblob)
                    URL.revokeObjectURL(oldblob);
                var blob=new Blob(datain, {type: 'text/plain' } );
                var url = URL.createObjectURL(blob);
                $doc.exportBlob = url;
                return url;
        }-*/;

	public ExportAsSubcircuitDialog() {
		super();
	    	error = false;

		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd-HHmm");
		DeviceInfo di = new DeviceInfo();
		if (!CirSim.theSim.getCircuitAsCustomDevice(di)) {
		    error = true;
		    return;
		}
		if (di.extList.size() == 0) {
		    Window.alert(CirSim.LS("Device has no external inputs/outputs!"));
		    error = true;
		    return;
		}
		Collections.sort(di.extList, new Comparator<ExtListEntry>() {
		    public int compare(ExtListEntry a, ExtListEntry b) {
			return a.name.toLowerCase().compareTo(b.name.toLowerCase());
		    }
		});
		int i;
		for (i = 0; i != di.extList.size()-1; i++)
		    if (di.extList.get(i).name.equals(di.extList.get(i+1).name)) {
			Window.alert(CirSim.LS("Input names must be unique"));
			error = true;
			return;
		    }
	    	
	    	String name = Window.prompt(CirSim.LS("Model Name"), "");
	    	if (name == null || name.length() == 0) {
	    	    error = true;
	    	    return;
	    	}

		Button okButton;
		Anchor a;
		vp=new VerticalPanel();
		setWidget(vp);
		setText(CirSim.LS("Export as Subcircuit"));
		vp.add(new Label(CirSim.LS("Click on the link below to save your model")));
		Date date = new Date();

		CustomCompositeElm.lastModelName = name;
		CustomCompositeModel model = CustomCompositeModel.createModel(name, di.dump, di.nodeList, di.extList);
		String text = "$ 1 0.000005 10 50 5 43\n";
		text += di.models;
		text += model.dump() + "\n";
		String dataURL = getBlobUrl(text);
		a=new Anchor(name + ".txt", dataURL);
		String fname = "circuit-"+ dtf.format(date) + ".txt";
		a.getElement().setAttribute("Download", fname);
		vp.add(a);
		vp.add(okButton = new Button(CirSim.LS("OK")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDialog();
			}
		});
		this.center();
	}
	
	protected void closeDialog()
	{
		this.hide();
	}

}
