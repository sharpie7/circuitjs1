package com.lushprojects.circuitjs1.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextArea;

public class DiodeModel implements Editable, Comparable<DiodeModel> {

    static HashMap<String, DiodeModel> modelMap;
    
    int flags;
    String name, description;
    double saturationCurrent, seriesResistance, emissionCoefficient, breakdownVoltage;
    boolean dumped;
    boolean readOnly;
    boolean builtIn;
    boolean oldStyle;
    
    // Electron thermal voltage at SPICE's default temperature of 27 C (300.15 K):
    static final double vt = 0.025865;
    // The diode's "scale voltage", the voltage increase which will raise current by a factor of e.
    double vscale;
    // The multiplicative equivalent of dividing by vscale (for speed).
    double vdcoef;
    // voltage drop @ 1A
    double fwdrop;
    
    protected DiodeModel(double sc, double sr, double ec, double bv, String d) {
	saturationCurrent = sc;
	seriesResistance = sr;
	emissionCoefficient = ec;
	breakdownVoltage = bv;
	description = d;
//	CirSim.console("creating diode model " + this);
//	CirSim.debugger();
	updateModel();
    }
    
    static DiodeModel getModelWithName(String name) {
	createModelMap();
	DiodeModel lm = modelMap.get(name);
	if (lm != null)
	    return lm;
	lm = new DiodeModel();
	lm.name = name;
	modelMap.put(name, lm);
	return lm;
    }
    
    static DiodeModel getModelWithNameOrCopy(String name, DiodeModel oldmodel) {
	createModelMap();
	DiodeModel lm = modelMap.get(name);
	if (lm != null)
	    return lm;
	if (oldmodel == null) {
	    CirSim.console("model not found: " + name);
	    return getDefaultModel();
	}
//	CirSim.console("copying to " + name);
	lm = new DiodeModel(oldmodel);
	lm.name = name;
	modelMap.put(name, lm);
	return lm;
    }
    
    static void createModelMap() {
	if (modelMap != null)
	    return;
	modelMap = new HashMap<String,DiodeModel>();
	addDefaultModel("spice-default", new DiodeModel(1e-14, 0, 1, 0, null));
	addDefaultModel("default", new DiodeModel(1.7143528192808883e-7, 0, 2, 0, null));
	addDefaultModel("default-zener", new DiodeModel(1.7143528192808883e-7, 0, 2, 5.6, null));
	
	// old default LED with saturation current that is way too small (causes numerical errors)
	addDefaultModel("old-default-led", new DiodeModel(2.2349907006671927e-18, 0, 2, 0, null));
	
	// default for newly created LEDs, https://www.diyaudio.com/forums/software-tools/25884-spice-models-led.html
	addDefaultModel("default-led", new DiodeModel(93.2e-12, .042, 3.73, 0, null));

	// https://www.allaboutcircuits.com/textbook/semiconductors/chpt-3/spice-models/
	addDefaultModel("1N5711", new DiodeModel(315e-9, 2.8, 2.03, 70, "Schottky"));
	addDefaultModel("1N5712", new DiodeModel(680e-12, 12, 1.003, 20, "Schottky"));
	addDefaultModel("1N34", new DiodeModel(200e-12, 84e-3, 2.19, 60, "germanium"));
	addDefaultModel("1N4004", new DiodeModel(18.8e-9, 28.6e-3, 2, 400, "general purpose"));
//	addDefaultModel("1N3891", new DiodeModel(63e-9, 9.6e-3, 2, 0));  // doesn't match datasheet very well
	
	// http://users.skynet.be/hugocoolens/spice/diodes/1n4148.htm
	addDefaultModel("1N4148", new DiodeModel(4.35e-9, .6458, 1, 75, "switching"));
    }

    static void addDefaultModel(String name, DiodeModel dm) {
	modelMap.put(name, dm);
	dm.readOnly = dm.builtIn = true;
	dm.name = name;
    }
    
    // create a new model using given parameters, keeping backward compatibility.  The method we use has problems, as explained below, but we don't want to
    // change circuit behavior
    static DiodeModel getModelWithParameters(double fwdrop, double zvoltage) {
	createModelMap();
	
	final double emcoef = 2;

	// look for existing model with same parameters
	Iterator it = modelMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<String,DiodeModel> pair = (Map.Entry)it.next();
	    DiodeModel dm = pair.getValue();
	    if (Math.abs(dm.fwdrop-fwdrop) < 1e-8 && dm.seriesResistance == 0 && Math.abs(dm.breakdownVoltage-zvoltage) < 1e-8 && dm.emissionCoefficient == emcoef)
		return dm;
	}

