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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;


public class Scrollbar extends  Composite implements 
	ClickHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler,
	MouseWheelHandler, TouchStartHandler, TouchCancelHandler, TouchEndHandler, TouchMoveHandler {
	
	static int HORIZONTAL =1;
	static int HMARGIN=2;
	static int SCROLLHEIGHT=14;
	static int BARWIDTH=3;
	static int BARMARGIN=3;

	Canvas can;
	VerticalPanel pan;
	Context2d g;
	int min;
	int max;
	int val;
	boolean dragging=false;
	boolean enabled=true;
	Command command=null;
	CircuitElm attachedElm=null;
	
	public Scrollbar(int orientation, int value, int visible, int minimum, int maximum) {
		min=minimum;
		max=maximum-1;
		val=value;
		 pan = new VerticalPanel();
		can = Canvas.createIfSupported();
		can.setWidth((CirSim.VERTICALPANELWIDTH)+" px");
		can.setHeight("40 px");
		can.setCoordinateSpaceWidth(CirSim.VERTICALPANELWIDTH);
		can.setCoordinateSpaceHeight(SCROLLHEIGHT);
		pan.add(can);
		g=can.getContext2d();
		g.setFillStyle("#ffffff");
		can.addClickHandler( this);
		can.addMouseDownHandler(this);
		can.addMouseUpHandler(this);
		can.addMouseMoveHandler(this);
		can.addMouseOutHandler(this);
		can.addMouseOverHandler(this);
		can.addMouseWheelHandler(this);
		
		// our hack from CirSim doesn't work here so we have to handle touch events explicitly
		can.addTouchStartHandler(this);
		can.addTouchMoveHandler(this);
		can.addTouchEndHandler(this);
		can.addTouchCancelHandler(this);
		
		this.draw();
		initWidget(pan);
	}
	
	public Scrollbar(int orientation, int value, int visible, int minimum, int maximum, 
			Command cmd, CircuitElm e) {
		this(orientation,value,visible,minimum,maximum);
		this.command=cmd;
		attachedElm=e;
	}
	
	public Scrollbar(int orientation, int value, int visible, int minimum, int maximum, Command cmd) {
		this(orientation, value, visible, minimum, maximum);
		this.command=cmd;
	}
	
	void draw() {
		if (enabled)
			g.setStrokeStyle("#000000");
		else
			g.setStrokeStyle("lightgrey");
		g.setLineWidth(1.0);
		g.fillRect(0,0,CirSim.VERTICALPANELWIDTH,SCROLLHEIGHT);
		g.beginPath();
		g.moveTo(HMARGIN+SCROLLHEIGHT-3, 0);
		g.lineTo(HMARGIN, SCROLLHEIGHT/2);
		g.lineTo(HMARGIN+SCROLLHEIGHT-3, SCROLLHEIGHT);
		g.moveTo(CirSim.VERTICALPANELWIDTH-HMARGIN-SCROLLHEIGHT+3, 0);
		g.lineTo(CirSim.VERTICALPANELWIDTH-HMARGIN, SCROLLHEIGHT/2);
		g.lineTo(CirSim.VERTICALPANELWIDTH-HMARGIN-SCROLLHEIGHT+3, SCROLLHEIGHT);
		g.stroke();
		if (enabled)
			g.setStrokeStyle("grey");
		g.beginPath();
		g.setLineWidth(5.0);
		g.moveTo(HMARGIN+SCROLLHEIGHT+BARMARGIN, SCROLLHEIGHT/2);
		g.lineTo(CirSim.VERTICALPANELWIDTH-HMARGIN-SCROLLHEIGHT-BARMARGIN, SCROLLHEIGHT/2);
		g.stroke();
		double p=HMARGIN+SCROLLHEIGHT+BARMARGIN+((CirSim.VERTICALPANELWIDTH-2*(HMARGIN+SCROLLHEIGHT+BARMARGIN))*((double)(val-min)))/(max-min);
		if (enabled) {
			if (attachedElm!=null && attachedElm.needsHighlight())
				g.setStrokeStyle("cyan");
			else
				g.setStrokeStyle("red");
			g.beginPath();
			g.moveTo(HMARGIN+SCROLLHEIGHT+BARMARGIN, SCROLLHEIGHT/2);
			g.lineTo(p, SCROLLHEIGHT/2);
			g.stroke();
			g.setStrokeStyle("#000000");
//			g.beginPath();
//			g.moveTo(p, 0);
//			g.lineTo(p, SCROLLHEIGHT);
			g.setLineWidth(2.0);
			g.fillRect(p-2, 2, 5, SCROLLHEIGHT-4);
			g.strokeRect(p-2, 2, 5, SCROLLHEIGHT-4);
//			g.stroke();
		}


		
	}
	
	int calcValueFromPos(int x){
		int v;
		v= min+(max-min)*(x-HMARGIN-SCROLLHEIGHT-BARMARGIN)/(CirSim.VERTICALPANELWIDTH-2*(HMARGIN+SCROLLHEIGHT+BARMARGIN));
		if (v<min)
			v=min;
		if (v>max)
			v=max;
		return v;
	}
	
	public void onMouseDown(MouseDownEvent e){
//		GWT.log("Down");
		dragging=false;
		e.preventDefault();
		doMouseDown(e.getX(), true);
	}
	
	void doMouseDown(int x, boolean mouse) {
	    if (enabled){
		if (x < HMARGIN+SCROLLHEIGHT ) {
		    if (val>min)
			val--;
		}
		else {
		    if (x > CirSim.VERTICALPANELWIDTH-HMARGIN-SCROLLHEIGHT ) {
			if (val<max)
			    val++;
		    }
		    else {
			val=calcValueFromPos(x);	
			dragging=true;
			
			// setCapture doesn't work on touch for some reason; touchend/touchmoved events
			// don't get sent
			if (mouse)
			    Event.setCapture(can.getElement());
		    }
		}
		draw();
		if (command!=null)
		    command.execute();
	    }
	}
	
	public void onMouseMove(MouseMoveEvent e){
//		GWT.log("Move");
		e.preventDefault();
		doMouseMove(e.getX());
	}
	
	void doMouseMove(int x) {
	    if (enabled) {
		if (dragging) {
		    val=calcValueFromPos(x);	
		    draw();
		    if (command!=null)
			command.execute();
		}
	    }
	}
	
	public void onMouseUp(MouseUpEvent e){
//		GWT.log("Up");
		e.preventDefault();
		Event.releaseCapture(can.getElement());
		if (enabled && dragging) {
			val=calcValueFromPos(e.getX());	
			dragging=false;
			draw();
			if (command!=null)
				command.execute();
		}
	}
	
	public void onMouseOut(MouseOutEvent e){
//		GWT.log("Out");
//		e.preventDefault();
	    	if (dragging)
	    	    return;
		if (enabled && attachedElm!=null && attachedElm.isMouseElm())
			CircuitElm.sim.setMouseElm(null);
	}
	
	public void onMouseOver(MouseOverEvent e){
		
		if (enabled && attachedElm!=null)
			 CircuitElm.sim.setMouseElm(attachedElm);
	}
	
	public void onMouseWheel(MouseWheelEvent e) {
		e.preventDefault();
		if (enabled)
			setValue(val+e.getDeltaY()/3);
	}
	
	public void onClick(ClickEvent e) {
//		GWT.log("Click");
		e.preventDefault();
//		if (e.getX()<HMARGIN+SCROLLHEIGHT ) {
//			if (val>min)
//				val--;
//		}
//		else {
//			if (e.getX()>CirSim.VERTICALPANELWIDTH-HMARGIN-SCROLLHEIGHT ) {
//				if (val<max)
//					val++;
//			}
//			else {
//				val=calcValueFromPos(e.getX());			}
//		}
//		draw();
		
	}
	
	public int getValue(){
		return val;
	}
	
	public void setValue(int i){
		if (i<min)
			i=min;
		else if (i>max)
			i=max;
		val =i;
		draw();
		if (command!=null)
			command.execute();
	}
	
	public void enable() {
		enabled=true;
		draw();
	}
	
	public void disable() {
		enabled=false;
		dragging=false;
		draw();
	}

	public void onTouchMove(TouchMoveEvent e) {
//	    GWT.log("touchmove");
	    e.preventDefault();
	    Touch t = e.getTouches().get(0);
	    doMouseMove(t.getRelativeX(getElement()));
	}

	public void onTouchEnd(TouchEndEvent event) {
//	    GWT.log("touchend");;
	    event.preventDefault();
	    if (enabled && dragging) {
		dragging=false;
		draw();
		if (command!=null)
		    command.execute();
	    }
	}

	public void onTouchCancel(TouchCancelEvent event) {
//	    GWT.log("touchcancel");;
	    event.preventDefault();
	    dragging = false;
	}

	public void onTouchStart(TouchStartEvent event) {
//	    GWT.log("touchstart");
	    event.preventDefault();
	    dragging=false;
	    Touch t = event.getTouches().get(0);
	    doMouseDown(t.getRelativeX(getElement()), false);
	}
	
}
