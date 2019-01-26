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

    class SevenSegElm extends ChipElm {
	// base segment count not including decimal point or colon
	int baseSegmentCount;
	
	// segment count including decimal point or colon
	int segmentCount;

	int extraSegment;
	static final int ES_NONE = 0;
	static final int ES_DP = 1;
	static final int ES_COLON = 2;
	
	int pinCount;
	int commonPin;
	
	// 1 = common cathode, -1 = common anode, 0 = no diodes
	int diodeDirection;
	
	public SevenSegElm(int xx, int yy) {
	    super(xx, yy);
	    setDefaults();
	    setPinCount();
	}
	public SevenSegElm(int xa, int ya, int xb, int yb, int f,
			   StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    setDefaults();
	    try {
		baseSegmentCount = Integer.parseInt(st.nextToken());
		extraSegment = Integer.parseInt(st.nextToken());
		diodeDirection = Integer.parseInt(st.nextToken());
	    } catch (Exception e) {}
	    setPinCount();
	}
	
	void setDefaults() {
	    baseSegmentCount = segmentCount = 7;
	    diodeDirection = 0;
	}
	
	String dump() { return super.dump() + " " + baseSegmentCount + " " + extraSegment + " " + diodeDirection; }
	
	String getChipName() { return segmentCount + "-segment display"; }
	Color darkred;
	
	void setupPins() {
	    if (pinCount == 0)
		return;
	    darkred = new Color(30, 0, 0);
	    int segmentPinsOnLeftSide = (baseSegmentCount+1)/2;
	    sizeY = segmentPinsOnLeftSide;
	    if (baseSegmentCount == 7) {
		sizeX = 4;
		if (pinCount > 7)
		    sizeX = 5;
	    } else
		sizeX = 5;
	    
	    // make room for common/dp/colon pins
	    if (pinCount > sizeY*2)
		sizeY++;
	    
	    pins = new Pin[pinCount];
	    int i;
	    for (i = 0; i != segmentPinsOnLeftSide; i++)
		pins[i] = new Pin(i, SIDE_W, Character.toString((char)('a'+i)));
	    
	    // retain backward compatibility pin layout for old 7-segment setup, otherwise put pins on left and right side
	    boolean backwardCompatibility = (segmentCount == 7 && diodeDirection == 0 && extraSegment == ES_NONE);
	    int s = (backwardCompatibility) ? 1 : 0;
	    for (; i != segmentCount; i++)
		pins[i] = new Pin(s++, backwardCompatibility ? SIDE_S : SIDE_E, Character.toString((char)('a'+i)));
	    if (extraSegment == ES_DP)
		pins[segmentCount-1].text = "dp";
	    if (commonPin > 0) {
		int side = SIDE_E;
		if (segmentCount != 7) {
		    side = SIDE_W;
		    s = segmentPinsOnLeftSide;
		}
		pins[commonPin] = new Pin(s++, side, (diodeDirection == 1) ? "gnd" : "Vcc");
	    }
	}
	
	void drawSegment(Graphics g, int x1, int y1, int x2, int y2) {
	    drawSegment(g, new Point(x1, y1), new Point(x2, y2));
	}
	void drawSegment(Graphics g, Point p1, Point p2) {
	    g.context.beginPath();
	    Point p3 = new Point();
	    Point p4 = new Point();
	    Point p5 = new Point();
	    Point p6 = new Point();
	    double dn = Math.hypot(p1.x-p2.x, p1.y-p2.y);
	    // from p1 to p2, calculate points 5 pixels from each end, 5 pixels offset from center of line on both sides
	    interpPoint2(p1, p2, p3, p4, 5/dn, 5); 
	    interpPoint2(p1, p2, p5, p6, 1-5/dn, 5);
	    g.context.moveTo(p1.x, p1.y);
	    g.context.lineTo(p3.x, p3.y);
	    g.context.lineTo(p5.x, p5.y);
	    g.context.lineTo(p2.x, p2.y);
	    g.context.lineTo(p6.x, p6.y);
	    g.context.lineTo(p4.x, p4.y);
	    g.context.lineTo(p1.x, p1.y);
	    g.context.fill();
	}
	void drawDecimal(Graphics g, int x, int y) {
	    int sp = 7;
	    g.context.beginPath();
	    g.context.moveTo(x, y-sp);
	    g.context.lineTo(x-sp, y);
	    g.context.lineTo(x, y+sp);
	    g.context.lineTo(x+sp, y);
	    g.context.lineTo(x, y-sp);
	    g.context.fill();
	}
	static int display7[] = {
		// x1, y1, x2, y2 for each segment
		0, 0, 2, 0,
		2, 0, 2, 1,
		2, 1, 2, 2,
		0, 2, 2, 2,
		0, 1, 0, 2,
		0, 0, 0, 1,
		0, 1, 2, 1
	};
	static int display16[] = {
		0, 0, 1, 0,
		1, 0, 2, 0,
		2, 0, 2, 1,
		2, 1, 2, 2,
		2, 2, 1, 2,
		1, 2, 0, 2,
		0, 2, 0, 1,
		0, 1, 0, 0,
		0, 0, 1, 1,
		1, 0, 1, 1,
		2, 0, 1, 1,
		1, 1, 2, 1,
		1, 1, 2, 2,
		1, 1, 1, 2,
		1, 1, 0, 2,
		0, 1, 1, 1
	};
	static int display14[] = {
		0, 0, 2, 0,
		2, 0, 2, 1,
		2, 1, 2, 2,
		2, 2, 0, 2,
		0, 2, 0, 1,
		0, 1, 0, 0,
		0, 0, 1, 1,
		1, 0, 1, 1,
		2, 0, 1, 1,
		1, 1, 2, 1,
		1, 1, 2, 2,
		1, 1, 1, 2,
		1, 1, 0, 2,
		0, 1, 1, 1
	};
	
	Diode diodes[];
	void stamp() {
	    super.stamp();
	    
	    if (diodeDirection == 0)
		return;
	    diodes = new Diode[segmentCount];
	    int i;
	    DiodeModel model = DiodeModel.getModelWithName("default-led");
	    for (i = 0; i != segmentCount; i++) {
		diodes[i] = new Diode(sim);
		diodes[i].setup(model);
		if (diodeDirection == 1)
		    diodes[i].stamp(nodes[i], nodes[commonPin]);
		else
		    diodes[i].stamp(nodes[commonPin], nodes[i]);
	    }
	} 
	void doStep() {
	    super.doStep();
	    
	    if (diodeDirection == 0)
		return;
	    
	    int i;
	    for (i = 0; i != segmentCount; i++)
		diodes[i].doStep(diodeDirection*(volts[i]-volts[commonPin]));
	}
        boolean nonLinear() { return diodeDirection != 0; }
	void draw(Graphics g) {
	    drawChip(g);
	    g.setColor(Color.red);
	    int spx = cspc*2;
	    
	    // make room for dp/colon
	    if (extraSegment != ES_NONE)
		spx = (int)(spx*.9);
	    
	    if (sizeY <= 4)
		spx /= 2;
	    int spy = spx*2;
	    int xl = x+cspc + sizeX*cspc - spx;
	    int yl = y-cspc + sizeY*cspc - spy;
	    int i;
	    int disp[] = (baseSegmentCount == 7) ? display7 : (baseSegmentCount == 14) ? display14 : display16;
	    int step;
	    for (step = 0; step != 2; step++)
		for (i = 0; i != segmentCount; i++) {
		    int i4 = i*4;
		    // draw diagonal lines in first pass, so the other lines overlap
		    boolean diag = (disp[i4] != disp[i4+2] && disp[i4+1] != disp[i4+3]);
		    if (diag != (step == 0))
			continue;
		    setColor(g, i);
		    drawSegment(g, xl+disp[i4]*spx, yl+disp[i4+1]*spy, xl+disp[i4+2]*spx, yl+disp[i4+3]*spy);
		}
	    if (extraSegment == ES_DP) {
		setColor(g, baseSegmentCount);
		int dist = (int)Math.max(spx*1.5, spx+12);
		drawDecimal(g, xl+spx+dist, yl+spy*2);
	    }
	    if (extraSegment == ES_COLON) {
		setColor(g, baseSegmentCount);
		int dist = (int)Math.max(spx*1.5, spx+14);
		drawDecimal(g, xl+spx+dist, yl+(int)(spy*.5));
		drawDecimal(g, xl+spx+dist, yl+(int)(spy*1.5));
	    }
	}
	
	void calculateCurrent() {
	    if (diodeDirection == 0) {
		// no current
		int i;
		for (i = 0; i != pinCount; i++)
		    pins[i].current = 0;
		return;
	    }
	    
	    // calculate diode currents
	    int i;
	    pins[commonPin].current = 0;
	    for (i = 0; i != segmentCount; i++) {
		pins[i].current = -diodeDirection*diodes[i].calculateCurrent(diodeDirection*(volts[i]-volts[commonPin]));
		pins[commonPin].current -= pins[i].current;
	    }
	}
	
	void stepFinished() {
	    // stop for huge currents that make simulator act weird
	    if (commonPin > 0 && Math.abs(pins[commonPin].current) > 1e12)
		sim.stop("max current exceeded", this);
	}

	void setColor(Graphics g, int p) {
	    if (diodeDirection == 0) {
		g.setColor(pins[p].value ? Color.red :
		       sim.printableCheckItem.getState() ? Color.white : darkred);
		return;
	    }
	    // 10mA current = max brightness
	    double w = -diodeDirection*pins[p].current / .01;
            if (w > 0)
                w = 255*(1+.2*Math.log(w));
            if (w > 255)
                w = 255;
            if (w < 30)
                w = 30;
            Color cc = new Color((int) w, 0, 0);
            g.setColor(cc);
	}
	int getPostCount() { return pinCount; }
	int getVoltageSourceCount() { return 0; }
	int getDumpType() { return 157; }
	
	public EditInfo getEditInfo(int n) {
	        if (n == 2) {
	            EditInfo ei =  new EditInfo("Segments", 0, -1, -1);
	            ei.choice = new Choice();
	            ei.choice.add("7 Segment");
	            ei.choice.add("14 Segment");
	            ei.choice.add("16 Segment");
	            ei.choice.select(baseSegmentCount == 7 ? 0 : baseSegmentCount == 14 ? 1 : 2);
	            return ei;
	        }
	        if (n == 3) {
	            EditInfo ei =  new EditInfo("Extra Segment", 0, -1, -1);
	            ei.choice = new Choice();
	            ei.choice.add("None");
	            ei.choice.add("Decimal Point");
	            ei.choice.add("Colon");
	            ei.choice.select(extraSegment);
	            return ei;
	        }
	        if (n == 4) {
	            EditInfo ei =  new EditInfo("Diodes", 0, -1, -1);
	            ei.choice = new Choice();
	            ei.choice.add("Common Cathode");
	            ei.choice.add("Common Anode");
	            ei.choice.add("None (logic inputs)");
	            ei.choice.select(diodeDirection == 1 ? 0 : diodeDirection == -1 ? 1 : 2);
	            return ei;
	        }
	        return super.getEditInfo(n);
	}
	
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 2) {
		int ix = ei.choice.getSelectedIndex();
		baseSegmentCount = (ix == 0) ? 7 : (ix == 1) ? 14 : 16;
		setPinCount();
		return;
	    }
	    if (n == 3) {
		extraSegment = ei.choice.getSelectedIndex();
		setPinCount();
		return;
	    }
	    if (n == 4) {
		int ix = ei.choice.getSelectedIndex();
		diodeDirection = (ix == 0) ? 1 : (ix == 1) ? -1 : 0;
		setPinCount();
		return;
	    }
	    super.setEditValue(n, ei);
	}
	
	void setPinCount() {
	    segmentCount = baseSegmentCount;
	    if (extraSegment > 0)
		segmentCount++;
	    if (diodeDirection == 0) {
		pinCount = segmentCount;
		commonPin = -1;
	    } else {
		pinCount = segmentCount + 1;
		commonPin = pinCount-1;
	    }
	    allocNodes();
	    setupPins();
	    setPoints();
	}
    }
