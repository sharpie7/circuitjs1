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

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;

public class ScrollValuePopup extends PopupPanel implements MouseOutHandler, MouseWheelHandler,
	MouseDownHandler {
	
	static final double e12[] = {1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2};
	static final int labMax=5;
	double values[];
	int minpow =0;
	int maxpow=1;
	int nvalues;
	int currentidx;
	int lastidx;
	VerticalPanel vp;
	CircuitElm myElm;
	Label labels[];
	int deltaY;
	String name;
	EditInfo inf;
	CirSim sim;
	
	
	ScrollValuePopup(final int x, final int y, int dy, CircuitElm e, CirSim s) {
		super();
		myElm=e;
		deltaY=0;
		sim=s;
		sim.pushUndo();
		vp=new VerticalPanel();
		setWidget(vp);
		setupValues();
		vp.add(new Label(name));
		labels=new Label[labMax];
		for (int i=0; i<labMax; i++) {
			labels[i] = new Label("---");
			labels[i].setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			if (i==2)
				labels[i].addStyleDependentName("selected");
			else if (i==1 || i==3)
				labels[i].addStyleDependentName("1off");
			else 
				labels[i].addStyleDependentName("2off");
			vp.add(labels[i]);
		}
		doDeltaY(dy);
		this.addDomHandler(this, MouseOutEvent.getType());
		this.addDomHandler(this, MouseWheelEvent.getType());
		this.addDomHandler(this, MouseDownEvent.getType());
//		this.addDomHandler(this, KeyPressEvent.getType());
        setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
              int left = Math.max(0, (x - offsetWidth/4));
              int top = Math.max(0, y - 7*offsetHeight/12);
              setPopupPosition(left, top);
            }
          });
	}
	
	private void setupValues() {
		if (myElm instanceof ResistorElm) {
			minpow=0;
			maxpow=6;
		} 
		if (myElm instanceof CapacitorElm) {
			minpow=-11;
			maxpow=-3;
		} 
		if (myElm instanceof InductorElm) {
			minpow=-6;
			maxpow=0;
		} 
		values = new double[2+(maxpow-minpow)*12];
		int ptr=0;
		for(int i=minpow; i<=maxpow; i++) {
			for(int j=0; j<((i!=maxpow)?12:1) ; j++, ptr++) {
				values[ptr]=Math.pow(10.0, (double)i)*e12[j];
			}
		}
		nvalues=ptr;
		values[nvalues]=1E99;
		inf=myElm.getEditInfo(0);
		double currentvalue=inf.value;
		for (int i=0; i<nvalues+1; i++) {
			if (CircuitElm.getShortUnitText(currentvalue,"")==
					CircuitElm.getShortUnitText(values[i],"")) { // match to an existing value
				values[i]=currentvalue; // Just in case it isn't 100% identical
				currentidx=i;
				break;
			}
			if (currentvalue<values[i])	 { // overshot - need to insert value
				currentidx=i;
				for(int j=nvalues-1; j>=i; j--)
					values[j+1]=values[j];
				values[i]=currentvalue;	
				nvalues++;
				break;	
			}
		}
		name= inf.name;
		lastidx=currentidx;
//		for(int i=0; i<nvalues; i++) 
//			GWT.log("i="+i+" values="+values[i] + " current? "+(i==currentidx));
	}
	
	
	private void setupLabels() {
		int thissel;
		thissel=getSelIdx();
		for(int i=0; i<labMax; i++) {
			labels[i].removeStyleDependentName("current");
			if ((thissel+i-2)<0 || (thissel+i-2)>= nvalues )
				labels[i].setText("---");
			else {
				labels[i].setText(CircuitElm.getShortUnitText(values[thissel+i-2], ""));
				if (thissel+i-2 == currentidx)
					labels[i].addStyleDependentName("current");
			}
		}
	}
	
	public void onMouseOut(MouseOutEvent e){
		close(true);
	}
	
	public void close(boolean keepChanges) {
		if (!keepChanges) {
			setElmValue(currentidx);
		} else {
			setElmValue();
		}
		
		this.hide();
	}
	
    public void onMouseWheel(MouseWheelEvent e) {
	e.preventDefault();
    	doDeltaY( e.getDeltaY());
    }
    
    public void onMouseDown(MouseDownEvent e) {
    	if (e.getNativeButton()==NativeEvent.BUTTON_LEFT || e.getNativeButton()==NativeEvent.BUTTON_MIDDLE)
    		close(true);
    	else
    		close(false);
    }
    
//    public void onKeyPress(KeyPressEvent e){
//    	int key=e.getNativeEvent().getKeyCode();
//    	GWT.log("key press. Key="+key);
//    	if (key==KEY_ESCAPE || key==KEY_SPACE)
//    		close(false);
//    	if (key==KEY_ENTER)
//    		close(true);
//    }
    
    public void doDeltaY(int dy) {
    	deltaY+=dy;
    	if (currentidx+deltaY/3 < 0)
    		deltaY=-3*currentidx;
    	if (currentidx+deltaY/3>=nvalues)
    		deltaY= (nvalues-currentidx-1)*3;
    	setElmValue();
    	setupLabels();
    }
    
    public void setElmValue() {
    	int idx=getSelIdx();
    	setElmValue(idx);
    }

    
    public void setElmValue(int i) {
    	if (i!=lastidx) {
    		lastidx=i;
    		inf.value=values[i];
    		myElm.setEditValue(0, inf);
    		sim.needAnalyze();
    	}
    }
    
    public int getSelIdx() {
    	int r;
    	r=currentidx+deltaY/3;
    	if (r<0)
    		r=0;
    	if (r>=nvalues)
    		r=nvalues-1;
    	return r;
    }

}
