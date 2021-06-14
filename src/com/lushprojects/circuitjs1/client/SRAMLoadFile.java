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

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class SRAMLoadFile extends FileUpload implements  ChangeHandler {
	
	static SRAMLoadFile singlet;
	
	static public final boolean isSupported() { return LoadFile.isSupported(); }
	
	SRAMLoadFile() {
		super();
		this.setName("Load File Into SRAM");
		this.getElement().setId("LoadSRAMElement");
		this.addChangeHandler(this);
		this.addStyleName("offScreen");
		this.setPixelSize(0, 0);
	}
	
	
	
	public void onChange(ChangeEvent e) {
		doLoad();
	}
	
	
	public final native void click() 
	/*-{
		$doc.getElementById("LoadSRAMElement").click();
	 }-*/;
	static public final native void doLoad()
	/*-{
		var oFiles = $doc.getElementById("LoadSRAMElement").files,
		nFiles = oFiles.length;
		if (nFiles>=1) {
			if (oFiles[0].size >= 128000) {
				$wnd.alert("Cannot load: That file is too large!");
				return;
			}
			
    		var reader = new FileReader();
			reader.onload = function(e) {
  				var arr = new Int8Array(reader.result);
  				@com.lushprojects.circuitjs1.client.SRAMLoadFile::startLoadCallback()();
  				for (var i = 0; i < arr.length; i++) {
  					@com.lushprojects.circuitjs1.client.SRAMLoadFile::doLoadCallback(Ljava/lang/Integer;)(arr[i]);
  				}
  				@com.lushprojects.circuitjs1.client.SRAMLoadFile::finishLoadCallback()();
    		};

			reader.readAsArrayBuffer(oFiles[0]);
		}
	 }-*/;
	static public void startLoadCallback() {
		SRAMElm.contentsOverride = "0:";
	}
	static public void doLoadCallback(Integer data) {
		SRAMElm.contentsOverride += " " + data;
	}
	static public void finishLoadCallback() {
		CirSim.console("Done");
		CirSim.editDialog.resetDialog();
		SRAMElm.contentsOverride = "";
	}
}
