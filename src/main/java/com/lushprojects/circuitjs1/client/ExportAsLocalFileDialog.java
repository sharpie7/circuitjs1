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

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.RichTextArea;

public class ExportAsLocalFileDialog extends DialogBox {
	
	VerticalPanel vp;
	
	static public final native boolean downloadIsSupported() 
	/*-{
		return !!(("download" in $doc.createElement("a")));
	 }-*/;
	
	static public final native String getBlobUrl(String data) 
	/*-{
		var datain=[""];
		datain[0]=data;
		var blob=new Blob(datain, {type: 'text/plain' } );
		var url = URL.createObjectURL(blob);
		return url;
	}-*/;
	
	public ExportAsLocalFileDialog(String data) {
		super();
		Button okButton;
		Anchor a;
		String url;
		vp=new VerticalPanel();
		setWidget(vp);
		setText("Export as Local File");
		vp.add(new Label("Click on the link below to save your circuit"));
		url=getBlobUrl(data);
		a=new Anchor("my circuit.txt", url);
		a.getElement().setAttribute("Download", "my circuit.txt");
		vp.add(a);
		vp.add(okButton = new Button("OK"));
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
