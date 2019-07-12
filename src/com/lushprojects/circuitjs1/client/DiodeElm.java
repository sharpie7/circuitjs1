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

import java.util.Vector;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;

class DiodeElm extends CircuitElm {
    Diode diode;
    static final int FLAG_FWDROP = 1;
    static final int FLAG_MODEL = 2;
    String modelName;
    DiodeModel model;
    static String lastModelName = "default";
    boolean hasResistance;
    int diodeEndNode;
    
    public DiodeElm(int xx, int yy) {
	super(xx, yy);
	modelName = lastModelName;
	diode = new Diode(sim);
	setup();
    }
    
    public DiodeElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	final double defaultdrop = .805904783;
	diode = new Diode(sim);
	double fwdrop = defaultdrop;
	double zvoltage = 0;
	if ((f & FLAG_MODEL) != 0) {
	    modelName = CustomLogicModel.unescape(st.nextToken());
	} else {
	    if ((f & FLAG_FWDROP) > 0) {
		try {
		    fwdrop = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
	    }
	    model = DiodeModel.getModelWithParameters(fwdrop, zvoltage);
	    modelName = model.name;
//	    CirSim.console("model name wparams = " + modelName);
	}
	setup();
    }
    boolean nonLinear() { return true; }
        
    void setup() {
//	CirSim.console("setting up for model " + modelName + " " + model);
        model = DiodeModel.getModelWithNameOrCopy(modelName, model);
        modelName = model.name;   // in case we couldn't find that model
	diode.setup(model);
	hasResistance = (model.seriesResistance > 0);
	diodeEndNode = (hasResistance) ? 2 : 1;
	allocNodes();
    }
    
    int getInternalNodeCount() { return hasResistance ? 1 : 0; }
    
    public void updateModels() {
	setup();
    }
    
    int getDumpType() { return 'd'; }
    String dump() {
	flags |= FLAG_MODEL;
/*	if (modelName == null) {
	    sim.console("model name is null??");
	    modelName = "default";
	}*/
	return super.dump() + " " + CustomLogicModel.escape(modelName);
    }
    
    String dumpModel() {
	if (model.builtIn || model.dumped)
	    return null;
	return model.dump();
    }
    
    final int hs = 8;
    Polygon poly;
    Point cathode[];
	
    void setPoints() {
	super.setPoints();
	calcLeads(16);
	cathode = newPointArray(2);
	Point pa[] = newPointArray(2);
	interpPoint2(lead1, lead2, pa[0], pa[1], 0, hs);
	interpPoint2(lead1, lead2, cathode[0], cathode[1], 1, hs);
	poly = createPolygon(pa[0], pa[1], lead2);
    }
	
    void draw(Graphics g) {
	drawDiode(g);
	doDots(g);
	drawPosts(g);
    }
	
    void reset() {
	diode.reset();
	volts[0] = volts[1] = curcount = 0;
	if (hasResistance)
	    volts[2] = 0;
    }
	
    void drawDiode(Graphics g) {
	setBbox(point1, point2, hs);

	double v1 = volts[0];
	double v2 = volts[1];

	draw2Leads(g);

	// draw arrow thingy
	setVoltageColor(g, v1);
	setPowerColor(g, true);
	g.fillPolygon(poly);

	// draw thing arrow is pointing to
	setVoltageColor(g, v2);
	setPowerColor(g, true);
	drawThickLine(g, cathode[0], cathode[1]);
    }
	
    void stamp() {
	if (hasResistance) {
	    // create diode from node 0 to internal node
	    diode.stamp(nodes[0], nodes[2]);
	    // create resistor from internal node to node 1
	    sim.stampResistor(nodes[1], nodes[2], model.seriesResistance);
	} else
	    // don't need any internal nodes if no series resistance
	    diode.stamp(nodes[0], nodes[1]);
    }
    void doStep() {
	diode.doStep(volts[0]-volts[diodeEndNode]);
    }
    void calculateCurrent() {
	current = diode.calculateCurrent(volts[0]-volts[diodeEndNode]);
    }
    void getInfo(String arr[]) {
	if (model.oldStyle)
	    arr[0] = "diode";
	else
	    arr[0] = sim.LS("diode") + " (" + modelName + ")";
	arr[1] = "I = " + getCurrentText(getCurrent());
	arr[2] = "Vd = " + getVoltageText(getVoltageDiff());
	arr[3] = "P = " + getUnitText(getPower(), "W");
	if (model.oldStyle)
	    arr[4] = "Vf = " + getVoltageText(model.fwdrop);
    }
    
    boolean customModelUI;
    Vector<DiodeModel> models;
    
    public EditInfo getEditInfo(int n) {
	if (!customModelUI && n == 0) {
	    EditInfo ei =  new EditInfo("Model", 0, -1, -1);
	    models = DiodeModel.getModelList(this instanceof ZenerElm);
	    ei.choice = new Choice();
	    int i;
	    for (i = 0; i != models.size(); i++) {
		DiodeModel dm = models.get(i);
		ei.choice.add(dm.getDescription());
		if (dm == model)
		    ei.choice.select(i);
	    }
	    ei.choice.add("Custom");
	    return ei;
	}
        if (n == 0) {
            EditInfo ei = new EditInfo("Model Name", 0, -1, -1);
            ei.text = modelName;
            return ei;
        }
        if (n == 1) {
            if (model.readOnly && !customModelUI)
        		return null;
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.button = new Button(sim.LS("Edit Model"));
            return ei;
        }
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.button = new Button(sim.LS("Create Simple Model"));
            return ei;
        }
        return super.getEditInfo(n);
    }
    
    public void setEditValue(int n, EditInfo ei) {
	if (!customModelUI && n == 0) {
	    int ix = ei.choice.getSelectedIndex();
	    if (ix >= models.size()) {
		models = null;
		customModelUI = true;
		ei.newDialog = true;
		return;
	    }
	    model = models.get(ei.choice.getSelectedIndex());
	    modelName = model.name;
	    setup();
	    return;
	}
        if (n == 0) {
            // the text field may not have been created yet, check to avoid exception
            if (ei.textf == null)
        		return;
            modelName = ei.textf.getText();
            setLastModelName(modelName);
            model = DiodeModel.getModelWithNameOrCopy(modelName, model);
            setup();
            return;
        }
        if (n == 1) {
            if (model.readOnly) {
        		Window.alert(sim.LS("This model cannot be modified.  Change the model name to allow customization."));
        		return;
            }
            EditDialog editDialog = new EditDialog(model, sim);
            CirSim.diodeModelEditDialog = editDialog;
            editDialog.show();
            return;
        }
        if (n == 2) {
            String val = Window.prompt(sim.LS("Fwd Voltage @ 1A"), sim.LS("0.8"));
            try {
        		double fwdrop = new Double(val).doubleValue();
        		if (fwdrop > 0) {
        		    model = DiodeModel.getModelWithVoltageDrop(fwdrop);
        		    modelName = model.name;
        		    ei.newDialog = true;
        		    return;
        		}
            } catch (Exception e) {
            }
        }
        
        super.setEditValue(n, ei);
    }
    int getShortcut() { return 'd'; }
    
    void setLastModelName(String n) {
	lastModelName = n;
    }
    
    void stepFinished() {
        // stop for huge currents that make simulator act weird
        if (Math.abs(current) > 1e12)
            sim.stop("max current exceeded", this);
    }


}
