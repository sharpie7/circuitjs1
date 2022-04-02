package com.lushprojects.circuitjs1.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextArea;

// model for subcircuits

class ExtListEntry {
    ExtListEntry(String s, int n) { name = s; node = n; side = ChipElm.SIDE_W; }
    ExtListEntry(String s, int n, int p, int sd) { name = s; node = n; pos = p; side = sd; }
    String name;
    int node, pos, side;
};

public class CustomCompositeModel implements Comparable<CustomCompositeModel> {

    static HashMap<String, CustomCompositeModel> modelMap;
    
    int flags, sizeX, sizeY;
    String name;
    String nodeList;
    Vector<ExtListEntry> extList;
    String elmDump;
    String modelCircuit;
    boolean dumped;
    boolean builtin;
    static int sequenceNumber;
    
    void setName(String n) {
	modelMap.remove(name);
	name = n;
	modelMap.put(name, this);
	sequenceNumber++;
    }

    static void initModelMap() {
	modelMap = new HashMap<String,CustomCompositeModel>();

	// create default stub model
	Vector<ExtListEntry> extList = new Vector<ExtListEntry>();
	extList.add(new ExtListEntry("gnd", 1));
	CustomCompositeModel d = createModel("default", "0 0", "GroundElm 1", extList);
	d.sizeX = d.sizeY = 1;
	modelMap.put(d.name, d);
	sequenceNumber = 1;
	
	// get models from local storage
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor != null) {
            int len = stor.getLength();
            int i;
            for (i = 0; i != len; i++) {
        	String key = stor.key(i);
        	if (!key.startsWith("subcircuit:"))
        	    continue;
        	String data = stor.getItem(key);
        	String firstLine = data;
        	int lineLen = data.indexOf('\n');
        	if (lineLen != -1)
        	    firstLine = data.substring(0, lineLen);
        	StringTokenizer st = new StringTokenizer(firstLine, " ");
        	if (st.nextToken() == ".") {
        	    CustomCompositeModel model = undumpModel(st);
        	    if (lineLen != -1)
        		model.modelCircuit = data.substring(lineLen+1);
        	}
            }
        }
        
