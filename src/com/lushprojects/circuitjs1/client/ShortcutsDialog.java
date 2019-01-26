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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickHandler;

import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.safehtml.shared.SafeHtml;

public class ShortcutsDialog extends DialogBox {
	
	VerticalPanel vp;
	CirSim sim;
	TextArea textArea;
	Vector<TextBox> textBoxes;
	Button okButton;
	
	public ShortcutsDialog(CirSim asim) {
		super();
		sim = asim;
		Button cancelButton;
		vp=new VerticalPanel();
		setWidget(vp);
		ScrollPanel sp = new ScrollPanel();
		vp.add(sp);
		sp.setHeight("400px");
		sp.setAlwaysShowScrollBars(true);
		setText(sim.LS("Edit Shortcuts"));
		textBoxes = new Vector<TextBox>();
		
		FlexTable table = new FlexTable();
		sp.add(table);
		int i;
		for (i = 0; i != asim.mainMenuItems.size(); i++) {
		    CheckboxMenuItem item = sim.mainMenuItems.get(i);
		    if (item.getShortcut().length() > 1)
			break;
		    table.setText(i, 0, item.getName());
		    TextBox text = new TextBox();
		    text.setText(item.getShortcut());
		    text.setMaxLength(1);
		    text.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent ev) {
			    checkForDuplicates();
			}
		    });
		    table.setWidget(i, 1, text);
		    textBoxes.add(text);
		}

		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp.setStyleName("topSpace");
		vp.add(hp);
		hp.add(okButton = new Button(sim.LS("OK")));
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hp.add(cancelButton = new Button(sim.LS("Cancel")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			    int i;
			    if (checkForDuplicates())
				return;
			    // clear existing shortcuts
			    for (i = 0; i != sim.shortcuts.length; i++)
				sim.shortcuts[i] = null;
			    // load new ones
			    for (i = 0; i != textBoxes.size(); i++) {
				String str = textBoxes.get(i).getText();
				CheckboxMenuItem item = sim.mainMenuItems.get(i);
				item.setShortcut(str);
				if (str.length() > 0)
				    sim.shortcuts[str.charAt(0)] = sim.mainMenuItemNames.get(i);
			    }
			    // save to local storage
			    sim.saveShortcuts();
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
	
	boolean checkForDuplicates() {
	    TextBox boxForShortcut[] = new TextBox[127];
	    boolean result = false;
	    int i;
	    for (i = 0; i != textBoxes.size(); i++) {
		TextBox box = textBoxes.get(i);
		String str = box.getText();
		if (str.length() == 0)
		    continue;
		char c = str.charAt(0);
		
		// check if character if out of range
		if (c > boxForShortcut.length) {
		    box.getElement().getStyle().setColor("red");
		    result = true;
		    continue;
		}
		
		// check for duplicates and mark them
		if (boxForShortcut[c] != null) {
		    box.getElement().getStyle().setColor("red");
		    boxForShortcut[c].getElement().getStyle().setColor("red");
		    result = true;
		} else
		    box.getElement().getStyle().setColor("black");
		
		boxForShortcut[c] = box;
	    }
	    okButton.setEnabled(!result);
	    return result;
	}
	
	protected void closeDialog()
	{
		this.hide();
	}

}
