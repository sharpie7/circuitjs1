package com.lushprojects.circuitjs1.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class TransistorModel implements Editable, Comparable<TransistorModel> {

    static HashMap<String, TransistorModel> modelMap;
    
    int flags;
    String name, description;
    double satCur, minBaseResist, baseResist, invRollOffF, BEleakCur, leakBEemissionCoeff, invRollOffR, BCleakCur, leakBCemissionCoeff;
    double baseCurrentHalfResist, emissionCoeffF, emissionCoeffR, invEarlyVoltF, invEarlyVoltR, betaR;
    
    boolean dumped;
    boolean readOnly;
    boolean builtIn;
    
    /*protected*/ TransistorModel(String d) {
	description = d;
	satCur = 1e-13;
        emissionCoeffF = emissionCoeffR = 1;
        leakBEemissionCoeff = 1.5;
        leakBCemissionCoeff = 2;
        betaR = 1;
	updateModel();
    }
    
    static TransistorModel getModelWithName(String name) {
	createModelMap();
	TransistorModel lm = modelMap.get(name);
	if (lm != null)
	    return lm;
	lm = new TransistorModel();
	lm.name = name;
	modelMap.put(name, lm);
	return lm;
    }

    /*
    static TransistorModel getModelWithNameOrCopy(String name, TransistorModel oldmodel) {
	createModelMap();
	TransistorModel lm = modelMap.get(name);
	if (lm != null)
	    return lm;
	if (oldmodel == null) {
	    CirSim.console("model not found: " + name);
	    return getDefaultModel();
	}
	lm = new TransistorModel(oldmodel);
	lm.name = name;
	modelMap.put(name, lm);
	return lm;
    }
    */
    
    static void createModelMap() {
	if (modelMap != null)
	    return;
	modelMap = new HashMap<String,TransistorModel>();
	addDefaultModel("default", new TransistorModel(null));
    }

    static void addDefaultModel(String name, TransistorModel dm) {
	modelMap.put(name, dm);
	dm.readOnly = dm.builtIn = true;
	dm.name = name;
    }
    
    static TransistorModel getDefaultModel() {
	return getModelWithName("default");
    }
    
    static void clearDumpedFlags() {
	if (modelMap == null)
	    return;
	Iterator it = modelMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<String,TransistorModel> pair = (Map.Entry)it.next();
	    pair.getValue().dumped = false;
	}
    }
    
    static Vector<TransistorModel> getModelList() {
	Vector<TransistorModel> vector = new Vector<TransistorModel>();
	Iterator it = modelMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<String,TransistorModel> pair = (Map.Entry)it.next();
	    TransistorModel dm = pair.getValue();
	    vector.add(dm);
	}
	Collections.sort(vector);
	return vector;
    }

    public int compareTo(TransistorModel dm) {
	return name.compareTo(dm.name);
    }
    
    String getDescription() {
	if (description == null)
	    return name;
	return name + " (" + CirSim.LS(description) + ")";
    }
    
    TransistorModel() {
	updateModel();
    }
    
    /*
    TransistorModel(TransistorModel copy) {
	flags = copy.flags;
	updateModel();
    }
    */

    static void undumpModel(StringTokenizer st) {
	String name = CustomLogicModel.unescape(st.nextToken());
	TransistorModel dm = TransistorModel.getModelWithName(name);
	dm.undump(st);
    }
    
    void undump(StringTokenizer st) {
	flags = new Integer(st.nextToken()).intValue();
/*
	saturationCurrent = Double.parseDouble(st.nextToken());
	seriesResistance = Double.parseDouble(st.nextToken());
	emissionCoefficient = Double.parseDouble(st.nextToken());
	breakdownVoltage = Double.parseDouble(st.nextToken());
	try {
	    forwardCurrent = Double.parseDouble(st.nextToken());
	} catch (Exception e) {}
*/
	updateModel();
    }
    
    public EditInfo getEditInfo(int n) {
	/*
	if (n == 0) {
	    EditInfo ei = new EditInfo("Model Name", 0);
	    ei.text = name == null ? "" : name;
	    return ei;
	}
	if (n == 1)
	    return new EditInfo("Saturation Current", saturationCurrent, -1, -1);
	if (isSimple()) {
	    if (n == 2)
		return new EditInfo("Forward Voltage", forwardVoltage, -1, -1);
	    if (n == 3)
		return new EditInfo("Current At Above Voltage (A)", forwardCurrent, -1, -1);
	} else {
	    if (n == 2)
		return new EditInfo("Series Resistance", seriesResistance, -1, -1);
	    if (n == 3)
		return new EditInfo(EditInfo.makeLink("diodecalc.html", "Emission Coefficient"), emissionCoefficient, -1, -1);
	}
	if (n == 4)
	    return new EditInfo("Breakdown Voltage", breakdownVoltage, -1, -1);
	    */
	return null;
    }

    public void setEditValue(int n, EditInfo ei) {
	/*
	if (n == 0) {
	    name = ei.textf.getText();
	    if (name.length() > 0)
		modelMap.put(name, this);
	}
	if (n == 1)
	    saturationCurrent = ei.value;
	if (isSimple()) {
	    if (n == 2)
		forwardVoltage = ei.value;
	    if (n == 3)
		forwardCurrent = ei.value;
	    setEmissionCoefficient();
	} else {
	    if (n == 2)
		seriesResistance = ei.value;
	    if (n == 3)
		emissionCoefficient = ei.value;
	}
	if (n == 4)
	    breakdownVoltage = Math.abs(ei.value);
	updateModel();
	CirSim.theSim.updateModels();
	*/
    }

    void updateModel() {
    }
    
/*
    String dump() {
	dumped = true;
	return "34 " + CustomLogicModel.escape(name) + " " + flags + " " + saturationCurrent + " " + seriesResistance + " " + emissionCoefficient + " " + breakdownVoltage + " " + forwardCurrent;
    }
    
*/
}
