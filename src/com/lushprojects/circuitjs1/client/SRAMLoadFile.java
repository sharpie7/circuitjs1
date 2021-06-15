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

import com.google.gwt.user.client.Window;

public class SRAMLoadFile extends GenericLoadFile {
	
	public final native void open() 
	/*-{
		var inputElm = $doc.getElementById("GenericLoadFileElement");
		inputElm.addEventListener("input", function() {
			var oFiles = inputElm.files,
			nFiles = oFiles.length;
			if (nFiles>=1) {
				if (oFiles[0].size >= 128000) {
					@com.lushprojects.circuitjs1.client.SRAMLoadFile::doErrorCallback(Ljava/lang/String;)("Cannot load: That file is too large!");
					return;
				}
				
				var reader = new FileReader();
				reader.onload = function(e) {
					var arr = new Int8Array(reader.result);
					var str = "0:";
					for (var i = 0; i < arr.length; i++)
						str += " " + arr[i];
					@com.lushprojects.circuitjs1.client.SRAMLoadFile::doLoadCallback(Ljava/lang/String;)(str);
				};
		
				reader.readAsArrayBuffer(oFiles[0]);
			}
		});
		inputElm.click();
	}-*/;
	static public void doErrorCallback(String msg) {
		Window.alert(CirSim.LS(msg));
	}
	static public void doLoadCallback(String data) {
		SRAMElm.contentsOverride = data;
		CirSim.editDialog.resetDialog();
		SRAMElm.contentsOverride = null;
	}
}
