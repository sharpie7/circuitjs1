package com.lushprojects.circuitjs1.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.lushprojects.circuitjs1.client.ChipElm.Pin;

// instances of subcircuits

public class CustomCompositeElm extends CompositeElm {
    String modelName;
    CustomCompositeChipElm chip;
    int postCount;
    int inputCount, outputCount;
    CustomCompositeModel model;
    static String lastModelName = "default";
    
    public CustomCompositeElm(int xx, int yy) {
	super(xx, yy);
	modelName = lastModelName;
	updateModels();
    }

    public CustomCompositeElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
	super(xa, ya);
	modelName = CustomLogicModel.unescape(st.nextToken());
	updateModels(st);
    }
    
    public String dump() {
	// insert model name before the elements
	String s = super.dumpWithMask(0);
	s += " " + CustomLogicModel.escape(modelName);
	s += dumpElements();
	return s;
    }
    
    String dumpModel() {
	String modelStr = "";
	
	// dump models of all children
	for (int i = 0; i < compElmList.size(); i++) {
	    CircuitElm ce = compElmList.get(i);
	    String m = ce.dumpModel();
	    if (m != null && !m.isEmpty()) {
		if (!modelStr.isEmpty())
		    modelStr += "\n";
		modelStr += m;
	    }
	}
	if (model.dumped)
	    return modelStr;
	
	// dump our model
	if (!modelStr.isEmpty())
	    modelStr += "\n";
	modelStr += model.dump();
	
	return modelStr;
    }
    
    void draw(Graphics g) {
	int i;
	for (i = 0; i != postCount; i++) {
	    chip.volts[i] = volts[i];
	    chip.pins[i].current = getCurrentIntoNode(i); 
	}
	chip.setSelected(needsHighlight());
	chip.draw(g);
	boundingBox = chip.boundingBox;
    }

    void setPoints() {
	chip = new CustomCompositeChipElm(x, y);
	chip.x2 = x2;
	chip.y2 = y2;
	
	chip.sizeX = 2;
	chip.sizeY = (postCount+1)/2;
	chip.allocPins(postCount);
	int i;
	for (i = 0; i != postCount; i++) {
	    boolean left = i < chip.sizeY;
	    int side = (left) ? chip.SIDE_W : chip.SIDE_E;
	    chip.setPin(i, left ? i : i-chip.sizeY, side, model.extList.get(i).name);
	}
	
	chip.setPoints();
	for (i = 0; i != getPostCount(); i++)
	    setPost(i, chip.getPost(i));
    }

    public void updateModels() {
	updateModels(null);
    }
    
    public void updateModels(StringTokenizer st) {
	model = CustomCompositeModel.getModelWithName(modelName);
	if (model == null)
	    return;
	postCount = model.extList.size();
	int externalNodes[] = new int[postCount];
	int i;
	for (i = 0; i != postCount; i++)
	    externalNodes[i] = model.extList.get(i).node;
	if (st == null)
	    st = new StringTokenizer(model.elmDump, " ");
	loadComposite(st, model.nodeList, externalNodes);
	allocNodes();
	setPoints();
    }
    
    int getPostCount() { return postCount; }
    
    public EditInfo getEditInfo(int n) {
	if (n == 0) {
	    EditInfo ei = new EditInfo("Model Name", 0, -1, -1);
	    ei.text = modelName;
	    return ei;
	}
	return null;
    }
    
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0) {
	    sim.debugger();
	    String newName = ei.textf.getText();
	    CustomCompositeModel newModel = CustomCompositeModel.getModelWithName(newName);
	    if (newModel == null) {
		Window.alert(CirSim.LS("Can't find that model."));
		return;
	    }
	    modelName = newName;
	    updateModels();
	    setPoints();
	    return;
	}
    }
    
    int getDumpType() { return 410; }

    void getInfo(String arr[]) {
	super.getInfo(arr);
	arr[0] = "subcircuit (" + model.name + ")";
	int i;
	for (i = 0; i != postCount; i++) {
	    if (i+1 >= arr.length)
		break;
	    arr[i+1] = model.extList.get(i).name + " = " + getVoltageText(volts[i]);
	}
    }
}