        loadBuiltinModels();
    }
    
    static CustomCompositeModel getModelWithName(String name) {
	if (modelMap == null)
	    initModelMap();
	CustomCompositeModel lm = modelMap.get(name);
	return lm;
    }

    static CustomCompositeModel createModel(String name, String elmDump, String nodeList, Vector<ExtListEntry> extList) {
	CustomCompositeModel lm = new CustomCompositeModel();
	lm.name = name;
	lm.elmDump = elmDump;
	lm.nodeList = nodeList;
	lm.extList = extList;
        modelMap.put(name, lm);
        sequenceNumber++;
        return lm;
    }

    static void clearDumpedFlags() {
	if (modelMap == null)
	    return;
	Iterator it = modelMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<String,CustomCompositeModel> pair = (Map.Entry)it.next();
	    pair.getValue().dumped = false;
	}
    }

    static Vector<CustomCompositeModel> getModelList() {
        Vector<CustomCompositeModel> vector = new Vector<CustomCompositeModel>();
        Iterator it = modelMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,CustomCompositeModel> pair = (Map.Entry)it.next();
            CustomCompositeModel dm = pair.getValue();
            if (dm.builtin)
        	continue;
            vector.add(dm);
        }
        Collections.sort(vector);
        return vector;
    }

    public int compareTo(CustomCompositeModel dm) {
        return name.compareTo(dm.name);
    }
    
    CustomCompositeModel() {
    }
    
    static CustomCompositeModel undumpModel(StringTokenizer st) {
	String name = CustomLogicModel.unescape(st.nextToken());
//	CustomCompositeElm.lastModelName = name;
	CustomCompositeModel model = getModelWithName(name);
	if (model == null) {
	    model = new CustomCompositeModel();
	    model.name = name;
	    modelMap.put(name, model);
	    sequenceNumber++;
	} else if (model.modelCircuit != null) {
	    // if model has an associated model circuit, don't overwrite it.  keep the old one.
	    CirSim.console("ignoring model " + name + ", using stored version instead");
	    return model;
	}
	model.undump(st);
	return model;
    }
    
    void undump(StringTokenizer st) {
	flags = Integer.parseInt(st.nextToken());
	sizeX = Integer.parseInt(st.nextToken());
	sizeY = Integer.parseInt(st.nextToken());
	int extCount = Integer.parseInt(st.nextToken());
	int i;
	extList = new Vector<ExtListEntry>();
	for (i = 0; i != extCount; i++) {
	    String s = CustomLogicModel.unescape(st.nextToken());
	    int n = Integer.parseInt(st.nextToken());
	    int p = Integer.parseInt(st.nextToken());
	    int sd = Integer.parseInt(st.nextToken());
	    extList.add(new ExtListEntry(s, n, p, sd));
	}
	nodeList = CustomLogicModel.unescape(st.nextToken());
	elmDump = CustomLogicModel.unescape(st.nextToken());
    }
    
    boolean isSaved() {
	if (name == null)
	    return false;
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return false;
        return stor.getItem("subcircuit:" + name) != null;
    }
    
    void setSaved(boolean sv) {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return;
        if (sv) {
            String cir = (modelCircuit == null) ? "" : modelCircuit;
            stor.setItem("subcircuit:" + name, dump() + "\n" + cir);
        } else
            stor.removeItem("subcircuit:" + name);
    }
    
    String arrayToList(String arr[]) {
	if (arr == null)
	    return "";
	if (arr.length == 0)
	    return "";
	String x = arr[0];
	int i;
	for (i = 1; i < arr.length; i++)
	    x += "," + arr[i];
	return x;
    }
    
    String [] listToArray(String arr) {
	return arr.split(",");
    }
    
    String dump() {
	if (builtin)
	    return "";
	dumped = true;
	String str = ". " + CustomLogicModel.escape(name) + " 0 " + sizeX + " " + sizeY + " " + extList.size() + " ";
        int i;
        for (i = 0; i != extList.size(); i++) {
            ExtListEntry ent = extList.get(i);
            if (i > 0)
                str += " ";
            str += CustomLogicModel.escape(ent.name) + " " + ent.node + " " + ent.pos + " " + ent.side;
        }
        str += " " + CustomLogicModel.escape(nodeList) + " " + CustomLogicModel.escape(elmDump);
        return str;
    }
    
    boolean canLoadModelCircuit() {
	return modelCircuit != null && modelCircuit.length() > 0;
    }
    
    static void loadBuiltinModels() {
	String lm317 = ". ~LM317-v2 0 2 2 3 adj 2 1 1 in 1 0 2 out 3 0 3 JfetElm\\s3\\s4\\s1\\s\\rResistorElm\\s5\\s39\\rCapacitorElm\\s39\\s6\\rCapacitorElm\\s39\\s5\\rTransistorElm\\s39\\s5\\s6\\s\\rResistorElm\\s7\\s40\\rCapacitorElm\\s40\\s8\\rCapacitorElm\\s40\\s5\\rTransistorElm\\s40\\s5\\s8\\s\\rResistorElm\\s5\\s41\\rCapacitorElm\\s41\\s9\\rCapacitorElm\\s41\\s7\\rTransistorElm\\s41\\s7\\s9\\s\\rResistorElm\\s7\\s42\\rCapacitorElm\\s42\\s3\\rCapacitorElm\\s42\\s10\\rTransistorElm\\s42\\s10\\s3\\s\\rResistorElm\\s10\\s43\\rCapacitorElm\\s43\\s11\\rCapacitorElm\\s43\\s3\\rTransistorElm\\s43\\s3\\s11\\s\\rResistorElm\\s10\\s44\\rCapacitorElm\\s44\\s13\\rCapacitorElm\\s44\\s12\\rTransistorElm\\s44\\s12\\s13\\s\\rResistorElm\\s5\\s45\\rCapacitorElm\\s45\\s14\\rCapacitorElm\\s45\\s11\\rTransistorElm\\s45\\s11\\s14\\s\\rResistorElm\\s12\\s46\\rCapacitorElm\\s46\\s11\\rCapacitorElm\\s46\\s15\\rTransistorElm\\s46\\s15\\s11\\s\\rResistorElm\\s5\\s47\\rCapacitorElm\\s47\\s17\\rCapacitorElm\\s47\\s16\\rTransistorElm\\s47\\s16\\s17\\s\\rResistorElm\\s15\\s48\\rCapacitorElm\\s48\\s18\\rCapacitorElm\\s48\\s16\\rTransistorElm\\s48\\s16\\s18\\s\\rResistorElm\\s19\\s49\\rCapacitorElm\\s49\\s16\\rCapacitorElm\\s49\\s3\\rTransistorElm\\s49\\s3\\s16\\s\\rResistorElm\\s20\\s50\\rCapacitorElm\\s50\\s19\\rCapacitorElm\\s50\\s1\\rTransistorElm\\s50\\s1\\s19\\s\\rResistorElm\\s5\\s51\\rCapacitorElm\\s51\\s21\\rCapacitorElm\\s51\\s20\\rTransistorElm\\s51\\s20\\s21\\s\\rResistorElm\\s22\\s52\\rCapacitorElm\\s52\\s20\\rCapacitorElm\\s52\\s3\\rTransistorElm\\s52\\s3\\s20\\s\\rResistorElm\\s23\\s53\\rCapacitorElm\\s53\\s16\\rCapacitorElm\\s53\\s22\\rTransistorElm\\s53\\s22\\s16\\s\\rResistorElm\\s3\\s54\\rCapacitorElm\\s54\\s24\\rCapacitorElm\\s54\\s22\\rTransistorElm\\s54\\s22\\s24\\s\\rResistorElm\\s23\\s55\\rCapacitorElm\\s55\\s16\\rCapacitorElm\\s55\\s23\\rTransistorElm\\s55\\s23\\s16\\s\\rResistorElm\\s3\\s56\\rCapacitorElm\\s56\\s25\\rCapacitorElm\\s56\\s23\\rTransistorElm\\s56\\s23\\s25\\s\\rResistorElm\\s26\\s57\\rCapacitorElm\\s57\\s16\\rCapacitorElm\\s57\\s3\\rTransistorElm\\s57\\s3\\s16\\s\\rResistorElm\\s27\\s58\\rCapacitorElm\\s58\\s3\\rCapacitorElm\\s58\\s26\\rTransistorElm\\s58\\s26\\s3\\s\\rResistorElm\\s28\\s59\\rCapacitorElm\\s59\\s1\\rCapacitorElm\\s59\\s28\\rTransistorElm\\s59\\s28\\s1\\s\\rResistorElm\\s28\\s60\\rCapacitorElm\\s60\\s1\\rCapacitorElm\\s60\\s16\\rTransistorElm\\s60\\s16\\s1\\s\\rResistorElm\\s16\\s61\\rCapacitorElm\\s61\\s29\\rCapacitorElm\\s61\\s28\\rTransistorElm\\s61\\s28\\s29\\s\\rResistorElm\\s31\\s62\\rCapacitorElm\\s62\\s32\\rCapacitorElm\\s62\\s30\\rTransistorElm\\s62\\s30\\s32\\s\\rResistorElm\\s31\\s63\\rCapacitorElm\\s63\\s33\\rCapacitorElm\\s63\\s30\\rTransistorElm\\s63\\s30\\s33\\s\\rResistorElm\\s34\\s64\\rCapacitorElm\\s64\\s35\\rCapacitorElm\\s64\\s1\\rTransistorElm\\s64\\s1\\s35\\s\\rResistorElm\\s35\\s65\\rCapacitorElm\\s65\\s36\\rCapacitorElm\\s65\\s1\\rTransistorElm\\s65\\s1\\s36\\s\\rDiodeElm\\s3\\s4\\rDiodeElm\\s37\\s1\\rDiodeElm\\s32\\s38\\rResistorElm\\s1\\s6\\rResistorElm\\s1\\s9\\rResistorElm\\s1\\s14\\rResistorElm\\s1\\s17\\rResistorElm\\s1\\s21\\rResistorElm\\s4\\s7\\rResistorElm\\s7\\s10\\rResistorElm\\s11\\s12\\rResistorElm\\s8\\s3\\rResistorElm\\s13\\s3\\rResistorElm\\s15\\s3\\rResistorElm\\s18\\s3\\rResistorElm\\s19\\s3\\rResistorElm\\s2\\s24\\rResistorElm\\s24\\s25\\rResistorElm\\s16\\s26\\rResistorElm\\s16\\s31\\rResistorElm\\s29\\s35\\rResistorElm\\s16\\s34\\rResistorElm\\s27\\s30\\rResistorElm\\s30\\s31\\rResistorElm\\s3\\s35\\rResistorElm\\s37\\s38\\rResistorElm\\s33\\s32\\rResistorElm\\s33\\s36\\rResistorElm\\s36\\s3\\rCapacitorElm\\s22\\s3\\rCapacitorElm\\s22\\s2\\rCapacitorElm\\s26\\s27\\rCapacitorElm\\s5\\s3\\rCapacitorElm\\s28\\s3\\rCapacitorElm\\s23\\s3\\r 0\\\\s-7\\\\s0.0001\\s0\\\\s200\\s2\\\\s1.5000000000000002e-13\\\\s0\\\\s0\\s2\\\\s1e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.1\\s0\\\\s500\\s2\\\\s4e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A0.2\\s0\\\\s200\\s2\\\\s1.5000000000000002e-13\\\\s0\\\\s0\\s2\\\\s1e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.1\\s0\\\\s500\\s2\\\\s4e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A0.2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s500\\s2\\\\s4e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A0.2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s500\\s2\\\\s4e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A0.2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s500\\s2\\\\s4e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A0.2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s500\\s2\\\\s4e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A0.2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s50\\s2\\\\s4e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A2\\s0\\\\s100\\s2\\\\s3.0000000000000003e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A0.2\\s0\\\\s500\\s2\\\\s4e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A0.2\\s0\\\\s10\\s2\\\\s3e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A2\\s0\\\\s10\\s2\\\\s3e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s40\\\\s~lm317-qpl-A2\\s0\\\\s50\\s2\\\\s4e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A2\\s0\\\\s500\\s2\\\\s4e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A0.2\\s0\\\\s500\\s2\\\\s4e-13\\\\s0\\\\s0\\s2\\\\s2e-13\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A0.2\\s0\\\\s20\\s2\\\\s1e-11\\\\s0\\\\s0\\s2\\\\s5e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A5\\s0\\\\s2\\s2\\\\s1e-10\\\\s0\\\\s0\\s2\\\\s5e-11\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s80\\\\s~lm317-qnl-A50\\s2\\\\s~lm317-dz\\s2\\\\s~lm317-dz\\s2\\\\s~lm317-dz\\s0\\\\s310\\s0\\\\s310\\s0\\\\s190\\s0\\\\s82\\s0\\\\s5600\\s0\\\\s100000\\s0\\\\s130\\s0\\\\s12400\\s0\\\\s180\\s0\\\\s4100\\s0\\\\s5800\\s0\\\\s72\\s0\\\\s5100\\s0\\\\s12000\\s0\\\\s2400\\s0\\\\s6700\\s0\\\\s12000\\s0\\\\s130\\s0\\\\s370\\s0\\\\s13000\\s0\\\\s400\\s0\\\\s160\\s0\\\\s18000\\s0\\\\s160\\s0\\\\s3\\s0\\\\s0.1\\s2\\\\s3e-11\\\\s0\\\\s0\\s2\\\\s3e-11\\\\s0\\\\s0\\s2\\\\s5e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s1e-12\\\\s0\\\\s0";
	String tl431 = ". ~TL431 0 1 3 3 A 2 0 1 C 1 0 0 ref 3 1 2 ResistorElm\\s3\\s18\\rCapacitorElm\\s18\\s4\\rCapacitorElm\\s18\\s1\\rTransistorElm\\s18\\s1\\s4\\s\\rResistorElm\\s4\\s5\\rResistorElm\\s5\\s6\\rResistorElm\\s5\\s7\\rResistorElm\\s6\\s19\\rCapacitorElm\\s19\\s2\\rCapacitorElm\\s19\\s6\\rTransistorElm\\s19\\s6\\s2\\s\\rResistorElm\\s6\\s20\\rCapacitorElm\\s20\\s8\\rCapacitorElm\\s20\\s7\\rTransistorElm\\s20\\s7\\s8\\s\\rResistorElm\\s8\\s2\\rResistorElm\\s4\\s21\\rCapacitorElm\\s21\\s10\\rCapacitorElm\\s21\\s9\\rTransistorElm\\s21\\s9\\s10\\s\\rResistorElm\\s10\\s11\\rResistorElm\\s7\\s22\\rCapacitorElm\\s22\\s2\\rCapacitorElm\\s22\\s11\\rTransistorElm\\s22\\s11\\s2\\s\\rResistorElm\\s13\\s23\\rCapacitorElm\\s23\\s2\\rCapacitorElm\\s23\\s12\\rTransistorElm\\s23\\s12\\s2\\s\\rResistorElm\\s9\\s24\\rCapacitorElm\\s24\\s14\\rCapacitorElm\\s24\\s9\\rTransistorElm\\s24\\s9\\s14\\s\\rResistorElm\\s9\\s25\\rCapacitorElm\\s25\\s15\\rCapacitorElm\\s25\\s12\\rTransistorElm\\s25\\s12\\s15\\s\\rResistorElm\\s1\\s14\\rResistorElm\\s1\\s15\\rResistorElm\\s12\\s26\\rCapacitorElm\\s26\\s16\\rCapacitorElm\\s26\\s1\\rTransistorElm\\s26\\s1\\s16\\s\\rResistorElm\\s17\\s16\\rResistorElm\\s17\\s27\\rCapacitorElm\\s27\\s2\\rCapacitorElm\\s27\\s1\\rTransistorElm\\s27\\s1\\s2\\s\\rResistorElm\\s17\\s2\\rResistorElm\\s12\\s28\\rCapacitorElm\\s28\\s3\\rCapacitorElm\\s28\\s12\\rTransistorElm\\s28\\s12\\s3\\s\\rDiodeElm\\s2\\s12\\rResistorElm\\s13\\s6\\rDiodeElm\\s2\\s1\\rCapacitorElm\\s1\\s12\\rCapacitorElm\\s7\\s11\\r 0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\s~tl431ed-qn_ed\\s0\\\\s3280\\s0\\\\s2400\\s0\\\\s7200\\s0\\\\s33.333333333333336\\s2\\\\s1.2e-12\\\\s0\\\\s0\\s2\\\\s2.4e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\s~tl431ed-qn_ed-A1.2\\s0\\\\s18.18181818181818\\s2\\\\s2.2000000000000003e-12\\\\s0\\\\s0\\s2\\\\s4.400000000000001e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\s~tl431ed-qn_ed-A2.2\\s0\\\\s800\\s0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\s~tl431ed-qn_ed\\s0\\\\s4000\\s0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\s~tl431ed-qn_ed\\s0\\\\s80\\s2\\\\s5e-13\\\\s0\\\\s0\\s2\\\\s1e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\s~tl431ed-qn_ed-A0.5\\s0\\\\s80\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s3e-12\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s60\\\\s~tl431ed-qp_ed\\s0\\\\s80\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s3e-12\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s60\\\\s~tl431ed-qp_ed\\s0\\\\s800\\s0\\\\s800\\s0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\s~tl431ed-qn_ed\\s0\\\\s150\\s0\\\\s8\\s2\\\\s5e-12\\\\s0\\\\s0\\s2\\\\s1e-11\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\s~tl431ed-qn_ed-A5\\s0\\\\s10000\\s0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\s~tl431ed-qn_ed\\s2\\\\s~tl431ed-d_ed\\s0\\\\s1000\\s2\\\\s~tl431ed-d_ed\\s2\\\\s1e-11\\\\s0\\\\s0\\s2\\\\s2e-11\\\\s0\\\\s0";


	String models[] = { lm317, tl431 };
	int i;
	for (i = 0; i != models.length; i++) {
	    StringTokenizer st = new StringTokenizer(models[i], " ");
	    st.nextToken();
	    CustomCompositeModel model = undumpModel(st);
	    model.builtin = true;
	}
    }
}
