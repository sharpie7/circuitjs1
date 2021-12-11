package com.lushprojects.circuitjs1.client;

import java.util.Vector;

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
	
	// use last model as default when creating new element in UI.
	// use default otherwise, to avoid infinite recursion when creating nested subcircuits.
	modelName = (xx == 0 && yy == 0) ? "default" : lastModelName;
	
	flags |= FLAG_ESCAPE;
	updateModels();
    }

    public CustomCompositeElm(int xx, int yy, String name) {
	super(xx, yy);
	modelName = name;
	flags |= FLAG_ESCAPE;
	updateModels();
    }
    
    public CustomCompositeElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
	super(xa, ya, xb, yb, f);
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
	chip.flags = (flags & (ChipElm.FLAG_FLIP_X | ChipElm.FLAG_FLIP_Y | ChipElm.FLAG_FLIP_XY));
	
	chip.sizeX = model.sizeX;
	chip.sizeY = model.sizeY;
	chip.allocPins(postCount);
	int i;
	for (i = 0; i != postCount; i++) {
	    ExtListEntry pin = model.extList.get(i);
	    chip.setPin(i, pin.pos, pin.side, pin.name);
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
    
    Vector<CustomCompositeModel> models;
    
    public EditInfo getEditInfo(int n) {
	// if model is built in, don't allow it to be changed
	if (model.builtin)
	    n += 2;
	
	if (n == 0) {
	    EditInfo ei = new EditInfo(EditInfo.makeLink("subcircuits.html", "Model Name"), 0, -1, -1);
            models = CustomCompositeModel.getModelList();
            ei.choice = new Choice();
            int i;
            for (i = 0; i != models.size(); i++) {
                CustomCompositeModel ccm = models.get(i);
                ei.choice.add(ccm.name);
                if (ccm == model)
                    ei.choice.select(i);
            }
	    return ei;
	}
        if (n == 1) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.button = new Button(sim.LS("Edit Pin Layout"));
            return ei;
        }
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new Checkbox("Flip X", (flags & ChipElm.FLAG_FLIP_X) != 0);
            return ei;
        }
        if (n == 3) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new Checkbox("Flip Y", (flags & ChipElm.FLAG_FLIP_Y) != 0);
            return ei;
        }
        if (n == 4) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new Checkbox("Flip X/Y", (flags & ChipElm.FLAG_FLIP_XY) != 0);
            return ei;
        }
        if (n == 5 && model.canLoadModelCircuit()) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.button = new Button(sim.LS("Load Model Circuit"));
            return ei;
        }
	return null;
    }

    public void setEditValue(int n, EditInfo ei) {
	if (model.builtin)
	    n += 2;
	if (n == 0) {
            model = models.get(ei.choice.getSelectedIndex());
	    lastModelName = modelName = model.name;
	    updateModels();
	    setPoints();
	    return;
	}
        if (n == 1) {
            if (model.name.equals("default")) {
        	Window.alert(CirSim.LS("Can't edit this model."));
        	return;
            }
            EditCompositeModelDialog dlg = new EditCompositeModelDialog();
            dlg.setModel(model);
            dlg.createDialog();
            CirSim.dialogShowing = dlg;
            dlg.show();
            return;
        }
        if (n == 2) {
            flags = ei.changeFlag(flags, ChipElm.FLAG_FLIP_X);
            setPoints();
        }
        if (n == 3) {
            flags = ei.changeFlag(flags, ChipElm.FLAG_FLIP_Y);
            setPoints();
        }
        if (n == 4) {
            flags = ei.changeFlag(flags, ChipElm.FLAG_FLIP_XY);
            setPoints();
        }
        if (n == 5) {
            sim.readCircuit(model.modelCircuit);
            sim.editDialog.closeDialog();
        }
    }
    
    int getDumpType() { return 410; }

    void getInfo(String arr[]) {
	super.getInfo(arr);
	if (model.builtin)
	    arr[0] = model.name.substring(1);
	else
	    arr[0] = "subcircuit (" + model.name + ")";
	int i;
	for (i = 0; i != postCount; i++) {
	    if (i+1 >= arr.length)
		break;
	    arr[i+1] = model.extList.get(i).name + " = " + getVoltageText(volts[i]);
	}
    }
}
