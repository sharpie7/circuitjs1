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

interface Editable {
    EditInfo getEditInfo(int n);
    void setEditValue(int n, EditInfo ei);
}

// class EditDialog extends Dialog implements AdjustmentListener, ActionListener, ItemListener {
class EditDialog extends DialogBox  {
	Editable elm;
	CirSim cframe;
	Button applyButton, okButton, cancelButton;
	EditInfo einfos[];
	int einfocount;
	final int barmax = 1000;
	VerticalPanel vp;
	HorizontalPanel hp;
	static NumberFormat noCommaFormat = NumberFormat.getFormat("####.##########");

	EditDialog(Editable ce, CirSim f) {
//		super(f, "Edit Component", false);
		super(); // Do we need this?
		setText(CirSim.LS("Edit Component"));
		cframe = f;
		elm = ce;
//		setLayout(new EditDialogLayout());
		vp=new VerticalPanel();
		setWidget(vp);
		einfos = new EditInfo[10];
//		noCommaFormat = DecimalFormat.getInstance();
//		noCommaFormat.setMaximumFractionDigits(10);
//		noCommaFormat.setGroupingUsed(false);
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
			Label l = null;
			einfos[i] = elm.getEditInfo(i);
			if (einfos[i] == null)
				break;
			EditInfo ei = einfos[i];
			idx = vp.getWidgetIndex(hp);
			String name = CirSim.LS(ei.name);
			if (ei.name.startsWith("<"))
			    vp.insert(l = new HTML(name),idx);
			else
			    vp.insert(l = new Label(name),idx);
			if (i!=0 && l != null)
				l.setStyleName("topSpace");
			idx = vp.getWidgetIndex(hp);
			if (ei.choice != null) {
				vp.insert(ei.choice,idx);
				ei.choice.addChangeHandler( new ChangeHandler() {
					public void onChange(ChangeEvent e){
						itemStateChanged(e);
					}
				});
			} else if (ei.checkbox != null) {
				vp.insert(ei.checkbox,idx);
				ei.checkbox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
					public void onValueChange(ValueChangeEvent<Boolean> e){
						itemStateChanged(e);
					}
				});
			} else if (ei.button != null) {
			    vp.insert(ei.button, idx);
			    ei.button.addClickHandler( new ClickHandler() {
				public void onClick(ClickEvent event) {
				    itemStateChanged(event);
				}
			    });
			} else if (ei.textArea != null) {
			    vp.insert(ei.textArea, idx);
			} else if (ei.widget != null) {
			    vp.insert(ei.widget, idx);
			} else {
				vp.insert(ei.textf = new TextBox(), idx);
				if (ei.text != null)
					ei.textf.setText(ei.text);
				if (ei.text == null) {
					ei.textf.setText(unitString(ei));
				}
			}
		}
		einfocount = i;
	}

	static final double ROOT2 = 1.41421356237309504880;
	
	double diffFromInteger(double x) {
	    return Math.abs(x-Math.round(x));
	}
	
	String unitString(EditInfo ei) {
	    // for voltage elements, express values in rms if that would be shorter
	    if (elm != null && elm instanceof VoltageElm &&
		Math.abs(ei.value) > 1e-4 &&
		diffFromInteger(ei.value*1e4) > diffFromInteger(ei.value*1e4/ROOT2))
		return unitString(ei, ei.value/ROOT2) + "rms";
	    return unitString(ei, ei.value);
	}

	static String unitString(EditInfo ei, double v) {
		double va = Math.abs(v);
		if (ei.dimensionless)
			return noCommaFormat.format(v);
		if (v == 0) return "0";
		if (va < 1e-9)
			return noCommaFormat.format(v*1e12) + "p";
		if (va < 1e-6)
			return noCommaFormat.format(v*1e9) + "n";
		if (va < 1e-3)
			return noCommaFormat.format(v*1e6) + "u";
		if (va < 1 /*&& !ei.forceLargeM*/)
			return noCommaFormat.format(v*1e3) + "m";
		if (va < 1e3)
			return noCommaFormat.format(v);
		if (va < 1e6)
			return noCommaFormat.format(v*1e-3) + "k";
		if (va < 1e9)
			return noCommaFormat.format(v*1e-6) + "M";
		return noCommaFormat.format(v*1e-9) + "G";
	}

	double parseUnits(EditInfo ei) throws java.text.ParseException {
		String s = ei.textf.getText();
		return parseUnits(ei, s);
	}
	
	static double parseUnits(EditInfo ei, String s) throws java.text.ParseException {
		s = s.trim();
		double rmsMult = 1;
		if (s.endsWith("rms")) {
		    s = s.substring(0, s.length()-3).trim();
		    rmsMult = ROOT2;
		}
		// rewrite shorthand (eg "2k2") in to normal format (eg 2.2k) using regex
		s=s.replaceAll("([0-9]+)([pPnNuUmMkKgG])([0-9]+)", "$1.$3$2");
		int len = s.length();
		char uc = s.charAt(len-1);
		double mult = 1;
		switch (uc) {
		case 'p': case 'P': mult = 1e-12; break;
		case 'n': case 'N': mult = 1e-9; break;
		case 'u': case 'U': mult = 1e-6; break;

		// for ohm values, we used to assume mega for lowercase m, otherwise milli
		case 'm': mult = /*(ei.forceLargeM) ? 1e6 : */ 1e-3; break;

		case 'k': case 'K': mult = 1e3; break;
		case 'M': mult = 1e6; break;
		case 'G': case 'g': mult = 1e9; break;
		}
		if (mult != 1)
			s = s.substring(0, len-1).trim();
		return noCommaFormat.parse(s) * mult * rmsMult;
	}

	void apply() {
		int i;
		for (i = 0; i != einfocount; i++) {
			EditInfo ei = einfos[i];
			if (ei.textf!=null && ei.text==null) {
				try {
					double d = parseUnits(ei);
					ei.value = d;
				} catch (Exception ex) { /* ignored */ }
			}
			if (ei.button != null)
			    continue;
			elm.setEditValue(i, ei);
			
			// update slider if any
			if (elm instanceof CircuitElm) {
			    Adjustable adj = cframe.findAdjustable((CircuitElm)elm, i);
			    if (adj != null)
				adj.setSliderValue(ei.value);
			}
		}
		cframe.needAnalyze();
	}

	public void itemStateChanged(GwtEvent e) {
	    Object src = e.getSource();
	    int i;
	    boolean changed = false;
	    boolean applied = false;
	    for (i = 0; i != einfocount; i++) {
		EditInfo ei = einfos[i];
		if (ei.choice == src || ei.checkbox == src || ei.button == src) {
		    
		    // if we're pressing a button, make sure to apply changes first
		    if (ei.button == src && !ei.newDialog) {
			apply();
			applied = true;
		    }
		    
		    elm.setEditValue(i, ei);
		    if (ei.newDialog)
			changed = true;
		    cframe.needAnalyze();
		}
	    }
	    if (changed) {
		// apply changes before we reset everything
		// (need to check if we already applied changes; otherwise Diode create simple model button doesn't work)
		if (!applied)
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
		EditDialog.this.hide();
		cframe.editDialog = null;
	}
}

