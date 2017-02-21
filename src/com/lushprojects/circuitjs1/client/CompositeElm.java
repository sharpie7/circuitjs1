package com.lushprojects.circuitjs1.client;

import java.util.Vector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

// Circuit element made up of a composition of other circuit elements
// Using this will be (relatively) inefficient in terms of simulation performance because
// all the internal workings of the element are simulated from the individual components.
// However, it may allow some types of components to be more quickly programed in to the simulator
// than writing each component from scratch.
// It may also, eventually, provide a path to allow user created circuits to be 
// re-imported in to the simuation as new circuit elements.

// Instatiations should:
// - Set the variable "diagonal" in the constructors
// - Override constructors to set up the elements posts/leads etc. and configure the contents of the CompositeElm
// - Override getDumpType, dump, draw, getPost, getInfo, setPoints, canViewInScope, getCurrentIntoPoint

public abstract class CompositeElm extends CircuitElm {

    Vector<CircuitElm> compElmList = new Vector<CircuitElm>();
    protected Vector<CircuitNode> compNodeList = new Vector<CircuitNode>();
    protected int numPosts = 0;
    protected int numNodes = 0;
    protected Point posts[];
    protected Vector<VoltageSourceRecord> voltageSources = new Vector<VoltageSourceRecord>();

    CompositeElm(int xx, int yy, String s, int externalNodes[]) {
	super(xx, yy);
	loadComposite(null, s, externalNodes);
	allocNodes();
    }

