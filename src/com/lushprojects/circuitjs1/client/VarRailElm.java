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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;

    class VarRailElm extends RailElm implements MouseWheelHandler {
	Scrollbar slider;
	Label label;
	String sliderText;
	public VarRailElm(int xx, int yy) {
	    super(xx, yy, WF_VAR);
	    sliderText = "Voltage";
	    frequency = maxVoltage;
	    createSlider();
	}
	public VarRailElm(int xa, int ya, int xb, int yb, int f,
		       StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    sliderText = st.nextToken();
	    while (st.hasMoreTokens())
		sliderText += ' ' + st.nextToken();
	    sliderText=sliderText.replaceAll("%2[bB]", "+");
	    createSlider();
	}
	String dump() {
	    return super.dump() + " " + sliderText.replaceAll("\\+","%2B");
	}
	int getDumpType() { return 172; }
	void createSlider() {
	    waveform = WF_VAR;
	    sim.addWidgetToVerticalPanel(label = new Label(sim.LS(sliderText)));
	    label.addStyleName("topSpace");
	    int value = (int) ((frequency-bias)*100/(maxVoltage-bias));
	    sim.addWidgetToVerticalPanel(slider = new Scrollbar(Scrollbar.HORIZONTAL, value, 1, 0, 101 ,
	    		null, this));
//	    sim.verticalPanel.validate();
	}
	double getVoltage() {
	    frequency = slider.getValue() * (maxVoltage-bias) / 100. + bias;
	    return frequency;
	}
	void delete() {
	    sim.removeWidgetFromVerticalPanel(label);
	    sim.removeWidgetFromVerticalPanel(slider);
            super.delete();
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Min Voltage", bias, -20, 20);
	    if (n == 1)
		return new EditInfo("Max Voltage", maxVoltage, -20, 20);
	    if (n == 2) {
		EditInfo ei = new EditInfo("Slider Text", 0, -1, -1);
		ei.text = sliderText;
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0)
		bias = ei.value;
	    if (n == 1)
		maxVoltage = ei.value;
	    if (n == 2) {
		sliderText = ei.textf.getText();
		label.setText(sim.LS(sliderText));
		sim.setiFrameHeight();
	    }
	}
	int getShortcut() { return 0; }
	
    void setMouseElm(boolean v) {
    	super.setMouseElm(v);
    	if (slider!=null)
    		slider.draw();
    }
	
    public void onMouseWheel(MouseWheelEvent e) {
    	if (slider!=null)
    		slider.onMouseWheel(e);
    }
    
    }
