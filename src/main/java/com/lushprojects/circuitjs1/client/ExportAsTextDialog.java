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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.safehtml.shared.SafeHtml;

public class ExportAsTextDialog extends DialogBox {
	
	VerticalPanel vp;
	CirSim sim;
	TextArea textArea;
	
	public ExportAsTextDialog(CirSim asim, String s) {
		super();
		sim = asim;
	//	RichTextArea tb;
		TextArea ta;
		Button okButton, importButton;
		Label  la2;
		SafeHtml html;
		vp=new VerticalPanel();
		setWidget(vp);
		setText(sim.LS("Export as Text"));
		vp.add(new Label(sim.LS("Text file for this circuit is...")));
//		vp.add(tb = new RichTextArea());
//		html=SafeHtmlUtils.fromString(s);
//		html=SafeHtmlUtils.fromTrustedString(html.asString().replace("\n", "<BR>"));
//		tb.setHTML(html);
		vp.add(ta= new TextArea());
		ta.setWidth("300px");
		ta.setHeight("200px");
		ta.setText(s);
		textArea = ta;
		vp.add(la2 = new Label(sim.LS("To save this file select it all (eg click in text and type control-A) and copy to your clipboard (eg control-C) before pasting to an empty text file (eg on Windows Notepad) and saving as a new file."), true));
		la2.setWidth("300px");
		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp.setStyleName("topSpace");
		vp.add(hp);
		hp.add(okButton = new Button(sim.LS("OK")));
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hp.add(importButton = new Button(sim.LS("Re-Import")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDialog();
			}
		});
		importButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String s;
				sim.pushUndo();
				closeDialog();
//				s=textBox.getHTML();
//				s=s.replace("<br>", "\r");
				s=textArea.getText();
				if (s!=null)
					sim.readSetup(s, true);
			}
		});
		this.center();
	}
	
	protected void closeDialog()
	{
		this.hide();
	}

}