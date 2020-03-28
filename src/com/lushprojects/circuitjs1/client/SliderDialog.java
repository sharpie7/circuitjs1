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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

// class EditDialog extends Dialog implements AdjustmentListener, ActionListener, ItemListener {
class SliderDialog extends DialogBox  {
	CircuitElm elm;
	CirSim sim;
	Button applyButton, okButton, cancelButton;
	EditInfo einfos[];
	int einfocount;
	final int barmax = 1000;
	VerticalPanel vp;
	HorizontalPanel hp;
	NumberFormat noCommaFormat;

	SliderDialog(CircuitElm ce, CirSim f) {
		super(); // Do we need this?
		setText(CirSim.LS("Add Sliders"));
		sim = f;
		elm = ce;
		vp=new VerticalPanel();
		setWidget(vp);
		einfos = new EditInfo[10];
		noCommaFormat=NumberFormat.getFormat("####.##########");
		hp=new HorizontalPanel();
		hp.setWidth("100%");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp.setStyleName("topSpace");
		vp.add(hp);
		hp.add(applyButton = new Button(CirSim.LS("Apply")));
		applyButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				apply();
			}
		});
		hp.add(okButton = new Button(CirSim.LS("OK")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				apply();
				closeDialog();
			}
		});
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hp.add(cancelButton = new Button(CirSim.LS("Cancel")));
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDialog();
			}
		});
		buildDialog();
		this.center();
	}
	
	void buildDialog() {
		int i;
		int idx;
		for (i = 0; ; i++) {
			einfos[i] = elm.getEditInfo(i);
			if (einfos[i] == null)
				break;
			EditInfo ei = einfos[i];
			if (!ei.canCreateAdjustable())
			    continue;
			Adjustable adj = findAdjustable(i);
			String name = CirSim.LS(ei.name);
			idx = vp.getWidgetIndex(hp);

			// remove HTML
			name = name.replaceAll("<[^>]*>", "");
			ei.checkbox = new Checkbox(name, adj != null);
			vp.insert(ei.checkbox, idx++);
                        ei.checkbox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
                            public void onValueChange(ValueChangeEvent<Boolean> e){
                                    itemStateChanged(e);
                            }
                        });

			if (adj != null) {
			    vp.insert(new Label("Min Value"), idx++);
			    ei.minBox = new TextBox();
			    vp.insert(ei.minBox, idx++);
			    vp.insert(new Label("Max Value"), idx++);
			    ei.maxBox = new TextBox();
			    vp.insert(ei.maxBox, idx++);
			    vp.insert(new Label("Label"), idx++);
			    ei.labelBox = new TextBox();
			    ei.labelBox.setText(adj.sliderText);
			    vp.insert(ei.labelBox, idx++);
			    ei.minBox.setText(EditDialog.unitString(ei, adj.minValue));
			    ei.maxBox.setText(EditDialog.unitString(ei, adj.maxValue));
			}
			    
		}
		einfocount = i;
	}
	
	Adjustable findAdjustable(int item) {
	    return sim.findAdjustable(elm, item);
	}

	void apply() {
		int i;
		for (i = 0; i != einfocount; i++) {
		    Adjustable adj = findAdjustable(i);
		    if (adj == null)
			continue;
		    EditInfo ei = einfos[i];
//		    if (ei.labelBox == null)  // haven't created UI yet?
//			continue;
		    try {
			adj.sliderText = ei.labelBox.getText();
			adj.label.setText(adj.sliderText);
			double d = EditDialog.parseUnits(ei, ei.minBox.getText());
			adj.minValue = d;
			d = EditDialog.parseUnits(ei, ei.maxBox.getText());
			adj.maxValue = d;
			adj.setSliderValue(ei.value);
		    } catch (Exception e) { }
		}
	}

	public void itemStateChanged(GwtEvent e) {
	    Object src = e.getSource();
	    int i;
	    boolean changed = false;
	    for (i = 0; i != einfocount; i++) {
		EditInfo ei = einfos[i];
		if (ei.checkbox == src) {
		    apply();
		    if (ei.checkbox.getState()) {
			Adjustable adj = new Adjustable(elm, i);
			adj.sliderText = ei.name.replaceAll(" \\(.*\\)$", "");
			adj.createSlider(sim, ei.value);
			sim.adjustables.add(adj);
		    } else {
			Adjustable adj = findAdjustable(i);
			adj.deleteSlider(sim);
			sim.adjustables.remove(adj);
		    }
		    changed = true;
		}
	    }
	    if (changed) {
		// apply changes before we reset everything
		apply();
		
		clearDialog();
		buildDialog();
	    }
	}
	
	public void clearDialog() {
		while (vp.getWidget(0)!=hp)
			vp.remove(0);
	}
	
	protected void closeDialog()
	{
		SliderDialog.this.hide();
		sim.sliderDialog = null;
	}
}

