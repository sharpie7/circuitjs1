package com.lushprojects.circuitjs1.client;

public class OpAmpRealElm extends CompositeElm {

    // from https://commons.wikimedia.org/wiki/File:OpAmpTransistorLevel_Colored_Labeled.svg
    private static String model741String =
	    "NTransistorElm 3 8 9\rNTransistorElm 2 8 10\rPTransistorElm 11 12 9\rPTransistorElm 11 13 10\rNTransistorElm 14 12 1\r" + // Q1-5
            "NTransistorElm 14 13 5\rNTransistorElm 12 7 14\rPTransistorElm 8 8 7\rPTransistorElm 8 11 7\rNTransistorElm 17 11 16\r" + // Q6-10
            "NTransistorElm 17 17 4\rPTransistorElm 18 18 7\rPTransistorElm 18 20 7\rNTransistorElm 20 7 25\rNTransistorElm 13 22 24\r" + // Q11-15
            "NTransistorElm 21 20 22\rNTransistorElm 25 20 6\rNTransistorElm 24 22 23\rPTransistorElm 22 4 15\rNTransistorElm 23 13 4\r" + // Q16-22 (no Q18, Q21)
            "CapacitorElm 13 20\r" +
            "ResistorElm 15 6\rResistorElm 6 25\r" + // output resistors
            "ResistorElm 4 1\rResistorElm 4 14\rResistorElm 4 5\rResistorElm 4 16\rResistorElm 4 24\rResistorElm 4 23\rResistorElm 17 18\r" +
            "ResistorElm 22 21\rResistorElm 21 20\r";
    private static int[] model741ExternalNodes = { 2, 3, 6, 7, 4 }; // , 1, 5 };
    // 0 = input -, 1 = input +, 2 = output, 3 = V+, 4 = V-, 5, 6 = offset null
    
    private static String lm324ModelString =
	    "TransistorElm 1 2 3\rCurrentElm 4 3\rTransistorElm 2 2 5\rTransistorElm 2 6 5\rCapacitorElm 6 7\rCurrentElm 4 8\rCurrentElm 4 7\rTransistorElm 8 4 9\r" +
	    "TransistorElm 7 4 10\rTransistorElm 10 4 11\rTransistorElm 11 7 12\rResistorElm 11 12\rTransistorElm 7 5 12\rCurrentElm 12 5\rTransistorElm 6 5 8\r" + 
	    "ResistorElm 9 5\rTransistorElm 9 7 5\rTransistorElm 13 6 3";
    private static int[] lm324ExternalNodes = { 1, 13, 12, 4, 5 };
    private static String lm324ModelDump =
	    "0 -1 -0 0 10000/0 0.000006/0 1 0 0 100/0 1 0 0 100/0 1e-11 0/0 0.000004/0 0.0001/0 1 0 0 100/0 1 0 0 100/0 1 0 0 100/0 1 0 0 100/0 25/0 -1 0 0 100/0 0.00005/" +
	    "0 -1 0 0 100/0 10000/0 1 0 0 100/0 -1 0 0 10000";
    
    static final int MODEL_741 = 0;
    static final int MODEL_324 = 1;
    
    private static double[] model741resistances = { 50, 25, 1e3, 50e3, 1e3, 5e3, 50e3, 50, 39e3, 7500, 4500 }; 

    int modelType;
    final int opheight = 16;
    final int opwidth = 32;
    double curCounts[];
    double slewRate;
    double currentLimit;
    double capValue;
    final double defaultCurrentLimit = .0231;
    final int FLAG_SWAP = 2;

    public OpAmpRealElm(int xx, int yy) {
	super(xx, yy); // , model741String, model741ExternalNodes);
	noDiagonal = true;
	slewRate = .6;
	currentLimit = defaultCurrentLimit;
	modelType = MODEL_741;
	initModel();
    }

