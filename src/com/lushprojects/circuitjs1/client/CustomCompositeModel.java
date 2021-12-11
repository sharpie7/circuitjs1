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
	String lm317 = ". ~LM317 0 2 2 3 adj 19 1 1 in 1 0 2 out 11 0 3 ResistorElm\\s1\\s2\\rResistorElm\\s1\\s3\\rResistorElm\\s1\\s4\\rResistorElm\\s1\\s5\\rResistorElm\\s1\\s6\\rResistorElm\\s7\\s8\\rResistorElm\\s8\\s9\\rResistorElm\\s10\\s11\\rResistorElm\\s12\\s13\\rResistorElm\\s14\\s11\\rResistorElm\\s15\\s11\\rResistorElm\\s16\\s11\\rResistorElm\\s17\\s11\\rResistorElm\\s18\\s19\\rResistorElm\\s18\\s20\\rResistorElm\\s21\\s22\\rResistorElm\\s23\\s24\\rResistorElm\\s21\\s25\\rResistorElm\\s25\\s24\\rResistorElm\\s21\\s26\\rResistorElm\\s27\\s28\\rResistorElm\\s28\\s11\\rResistorElm\\s29\\s30\\rResistorElm\\s31\\s32\\rResistorElm\\s32\\s33\\rResistorElm\\s33\\s11\\rTransistorElm\\s34\\s34\\s2\\rTransistorElm\\s34\\s8\\s3\\rTransistorElm\\s34\\s12\\s4\\rTransistorElm\\s34\\s21\\s5\\rTransistorElm\\s34\\s35\\s6\\rTransistorElm\\s9\\s11\\s12\\rTransistorElm\\s13\\s15\\s12\\rTransistorElm\\s17\\s11\\s21\\rTransistorElm\\s36\\s11\\s35\\rTransistorElm\\s22\\s11\\s21\\rTransistorElm\\s37\\s36\\s21\\rTransistorElm\\s37\\s37\\s21\\rTransistorElm\\s38\\s21\\s1\\rTransistorElm\\s8\\s34\\s10\\rTransistorElm\\s8\\s9\\s11\\rTransistorElm\\s9\\s13\\s14\\rTransistorElm\\s15\\s21\\s16\\rTransistorElm\\s35\\s1\\s17\\rTransistorElm\\s11\\s36\\s18\\rTransistorElm\\s11\\s37\\s20\\rTransistorElm\\s23\\s22\\s11\\rTransistorElm\\s25\\s24\\s31\\rTransistorElm\\s25\\s24\\s32\\rTransistorElm\\s21\\s38\\s27\\rTransistorElm\\s26\\s1\\s28\\rTransistorElm\\s28\\s1\\s33\\rZenerElm\\s11\\s7\\rZenerElm\\s31\\s30\\rZenerElm\\s29\\s1\\rJfetElm\\s11\\s7\\s1\\rCapacitorElm\\s36\\s11\\rCapacitorElm\\s36\\s19\\rCapacitorElm\\s22\\s23\\rTransistorElm\\s38\\s38\\s1 0\\\\s310\\s0\\\\s310\\s0\\\\s230\\s0\\\\s120\\s0\\\\s5600\\s0\\\\s125000\\s0\\\\s135\\s0\\\\s190\\s0\\\\s12400\\s0\\\\s3600\\s0\\\\s5800\\s0\\\\s110\\s0\\\\s5100\\s0\\\\s12500\\s0\\\\s2400\\s0\\\\s6700\\s0\\\\s6800\\s0\\\\s12000\\s0\\\\s510\\s0\\\\s170\\s0\\\\s160\\s0\\\\s200\\s0\\\\s13000\\s0\\\\s105\\s0\\\\s4\\s0\\\\s0.1\\s0\\\\s-1\\\\s0\\\\s0.0012430534246632061\\\\s100\\s0\\\\s-1\\\\s0.0012430521678491917\\\\s0.001243053426117809\\\\s100\\s0\\\\s-1\\\\s0.0010674884234497975\\\\s0.0012430534259722655\\\\s100\\s0\\\\s-1\\\\s-0.005956547992904524\\\\s0.0012430534224560157\\\\s100\\s0\\\\s-1\\\\s0.0007478827103121987\\\\s0.0012430534155533266\\\\s100\\s0\\\\s-1\\\\s1.6193241278506857e-8\\\\s-0.00017556374430716814\\\\s100\\s0\\\\s-1\\\\s0.00017557976361127128\\\\s-2.5166262005099985e-11\\\\s100\\s0\\\\s-1\\\\s1.7357417616350557e-12\\\\s-0.007199616352167026\\\\s100\\s0\\\\s-1\\\\s0.10270396790524289\\\\s0.10220878225455685\\\\s100\\s0\\\\s-1\\\\s0.0071996159760736\\\\s-3.7782916812584277e-10\\\\s100\\s0\\\\s-1\\\\s-0.02723527531684257\\\\s0.06826907623449754\\\\s100\\s0\\\\s-1\\\\s0\\\\s0.06826907623449754\\\\s100\\s0\\\\s-1\\\\s-0.0036325711850658236\\\\s0.0035670302339710566\\\\s100\\s0\\\\s1\\\\s-0.0012430521678491917\\\\s1.6192257509667906e-8\\\\s100\\s0\\\\s1\\\\s-9.222605475540363e-14\\\\s1.61931490524521e-8\\\\s100\\s0\\\\s1\\\\s-0.00017556371914090614\\\\s1.6190805755241634e-8\\\\s100\\s0\\\\s1\\\\s-0.007199616205131854\\\\s1.460982331743382e-10\\\\s100\\s0\\\\s1\\\\s0.0004951707158201579\\\\s0.0004951856489503036\\\\s100\\s0\\\\s1\\\\s-0.10270396790524289\\\\s-0.09331806904928704\\\\s100\\s0\\\\s1\\\\s-0.07546869258840032\\\\s-0.09331806904048556\\\\s100\\s0\\\\s1\\\\s-4.3909618042264897e-10\\\\s0.0071996155369774195\\\\s100\\s0\\\\s1\\\\s3.290091029478637e-11\\\\s0.007199615565749392\\\\s100\\s0\\\\s1\\\\s3.290091029478637e-11\\\\s0.007199615571695705\\\\s100\\s0\\\\s1\\\\s0.0036325711850658236\\\\s0.007199616347596917\\\\s100\\s0\\\\s1\\\\s0.007199601413526126\\\\s0.007199616344860222\\\\s100\\s0\\\\s1\\\\s-1.4931334096107016e-8\\\\s3.5228866474351733e-12\\\\s100\\s2\\\\szvoltage\\\\\\\\q6.3\\s2\\\\szvoltage\\\\\\\\q6.3\\s2\\\\szvoltage\\\\\\\\q6.3\\s32\\\\s-5\\\\s0.10025\\s0\\\\s3e-11\\\\s0.10270396790524289\\s0\\\\s3e-11\\\\s0.009385898808266277\\s0\\\\s5e-12\\\\s4.3909618042264897e-10\\s0\\\\s-1\\\\s0\\\\s0.0035670302339710566\\\\s100";
	String tl431 = ". ~TL431 0 1 3 3 A 2 0 1 C 1 0 0 ref 3 1 2 ResistorElm\\s3\\s18\\rCapacitorElm\\s18\\s4\\rCapacitorElm\\s18\\s1\\rTransistorElm\\s18\\s1\\s4\\s\\rResistorElm\\s4\\s5\\rResistorElm\\s5\\s6\\rResistorElm\\s5\\s7\\rResistorElm\\s6\\s19\\rCapacitorElm\\s19\\s2\\rCapacitorElm\\s19\\s6\\rTransistorElm\\s19\\s6\\s2\\s\\rResistorElm\\s6\\s20\\rCapacitorElm\\s20\\s8\\rCapacitorElm\\s20\\s7\\rTransistorElm\\s20\\s7\\s8\\s\\rResistorElm\\s8\\s2\\rResistorElm\\s4\\s21\\rCapacitorElm\\s21\\s10\\rCapacitorElm\\s21\\s9\\rTransistorElm\\s21\\s9\\s10\\s\\rResistorElm\\s10\\s11\\rResistorElm\\s7\\s22\\rCapacitorElm\\s22\\s2\\rCapacitorElm\\s22\\s11\\rTransistorElm\\s22\\s11\\s2\\s\\rResistorElm\\s13\\s23\\rCapacitorElm\\s23\\s2\\rCapacitorElm\\s23\\s12\\rTransistorElm\\s23\\s12\\s2\\s\\rResistorElm\\s9\\s24\\rCapacitorElm\\s24\\s14\\rCapacitorElm\\s24\\s9\\rTransistorElm\\s24\\s9\\s14\\s\\rResistorElm\\s9\\s25\\rCapacitorElm\\s25\\s15\\rCapacitorElm\\s25\\s12\\rTransistorElm\\s25\\s12\\s15\\s\\rResistorElm\\s1\\s14\\rResistorElm\\s1\\s15\\rResistorElm\\s12\\s26\\rCapacitorElm\\s26\\s16\\rCapacitorElm\\s26\\s1\\rTransistorElm\\s26\\s1\\s16\\s\\rResistorElm\\s17\\s16\\rResistorElm\\s17\\s27\\rCapacitorElm\\s27\\s2\\rCapacitorElm\\s27\\s1\\rTransistorElm\\s27\\s1\\s2\\s\\rResistorElm\\s17\\s2\\rResistorElm\\s12\\s28\\rCapacitorElm\\s28\\s3\\rCapacitorElm\\s28\\s12\\rTransistorElm\\s28\\s12\\s3\\s\\rDiodeElm\\s2\\s12\\rResistorElm\\s13\\s6\\rDiodeElm\\s2\\s1\\rCapacitorElm\\s1\\s12\\rCapacitorElm\\s7\\s11\\r 0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\stl431ed-qn_ed\\s0\\\\s3280\\s0\\\\s2400\\s0\\\\s7200\\s0\\\\s48\\s2\\\\s1.2e-12\\\\s0\\\\s0\\s2\\\\s2.4e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\stl431ed-qn_ed-A1.2\\s0\\\\s88\\s2\\\\s2.2000000000000003e-12\\\\s0\\\\s0\\s2\\\\s4.400000000000001e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\stl431ed-qn_ed-A2.2\\s0\\\\s800\\s0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\stl431ed-qn_ed\\s0\\\\s4000\\s0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\stl431ed-qn_ed\\s0\\\\s20\\s2\\\\s5e-13\\\\s0\\\\s0\\s2\\\\s1e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\stl431ed-qn_ed-A0.5\\s0\\\\s80\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s3e-12\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s60\\\\stl431ed-qp_ed\\s0\\\\s80\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s3e-12\\\\s0\\\\s0\\s0\\\\s-1\\\\s0\\\\s0\\\\s60\\\\stl431ed-qp_ed\\s0\\\\s800\\s0\\\\s800\\s0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\stl431ed-qn_ed\\s0\\\\s150\\s0\\\\s200\\s2\\\\s5e-12\\\\s0\\\\s0\\s2\\\\s1e-11\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\stl431ed-qn_ed-A5\\s0\\\\s10000\\s0\\\\s40\\s2\\\\s1e-12\\\\s0\\\\s0\\s2\\\\s2e-12\\\\s0\\\\s0\\s0\\\\s1\\\\s0\\\\s0\\\\s140\\\\stl431ed-qn_ed\\s2\\\\stl431ed-d_ed\\s0\\\\s1000\\s2\\\\stl431ed-d_ed\\s2\\\\s1e-11\\\\s0\\\\s0\\s2\\\\s2e-11\\\\s0\\\\s0";
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
