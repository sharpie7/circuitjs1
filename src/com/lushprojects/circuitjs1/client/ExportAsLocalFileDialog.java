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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lushprojects.circuitjs1.client.util.Locale;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.i18n.client.DateTimeFormat;

public class ExportAsLocalFileDialog extends Dialog implements ValueChangeHandler<String> {
	
	VerticalPanel vp;
	
	static public final native boolean downloadIsSupported() 
	/*-{
		return !!(("download" in $doc.createElement("a")));
	 }-*/;
	
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
	
	TextBox textBox;
	static String lastFileName;
	String url;
	
	public static void setLastFileName(String s) {
	    // remember filename for use when saving a new file.
	    // if s is null or automatically generated then just clear out old filename.
	    if (s == null || (s.startsWith("circuit-") && s.contains(".circuitjs")))
		lastFileName = null;
	    else
		lastFileName = s;
	}

	public ExportAsLocalFileDialog(String data) {
		super();
		Button okButton, cancelButton;
		vp=new VerticalPanel();
		setWidget(vp);
		setText(Locale.LS("Export as Local File"));
		vp.add(new Label(Locale.LS("File name:")));
		textBox = new TextBox();
                textBox.addValueChangeHandler(this);
		textBox.setWidth("250px"); // "90%");
		vp.add(textBox);
		url=getBlobUrl(data);
		Date date = new Date();
		String fname;
		if (lastFileName != null)
		    fname = lastFileName;
		else {
		    DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd-HHmm");
		    fname = "circuit-"+ dtf.format(date) + ".circuitjs.txt";
		}
		textBox.setText(fname);
		
                HorizontalPanel hp = new HorizontalPanel();
                hp.setWidth("100%");
                hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
                hp.setStyleName("topSpace");
                vp.add(hp);
                hp.add(okButton = new Button(Locale.LS("OK")));
                hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hp.add(cancelButton = new Button(Locale.LS("Cancel")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			    apply();
			    closeDialog();
			}
		});
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			    closeDialog();
			}
		});
		this.center();
	}
	
	static native void click(Element elem) /*-{
	    elem.click();
	}-*/;
	
	void apply() {
	    String fname = textBox.getText();
	    if (!fname.contains("."))
		fname += ".txt";
	    Anchor a  = new Anchor(fname, url);
	    a.getElement().setAttribute("Download", fname);
	    vp.add(a);
	    click(a.getElement());
	}
	
	public void onValueChange(ValueChangeEvent<String> event) {
	    // update filename
	    String fname = textBox.getText();
	    if (fname.length() == 0)
		return;
	    lastFileName = fname;
	}
}
