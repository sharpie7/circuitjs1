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

import com.google.gwt.core.client.JsArrayNumber;

import java.util.ArrayList;
import java.util.HashMap;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FileUpload;

class DataFileEntry {
    String fileName;
    ArrayList<Double> data;
}

class DataInputElm extends RailElm {
    	ArrayList<Double> data;
    	double sampleLength;
    	double scaleFactor;
    	double timeOffset;
    	int fileNum;
    	String fileName;
    	final int FLAG_REPEAT = 1<<8;
    	
    	// cache to preserve data when doing cut/paste, or undo/redo
    	static int fileNumCounter = 1;
    	static HashMap<Integer, DataFileEntry> dataFileMap = new HashMap<Integer, DataFileEntry>();
    	
	public DataInputElm(int xx, int yy) {
	    super(xx, yy, WF_AC);
	    scaleFactor = 1;
	    sampleLength = 1e-3;
	}
	
	public DataInputElm(int xa, int ya, int xb, int yb, int f,
		       StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    waveform = WF_AC;
	    sampleLength = Double.parseDouble(st.nextToken());
	    scaleFactor = Double.parseDouble(st.nextToken());
	    fileNum = Integer.parseInt(st.nextToken());

	    DataFileEntry ent = dataFileMap.get(fileNum);
	    if (ent != null) {
		fileName = ent.fileName;
		data = ent.data;
	    }
	}
	
	double fmphase;
	
	String dump() {
	    // add a file number to the dump so we can preserve the data when doing cut and paste, or undo/redo.
	    // we don't save the entire file in the dump because it would be huge.
	    if (data != null) {
		if (fileNum == 0)
		    fileNum = fileNumCounter++;
		DataFileEntry ent = new DataFileEntry();
		ent.fileName = fileName;
		ent.data = data;
		dataFileMap.put(fileNum, ent);
	    }
	    return super.dump() + " " + sampleLength + " " + scaleFactor + " " + fileNum;
	}
	
	void reset() {
	    timeOffset = 0;
	}
	
	void drawRail(Graphics g) {
	    drawRailText(g, fileName == null ? "No file" : fileName);
	}
	
	String getRailText() {
	    return fileName == null ? "No file" : fileName;
	}
	
	boolean doesRepeat() { return (flags & FLAG_REPEAT) != 0; }
	
	double getVoltage() {
	    if (data == null)
		return 0;
	    int ptr = (int) (timeOffset / sampleLength);
	    if (ptr >= data.size()) {
		if (doesRepeat()) {
		    ptr = 0;
		    timeOffset = 0;
		} else
		    ptr = data.size()-1;
	    }
	    return data.get(ptr) * scaleFactor;
	}
	
	void stepFinished() {
	    timeOffset += sim.timeStep;
	}
	
	int getDumpType() { return 424; }
	int getShortcut() { return 0; }
	
	public EditInfo getEditInfo(int n) {
            if (n == 0) {
                EditInfo ei = new EditInfo("", 0, -1, -1);
                final DataInputElm thisElm = this;
                final FileUpload file = new FileUpload();
                ei.widget = file;
                file.addChangeHandler(new ChangeHandler() {
			    public void onChange(ChangeEvent event) {
				fileName = file.getFilename().replaceAll("^.*\\\\", "").replaceAll("\\.[^.]*$", "");
				DataInputElm.fetchLoadFileData(thisElm, file.getElement());
			    }
			  });
                return ei;
            }
            if (n == 1)
                return new EditInfo("Scale Factor", scaleFactor);
            if (n == 2)
                return new EditInfo("Sample Length (s)", sampleLength);
            if (n == 3)
                return EditInfo.createCheckbox("Repeat", doesRepeat());
	    return null;
	}
	
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 1)
		scaleFactor = ei.value;
	    if (n == 2)
		sampleLength = ei.value;
	    if (n == 3)
		flags = ei.changeFlag(flags, FLAG_REPEAT);
	}
	
        // fetch data for a selected file
        static native String fetchLoadFileData(DataInputElm elm, Element uploadElement) /*-{
            var oFiles = uploadElement.files;
            if (oFiles.length >= 1) {
                        var reader = new FileReader();
                        reader.onload = function(e) {
                                var text = reader.result;
                                elm.@com.lushprojects.circuitjs1.client.DataInputElm::doLoadCallback(Ljava/lang/String;Ljava/lang/String;)(text, oFiles[0].name);
                        };

                        reader.readAsText(oFiles[0]);
            }
        }-*/;

        void doLoadCallback(String s, String t) {
            // parse data file.  each line contains a single voltage value
            String arr[] = s.split("\r*\n");
            data = new ArrayList<Double>();
            int i;
            for (i = 0; i != arr.length; i++) {
        	// skip blank lines
        	if (arr[i].length() == 0)
        	    continue;
        	
        	// skip comments
        	if (arr[i].charAt(0) == '#')
        	    continue;
        	try {
        	    double d = Double.parseDouble(arr[i]);
        	    data.add(d);
        	} catch (Exception e) {
        	    CirSim.console("parse error on line " + i);
        	}
            }
	}

	void getInfo(String arr[]) {
	    arr[0] = "data input";
	    if (data == null) {
		arr[1] = "no file loaded";
		return;
	    }
	    arr[1] = "V = " + getVoltageText(volts[0]);
	    arr[2] = "pos = " + getUnitText(timeOffset, "s");
	    double dur = data.size() * sampleLength;
	    arr[3] = "dur = " + getUnitText(dur, "s");
	}
	
	public static void clearCache() {
	    dataFileMap.clear();
	}
    }
