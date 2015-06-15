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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import java.util.Iterator;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
//import java.awt.*;
//import java.awt.event.*;
//import java.text.NumberFormat;
//import java.text.DecimalFormat;

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
	NumberFormat noCommaFormat;

	EditDialog(Editable ce, CirSim f) {
//		super(f, "Edit Component", false);
		super(); // Do we need this?
		setText("Edit Component");
		cframe = f;
		elm = ce;
//		setLayout(new EditDialogLayout());
		vp=new VerticalPanel();
		setWidget(vp);
		einfos = new EditInfo[10];
		noCommaFormat=NumberFormat.getFormat("####.##########");
//		noCommaFormat = DecimalFormat.getInstance();
//		noCommaFormat.setMaximumFractionDigits(10);
//		noCommaFormat.setGroupingUsed(false);
		hp=new HorizontalPanel();
		hp.setWidth("100%");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp.setStyleName("topSpace");
		vp.add(hp);
		hp.add(applyButton = new Button("Apply"));
		applyButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				apply();
			}
		});
		hp.add(okButton = new Button("OK"));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				apply();
				closeDialog();
			}
		});
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hp.add(cancelButton = new Button("Cancel"));
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
		Label l;
		for (i = 0; ; i++) {
			einfos[i] = elm.getEditInfo(i);
			if (einfos[i] == null)
				break;
			EditInfo ei = einfos[i];
			idx = vp.getWidgetIndex(hp);
			vp.insert(l = new Label(ei.name),idx);
			if (i!=0)
				l.setStyleName("topSpace");
			idx = vp.getWidgetIndex(hp);
			if (ei.choice != null) {
				vp.insert(ei.choice,idx);
				ei.choice.addChangeHandler( new ChangeHandler() {
					public void onChange(ChangeEvent e){
						itemStateChanged(e);
					}
				});
//				ei.choice.addItemListener(this);
			} else if (ei.checkbox != null) {
				vp.insert(ei.checkbox,idx);
				ei.checkbox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
					public void onValueChange(ValueChangeEvent<Boolean> e){
						itemStateChanged(e);
					}
				});
//				ei.checkbox.addItemListener(this);
			} else {
				vp.insert(ei.textf =
					//	new TextBox(unitString(ei), 10));
						new TextBox(), idx);
				if (ei.text != null)
					ei.textf.setText(ei.text);
//				ei.textf.addActionListener(this);
				if (ei.text == null) {
//					add(ei.bar = new Scrollbar(Scrollbar.HORIZONTAL,
//							50, 10, 0, barmax+2));
//					setBar(ei);
//					ei.bar.addAdjustmentListener(this);
					ei.textf.setText(unitString(ei));
				}
			}
		}
		einfocount = i;
	}

	String unitString(EditInfo ei) {
		double v = ei.value;
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
		if (va < 1 && !ei.forceLargeM)
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
		s = s.trim();
		// rewrite shorthand (eg "2k2") in to normal format (eg 2.2k) using regex
		s=s.replaceAll("([0-9]+)([pPnNuUmMkKgG])([0-9]+)", "$1.$3$2");
		int len = s.length();
		char uc = s.charAt(len-1);
		double mult = 1;
		switch (uc) {
		case 'p': case 'P': mult = 1e-12; break;
		case 'n': case 'N': mult = 1e-9; break;
		case 'u': case 'U': mult = 1e-6; break;

		// for ohm values, we assume mega for lowercase m, otherwise milli
		case 'm': mult = (ei.forceLargeM) ? 1e6 : 1e-3; break;

		case 'k': case 'K': mult = 1e3; break;
		case 'M': mult = 1e6; break;
		case 'G': case 'g': mult = 1e9; break;
		}
		if (mult != 1)
			s = s.substring(0, len-1).trim();
		return noCommaFormat.parse(s) * mult;
	}

	void apply() {
		int i;
		for (i = 0; i != einfocount; i++) {
			EditInfo ei = einfos[i];
//			if (ei.textf == null)
//				continue;
//			if (ei.text == null) {
			if (ei.textf!=null && ei.text==null) {
				try {
					double d = parseUnits(ei);
					ei.value = d;
				} catch (Exception ex) { /* ignored */ }
			}
			elm.setEditValue(i, ei);

//			if (ei.text == null)
//				setBar(ei);
		}
		cframe.needAnalyze();
	}

//	public void actionPerformed(ActionEvent e) {
//		int i;
//		Object src = e.getSource();
//		for (i = 0; i != einfocount; i++) {
//			EditInfo ei = einfos[i];
//			if (src == ei.textf) {
//				if (ei.text == null) {
//					try {
//						double d = parseUnits(ei);
//						ei.value = d;
//					} catch (Exception ex) { /* ignored */ }
//				}
//				elm.setEditValue(i, ei);
//				if (ei.text == null)
//					setBar(ei);
//				cframe.needAnalyze();
//			}
//		}
//		if (e.getSource() == okButton) {
//			apply();
//			closeDialog();
//		}
//		if (e.getSource() == applyButton)
//			apply();
//	}
//
//	public void adjustmentValueChanged(AdjustmentEvent e) {
//		Object src = e.getSource();
//		int i;
//		for (i = 0; i != einfocount; i++) {
//			EditInfo ei = einfos[i];
//			if (ei.bar == src) {
//				double v = ei.bar.getValue() / 1000.;
//				if (v < 0)
//					v = 0;
//				if (v > 1)
//					v = 1;
//				ei.value = (ei.maxval-ei.minval)*v + ei.minval;
//				/*if (ei.maxval-ei.minval > 100)
//		    ei.value = Math.round(ei.value);
//		else
//		ei.value = Math.round(ei.value*100)/100.;*/
//				ei.value = Math.round(ei.value/ei.minval)*ei.minval;
//				elm.setEditValue(i, ei);
//				ei.textf.setText(unitString(ei));
//				cframe.needAnalyze();
//			}
//		}
//	}
//
	public void itemStateChanged(GwtEvent e) {
		Object src = e.getSource();
		int i;
		boolean changed = false;
		for (i = 0; i != einfocount; i++) {
			EditInfo ei = einfos[i];
			if (ei.choice == src || ei.checkbox == src) {
				elm.setEditValue(i, ei);
				if (ei.newDialog)
					changed = true;
				cframe.needAnalyze();
			}
		}
		if (changed) {
		//	apply();
//			setVisible(false);
//			cframe.editDialog = new EditDialog(elm, cframe);
//			cframe.editDialog.show();
			clearDialog();
			buildDialog();
		}
	}
	
	public void clearDialog() {
//		Iterator<Widget> wa = vp.iterator();
//		while (wa.hasNext()){
//			Widget w=wa.next();
//			if (w!=hp)
//				vp.remove(w);
//		}
		while (vp.getWidget(0)!=hp)
			vp.remove(0);
	}
//
//	public boolean handleEvent(Event ev) {
//		if (ev.id == Event.WINDOW_DESTROY) {
//			closeDialog();
//			return true;
//		}
//		return super.handleEvent(ev);
//	}
//
//	void setBar(EditInfo ei) {
//		int x = (int) (barmax*(ei.value-ei.minval)/(ei.maxval-ei.minval));
//		ei.bar.setValue(x);
//	}
//
	protected void closeDialog()
	{
		EditDialog.this.hide();
		cframe.editDialog = null;
	}
}

