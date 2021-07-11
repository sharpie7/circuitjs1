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

import java.util.HashMap;

//CirSim.java (c) 2010 - 2017 by Paul Falstad
//GWT conversion (c) 2015 - 2017 by Iain Sharp

//Version History
//v1.9.1js 16-11-06 Iain Sharp
//Add import of file from CORS compatible link
//v1.9.0js 16-11-06 Iain Sharp
// Add URL-shortener and Dropbox integration
//v1.8.0js 16-10-30 Iain Sharp
// Incorporate latest Falstad updates. Improvements to UI and bug fixes
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

public class circuitjs1 implements EntryPoint {

	public static final String versionString="2.5.2js";
	
	// Set to true if the server runs the shortrelay.php file in the same directory as the circuit simulator
	public static final boolean shortRelaySupported = true;

	static CirSim mysim;
	HashMap<String,String> localizationMap;
	
  public void onModuleLoad() {
      localizationMap = new HashMap<String,String>();
      
      loadLocale();
  }

  native String language()  /*-{ // Modified to support Electron which return empty array for navigator.languages
      if (navigator.languages) {
        if (navigator.languages.length>0)
          return navigator.languages[0];
        else
          return "en-US";
      } else {
      	return  (navigator.language || navigator.userLanguage) ;  
      }
  }-*/;

  void loadLocale() {
  	String url;
	QueryParameters qp = new QueryParameters();
	String lang = qp.getValue("lang");
	if (lang == null) {
	    Storage stor = Storage.getLocalStorageIfSupported();
	    if (stor != null)
		lang = stor.getItem("language");
	    if (lang == null)
		lang = language();
	}
  	GWT.log("got language " + lang);
  	
  	// check for Taiwan Chinese.  Otherwise, strip the region code
  	if (lang.equalsIgnoreCase("zh-tw") || lang.equalsIgnoreCase("zh-cht"))
  	    lang = "zh-tw";
  	else
  	    lang = lang.replaceFirst("-.*", "");
  	
  	if (lang.startsWith("en")) {
  	    // no need to load locale file for English
  	    loadSimulator();
  	    return;
  	}
  	url = GWT.getModuleBaseURL()+"locale_" + lang + ".txt";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("File Error Response", exception);
				}

				public void onResponseReceived(Request request, Response response) {
					// processing goes here
					if (response.getStatusCode()==Response.SC_OK) {
					String text = response.getText();
					processLocale(text);
					// end or processing
					}
					else {
						GWT.log("Bad file server response:"+response.getStatusText() );
						loadSimulator();
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("failed file reading", e);
		}

  }
  
  void processLocale(String data) {
      String lines[] = data.split("\r?\n");
      int i;
      for (i = 0; i != lines.length; i++) {
	  String line = lines[i];
	  if (line.length() == 0)
	      continue;
	  if (line.charAt(0) != '"') {
	      CirSim.console("ignoring line in string catalog: " + line);
	      continue;
	  }
	  int q2 = line.indexOf('"', 1);
	  if (q2 < 0 || line.charAt(q2+1) != '=' || line.charAt(q2+2) != '"' ||
		  line.charAt(line.length()-1) != '"') {
	      CirSim.console("ignoring line in string catalog: " + line);
	      continue;
	  }
	  String str1 = line.substring(1, q2);
	  String str2 = line.substring(q2+3, line.length()-1);
	  localizationMap.put(str1, str2);
      }
      loadSimulator();
  }
  
  public void loadSimulator() {
	  mysim = new CirSim();
	  mysim.localizationMap = localizationMap;
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
	  
