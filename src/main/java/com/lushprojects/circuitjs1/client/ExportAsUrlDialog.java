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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.RichTextArea;

public class ExportAsUrlDialog extends DialogBox {
	
	VerticalPanel vp;
	
	public ExportAsUrlDialog( String s) {
		super();
		//TextBox tb;
		RichTextArea tb;
		Button okButton;
		Label la1, la2;
		vp=new VerticalPanel();
		setWidget(vp);
		setText("Export as URL");
		vp.add(new Label("URL for this circuit is..."));
		if (s.length()>2000) {
			vp.add( la1= new Label("Warning: this URL is longer than 2000 characters and may not work in some browsers.", true));
			la1.setWidth("300px");
		}
		vp.add(tb = new RichTextArea());
		tb.setText(s);
//		tb.setMaxLength(s.length());
//		tb.setVisibleLength(s.length());
		vp.add(la2 = new Label("To save this URL select it all (eg click in text and type control-A) and copy to your clipboard (eg control-C) before pasting to a suitable place.", true));
		la2.setWidth("300px");
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