    public OpAmpRealElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	super(xa, ya, xb, yb, f); // , null, model741String, model741ExternalNodes);
	noDiagonal = true;
	slewRate = Double.parseDouble(st.nextToken());
	capValue = Double.parseDouble(st.nextToken());
	currentLimit = defaultCurrentLimit;
	modelType = MODEL_741;
	try {
	    currentLimit = Double.parseDouble(st.nextToken());
	    modelType = Integer.parseInt(st.nextToken());
	} catch (Exception e) {}
	initModel();
    }

    private void initModel() {
	flags |= FLAG_ESCAPE;
	if (modelType == MODEL_741)
	    init741();
	else
	    init324();
	curCounts = new double[5];
	setPoints();
    }
    
    private void init741() {
	loadComposite(null, model741String, model741ExternalNodes);
	
	// adjust capacitor value to get desired slew rate
	getCapacitor().capacitance = 30e-12 / (slewRate/.6);
	getCapacitor().voltdiff = capValue;
	
	// set resistor values
	int i;
	for (i = 0; i != 11; i++)
	    ((ResistorElm) compElmList.get(21+i)).resistance = model741resistances[i];
	
	// adjust output stage resistor values and transistor betas to increase current if desired
	double currentMult = currentLimit / defaultCurrentLimit;
	((ResistorElm) compElmList.get(21)).resistance /= currentMult;
	((ResistorElm) compElmList.get(22)).resistance /= currentMult;
	((TransistorElm) compElmList.get(13)).setBeta(currentMult * 100); // Q14
	((TransistorElm) compElmList.get(18)).setBeta(currentMult * 100); // Q20
	
    }

    private void init324() {
	StringTokenizer st = new StringTokenizer(lm324ModelDump, "/");
	loadComposite(st, lm324ModelString, lm324ExternalNodes);
	
	// adjust capacitor value to get desired slew rate
	getCapacitor().capacitance = 10e-12 / (slewRate/.55);
	getCapacitor().voltdiff = capValue;
	
	// adjust output stage resistor values and transistor betas to increase current if desired
	double currentMult = currentLimit / defaultCurrentLimit;
	((ResistorElm) compElmList.get(11)).resistance /= currentMult;
	((TransistorElm) compElmList.get(9)).setBeta(currentMult * 100);
	((TransistorElm) compElmList.get(10)).setBeta(currentMult * 100);
	((TransistorElm) compElmList.get(12)).setBeta(currentMult * 100);
	((TransistorElm) compElmList.get(16)).setBeta(currentMult * 100);
    }
    
    public void reset() {
	super.reset();
	curCounts = new double[5];
    }

    CapacitorElm getCapacitor() { return ((CapacitorElm) compElmList.get(modelType == MODEL_741 ? 20 : 4)); }
    
    public String dump() {
	return super.dumpWithMask(0) + " " + slewRate + " " + getCapacitor().voltdiff + " " + currentLimit + " " + modelType;
    }
    
    public boolean getConnection(int n1, int n2) {
	return true;
    }

    void draw(Graphics g) {
        setBbox(point1, point2, opheight*2);
        setVoltageColor(g, volts[0]);
        drawThickLine(g, in1p[0], in1p[1]);
        setVoltageColor(g, volts[1]);
        drawThickLine(g, in2p[0], in2p[1]);
        setVoltageColor(g, volts[2]);
        drawThickLine(g, lead2, point2);
        setVoltageColor(g, volts[3]);
        drawThickLine(g, rail1p[0], rail1p[1]);
        setVoltageColor(g, volts[4]);
        drawThickLine(g, rail2p[0], rail2p[1]);
        g.setColor(needsHighlight() ? selectColor : lightGrayColor);
        setPowerColor(g, true);
        drawThickPolygon(g, triangle);
        g.setFont(plusFont);
        drawCenteredText(g, "-", textp[0].x, textp[0].y-2, true);
        drawCenteredText(g, "+", textp[1].x, textp[1].y  , true);
        int i;
        for (i = 0; i != 5; i++)
            curCounts[i] = updateDotCount(getCurrentIntoNode(i), curCounts[i]);
        drawDots(g, in1p[1], in1p[0], curCounts[0]);
        drawDots(g, in2p[1], in2p[0], curCounts[1]);
        drawDots(g, lead2, point2,    curCounts[2]); 
        // these two segments may not be an event multiple of gridSize so we draw them the other way so the dots line up
        drawDots(g, rail1p[0], rail1p[1], -curCounts[3]); 
        drawDots(g, rail2p[0], rail2p[1], -curCounts[4]); 
        drawPosts(g);
    }
    
    Point in1p[], in2p[], textp[], rail1p[], rail2p[];
    Polygon triangle;
    Font plusFont;

    void setPoints() {
        super.setPoints();
        int ww = opwidth;
        if (ww > dn/2)
            ww = (int) (dn/2);
        calcLeads(ww*2);
        int hs = opheight*dsign;
        int hsswap = hs;
        if ((flags & FLAG_SWAP) != 0)
            hsswap = -hsswap;
        in1p = newPointArray(2);
        in2p = newPointArray(2);
        textp = newPointArray(2);
        rail1p = newPointArray(2);
        rail2p = newPointArray(2);
        interpPoint2(point1, point2, in1p[0],  in2p[0], 0, hsswap);
        interpPoint2(lead1 , lead2,  in1p[1],  in2p[1], 0, hsswap);
        interpPoint2(lead1 , lead2,  textp[0], textp[1], .2, hsswap);
        
        // position rails; ideally in middle, but may need to be off-center to fit grid
        double railPos = .5 - ((dn/2) % sim.gridSize)/(ww*2);
        interpPoint2(lead1 , lead2,  rail1p[1], rail2p[1], railPos, hs*2*(1-railPos));
        interpPoint2(lead1 , lead2,  rail1p[0], rail2p[0], railPos, hs*2);
        
        Point tris[] = newPointArray(2);
        interpPoint2(lead1,  lead2,  tris[0], tris[1],  0, hs*2);
        triangle = createPolygon(tris[0], tris[1], lead2);
        plusFont = new Font("SansSerif", 0, 14);
        setPost(0, in1p[0]);
        setPost(1, in2p[0]);
        setPost(2, point2);
        setPost(3, rail1p[0]);
        setPost(4, rail2p[0]);
    }


    @Override
    public int getDumpType() {
	return 409;
    }

    void getInfo(String arr[]) {
	String type = (modelType == MODEL_741) ? "LM741" : "LM324";
        arr[0] = "op-amp (" + type + ")";
        arr[1] = "V+ = " + getVoltageText(volts[1]);
        arr[2] = "V- = " + getVoltageText(volts[0]);
        arr[3] = "Vout = " + getVoltageText(volts[2]);
        arr[4] = "Iout = " + getCurrentText(getCurrentIntoNode(2));
    }
    
    public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo("Slew Rate (V/usec)", slewRate);
	if (n == 1)
	    return new EditInfo("Output Current Limit (A)", currentLimit);
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new Checkbox("Swap Inputs", (flags & FLAG_SWAP) != 0);
            return ei;
        }
        if (n == 3) {
            EditInfo ei =  new EditInfo("<a href=\"opampreal.html\" target=\"_blank\">Model</a>", modelType);
            ei.choice = new Choice();
            ei.choice.add("LM741");
            ei.choice.add("LM324");
            ei.choice.select(modelType);
            return ei;

        }
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0) {
	    slewRate = ei.value;
	    initModel();
	}
	if (n == 1) {
	    currentLimit = ei.value;
	    initModel();
	}
	if (n == 2) {
	    flags = ei.changeFlag(flags, FLAG_SWAP);
	    setPoints();
	}
	if (n == 3) {
	    modelType = ei.choice.getSelectedIndex();
	    capValue = 0;
	    initModel();
	}
    }
    
}
