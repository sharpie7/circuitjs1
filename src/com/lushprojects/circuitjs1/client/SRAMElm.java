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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;

    class SRAMElm extends ChipElm {
	int addressNodes, dataNodes, internalNodes;
	int addressBits, dataBits;
	HashMap<Integer, Integer> map;
	static String contentsOverride = null;

	public SRAMElm(int xx, int yy) {
	    super(xx, yy);
	    addressBits = dataBits = 4;
	    map = new HashMap<Integer, Integer>();
	    setupPins();
	}

	public SRAMElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    map = new HashMap<Integer, Integer>();
	    addressBits = Integer.parseInt(st.nextToken());
	    dataBits    = Integer.parseInt(st.nextToken());
	    setupPins();
	    try {
		// load contents
		// format: addr val(addr) val(addr+1) val(addr+2) ... -1 addr val val ... -1 ... -2
		while (true) {
		    int a = Integer.parseInt(st.nextToken());
		    if (a < 0)
			break;
		    int v = Integer.parseInt(st.nextToken());
		    map.put(a, v);
		    while (true) {
			v = Integer.parseInt(st.nextToken());
			if (v < 0)
			    break;
			map.put(++a, v);
		    }
		}
	    } catch (Exception e) {}
	}

	String dump() {
	    String s = super.dump() + " " + addressBits + " " + dataBits;
	    
	    // dump contents
	    int maxI = 1<<addressBits;
	    int i;
	    for (i = 0; i < maxI; i++) {
		Integer val = map.get(i);
		if (val == null)
		    continue;
		s += " " + i + " " + val;
		while (true) {
		    val = map.get(++i);
		    if (val == null)
			break;
		    s += " " + val;
		}
		s += " -1";
	    }
	    s += " -2";
	    return s;
	}

	boolean nonLinear() { return true; }
	String getChipName() { return "Static RAM"; }
	void setupPins() {
	    sizeX = 2;
	    sizeY = max(addressBits, dataBits) + 1;
	    pins = new Pin[getPostCount()];
	    pins[0] = new Pin(0, SIDE_W, "WE");
	    pins[0].lineOver = true;
	    pins[1] = new Pin(0, SIDE_E, "OE");
	    pins[1].lineOver = true;
	    int i;
	    addressNodes = 2;
	    dataNodes = 2+addressBits;
	    internalNodes = 2+addressBits+dataBits;
	    for (i = 0; i != addressBits; i++) {
		int ii = i+addressNodes;
		pins[ii] = new Pin(sizeY-addressBits+i, SIDE_W, "A" + (addressBits-i-1));
	    }
	    for (i = 0; i != dataBits; i++) {
		int ii = i+dataNodes;
		pins[ii] = new Pin(sizeY-dataBits+i, SIDE_E, "D" + (dataBits-i-1));
		pins[ii].output = true;
	    }
	    allocNodes();
	}
	int getPostCount() {
	    return 2 + addressBits + dataBits;
	}
	public EditInfo getEditInfo(int n) {
            if (n == 2)
                return new EditInfo("# of Address Bits", addressBits, 1, 1).setDimensionless();
            if (n == 3)
                return new EditInfo("# of Data Bits", dataBits, 1, 1).setDimensionless();
            if (n == 4) {
        	EditInfo ei = new EditInfo("Contents", 0);
        	ei.textArea = new TextArea();
        	ei.textArea.setVisibleLines(5);
        	String s = "";
        	if (contentsOverride != null) {
        		s = contentsOverride;
        		contentsOverride = null;
        	} else {
        	int i;
        	int maxI = 1<<addressBits;
        	for (i = 0; i < maxI; i++) {
        	    Integer val = map.get(i);
        	    if (val == null)
        		continue;
    	    	    s += i + ": " + val;
    	    	    int ct = 1;
    	    	    while (true) {
    	    		val = map.get(++i);
    	    		if (val == null)
    	    		    break;
    	    		s += " " + val;
    	    		if (++ct == 8)
    	    		    break;
    	    	    }
    	    	    s += "\n";
//    	    	    sim.console("got " + i + " " + s);
    	    	}
        	}
    	    	ei.textArea.setText(s);
    	    	return ei;
            }
            if (n == 5 && SRAMLoadFile.isSupported()) {
            	EditInfo ei = new EditInfo("", 0, -1, -1);
            	ei.loadFile = new SRAMLoadFile();
            	ei.button = new Button("Load Contents From File");
            	ei.newDialog = true;
            	return ei;
            }
	    return super.getEditInfo(n);
	}
	
	public void setEditValue(int n, EditInfo ei) {
	    if (n < 2)
		super.setEditValue(n, ei);
	    if (n == 2 && ei.value >= 2 && ei.value <= 16) {
		addressBits = (int)ei.value;
		setupPins();
		setPoints();
	    }
	    if (n == 3 && ei.value >= 2 && ei.value <= 16) {
		dataBits = (int)ei.value;
		setupPins();
		setPoints();
	    }
	    if (n == 4) {
		String s = ei.textArea.getText();
		String lines[] = s.split("\n");
		int i;
		map.clear();
		for (i = 0; i != lines.length; i++) {
		    try {
			String line = lines[i];
			String args[] = line.split(": *");
			int addr = Integer.parseInt(args[0]);
			String vals[] = args[1].split(" +");
			int j;
			for (j = 0; j != vals.length; j++) {
			    int val = Integer.parseInt(vals[j]);
			    map.put(addr++, val);
			}
		    } catch (Exception e) {}
		}
	    }
	}
	int getVoltageSourceCount() { return dataBits; }
	int getInternalNodeCount() { return dataBits; }
	
	int address;
	
	void stamp() {
	    int i;
	    for (i = 0; i != dataBits; i++) {
		Pin p = pins[i+dataNodes];
		sim.stampVoltageSource(0, nodes[internalNodes+i], p.voltSource);
		sim.stampNonLinear(nodes[internalNodes+i]);
		sim.stampNonLinear(nodes[dataNodes+i]);
	    }
	}
	
	void doStep() {
	    int i;
	    boolean writeEnabled = volts[0] < 2.5;
	    boolean outputEnabled = (volts[1] < 2.5) && !writeEnabled;
	    
	    // get address
	    address = 0;
	    for (i = 0; i != addressBits; i++) {
		address |= (volts[addressNodes+i] > 2.5) ? 1<<(addressBits-1-i) : 0;
	    }
	    
	    Integer dataObj = map.get(address);
	    int data = (dataObj == null) ? 0 : dataObj;
	    for (i = 0; i != dataBits; i++) {
		Pin p = pins[i+dataNodes];
		sim.updateVoltageSource(0, nodes[internalNodes+i], p.voltSource, (data & (1<<(dataBits-1-i))) == 0 ? 0 : 5);
		
		// stamp resistor from internal voltage source to data pin.
		// if output enabled, make it a small resistor.  otherwise large.
		sim.stampResistor(nodes[internalNodes+i], nodes[dataNodes+i], outputEnabled ? 1 : 1e8);
	    }
	}
	
	void stepFinished() {
	    int i;
	    int data = 0;
	    boolean writeEnabled = volts[0] < 2.5;
	    if (!writeEnabled)
		return;
	    
	    // store data in RAM
	    for (i = 0; i != dataBits; i++) {
		data |= (volts[dataNodes+i] > 2.5) ? 1<<(dataBits-1-i) : 0;
	    }
	    map.put(address, data);	    
	}
	int getDumpType() { return 413; }
    }