	// create a new one, converting to new parameter values
	final double vscale = emcoef * vt;
	final double vdcoef = 1 / vscale;
	double leakage = 1 / (Math.exp(fwdrop * vdcoef) - 1);
	String name = "fwdrop=" + fwdrop;
	if (zvoltage != 0)
	    name = name + " zvoltage=" + zvoltage;
	DiodeModel dm = getModelWithName(name);
//	CirSim.console("got model with name " + name);
	dm.saturationCurrent = leakage;
	dm.emissionCoefficient = emcoef;
	dm.breakdownVoltage = zvoltage;
	dm.readOnly = dm.oldStyle = true;
//	CirSim.console("at drop current is " + (leakage*(Math.exp(fwdrop*vdcoef)-1)));
//	CirSim.console("sat " + leakage + " em " + emcoef);
	dm.updateModel();
	return dm;
    }
    
    // create a new model using given fwdrop, using older method (pre-Aug 2017) that keeps a constant leakage current, but changes the emission coefficient.
    // We discovered that changing the leakage current to get a given fwdrop does not work well; the leakage currents can be way too high or low.
    static DiodeModel getModelWithVoltageDrop(double fwdrop) {
	createModelMap();

	// look for existing model with same parameters
	Iterator it = modelMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<String,DiodeModel> pair = (Map.Entry)it.next();
	    DiodeModel dm = pair.getValue();
	    if (Math.abs(dm.fwdrop-fwdrop) < 1e-8 && Math.abs(dm.breakdownVoltage) < 1e-8)
		return dm;
	}

	// create a new one, converting to new parameter values
	double leakage = 100e-9;
	double vdcoef = Math.log(1/leakage + 1)/fwdrop;
	double emcoef = 1/(vdcoef*vt);
	String name = "fwdrop=" + fwdrop;
	DiodeModel dm = getModelWithName(name);
//	CirSim.console("got model with name " + name);
	dm.saturationCurrent = leakage;
	dm.emissionCoefficient = emcoef;
	dm.breakdownVoltage = 0;
	dm.updateModel();
	return dm;
    }

    // create a new model using given zener voltage, otherwise the same as default
    static DiodeModel getZenerModel(double zvoltage) {
	createModelMap();

	// look for existing model with same parameters
	Iterator it = modelMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<String,DiodeModel> pair = (Map.Entry)it.next();
	    DiodeModel dm = pair.getValue();
	    if (Math.abs(dm.breakdownVoltage-zvoltage) < 1e-8)
		return dm;
	}

	// create a new one from default
	DiodeModel dd = getModelWithName("default");
	
	String name = "zvoltage=" + zvoltage;
	DiodeModel dm = getModelWithName(name);
	dm.saturationCurrent = dd.saturationCurrent;
	dm.emissionCoefficient = dd.emissionCoefficient;
	dm.breakdownVoltage = zvoltage;
	dm.updateModel();
	return dm;
    }
    
    static DiodeModel getDefaultModel() {
	return getModelWithName("default");
    }
    
    static void clearDumpedFlags() {
	if (modelMap == null)
	    return;
	Iterator it = modelMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<String,DiodeModel> pair = (Map.Entry)it.next();
	    pair.getValue().dumped = false;
	}
    }
    
    static Vector<DiodeModel> getModelList(boolean zener) {
	Vector<DiodeModel> vector = new Vector<DiodeModel>();
	Iterator it = modelMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<String,DiodeModel> pair = (Map.Entry)it.next();
	    DiodeModel dm = pair.getValue();
	    if (zener && dm.breakdownVoltage == 0)
		continue;
	    vector.add(dm);
	}
	Collections.sort(vector);
	return vector;
    }

    public int compareTo(DiodeModel dm) {
	return name.compareTo(dm.name);
    }
    
    String getDescription() {
	if (description == null)
	    return name;
	return name + " (" + CirSim.LS(description) + ")";
    }
    
    DiodeModel() {
	saturationCurrent = 1e-14;
	seriesResistance = 0;
	emissionCoefficient = 1;
	breakdownVoltage = 0;
	updateModel();
    }
    
    DiodeModel(DiodeModel copy) {
	flags = copy.flags;
	saturationCurrent = copy.saturationCurrent;
	seriesResistance = copy.seriesResistance;
	emissionCoefficient = copy.emissionCoefficient;
	breakdownVoltage = copy.breakdownVoltage;
	updateModel();
    }

    static void undumpModel(StringTokenizer st) {
	String name = CustomLogicModel.unescape(st.nextToken());
	DiodeModel dm = DiodeModel.getModelWithName(name);
	dm.undump(st);
    }
    
    void undump(StringTokenizer st) {
	flags = new Integer(st.nextToken()).intValue();
	saturationCurrent = Double.parseDouble(st.nextToken());
	seriesResistance = Double.parseDouble(st.nextToken());
	emissionCoefficient = Double.parseDouble(st.nextToken());
	breakdownVoltage = Double.parseDouble(st.nextToken());
	updateModel();
    }
    
    public EditInfo getEditInfo(int n) {
        if (n == 0)
            return new EditInfo("Saturation Current", saturationCurrent, -1, -1);
        if (n == 1)
            return new EditInfo("Series Resistance", seriesResistance, -1, -1);
        if (n == 2)
            return new EditInfo("Emission Coefficient", emissionCoefficient, -1, -1);
        if (n == 3)
            return new EditInfo("Breakdown Voltage", breakdownVoltage, -1, -1);
	return null;
    }

    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    saturationCurrent = ei.value;
	if (n == 1)
	    seriesResistance = ei.value;
	if (n == 2)
	    emissionCoefficient = ei.value;
	if (n == 3)
	    breakdownVoltage = Math.abs(ei.value);
	updateModel();
	CirSim.theSim.updateModels();
    }

    void updateModel() {
	vscale = emissionCoefficient * vt;
	vdcoef = 1/vscale;
	fwdrop = Math.log(1/saturationCurrent + 1) * emissionCoefficient * vt;
    }
    
    String dump() {
	dumped = true;
	return "34 " + CustomLogicModel.escape(name) + " " + flags + " " + saturationCurrent + " " + seriesResistance + " " + emissionCoefficient + " " + breakdownVoltage;
    }    
}