    public CompositeElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st, String s, int externalNodes[]) {
	super(xa, ya, xb, yb, f);
	loadComposite(st, s, externalNodes);
	allocNodes();
    }

    public void loadComposite(StringTokenizer stIn, String model, int externalNodes[]) {
	HashMap<Integer, CircuitNode> compNodeHash = new HashMap<Integer, CircuitNode>();
	StringTokenizer modelLinet = new StringTokenizer(model, "\r");
	CircuitNode cn;
	CircuitNodeLink cnLink;
	VoltageSourceRecord vsRecord;

	// Build compElmList and compNodeHash from input string

	while (modelLinet.hasMoreTokens()) {
	    String line = modelLinet.nextToken();
	    StringTokenizer stModel = new StringTokenizer(line, " +\t\n\r\f");
	    String ceType = stModel.nextToken();
	    CircuitElm newce = CirSim.constructElement(ceType, 0, 0);
	    if (stIn!=null) {
		int tint = newce.getDumpType();
		String dumpedCe= stIn.nextToken();
		StringTokenizer stCe = new StringTokenizer(dumpedCe, "_");
		int flags = new Integer(stCe.nextToken()).intValue();
		newce = CirSim.createCe(tint, 0, 0, 0, 0, flags, stCe);
	    }
	    compElmList.add(newce);

	    int thisPost = 0;
	    while (stModel.hasMoreTokens()) {
		int nodeOfThisPost = new Integer(stModel.nextToken()).intValue();
		cnLink = new CircuitNodeLink();
		cnLink.num = thisPost;
		cnLink.elm = newce;
		if (!compNodeHash.containsKey(nodeOfThisPost)) {
		    cn = new CircuitNode();
		    cn.links.add(cnLink);
		    compNodeHash.put(nodeOfThisPost, cn);
		} else {
		    cn = compNodeHash.get(nodeOfThisPost);
		    cn.links.add(cnLink);
		}
		thisPost++;
	    }
	}

	// Flatten compNodeHash in to compNodeList
	numPosts = externalNodes.length;
	for (int i = 0; i < externalNodes.length; i++) { // External Nodes First
	    if (compNodeHash.containsKey(externalNodes[i])) {
		compNodeList.add(compNodeHash.get(externalNodes[i]));
		compNodeHash.remove(externalNodes[i]);
	    } else
		throw new IllegalArgumentException();
	}
	for (Entry<Integer, CircuitNode> entry : compNodeHash.entrySet()) {
	    int key = entry.getKey();
	    compNodeList.add(compNodeHash.get(key));
	}

	numNodes = compNodeList.size();

//	CirSim.console("Dumping compNodeList");
//	for (int i = 0; i < numNodes; i++) {
//	    CirSim.console("New node" + i + " Size of links:" + compNodeList.get(i).links.size());
//	}

	posts = new Point[numPosts];
	
	// Enumerate voltage sources
	for (int i = 0; i < compElmList.size(); i++) {
	    int cnt = compElmList.get(i).getVoltageSourceCount();
	    for (int j=0;j < cnt ; j++) {
		vsRecord = new VoltageSourceRecord();
		vsRecord.elm = compElmList.get(i);
		vsRecord.vsNumForElement = j;
		voltageSources.add(vsRecord);
	    }
	}

    }

    public boolean nonLinear() {
	return true; // Lets assume that any useful composite elements are
		     // non-linear
    }
    
    abstract public int getDumpType();
    
    public String dump() {
		String dumpStr=super.dump();
		for (int i = 0; i < compElmList.size(); i++) {
		    String tstring = compElmList.get(i).dump().replace(' ', '_');
		    tstring = tstring.replaceFirst("[A-Za-z0-9]+_0_0_0_0_", ""); // remove unused tint_x1 y1 x2 y2 coords for internal components
		    dumpStr += " "+ tstring;
		}
//		for (int i=0; i<numPosts; i++) {
//		    dumpStr += " "+posts[i].x + " " + posts[i].y;
//		}
		return dumpStr;
    }
    
    public boolean getConnection(int n1, int n2) {
	// TODO Find out if more sophisticated handling is needed here
	// In the meantime subclasses should override this if they know  nodes are not connected
	return true;
    }
    
    // is n1 connected to ground somehow?
    public boolean hasGroundConnection(int n1) {
	Vector<CircuitNodeLink> cnLinks;
	cnLinks = compNodeList.get(n1).links;
	for (int i = 0; i < cnLinks.size(); i++) {
	    if (cnLinks.get(i).elm.hasGroundConnection(cnLinks.get(i).num))
		    return true;
	}
	return false; 
    }

    public void reset() {
	for (int i = 0; i < compElmList.size(); i++)
	    compElmList.get(i).reset();
    }

    int getPostCount() {
	return numPosts;
    }

    int getInternalNodeCount() {
	return numNodes - numPosts;
    }

    Point getPost(int n) {
	return posts[n];
    }

    void setPost(int n, Point p) {
	posts[n] = p;
    }

    void setPost(int n, int x, int y) {
	posts[n].x = x;
	posts[n].y = y;
    }

    public double getPower() {
	double power;
	power = 0;
	for (int i = 0; i < compElmList.size(); i++)
	    power += compElmList.get(i).getPower();
	return power;
    }

    public void stamp() {
	for (int i = 0; i < compElmList.size(); i++)
	    compElmList.get(i).stamp();
    }

    public void doStep() {
	for (int i = 0; i < compElmList.size(); i++)
	    compElmList.get(i).doStep();
    }

    public void stepFinished() {
	for (int i = 0; i < compElmList.size(); i++)
	    compElmList.get(i).stepFinished();
    }

    abstract void getInfo(String arr[]);

    public void setNode(int p, int n) {
	// nodes[p] = n
	Vector<CircuitNodeLink> cnLinks;
	super.setNode(p, n);
	cnLinks = compNodeList.get(p).links;
	for (int i = 0; i < cnLinks.size(); i++) {
	    cnLinks.get(i).elm.setNode(cnLinks.get(i).num, n);
	}

    }

    public void setNodeVoltage(int n, double c) {
	// volts[n] = c;
	Vector<CircuitNodeLink> cnLinks;
	super.setNodeVoltage(n, c);
	cnLinks = compNodeList.get(n).links;
	for (int i = 0; i < cnLinks.size(); i++) {
	    cnLinks.get(i).elm.setNodeVoltage(cnLinks.get(i).num, c);
	}
	volts[n]=c;
    }

    public boolean canViewInScope() {
	return false;
    }

    public void delete() {
	for (int i = 0; i < compElmList.size(); i++)
	    compElmList.get(i).delete();
    }

    public int getVoltageSourceCount() {
	return voltageSources.size();
    }

    // Find the component with the nth voltage
    // and set the
    // appropriate source in that component
    void setVoltageSource(int n, int v) {
	// voltSource(n) = v;
	VoltageSourceRecord vsr;
	vsr=voltageSources.get(n);
	vsr.elm.setVoltageSource(vsr.vsNumForElement, v);
	vsr.vsNode=v;
    }
    
    @Override
     public void   setCurrent(int vsn, double c) {
	for (int i=0;i<voltageSources.size(); i++)
	    if (voltageSources.get(i).vsNode == vsn) {
		voltageSources.get(i).elm.setCurrent(voltageSources.get(i).vsNumForElement, c);
	    }
	
    }

 // It is hard to write a general purpose getCurrentIntoNode routine because this is not defined
 // for many circuit elements. Working-around by using getCurrentIntoPoint doesn't work for 
    // internal components as they don't have points.
    // If all components in the composite have "getCurrentIntoNode" implemented then you can use this
    // routine. If not then you must override with your own code.
    double getCurrentIntoNode(int n) {
	double c=0;
	Vector<CircuitNodeLink> cnLinks;
	cnLinks = compNodeList.get(n).links;
	for (int i = 0; i < cnLinks.size(); i++) {
	    c+=cnLinks.get(i).elm.getCurrentIntoNode(cnLinks.get(i).num);
	}
	return c;
    }
    
    
    double getCurrentIntoPoint(int xa, int ya) {
	for(int i=0; i<posts.length; i++) {
	    if (posts[i].x==xa && posts[i].y == ya)
		return getCurrentIntoNode(i);
	}
	return 0;
    }

}


class VoltageSourceRecord {
	int vsNumForElement;
	int vsNode;
	CircuitElm elm;
}
