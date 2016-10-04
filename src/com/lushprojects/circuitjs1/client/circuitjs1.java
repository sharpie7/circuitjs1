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

//CirSim.java (c) 2010 by Paul Falstad
//GWT conversion (c) 2015 by Iain Sharp

//Version History
//v1.0.1 15-06-15
//Convert source code to GPLv2
//Incorporate example files in to project
//v1.0.0 15-06-05
//Import/export to/from text now fixed
//v0.1.3 15-06-03
//Handles appear on components when dragged
//Improved integration of potentiometers and VarRails with sliders - colour changes and support
//for scroll wheel.
//v0.1.2 15-06-01
//Automatic selection of post drag mode when user is near a handle in select mode
//Visual appearance of handles changed
//Accepts "2k2" style engineers short-hand for component values
//Menus prettified
//v0.1.1 
//Bug fix for PNP transistors and past
//v0.1.0 - 
//Initial test release on web


//ToDos
// Scope improvements
//UI improvements
//Potentiometer - improve drawing code
//Coil drawing - find out why my alternative code doesn't work

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

public class circuitjs1 implements EntryPoint {
	
	public static final String versionString="1.7.1js";

	static CirSim mysim;
	
  public void onModuleLoad() {
	  mysim = new CirSim();
	  mysim.init();

	    Window.addResizeHandler(new ResizeHandler() {
	    	 
            public void onResize(ResizeEvent event)
            {               
            	mysim.setCanvasSize();
                mysim.setiFrameHeight();	
                	
            }
        });
	    
	    /*
	    Window.addWindowClosingHandler(new Window.ClosingHandler() {

	        public void onWindowClosing(ClosingEvent event) {
	            event.setMessage("Are you sure?");
	        }
	    });
	     */

	  mysim.updateCircuit();
	  

	  
  	}
  
  }
	  