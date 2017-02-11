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

    CompositeElm(int xx, int yy, String s, int externalNodes[]) {
	super(xx, yy);
	loadComposite(s, externalNodes);
	allocNodes();
    }

    public CompositeElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st, String s, int externalNodes[]) {
	super(xa, ya, xb, yb, f);
	loadComposite(s, externalNodes);
	allocNodes();
    }

    public void loadComposite(String s, int externalNodes[]) {
	// TODO Optimize for wires
	HashMap<Integer, CircuitNode> compNodeHash = new HashMap<Integer, CircuitNode>();
	StringTokenizer linet = new StringTokenizer(s, "\r");
	CircuitNode cn;
	CircuitNodeLink cnLink;

	// Build compElmList and compNodeHash from input string

	while (linet.hasMoreTokens()) {
	    String line = linet.nextToken();
	    StringTokenizer st = new StringTokenizer(line, " +\t\n\r\f");
	    String ceType = st.nextToken();
	    CircuitElm newce = CirSim.constructElement(ceType, 0, 0);
	    compElmList.add(newce);

	    int thisPost = 0;
	    while (st.hasMoreTokens()) {
		int nodeOfThisPost = new Integer(st.nextToken()).intValue();
		cnLink = new CircuitNodeLink();
		cnLink.num = thisPost;
		cnLink.elm = newce;
		if (! compNodeHash.containsKey(nodeOfThisPost)) {
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
	CirSim.console("Dumping compNodeHash");
	for (Entry<Integer, CircuitNode> entry : compNodeHash.entrySet()) {
	    int key = entry.getKey();
	    CirSim.console("old node"+key+" Size of links"+compNodeHash.get(key).links.size());
	    compNodeList.add(compNodeHash.get(key));
	}

	numNodes = compNodeList.size();
	
	CirSim.console("Dumping compNodeList");
	for (int i=0;i<numNodes;i++) {
	    CirSim.console("New node"+i+" Size of links:"+compNodeList.get(i).links.size());
	}

	posts = new Point[numPosts];

    }

    public boolean nonLinear() {
	return true; // Lets assume that any useful composite elements are
		     // non-linear
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
	posts[n]=p;
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
    }

    public boolean canViewInScope() {
	return false;
    }

    public void delete() {
	for (int i = 0; i < compElmList.size(); i++)
	    compElmList.get(i).delete();
    }

    abstract double getCurrentIntoPoint(int xa, int ya);

}
