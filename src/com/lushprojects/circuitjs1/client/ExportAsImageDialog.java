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

import java.util.Date;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.i18n.client.DateTimeFormat;

public class ExportAsImageDialog extends Dialog {
	
	VerticalPanel vp;
	
	private static native String b64encode(String a) /*-{
	  // string may have unicode text strings in it, so we don't just call btoa() 
	  return window.btoa(unescape(encodeURIComponent(a)));
	}-*/;

	public ExportAsImageDialog(int type) {
		super();
		Button okButton;
		Anchor a;
		vp=new VerticalPanel();
		setWidget(vp);
		setText(CirSim.LS("Export as Image"));
		vp.add(new Label(CirSim.LS("Click on the link below to save your image")));
		Date date = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd-HHmm");
		String dataURL;
		String ext = ".png";
		if (type == CirSim.CAC_IMAGE) {
		    dataURL = CirSim.theSim.getCircuitAsCanvas(type).toDataUrl();
		} else {
		    String data = CirSim.theSim.getCircuitAsSVG();
		    dataURL = "data:text/plain;base64," + b64encode(data);
		    ext = ".svg";
		}
		a=new Anchor("image" + ext, dataURL);
		String fname = "circuit-"+ dtf.format(date) + ext;
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
}
