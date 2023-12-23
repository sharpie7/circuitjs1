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

import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.storage.client.Storage;


import java.util.Vector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;

// plot of single value on a scope
class ScopePlot {
    double minValues[], maxValues[];
    int scopePointCount;
    int ptr; // ptr is pointer to the current sample
    int value; // Value - the property being shown - e.g. VAL_CURRENT
    // scopePlotSpeed is in sim timestep units per pixel
    int scopePlotSpeed, units;
    double lastUpdateTime;
    double lastValue;
    String color;
    CircuitElm elm;
   // Has a manual scale in "/div" format been put in by the user (as opposed to being
   // inferred from a "MaxValue" format or from an automatically calculated scale)?
   // Manual scales should be kept to sane values anyway, but this shows if this is a user
   // intention we should respect, or if we should try and populate reasonable values from
   // the data we have
    boolean manScaleSet = false; 
    double manScale = 1.0; // Units per division
    int manVPosition = 0; // 0 is center of screen. +V_POSITION_STEPS/2 is top of screen
    double gridMult;
    double plotOffset;
    boolean acCoupled = false;
    double acAlpha = 0.9999; // Filter coefficient for AC coupling
    double acLastOut = 0; // Store y[i-1] term for AC coupling filter
    
    final static int FLAG_AC=1;
    
    ScopePlot(CircuitElm e, int u) {
	elm = e;
	units = u;
    }
    
    ScopePlot(CircuitElm e, int u, int v, double manS) {
	elm = e;
	units = u;
	value = v;
	manScale = manS;
	// I *think* all units other than V and A can only be positive, so for these move the v position to the bottom.
	if (units > Scope.UNITS_A)
	    manVPosition = -Scope.V_POSITION_STEPS/2;
    }

    int startIndex(int w) {
	return ptr + scopePointCount - w; 
    }
    
    void reset(int spc, int sp, boolean full) {
	int oldSpc = scopePointCount;
	scopePointCount = spc;
	if (scopePlotSpeed != sp)
	    oldSpc = 0; // throw away old data
	scopePlotSpeed = sp;
	// Adjust the time constant of the AC coupled filter in proportion to the number of samples
	// we are seeing on the scope (if my maths is right). The constant is empirically determined
	acAlpha = 1.0-1.0/(1.15*scopePlotSpeed*scopePointCount);
	double oldMin[] = minValues;
	double oldMax[] = maxValues;
    	minValues = new double[scopePointCount];
    	maxValues = new double[scopePointCount];
    	if (oldMin != null && !full) {
    	    // preserve old data if possible
    	    int i;
    	    for (i = 0; i != scopePointCount && i != oldSpc; i++) {
    		int i1 = (-i) & (scopePointCount-1);
    		int i2 = (ptr-i) & (oldSpc-1);
    		minValues[i1] = oldMin[i2];
    		maxValues[i1] = oldMax[i2];
    	    }
    	} else
    	    lastUpdateTime = CirSim.theSim.t;
    	ptr = 0;
    }

    void timeStep() {
	if (elm == null)
		return;
	double v = elm.getScopeValue(value);
	 // AC coupling filter. 1st order IIR high pass
	 // y[i] = alpha x (y[i-1]+x[i]-x[i-1])
	 // We calculate for all iterations (even DC coupled) to prime the data in case they switch to AC later
	double newAcOut=acAlpha*(acLastOut+v-lastValue);
	lastValue = v;
	acLastOut = newAcOut;
	if (isAcCoupled())
	    v = newAcOut;
	if (v < minValues[ptr])
		minValues[ptr] = v;
	if (v > maxValues[ptr])
		maxValues[ptr] = v;
	if (CirSim.theSim.t-lastUpdateTime >= CirSim.theSim.maxTimeStep * scopePlotSpeed) {
	    ptr = (ptr+1) & (scopePointCount-1);
	    minValues[ptr] = maxValues[ptr] = v;
	    lastUpdateTime += CirSim.theSim.maxTimeStep * scopePlotSpeed;
	}
    }
    
    String getUnitText(double v) {
	switch (units) {
	case Scope.UNITS_V:
	    return CircuitElm.getVoltageText(v);
	case Scope.UNITS_A:
	    return CircuitElm.getCurrentText(v);
	case Scope.UNITS_OHMS:
	    return CircuitElm.getUnitText(v, CirSim.ohmString);
	case Scope.UNITS_W:
	    return CircuitElm.getUnitText(v, "W");
	}
	return null;
    }

    static final String colors[] = {
	    "#FF0000", "#FF8000", "#FF00FF", "#7F00FF",
	    "#0000FF", "#0080FF", "#FFFF00", "#00FFFF", 
    };
    
    void assignColor(int count) {
	if (count > 0) {
	    color = colors[(count-1) % 8];
	    return;
	}
	switch (units) {
	case Scope.UNITS_V:
	    color = CircuitElm.positiveColor.getHexValue();
	    break;
	case Scope.UNITS_A:
	    color = (CirSim.theSim.printableCheckItem.getState()) ? "#A0A000" : "#FFFF00";
	    break;
	default:
	    color = (CirSim.theSim.printableCheckItem.getState()) ? "#000000" : "#FFFFFF";
	    break;
	}
    }
    
    void setAcCoupled(boolean b) {
	if (canAcCouple()) {
	    acCoupled = b;
	}
	else
	    acCoupled = false;
    }
    
    boolean canAcCouple() {
	return units == Scope.UNITS_V; // AC coupling is permitted if the plot is displaying volts
    }
    
    boolean isAcCoupled() {
	return acCoupled;
    }
    
    int getPlotFlags() {
	return (acCoupled ? FLAG_AC : 0);
    }
}

class Scope {
    final int FLAG_YELM = 32;
    
    // bunch of other flags go here, see dump()
    final int FLAG_IVALUE = 2048; // Flag to indicate if IVALUE is included in dump
    final int FLAG_PLOTS = 4096; // new-style dump with multiple plots
    final int FLAG_PERPLOTFLAGS = 1<< 18; // new-new style dump with plot flags
    final int FLAG_PERPLOT_MAN_SCALE = 1<<19; // new-new style dump with manual included in each plot
    final int FLAG_MAN_SCALE = 16;
    // other flags go here too, see dump()
    
    static final int VAL_POWER = 7;
    static final int VAL_POWER_OLD = 1;
    static final int VAL_VOLTAGE = 0;
    static final int VAL_CURRENT = 3;
    static final int VAL_IB = 1;
    static final int VAL_IC = 2;
    static final int VAL_IE = 3;
    static final int VAL_VBE = 4;
    static final int VAL_VBC = 5;
    static final int VAL_VCE = 6;
    static final int VAL_R = 2;
    static final int UNITS_V = 0;
    static final int UNITS_A = 1;
    static final int UNITS_W = 2;
    static final int UNITS_OHMS = 3;
    static final int UNITS_COUNT = 4;
    static final double multa[] = {2.0, 2.5, 2.0};
    static final int V_POSITION_STEPS=200;
    static final double MIN_MAN_SCALE = 1e-9;
    int scopePointCount = 128;
    FFT fft;
    int position;
    // speed is sim timestep units per pixel
    int speed;
    int stackCount; // number of scopes in this column
    String text;
    Rectangle rect;
    private boolean manualScale;
    boolean showI, showV, showScale, showMax, showMin, showFreq;
    boolean plot2d;
    boolean plotXY;
    boolean maxScale;

    boolean logSpectrum;
    boolean showFFT, showNegative, showRMS, showAverage, showDutyCycle;
    Vector<ScopePlot> plots, visiblePlots;
    int pixels[];
    int draw_ox, draw_oy;
    float dpixels[];
    CirSim sim;
    Canvas imageCanvas;
    Context2d imageContext;
    int alphadiv =0;
    // scopeTimeStep to check if sim timestep has changed from previous value when redrawing
    double scopeTimeStep;
    double scale[]; // Max value to scale the display to show - indexed for each value of UNITS - e.g. UNITS_V, UNITS_A etc.
    boolean reduceRange[];
    double scaleX, scaleY;  // for X-Y plots
    int wheelDeltaY;
    int selectedPlot;
    ScopePropertiesDialog properties;
    String curColor, voltColor;
    double gridStepX, gridStepY;
    double maxValue, minValue;
    int manDivisions = 8; // Number of vertical divisions when in manual mode
    boolean drawGridLines;
    boolean somethingSelected;
    
    Scope(CirSim s) {
    	sim = s;
    	scale = new double[UNITS_COUNT];
    	reduceRange = new boolean[UNITS_COUNT];
    	
    	rect = new Rectangle(0, 0, 1, 1);
   	imageCanvas=Canvas.createIfSupported();
   	imageContext=imageCanvas.getContext2d();
	allocImage();
    	initialize();
    }
    
    void showCurrent(boolean b) {
	showI = b;
	if (b && !showingVoltageAndMaybeCurrent())
	    setValue(0);
	calcVisiblePlots();
    }
    void showVoltage(boolean b) {
	showV = b;
	if (b && !showingVoltageAndMaybeCurrent())
	    setValue(0);
	calcVisiblePlots();
    }

    void showMax    (boolean b) { showMax = b; }
    void showScale    (boolean b) { showScale = b; }
    void showMin    (boolean b) { showMin = b; }
    void showFreq   (boolean b) { showFreq = b; }
    void showFFT(boolean b) {
      showFFT = b;
      if (!showFFT)
    	  fft = null;
    }
    
    void setManualScale(boolean value, boolean roundup) { 
	if (value!=manualScale)
	    clear2dView();
	manualScale = value; 
	for (ScopePlot p : plots) {
	    if (!p.manScaleSet) {
		p.manScale=getManScaleFromMaxScale(p.units, roundup);
		p.manVPosition=0;
		p.manScaleSet = true;
	    }
	}
    }
    
    void resetGraph() { resetGraph(false); }
    
    void resetGraph(boolean full) {
    	scopePointCount = 1;
    	while (scopePointCount <= rect.width)
    		scopePointCount *= 2;
    	if (plots == null)
    	    plots = new Vector<ScopePlot>();
    	showNegative = false;
    	int i;
    	for (i = 0; i != plots.size(); i++)
    	    plots.get(i).reset(scopePointCount, speed, full);
	calcVisiblePlots();
    	scopeTimeStep = sim.maxTimeStep;
    	allocImage();
    }
    
    void setManualScaleValue(int plotId, double d) {
	if (plotId >= visiblePlots.size() )
	    return; // Shouldn't happen, but just in case...
	clear2dView();
	visiblePlots.get(plotId).manScale=d;
	visiblePlots.get(plotId).manScaleSet=true;
    }
    
    double getScaleValue() {
	if (visiblePlots.size() == 0)
	    return 0;
	ScopePlot p = visiblePlots.get(0);
	return scale[p.units];
    }
    
    String getScaleUnitsText() {
	if (visiblePlots.size() == 0)
	    return "V";
	ScopePlot p = visiblePlots.get(0);
	return getScaleUnitsText(p.units);
    }
    
    static String getScaleUnitsText(int units) {
	switch (units) {
	case UNITS_A: return "A";
	case UNITS_OHMS: return CirSim.ohmString;
	case UNITS_W: return "W";
	default: return "V";
	}
    }
    
    boolean active() { return plots.size() > 0 && plots.get(0).elm != null; }
    
    void initialize() {
    	resetGraph();
    	scale[UNITS_W] = scale[UNITS_OHMS] = scale[UNITS_V] = 5;
    	scale[UNITS_A] = .1;
    	scaleX = 5;
    	scaleY = .1;
    	speed = 64;
    	showMax = true;
    	showV = showI = false;
    	showScale = showFreq = manualScale = showMin = false;
    	showFFT = false;
    	plot2d = false;
    	if (!loadDefaults()) {
    	    // set showV and showI appropriately depending on what plots are present
    	    int i;
    	    for (i = 0; i != plots.size(); i++) {
    		ScopePlot plot = plots.get(i);
    		if (plot.units == UNITS_V)
    		    showV = true;
    		if (plot.units == UNITS_A)
    		    showI = true;
    	    }
    	}
    }
    
    void calcVisiblePlots() {
	visiblePlots = new Vector<ScopePlot>();
	int i;
	int vc = 0, ac = 0, oc = 0;
	if (!plot2d) {
        	for (i = 0; i != plots.size(); i++) {
        	    ScopePlot plot = plots.get(i);
        	    if (plot.units == UNITS_V) {
        		if (showV) {
        		    visiblePlots.add(plot);
        		    plot.assignColor(vc++);
        		}
        	    } else if (plot.units == UNITS_A) {
        		if (showI) {
        		    visiblePlots.add(plot);
        		    plot.assignColor(ac++);
        		}
        	    } else {
        		visiblePlots.add(plot);
        		plot.assignColor(oc++);
        	    }
        	}
	} else { // In 2D mode the visible plots are the first two plots
	    for(i =0; (i<2) && (i<plots.size()); i++) {
		visiblePlots.add(plots.get(i));
	    }
	}
    }
    
    void setRect(Rectangle r) {
	int w = this.rect.width;
	this.rect = r;
	if (this.rect.width != w)
	    resetGraph();
    }
    
    int getWidth() { return rect.width; }
    
    int rightEdge() { return rect.x+rect.width; }
	
    void setElm(CircuitElm ce) {
	plots = new Vector<ScopePlot>();
    	if (ce instanceof TransistorElm)
    	    setValue(VAL_VCE, ce);
    	else
    	    setValue(0, ce);
    	initialize();
    }
    
    void addElm(CircuitElm ce) {
    	if (ce instanceof TransistorElm)
    	    addValue(VAL_VCE, ce);
    	else
    	    addValue(0, ce);
    }

    void setValue(int val) {
	if (plots.size() > 2 || plots.size() == 0)
	    return;
	CircuitElm ce = plots.firstElement().elm;
	if (plots.size() == 2 && plots.get(1).elm != ce)
	    return;
	plot2d = plotXY = false;
	setValue(val, ce);
    }
    
    void addValue(int val, CircuitElm ce) {
	if (val == 0) {
	    plots.add(new ScopePlot(ce, UNITS_V, VAL_VOLTAGE, getManScaleFromMaxScale(UNITS_V, false)));
	    
	    // create plot for current if applicable
	    if (ce != null && !(ce instanceof OutputElm ||
		    ce instanceof LogicOutputElm ||
		    ce instanceof AudioOutputElm ||
		    ce instanceof ProbeElm))
		plots.add(new ScopePlot(ce, UNITS_A, VAL_CURRENT, getManScaleFromMaxScale(UNITS_A, false)));
	} else {
	    int u = ce.getScopeUnits(val);
	    plots.add(new ScopePlot(ce, u, val, getManScaleFromMaxScale(u, false)));
	    if (u == UNITS_V)
		showV = true;
	    if (u == UNITS_A)
		showI = true;
	}
	calcVisiblePlots();
	resetGraph();
    }
    
    void setValue(int val, CircuitElm ce) {
	plots = new Vector<ScopePlot>();
	addValue(val, ce);
//    	initialize();
    }

    void setValues(int val, int ival, CircuitElm ce, CircuitElm yelm) {
	if (ival > 0) {
	    plots = new Vector<ScopePlot>();
	    plots.add(new ScopePlot(ce, ce.getScopeUnits( val),  val, getManScaleFromMaxScale(ce.getScopeUnits( val), false)));
	    plots.add(new ScopePlot(ce, ce.getScopeUnits(ival), ival, getManScaleFromMaxScale(ce.getScopeUnits(ival), false)));
	    return;
	}
	if (yelm != null) {
	    plots = new Vector<ScopePlot>();
	    plots.add(new ScopePlot(ce,   ce.getScopeUnits( val), 0, getManScaleFromMaxScale(ce.getScopeUnits( val), false)));
	    plots.add(new ScopePlot(yelm, ce.getScopeUnits(ival), 0, getManScaleFromMaxScale(ce.getScopeUnits( val), false)));
	    return;
	}
	setValue(val);
    }
    
    void setText(String s) {
	text = s;
    }
    
    String getText() {
	return text;
    }
    
    boolean showingValue(int v) {
	int i;
	for (i = 0; i != plots.size(); i++) {
	    ScopePlot sp = plots.get(i);
	    if (sp.value != v)
		return false;
	}
	return true;
    }

    // returns true if we have a plot of voltage and nothing else (except current).
    // The default case is a plot of voltage and current, so we're basically checking if that case is true. 
    boolean showingVoltageAndMaybeCurrent() {
	int i;
	boolean gotv = false;
	for (i = 0; i != plots.size(); i++) {
	    ScopePlot sp = plots.get(i);
	    if (sp.value == VAL_VOLTAGE)
		gotv = true;
	    else if (sp.value != VAL_CURRENT)
		return false;
	}
	return gotv;
    }
    

    void combine(Scope s) {
	/*
	// if voltage and current are shown, remove current
	if (plots.size() == 2 && plots.get(0).elm == plots.get(1).elm)
	    plots.remove(1);
	if (s.plots.size() == 2 && s.plots.get(0).elm == s.plots.get(1).elm)
	    plots.add(s.plots.get(0));
	else
	*/
	plots = visiblePlots;
	plots.addAll(s.visiblePlots);
	s.plots.removeAllElements();
	calcVisiblePlots();
    }

    // separate this scope's plots into separate scopes and return them in arr[pos], arr[pos+1], etc.  return new length of array.
    int separate(Scope arr[], int pos) {
	int i;
	ScopePlot lastPlot = null;
	for (i = 0; i != visiblePlots.size(); i++) {
	    if (pos >= arr.length)
		return pos;
	    Scope s = new Scope(sim);
	    ScopePlot sp = visiblePlots.get(i);
	    if (lastPlot != null && lastPlot.elm == sp.elm && lastPlot.value == VAL_VOLTAGE && sp.value == VAL_CURRENT)
		continue;
	    s.setValue(sp.value, sp.elm);
	    s.position = pos;
	    arr[pos++] = s;
	    lastPlot = sp;
	    s.setFlags(getFlags());
	    s.setSpeed(speed);
	}
	return pos;
    }

    void removePlot(int plot) {
	if (plot < visiblePlots.size()) {
	    ScopePlot p = visiblePlots.get(plot);
	    plots.remove(p);
	    calcVisiblePlots();
	}
    }
    
    // called for each timestep
    void timeStep() {
	int i;
	for (i = 0; i != plots.size(); i++)
	    plots.get(i).timeStep();

	int x=0;
	int y=0;
	
	// For 2d plots we draw here rather than in the drawing routine
    	if (plot2d && imageContext!=null && plots.size()>=2) {
    	    double v = plots.get(0).lastValue;
    	    double yval = plots.get(1).lastValue;
    	    if (!isManualScale()) {
        	    boolean newscale = false;
        	    while (v > scaleX || v < -scaleX) {
        		scaleX *= 2;
        		newscale = true;
        	    }
        	    while (yval > scaleY || yval < -scaleY) {
        		scaleY *= 2;
        		newscale = true;
        	    }
        	    if (newscale)
        		clear2dView();
        	    double xa = v   /scaleX;
        	    double ya = yval/scaleY;
        	    x = (int) (rect.width *(1+xa)*.499);
        	    y = (int) (rect.height*(1-ya)*.499);
    	    } else {
    		double gridPx = calc2dGridPx(rect.width, rect.height);
    		x=(int)(rect.width*.499+(v/plots.get(0).manScale)*gridPx+gridPx*manDivisions*(double)(plots.get(0).manVPosition)/(double)(V_POSITION_STEPS));
    		y=(int)(rect.height*.499-(yval/plots.get(1).manScale)*gridPx-gridPx*manDivisions*(double)(plots.get(1).manVPosition)/(double)(V_POSITION_STEPS));

    	    }
    	    drawTo(x, y);
    	}
    }

    double calc2dGridPx(int width, int height) {
	int m = width<height?width:height;
	return ((double)(m)/2)/((double)(manDivisions)/2+0.05);
	
    }
    
    
    void drawTo(int x2, int y2) {
    	if (draw_ox == -1) {
    		draw_ox = x2;
    		draw_oy = y2;
    	}
		if (sim.printableCheckItem.getState()) {
			imageContext.setStrokeStyle("#000000");
		} else {
			imageContext.setStrokeStyle("#ffffff");
		}
		imageContext.beginPath();
		imageContext.moveTo(draw_ox, draw_oy);
		imageContext.lineTo(x2,y2);
		imageContext.stroke();
    	draw_ox = x2;
    	draw_oy = y2;
    }
	
    void clear2dView() {
    	if (imageContext!=null) {
    		if (sim.printableCheckItem.getState()) {
    			imageContext.setFillStyle("#ffffff");
    		} else {
    			imageContext.setFillStyle("#000000");
    		}
    		imageContext.fillRect(0, 0, rect.width-1, rect.height-1);
    	}
    	draw_ox = draw_oy = -1;
    }
	
    /*
    void adjustScale(double x) {
	scale[UNITS_V] *= x;
	scale[UNITS_A] *= x;
	scale[UNITS_OHMS] *= x;
	scale[UNITS_W] *= x;
	scaleX *= x;
	scaleY *= x;
    }
    */
    
    void setMaxScale(boolean s) {
	// This procedure is added to set maxscale to an explicit value instead of just having a toggle
	// We call the toggle procedure first because it has useful side-effects and then set the value explicitly.
	maxScale();
	maxScale = s;
    }
    
    void maxScale() {
	if (plot2d) {
	    double x = 1e-8;
	    scale[UNITS_V] *= x;
	    scale[UNITS_A] *= x;
	    scale[UNITS_OHMS] *= x;
	    scale[UNITS_W] *= x;
	    scaleX *= x; // For XY plots
	    scaleY *= x;
	    return;
	}
	// toggle max scale.  This isn't on by default because, for the examples, we sometimes want two plots
	// matched to the same scale so we can show one is larger.  Also, for some fast-moving scopes
	// (like for AM detector), the amplitude varies over time but you can't see that if the scale is
	// constantly adjusting.  It's also nice to set the default scale to hide noise and to avoid
	// having the scale moving around a lot when a circuit starts up.
	maxScale = !maxScale;
	showNegative = false;
    }

    void drawFFTVerticalGridLines(Graphics g) {
      // Draw x-grid lines and label the frequencies in the FFT that they point to.
      int prevEnd = 0;
      int divs = 20;
      double maxFrequency = 1 / (sim.maxTimeStep * speed * divs * 2);
      for (int i = 0; i < divs; i++) {
        int x = rect.width * i / divs;
        if (x < prevEnd) continue;
        String s = ((int) Math.round(i * maxFrequency)) + "Hz";
        int sWidth = (int) Math.ceil(g.context.measureText(s).getWidth());
        prevEnd = x + sWidth + 4;
        if (i > 0) {
          g.setColor("#880000");
          g.drawLine(x, 0, x, rect.height);
        }
        g.setColor("#FF0000");
        g.drawString(s, x + 2, rect.height);
      }
    }

    void drawFFT(Graphics g) {
    	if (fft == null || fft.getSize() != scopePointCount)
    		fft = new FFT(scopePointCount);
      double[] real = new double[scopePointCount];
      double[] imag = new double[scopePointCount];
      ScopePlot plot = (visiblePlots.size() == 0) ? plots.firstElement() : visiblePlots.firstElement();
      double maxV[] = plot.maxValues;
      double minV[] = plot.minValues;
      int ptr = plot.ptr;
      for (int i = 0; i < scopePointCount; i++) {
	  int ii = (ptr - i + scopePointCount) & (scopePointCount - 1);
	  // need to average max and min or else it could cause average of function to be > 0, which
	  // produces spike at 0 Hz that hides rest of spectrum
	  real[i] = .5*(maxV[ii]+minV[ii]);
	  imag[i] = 0;
      }
      fft.fft(real, imag);
      double maxM = 1e-8;
      for (int i = 0; i < scopePointCount / 2; i++) {
    	  double m = fft.magnitude(real[i], imag[i]);
    	  if (m > maxM)
    		  maxM = m;
      }
      int prevX = 0;
      g.setColor("#FF0000");
      if (!logSpectrum) {
	  int prevHeight = 0;
	  int y = (rect.height - 1) - 12;
	  for (int i = 0; i < scopePointCount / 2; i++) {
	      int x = 2 * i * rect.width / scopePointCount;
	      // rect.width may be greater than or less than scopePointCount/2,
	      // so x may be greater than or equal to prevX.
	      double magnitude = fft.magnitude(real[i], imag[i]);
	      int height = (int) ((magnitude * y) / maxM);
	      if (x != prevX)
		  g.drawLine(prevX, y - prevHeight, x, y - height);
	      prevHeight = height;
	      prevX = x;
	  }
      } else {
	  int y0 = 5;
	  int prevY = 0;
	  double ymult = rect.height/10;
	  double val0 = Math.log(scale[plot.units])*ymult;
	  for (int i = 0; i < scopePointCount / 2; i++) {
	      int x = 2 * i * rect.width / scopePointCount;
	      // rect.width may be greater than or less than scopePointCount/2,
	      // so x may be greater than or equal to prevX.
	      double val = Math.log(fft.magnitude(real[i], imag[i]));
	      int y = y0-(int) (val*ymult-val0);
	      if (x != prevX)
		  g.drawLine(prevX, prevY, x, y);
	      prevY = y;
	      prevX = x;
	  }
      }
    }
    
    void drawSettingsWheel(Graphics g) {
	final int outR = 8;
	final int inR= 5;
	final int inR45 = 4;
	final int outR45 = 6;
	if (showSettingsWheel()) {
	    g.context.save();
	    if (cursorInSettingsWheel())
		g.setColor(Color.cyan);
	    else
		g.setColor(Color.dark_gray);
	    g.context.translate(rect.x+18, rect.y+rect.height-18);
	    CircuitElm.drawThickCircle(g,0, 0, inR);
	    CircuitElm.drawThickLine(g, -outR, 0, -inR, 0);
	    CircuitElm.drawThickLine(g, outR, 0, inR, 0);
	    CircuitElm.drawThickLine(g, 0, -outR, 0, -inR);
	    CircuitElm.drawThickLine(g, 0, outR, 0, inR);
	    CircuitElm.drawThickLine(g, -outR45, -outR45,-inR45,-inR45);
	    CircuitElm.drawThickLine(g, outR45, -outR45,inR45,-inR45);
	    CircuitElm.drawThickLine(g, -outR45, outR45,-inR45,inR45);
	    CircuitElm.drawThickLine(g, outR45, outR45,inR45,inR45);
	g.context.restore();
	}
    }

    void draw2d(Graphics g) {
    	if (imageContext==null)
    		return;
    	g.context.save();
    	g.context.translate(rect.x, rect.y);
    	g.clipRect(0, 0, rect.width, rect.height);
    	
    	alphadiv++;
    	
    	if (alphadiv>2) {
    		alphadiv=0;
    		imageContext.setGlobalAlpha(0.01);
    		if (sim.printableCheckItem.getState()) {
    			imageContext.setFillStyle("#ffffff");
    		} else {
    			imageContext.setFillStyle("#000000");
    		}
    		imageContext.fillRect(0,0,rect.width,rect.height);
    		imageContext.setGlobalAlpha(1.0);
    	}
    	
    	g.context.drawImage(imageContext.getCanvas(), 0.0, 0.0);
//    	g.drawImage(image, r.x, r.y, null);
    	g.setColor(CircuitElm.whiteColor);
    	g.fillOval(draw_ox-2, draw_oy-2, 5, 5);
    	// Axis
    	g.setColor(CircuitElm.positiveColor);
    	g.drawLine(0, rect.height/2, rect.width-1, rect.height/2);
    	if (!plotXY)
    		g.setColor(Color.yellow);
    	g.drawLine(rect.width/2, 0, rect.width/2, rect.height-1);
    	if (isManualScale()) {
    	    double gridPx=calc2dGridPx(rect.width, rect.height);
    	    g.setColor("#404040");
    	    for(int i=-manDivisions; i<=manDivisions; i++) {
    		if (i!=0)
    		    g.drawLine((int)(gridPx*i)+rect.width/2, 0,(int)(gridPx*i)+rect.width/2, rect.height);
    		    g.drawLine(0, (int)(gridPx*i)+rect.height/2,rect.width, (int)(gridPx*i)+rect.height/2);
    	    }
    	}
	textY=10;
	g.setColor(CircuitElm.whiteColor);
    	if (text != null) {
    	    drawInfoText(g, text);
	}
    	if (showScale && plots.size()>=2 && isManualScale()) {
    	    ScopePlot px = plots.get(0);
    	    String sx=px.getUnitText(px.manScale);
    	    ScopePlot py = plots.get(1);
    	    String sy=py.getUnitText(py.manScale);
    	    drawInfoText(g,"X="+sx+"/div, Y="+sy+"/div");
    	}
    	g.context.restore();
    	drawSettingsWheel(g);
    	if ( !sim.dialogIsShowing() && rect.contains(sim.mouseCursorX, sim.mouseCursorY) && plots.size()>=2) {
    	    double gridPx=calc2dGridPx(rect.width, rect.height);
    	    String info[] = new String [2];
    	    ScopePlot px = plots.get(0);
    	    ScopePlot py = plots.get(1);
    	    double xValue;
    	    double yValue;
    	    if (isManualScale()) {
    		xValue = px.manScale*((double)(sim.mouseCursorX-rect.x-rect.width/2)/gridPx-manDivisions*px.manVPosition/(double)(V_POSITION_STEPS));
    		yValue = py.manScale*((double)(-sim.mouseCursorY+rect.y+rect.height/2)/gridPx-manDivisions*py.manVPosition/(double)(V_POSITION_STEPS));
    	    } else {
    		xValue = ((double)(sim.mouseCursorX-rect.x)/(0.499*(double)(rect.width))-1.0)*scaleX;
    		yValue = -((double)(sim.mouseCursorY-rect.y)/(0.499*(double)(rect.height))-1.0)*scaleY;
    	    }
 	    info[0]=px.getUnitText(xValue);
    	    info[1]=py.getUnitText(yValue);
    	    
    	    drawCrosshairsInfo(g, info, 2, true);
    	    
    	}
    }
	
  
    
    boolean showSettingsWheel() {
	return rect.height > 100 && rect.width > 100;
    }
    
    boolean cursorInSettingsWheel() {
	return showSettingsWheel() &&
		sim.mouseCursorX >= rect.x &&
		sim.mouseCursorX <= rect.x + 36 &&
		sim.mouseCursorY >= rect.y + rect.height - 36 && 
		sim.mouseCursorY <= rect.y + rect.height;
    }
    

    
    void draw(Graphics g) {
	if (plots.size() == 0)
	    return;
    	
    	// reset if timestep changed
    	if (scopeTimeStep != sim.maxTimeStep) {
    	    scopeTimeStep = sim.maxTimeStep;
    	    resetGraph();
    	}
    	
    	
    	if (plot2d) {
    		draw2d(g);
    		return;
    	}

    	drawSettingsWheel(g);
    	g.context.save();
    	g.setColor(Color.red);
    	g.context.translate(rect.x, rect.y);    	
    	g.clipRect(0, 0, rect.width, rect.height);

        if (showFFT) {
            drawFFTVerticalGridLines(g);
            drawFFT(g);
        }

    	int i;
    	for (i = 0; i != UNITS_COUNT; i++) {
    	    reduceRange[i] = false;
    	    if (maxScale && !manualScale)
    		scale[i] = 1e-4;
    	}
    	
    	int si;
    	somethingSelected = false;  // is one of our plots selected?
    	
    	for (si = 0; si != visiblePlots.size(); si++) {
    	    ScopePlot plot = visiblePlots.get(si);
    	    calcPlotScale(plot);
    	    if (sim.scopeSelected == -1 && plot.elm !=null && plot.elm.isMouseElm())
    		somethingSelected = true;
    	    reduceRange[plot.units] = true;
    	}
    	
    	checkForSelection();
    	if (selectedPlot >= 0)
    	    somethingSelected = true;

    	drawGridLines = true;
    	boolean allPlotsSameUnits = true;
    	for (i = 1; i < visiblePlots.size(); i++) {
    	    if (visiblePlots.get(i).units != visiblePlots.get(0).units)
    		allPlotsSameUnits = false; // Don't draw horizontal grid lines unless all plots are in same units
    	}
    	
    	if ((allPlotsSameUnits || showMax || showMin) && visiblePlots.size() > 0)
    	    calcMaxAndMin(visiblePlots.firstElement().units);
    	
    	// draw volt plots on top (last), then current plots underneath, then everything else
    	for (i = 0; i != visiblePlots.size(); i++) {
    	    if (visiblePlots.get(i).units > UNITS_A && i != selectedPlot)
    		drawPlot(g, visiblePlots.get(i), allPlotsSameUnits, false);
    	}
    	for (i = 0; i != visiblePlots.size(); i++) {
    	    if (visiblePlots.get(i).units == UNITS_A && i != selectedPlot)
    		drawPlot(g, visiblePlots.get(i), allPlotsSameUnits, false);
    	}
    	for (i = 0; i != visiblePlots.size(); i++) {
    	    if (visiblePlots.get(i).units == UNITS_V && i != selectedPlot)
    		drawPlot(g, visiblePlots.get(i), allPlotsSameUnits, false);
    	}
    	// draw selection on top.  only works if selection chosen from scope
    	if (selectedPlot >= 0 && selectedPlot < visiblePlots.size())
    	    drawPlot(g, visiblePlots.get(selectedPlot), allPlotsSameUnits, true);
    	
        if (visiblePlots.size() > 0)
            drawInfoTexts(g);
    	
    	g.restore();
    	
    	drawCrosshairs(g);
    	
    	if (plots.get(0).ptr > 5 && !manualScale) {
    	    for (i = 0; i != UNITS_COUNT; i++)
    		if (scale[i] > 1e-4 && reduceRange[i])
    		    scale[i] /= 2;
    	}
    	
    	if ( (properties != null ) && properties.isShowing() )
    	    properties.refreshDraw();

    }

    
    // calculate maximum and minimum values for all plots of given units
    void calcMaxAndMin(int units) {
	maxValue = -1e8;
	minValue = 1e8;
    	int i;
    	int si;
    	for (si = 0; si != visiblePlots.size(); si++) {
    	    ScopePlot plot = visiblePlots.get(si);
    	    if (plot.units != units)
    		continue;
    	    int ipa = plot.startIndex(rect.width);
    	    double maxV[] = plot.maxValues;
    	    double minV[] = plot.minValues;
    	    for (i = 0; i != rect.width; i++) {
    		int ip = (i+ipa) & (scopePointCount-1);
    		if (maxV[ip] > maxValue)
    		    maxValue = maxV[ip];
    		if (minV[ip] < minValue)
    		    minValue = minV[ip];
    	    }
        }
    }
    
    // adjust scale of a plot
    void calcPlotScale(ScopePlot plot) {
	if (manualScale)
	    return;
    	int i;
    	int ipa = plot.startIndex(rect.width);
    	double maxV[] = plot.maxValues;
    	double minV[] = plot.minValues;
    	double max = 0;
    	double gridMax = scale[plot.units];
    	for (i = 0; i != rect.width; i++) {
    	    int ip = (i+ipa) & (scopePointCount-1);
    	    if (maxV[ip] > max)
    		max = maxV[ip];
    	    if (minV[ip] < -max)
    		max = -minV[ip];
    	}
    	// scale fixed at maximum?
    	if (maxScale)
    	    gridMax = Math.max(max, gridMax);
    	else
    	    // adjust in powers of two
    	    while (max > gridMax)
    		gridMax *= 2;
    	scale[plot.units] = gridMax;
    }
    
    double calcGridStepX() {
	int multptr=0;
    	double gsx = 1e-15;

    	double ts = sim.maxTimeStep*speed;
    	while (gsx < ts*20) {
    	    gsx *=multa[(multptr++)%3];
    	}
    	return gsx;
    }


    double getGridMaxFromManScale(ScopePlot plot) {
	return ((double)(manDivisions)/2+0.05)*plot.manScale;
    }
    
    void drawPlot(Graphics g, ScopePlot plot, boolean allPlotsSameUnits, boolean selected) {
	if (plot.elm == null)
	    return;
    	int i;
    	String col;
//    	int col = (sim.printableCheckItem.getState()) ? 0xFFFFFFFF : 0;
//    	for (i = 0; i != pixels.length; i++)
//    		pixels[i] = col;
    	
    	double gridMid, positionOffset;
    	int multptr=0;
    	int x = 0;
    	final int maxy = (rect.height-1)/2;

    	String color = (somethingSelected) ? "#A0A0A0" : plot.color;
	if (sim.scopeSelected == -1  && plot.elm.isMouseElm())
    	    color = "#00FFFF";
	else if (selected)
	    color = plot.color;
    	int ipa = plot.startIndex(rect.width);
    	double maxV[] = plot.maxValues;
    	double minV[] = plot.minValues;
    	double gridMax;
    	
    	
    	// Calculate the max value (positive) to show and the value at the mid point of the grid
    	if (!isManualScale()) {
    	    	gridMax = scale[plot.units];
    	    	gridMid = 0;
    	    	positionOffset = 0;
        	if (allPlotsSameUnits) {
        	    // if we don't have overlapping scopes of different units, we can move zero around.
        	    // Put it at the bottom if the scope is never negative.
        	    double mx = gridMax;
        	    double mn = 0;
        	    if (maxScale) {
        		// scale is maxed out, so fix boundaries of scope at maximum and minimum. 
        		mx = maxValue;
        		mn = minValue;
        	    } else if (showNegative || minValue < (mx+mn)*.5 - (mx-mn)*.55) {
        		mn = -gridMax;
        		showNegative = true;
        	    }
        	    gridMid = (mx+mn)*.5;
        	    gridMax = (mx-mn)*.55;  // leave space at top and bottom
        	}
    	} else {
    	    gridMid =0;
    	    gridMax = getGridMaxFromManScale(plot);
    	    positionOffset = gridMax*2.0*(double)(plot.manVPosition)/(double)(V_POSITION_STEPS);
    	}
    	plot.plotOffset = -gridMid+positionOffset;
    	
    	plot.gridMult = maxy/gridMax;
    	
    	int minRangeLo = -10-(int) (gridMid*plot.gridMult);
    	int minRangeHi =  10-(int) (gridMid*plot.gridMult);
    	if (!isManualScale()) {
    	    gridStepY = 1e-8;    	
        	while (gridStepY < 20*gridMax/maxy) {
      			gridStepY *=multa[(multptr++)%3];
        	}
    	} else {
    	    gridStepY = plot.manScale;
    	}

    	String minorDiv = "#404040";
    	String majorDiv = "#A0A0A0";
    	if (sim.printableCheckItem.getState()) {
    	    minorDiv = "#D0D0D0";
    	    majorDiv = "#808080";
    	    curColor = "#A0A000";
    	}
    	
    	// Vertical (T) gridlines
    	double ts = sim.maxTimeStep*speed;
    	gridStepX = calcGridStepX();

    	if (drawGridLines) {
    	    // horizontal gridlines
    	    
    	    // don't show hgridlines if lines are too close together (except for center line)
    	    boolean showHGridLines = (gridStepY != 0) && (isManualScale() || allPlotsSameUnits); // Will only show center line if we have mixed units
    	    for (int ll = -100; ll <= 100; ll++) {
    		if (ll != 0 && !showHGridLines)
    		    continue;
    		int yl = maxy-(int) ((ll*gridStepY-gridMid)*plot.gridMult);
    		if (yl < 0 || yl >= rect.height-1)
    		    continue;
    		col = ll == 0 ? majorDiv : minorDiv;
    		g.setColor(col);
    		g.drawLine(0,yl,rect.width-1,yl);
    	    }
    	    
    	    // vertical gridlines
    	    double tstart = sim.t-sim.maxTimeStep*speed*rect.width;
    	    double tx = sim.t-(sim.t % gridStepX);

    	    for (int ll = 0; ; ll++) {
    		double tl = tx-gridStepX*ll;
    		int gx = (int) ((tl-tstart)/ts);
    		if (gx < 0)
    		    break;
    		if (gx >= rect.width)
    		    continue;
    		if (tl < 0)
    		    continue;
    		col = minorDiv;
    		// first = 0;
    		if (((tl+gridStepX/4) % (gridStepX*10)) < gridStepX) {
    		    col = majorDiv;
    		}
    		g.setColor(col);
    		g.drawLine(gx,0,gx,rect.height-1);
    	    }
    	}
    	
    	// only need gridlines drawn once
    	drawGridLines = false;

        g.setColor(color);
        
        if (isManualScale()) {
            int y0= maxy-(int) (plot.gridMult*plot.plotOffset);
            g.drawLine(0, y0, 8, y0);
            g.drawString("0", 0, y0-2);
        }
        
        int ox = -1, oy = -1;
        for (i = 0; i != rect.width; i++) {
            int ip = (i+ipa) & (scopePointCount-1);
            int minvy = (int) (plot.gridMult*(minV[ip]+plot.plotOffset));
            int maxvy = (int) (plot.gridMult*(maxV[ip]+plot.plotOffset));
            if (minvy <= maxy) {
        	if (minvy < minRangeLo || maxvy > minRangeHi) {
        	    // we got a value outside min range, so we don't need to rescale later
        	    reduceRange[plot.units] = false;
        	    minRangeLo = -1000;
        	    minRangeHi = 1000; // avoid triggering this test again
        	}
        	if (ox != -1) {
        	    if (minvy == oy && maxvy == oy)
        		continue;
        	    g.drawLine(ox, maxy-oy, x+i-1, maxy-oy);
        	    ox = oy = -1;
        	}
        	if (minvy == maxvy) {
        	    ox = x+i;
        	    oy = minvy;
        	    continue;
        	}
        	g.drawLine(x+i, maxy-minvy, x+i, maxy-maxvy-1);
            }
        } // for (i=0...)
        if (ox != -1)
            g.drawLine(ox, maxy-oy, x+i-1, maxy-oy); // Horizontal
        
    }

    // find selected plot
    void checkForSelection() {
	if (sim.dialogIsShowing())
	    return;
	if (!rect.contains(sim.mouseCursorX, sim.mouseCursorY)) {
	    selectedPlot = -1;
	    return;
	}
	int ipa = plots.get(0).startIndex(rect.width);
	int ip = (sim.mouseCursorX-rect.x+ipa) & (scopePointCount-1);
    	int maxy = (rect.height-1)/2;
    	int y = maxy;
    	int i;
    	int bestdist = 10000;
    	int best = -1;
    	for (i = 0; i != visiblePlots.size(); i++) {
    	    ScopePlot plot = visiblePlots.get(i);
    	    int maxvy = (int) (plot.gridMult*(plot.maxValues[ip]+plot.plotOffset));
    	    int dist = Math.abs(sim.mouseCursorY-(rect.y+y-maxvy));
    	    if (dist < bestdist) {
    		bestdist = dist;
    		best = i;
    	    }
    	}
    	selectedPlot = best;
    }
    
    void drawCrosshairs(Graphics g) {
	if (sim.dialogIsShowing())
	    return;
	if (!rect.contains(sim.mouseCursorX, sim.mouseCursorY))
	    return;
	if (selectedPlot < 0 && !showFFT)
	    return;
	String info[] = new String[4];
	int ipa = plots.get(0).startIndex(rect.width);
	int ip = (sim.mouseCursorX-rect.x+ipa) & (scopePointCount-1);
	int ct = 0;
    	int maxy = (rect.height-1)/2;
    	int y = maxy;
    	if (selectedPlot >= 0) {
    	    ScopePlot plot = visiblePlots.get(selectedPlot);
    	    info[ct++] = plot.getUnitText(plot.maxValues[ip]);
    	    int maxvy = (int) (plot.gridMult*(plot.maxValues[ip]+plot.plotOffset));
    	    g.setColor(plot.color);
    	    g.fillOval(sim.mouseCursorX-2, rect.y+y-maxvy-2, 5, 5);
    	}
        if (showFFT) {
    		double maxFrequency = 1 / (sim.maxTimeStep * speed * 2);
    		info[ct++] = CircuitElm.getUnitText(maxFrequency*(sim.mouseCursorX-rect.x)/rect.width, "Hz");
        }
	if (visiblePlots.size() > 0) {
	    double t = sim.t-sim.maxTimeStep*speed*(rect.x+rect.width-sim.mouseCursorX);
	    info[ct++] = CircuitElm.getTimeText(t);
	}
	drawCrosshairsInfo(g, info, ct, false);
    }
    
    void drawCrosshairsInfo(Graphics g, String[] info, int ct, Boolean drawY) {
	int szw = 0, szh = 15*ct;
	int i;
	for (i = 0; i != ct; i++) {
	    int w=(int)g.context.measureText(info[i]).getWidth();
	    if (w > szw)
		szw = w;
	}

	g.setColor(CircuitElm.whiteColor);
	g.drawLine(sim.mouseCursorX, rect.y, sim.mouseCursorX, rect.y+rect.height);
	if (drawY)
	    g.drawLine(rect.x, sim.mouseCursorY, rect.x+rect.width, sim.mouseCursorY);
	g.setColor(sim.printableCheckItem.getState() ? Color.white : Color.black);
	int bx = sim.mouseCursorX;
	if (bx < szw/2)
	    bx = szw/2;
	g.fillRect(bx-szw/2, rect.y-szh, szw, szh);
	g.setColor(CircuitElm.whiteColor);
	for (i = 0; i != ct; i++) {
	    int w=(int)g.context.measureText(info[i]).getWidth();
	    g.drawString(info[i], bx-w/2, rect.y-2-(ct-1-i)*15);
	}
	
    }

    boolean canShowRMS() {
	if (visiblePlots.size() == 0)
	    return false;
	ScopePlot plot = visiblePlots.firstElement();
	return (plot.units == Scope.UNITS_V || plot.units == Scope.UNITS_A);
    }
    
    // calc RMS and display it
    void drawRMS(Graphics g) {
	if (!canShowRMS()) {
	    // needed for backward compatibility
	    showRMS = false;
	    showAverage = true;
	    drawAverage(g);
	    return;
	}
	ScopePlot plot = visiblePlots.firstElement();
	int i;
	double avg = 0;
    	int ipa = plot.ptr+scopePointCount-rect.width;
    	double maxV[] = plot.maxValues;
    	double minV[] = plot.minValues;
    	double mid = (maxValue+minValue)/2;
	int state = -1;
	
	// skip zeroes
	for (i = 0; i != rect.width; i++) {
	    int ip = (i+ipa) & (scopePointCount-1);
	    if (maxV[ip] != 0) {
		if (maxV[ip] > mid)
		    state = 1;
		break;
	    }
	}
	int firstState = -state;
	int start = i;
	int end = 0;
	int waveCount = 0;
	double endAvg = 0;
	for (; i != rect.width; i++) {
	    int ip = (i+ipa) & (scopePointCount-1);
	    boolean sw = false;
	    
	    // switching polarity?
	    if (state == 1) {
		if (maxV[ip] < mid)
		    sw = true;
	    } else if (minV[ip] > mid)
		sw = true;
	    
	    if (sw) {
		state = -state;
		
		// completed a full cycle?
		if (firstState == state) {
		    if (waveCount == 0) {
			start = i;
			firstState = state;
			avg = 0;
		    }
		    waveCount++;
		    end = i;
		    endAvg = avg;
		}
	    }
	    if (waveCount > 0) {
		double m = (maxV[ip]+minV[ip])*.5;
		avg += m*m;
	    }
	}
	double rms;
	if (waveCount > 1) {
	    rms = Math.sqrt(endAvg/(end-start));
	    drawInfoText(g, plot.getUnitText(rms) + "rms");
	}
    }
    
    void drawScale(ScopePlot plot, Graphics g) {
    	    if (!isManualScale()) {
        	    if ( gridStepY!=0 && (!(showV && showI))) {
        		String vScaleText=" V=" + plot.getUnitText(gridStepY)+"/div";
        	    	drawInfoText(g, "H="+CircuitElm.getUnitText(gridStepX, "s")+"/div" + vScaleText);
        	    }
    	    }  else {
    		if (rect.y + rect.height <= textY+5)
    		    return;
    		double x = 0;
    		String hs = "H="+CircuitElm.getUnitText(gridStepX, "s")+"/div";
    		g.drawString(hs, 0, textY);
    		x+=g.measureWidth(hs);
		final double bulletWidth = 17;
    		for (int i=0; i<visiblePlots.size(); i++) {
    		    ScopePlot p=visiblePlots.get(i);
    		    String s=p.getUnitText(p.manScale);
    		    if (p!=null) {
    			String vScaleText="="+s+"/div";
    			double vScaleWidth=g.measureWidth(vScaleText);
    			if (x+bulletWidth+vScaleWidth > rect.width) {
    			    x=0;
    			    textY += 15;
    			    if (rect.y + rect.height <= textY+5)
    	    		    	return;
    			}
    			g.setColor(p.color);
    			g.fillOval((int)x+7, textY-9, 8, 8);
    			x+=bulletWidth;
    			g.setColor(CircuitElm.whiteColor);
    			g.drawString(vScaleText, (int)x, textY);
    			x+=vScaleWidth;
    		    }
    		}
    		textY += 15;
    	    }

	
    }
    
    void drawAverage(Graphics g) {
	ScopePlot plot = visiblePlots.firstElement();
	int i;
	double avg = 0;
    	int ipa = plot.ptr+scopePointCount-rect.width;
    	double maxV[] = plot.maxValues;
    	double minV[] = plot.minValues;
    	double mid = (maxValue+minValue)/2;
	int state = -1;
	
	// skip zeroes
	for (i = 0; i != rect.width; i++) {
	    int ip = (i+ipa) & (scopePointCount-1);
	    if (maxV[ip] != 0) {
		if (maxV[ip] > mid)
		    state = 1;
		break;
	    }
	}
	int firstState = -state;
	int start = i;
	int end = 0;
	int waveCount = 0;
	double endAvg = 0;
	for (; i != rect.width; i++) {
	    int ip = (i+ipa) & (scopePointCount-1);
	    boolean sw = false;
	    
	    // switching polarity?
	    if (state == 1) {
		if (maxV[ip] < mid)
		    sw = true;
	    } else if (minV[ip] > mid)
		sw = true;
	    
	    if (sw) {
		state = -state;
		
		// completed a full cycle?
		if (firstState == state) {
		    if (waveCount == 0) {
			start = i;
			firstState = state;
			avg = 0;
		    }
		    waveCount++;
		    end = i;
		    endAvg = avg;
		}
	    }
	    if (waveCount > 0) {
		double m = (maxV[ip]+minV[ip])*.5;
		avg += m;
	    }
	}
	if (waveCount > 1) {
	    avg = (endAvg/(end-start));
	    drawInfoText(g, plot.getUnitText(avg) + CirSim.LS(" average"));
	}
    }

    void drawDutyCycle(Graphics g) {
	ScopePlot plot = visiblePlots.firstElement();
	int i;
    	int ipa = plot.ptr+scopePointCount-rect.width;
    	double maxV[] = plot.maxValues;
    	double minV[] = plot.minValues;
    	double mid = (maxValue+minValue)/2;
	int state = -1;
	
	// skip zeroes
	for (i = 0; i != rect.width; i++) {
	    int ip = (i+ipa) & (scopePointCount-1);
	    if (maxV[ip] != 0) {
		if (maxV[ip] > mid)
		    state = 1;
		break;
	    }
	}
	int firstState = 1;
	int start = i;
	int end = 0;
	int waveCount = 0;
	int dutyLen = 0;
	int middle = 0;
	for (; i != rect.width; i++) {
	    int ip = (i+ipa) & (scopePointCount-1);
	    boolean sw = false;
	    
	    // switching polarity?
	    if (state == 1) {
		if (maxV[ip] < mid)
		    sw = true;
	    } else if (minV[ip] > mid)
		sw = true;
	    
	    if (sw) {
		state = -state;
		
		// completed a full cycle?
		if (firstState == state) {
		    if (waveCount == 0) {
			start = end = i;
		    } else {
			end = start;
			start = i;
			dutyLen = end-middle;
		    }
		    waveCount++;
		} else
		    middle = i;
	    }
	}
	if (waveCount > 1) {
	    int duty = 100*dutyLen/(end-start);
	    drawInfoText(g, CirSim.LS("Duty cycle ") + duty + "%");
	}
    }

    // calc frequency if possible and display it
    void drawFrequency(Graphics g) {
	// try to get frequency
	// get average
	double avg = 0;
	int i;
	ScopePlot plot = visiblePlots.firstElement();
    	int ipa = plot.ptr+scopePointCount-rect.width;
    	double minV[] = plot.minValues;
    	double maxV[] = plot.maxValues;
	for (i = 0; i != rect.width; i++) {
	    int ip = (i+ipa) & (scopePointCount-1);
	    avg += minV[ip]+maxV[ip];
	}
	avg /= i*2;
	int state = 0;
	double thresh = avg*.05;
	int oi = 0;
	double avperiod = 0;
	int periodct = -1;
	double avperiod2 = 0;
	// count period lengths
	for (i = 0; i != rect.width; i++) {
	    int ip = (i+ipa) & (scopePointCount-1);
	    double q = maxV[ip]-avg;
	    int os = state;
	    if (q < thresh)
		state = 1;
	    else if (q > -thresh)
		state = 2;
	    if (state == 2 && os == 1) {
		int pd = i-oi;
		oi = i;
		// short periods can't be counted properly
		if (pd < 12)
		    continue;
		// skip first period, it might be too short
		if (periodct >= 0) {
		    avperiod += pd;
		    avperiod2 += pd*pd;
		}
		periodct++;
	    }
	}
	avperiod /= periodct;
	avperiod2 /= periodct;
	double periodstd = Math.sqrt(avperiod2-avperiod*avperiod);
	double freq = 1/(avperiod*sim.maxTimeStep*speed);
	// don't show freq if standard deviation is too great
	if (periodct < 1 || periodstd > 2)
	    freq = 0;
	// System.out.println(freq + " " + periodstd + " " + periodct);
	if (freq != 0)
	    drawInfoText(g, CircuitElm.getUnitText(freq, "Hz"));
    }

    int textY;
    
    void drawInfoText(Graphics g, String text) {
	if (rect.y + rect.height <= textY+5)
	    return;
	g.drawString(text, 0, textY);
	textY += 15;
    }
    
    void drawInfoTexts(Graphics g) {
    	g.setColor(CircuitElm.whiteColor);
    	textY = 10;
    	/*
    	String x = position +" " + plots.size()+" ";
	int i;
    	for (i = 0; i < plots.size(); i++) {
    	    x+=",";
    	    ScopePlot p = plots.get(i);
    	  //  if (i > 0)
    		x += " " + sim.locateElm(p.elm) + " " + p.value;
    	    // dump scale if units are not V or A
    	    if (p.units > UNITS_A)
    		x += " " + scale[p.units];
    	}
    	drawInfoText(g, x);
    	*/
    	ScopePlot plot = visiblePlots.firstElement();
    	if (showScale) 
    	    drawScale(plot, g);
//    	if (showMax || showMin)
//    	    calcMaxAndMin(plot.units);
    	if (showMax)
    	    drawInfoText(g, "Max="+plot.getUnitText(maxValue));
    	if (showMin) {
    	    int ym=rect.height-5;
    	    g.drawString("Min="+plot.getUnitText(minValue), 0, ym);
    	}
    	if (showRMS)
    	    drawRMS(g);
    	if (showAverage)
    	    drawAverage(g);
    	if (showDutyCycle)
    	    drawDutyCycle(g);
    	String t = getScopeLabelOrText();
    	if (t != null &&  t!= "") 
    	    drawInfoText(g, t);
    	if (showFreq)
    	    drawFrequency(g);
    }

    String getScopeText() {
	// stacked scopes?  don't show text
	if (stackCount != 1)
	    return null;
	
	// multiple elms?  don't show text (unless one is selected)
	if (selectedPlot < 0 && getSingleElm() == null)
	    return null;
	
	ScopePlot plot = visiblePlots.firstElement();
	if (selectedPlot >= 0 && visiblePlots.size() > selectedPlot)
	    plot = visiblePlots.get(selectedPlot);
	if (plot.elm == null)
		return "";
	else
	    	return plot.elm.getScopeText(plot.value);
    }
    
    String getScopeLabelOrText() {
    	String t = text;
    	if (t == null) {
    	    t = getScopeText();
    	    if (t==null)
    		return "";
    	    return CirSim.LS(t);
    	}
    	else
    	    return t;
    }
    
    void setSpeed(int sp) {
	if (sp < 1)
	    sp = 1;
	if (sp > 1024)
	    sp = 1024;
	speed = sp;
	resetGraph();
    }
    
    void properties() {
	properties = new ScopePropertiesDialog(sim, this);
	CirSim.dialogShowing = properties;
    }
    
    void speedUp() {
	if (speed > 1) {
	    speed /= 2;
	    resetGraph();
	}
    }

    void slowDown() {
	if (speed < 1024)
	    speed *= 2;
    	resetGraph();
    }
    
    void setPlotPosition(int plot, int v) {
	visiblePlots.get(plot).manVPosition = v;
    }
	
    // get scope element, returning null if there's more than one
    CircuitElm getSingleElm() {
	CircuitElm elm = plots.get(0).elm;
	int i;
	for (i = 1; i < plots.size(); i++) {
	    if (plots.get(i).elm != elm)
		return null;
	}
	return elm;
    }
    
    boolean canMenu() {
    	return (plots.get(0).elm != null);
    }
    
    boolean canShowResistance() {
    	CircuitElm elm = getSingleElm();
    	return elm != null && elm.canShowValueInScope(VAL_R);
    }
    
    boolean isShowingVceAndIc() {
	return plot2d && plots.size() == 2 && plots.get(0).value == VAL_VCE && plots.get(1).value == VAL_IC;
    }

    int getFlags() {
    	int flags = (showI ? 1 : 0) | (showV ? 2 : 0) |
			(showMax ? 0 : 4) |   // showMax used to be always on
			(showFreq ? 8 : 0) |
			// In this version we always dump manual settings using the PERPLOT format
			(isManualScale() ? (FLAG_MAN_SCALE | FLAG_PERPLOT_MAN_SCALE): 0) |
			(plot2d ? 64 : 0) |
			(plotXY ? 128 : 0) | (showMin ? 256 : 0) | (showScale? 512:0) |
			(showFFT ? 1024 : 0) | (maxScale ? 8192 : 0) | (showRMS ? 16384 : 0) |
			(showDutyCycle ? 32768 : 0) | (logSpectrum ? 65536 : 0) |
			(showAverage ? (1<<17) : 0);
	flags |= FLAG_PLOTS; // 4096
	int allPlotFlags = 0;
	for (ScopePlot p : plots) {
	    allPlotFlags |= p.getPlotFlags();
	
	}
	// If none of our plots has a flag set we will use the old format with no plot flags, or
	// else we will set FLAG_PLOTFLAGS and include flags in all plots
	flags |= (allPlotFlags !=0) ? FLAG_PERPLOTFLAGS :0; // (1<<18)
	return flags;
    }
    

    
    String dump() {
	ScopePlot vPlot = plots.get(0);
	
	CircuitElm elm = vPlot.elm;
    	if (elm == null)
    		return null;
    	int flags = getFlags();
    	int eno = sim.locateElm(elm);
    	if (eno < 0)
    		return null;
    	String x = "o " + eno + " " +
    			vPlot.scopePlotSpeed + " " + vPlot.value + " " 
    			+ exportAsDecOrHex(flags, FLAG_PERPLOTFLAGS) + " " +
    			scale[UNITS_V] + " " + scale[UNITS_A] + " " + position + " " +
    			plots.size();
    	int i;
    	for (i = 0; i < plots.size(); i++) {
    	    ScopePlot p = plots.get(i);
    	    if ((flags & FLAG_PERPLOTFLAGS) !=0)
    		x += " " + Integer.toHexString(p.getPlotFlags()); // NB always export in Hex (no prefix)
    	    if (i > 0)
    		x += " " + sim.locateElm(p.elm) + " " + p.value;
    	    // dump scale if units are not V or A
    	    if (p.units > UNITS_A)
    		x += " " + scale[p.units];
    	    if (isManualScale()) {// In this version we always dump manual settings using the PERPLOT format
    	        x += " " + p.manScale + " "  
    		+ p.manVPosition;
    	    }
    	}
    	if (text != null)
    	    	x += " " + CustomLogicModel.escape(text);
    	return x;
    }
    
    void undump(StringTokenizer st) {
    	initialize();
    	int e = new Integer(st.nextToken()).intValue();
    	if (e == -1)
    		return;
    	CircuitElm ce = sim.getElm(e);
    	setElm(ce);
    	speed = new Integer(st.nextToken()).intValue();
    	int value = new Integer(st.nextToken()).intValue();
    	
    	// fix old value for VAL_POWER which doesn't work for transistors (because it's the same as VAL_IB) 
    	if (!(ce instanceof TransistorElm) && value == VAL_POWER_OLD)
    	    value = VAL_POWER;
    	
    	int flags = importDecOrHex(st.nextToken());
    	scale[UNITS_V] = new Double(st.nextToken()).doubleValue();
    	scale[UNITS_A] = new Double(st.nextToken()).doubleValue();
    	if (scale[UNITS_V] == 0)
    	    scale[UNITS_V] = .5;
    	if (scale[UNITS_A] == 0)
    	    scale[UNITS_A] = 1;
    	scaleX = scale[UNITS_V];
    	scaleY = scale[UNITS_A];
    	scale[UNITS_OHMS] = scale[UNITS_W] = scale[UNITS_V];
    	text = null;
    	boolean plot2dFlag = (flags & 64) != 0;
    	boolean hasPlotFlags = (flags & FLAG_PERPLOTFLAGS) != 0;
    	if ((flags & FLAG_PLOTS) != 0) {
    	    // new-style dump
    	    try {
    		position = Integer.parseInt(st.nextToken());
    		int sz = Integer.parseInt(st.nextToken());
    		int i;
    		int u = ce.getScopeUnits(value);
		if (u > UNITS_A)
		    scale[u] = Double.parseDouble(st.nextToken());
    		setValue(value);
    		// setValue(0) creates an extra plot for current, so remove that
    		while (plots.size() > 1)
    		    plots.removeElementAt(1);

		
    		int plotFlags = 0;
    		for (i = 0; i != sz; i++) {
    		    if (hasPlotFlags)
    			plotFlags=Integer.parseInt(st.nextToken(), 16); // Import in hex (no prefix)
    		    if (i!=0) {
        		    int ne = Integer.parseInt(st.nextToken());
        		    int val = Integer.parseInt(st.nextToken());
        		    CircuitElm elm = sim.getElm(ne);
        		    u = elm.getScopeUnits(val);
        		    if (u > UNITS_A)
        			scale[u] = Double.parseDouble(st.nextToken());
        		    plots.add(new ScopePlot(elm, u, val, getManScaleFromMaxScale(u, false)));
    		    }
    		    ScopePlot p = plots.get(i);
    		    p.acCoupled = (plotFlags & ScopePlot.FLAG_AC) != 0;
    		    if ( (flags & FLAG_PERPLOT_MAN_SCALE) != 0) {
    			p.manScaleSet = true;
    			p.manScale=Double.parseDouble(st.nextToken());
    			p.manVPosition=Integer.parseInt(st.nextToken());
    		    }
    		}
    		while (st.hasMoreTokens()) {
    		    if (text == null)
    			text = st.nextToken();
    		    else
    			text += " " + st.nextToken();
    		}
    	    } catch (Exception ee) {
    	    }
    	} else {
    	    // old-style dump
    	    CircuitElm yElm = null;
    	    int ivalue = 0;
    	    try {
    		position = new Integer(st.nextToken()).intValue();
    		int ye = -1;
    		if ((flags & FLAG_YELM) != 0) {
    		    ye = new Integer(st.nextToken()).intValue();
    		    if (ye != -1)
    			yElm = sim.getElm(ye);
    		    // sinediode.txt has yElm set to something even though there's no xy plot...?
    		    if (!plot2dFlag)
    			yElm = null;
    		}
    		if ((flags & FLAG_IVALUE) !=0) {
    		    ivalue = new Integer(st.nextToken()).intValue();
    		}
    		while (st.hasMoreTokens()) {
    		    if (text == null)
    			text = st.nextToken();
    		    else
    			text += " " + st.nextToken();
    		}
    	    } catch (Exception ee) {
    	    }
    	    setValues(value, ivalue, sim.getElm(e), yElm);
    	}
    	if (text != null)
    	    text = CustomLogicModel.unescape(text);
    	plot2d = plot2dFlag;
    	setFlags(flags);
    }
    
    void setFlags(int flags) {
    	showI = (flags & 1) != 0;
    	showV = (flags & 2) != 0;
    	showMax = (flags & 4) == 0;
    	showFreq = (flags & 8) != 0;
    	manualScale = (flags & FLAG_MAN_SCALE) != 0;
    	plotXY = (flags & 128) != 0;
    	showMin = (flags & 256) != 0;
    	showScale = (flags & 512) !=0;
    	showFFT((flags & 1024) != 0);
    	maxScale = (flags & 8192) != 0;
    	showRMS = (flags & 16384) != 0;
    	showDutyCycle = (flags & 32768) != 0;
    	logSpectrum = (flags & 65536) != 0;
    	showAverage = (flags & (1<<17)) != 0;
    }
    
    void saveAsDefault() {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return;
	ScopePlot vPlot = plots.get(0);
    	int flags = getFlags();
    	
    	// store current scope settings as default.  1 is a version code
    	stor.setItem("scopeDefaults", "1 " + flags + " " + vPlot.scopePlotSpeed);
    	CirSim.console("saved defaults " + flags);
    }

    boolean loadDefaults() {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return false;
        String str = stor.getItem("scopeDefaults");
        if (str == null)
            return false;
        String arr[] = str.split(" ");
        int flags = Integer.parseInt(arr[1]);
        setFlags(flags);
        speed = Integer.parseInt(arr[2]);
        return true;
    }
    
    void allocImage() {
	if (imageCanvas != null) {
	    imageCanvas.setWidth(rect.width + "PX");
	    imageCanvas.setHeight(rect.height + "PX");
	    imageCanvas.setCoordinateSpaceWidth(rect.width);
	    imageCanvas.setCoordinateSpaceHeight(rect.height);
	    clear2dView();
	}
    }
    
    void handleMenu(String mi, boolean state) {
	if (mi == "maxscale")
	    	maxScale();
    	if (mi == "showvoltage")
    		showVoltage(state);
    	if (mi == "showcurrent")
    		showCurrent(state);
    	if (mi=="showscale")
    		showScale(state);
    	if (mi == "showpeak")
    		showMax(state);
    	if (mi == "shownegpeak")
    		showMin(state);
    	if (mi == "showfreq")
    		showFreq(state);
    	if (mi == "showfft")
    		showFFT(state);
    	if (mi == "logspectrum")
    	    	logSpectrum = state;
    	if (mi == "showrms")
    	    	showRMS = state;
    	if (mi == "showaverage")
	    	showAverage = state;
    	if (mi == "showduty")
    	    	showDutyCycle = state;
    	if (mi == "showpower")
    		setValue(VAL_POWER);
    	if (mi == "showib")
    		setValue(VAL_IB);
    	if (mi == "showic")
    		setValue(VAL_IC);
    	if (mi == "showie")
    		setValue(VAL_IE);
    	if (mi == "showvbe")
    		setValue(VAL_VBE);
    	if (mi == "showvbc")
    		setValue(VAL_VBC);
    	if (mi == "showvce")
    		setValue(VAL_VCE);
    	if (mi == "showvcevsic") {
    		plot2d = true;
    		plotXY = false;
    		setValues(VAL_VCE, VAL_IC, getElm(), null);
    		resetGraph();
    	}

    	if (mi == "showvvsi") {
    		plot2d = state;
    		plotXY = false;
    		resetGraph();
    	}
    	if (mi == "manualscale")
		setManualScale(state, true);
    	if (mi == "plotxy") {
    		plotXY = plot2d = state;
    		if (plot2d)
    		    plots = visiblePlots;
    		if (plot2d && plots.size() == 1)
    		    selectY();
    		resetGraph();
    	}
    	if (mi == "showresistance")
    		setValue(VAL_R);
    }

//    void select() {
//    	sim.setMouseElm(elm);
//    	if (plotXY) {
//    		sim.plotXElm = elm;
//    		sim.plotYElm = yElm;
//    	}
//    }

    void selectY() {
	CircuitElm yElm = (plots.size() == 2) ? plots.get(1).elm : null;
    	int e = (yElm == null) ? -1 : sim.locateElm(yElm);
    	int firstE = e;
    	while (true) {
    	    for (e++; e < sim.elmList.size(); e++) {
    		CircuitElm ce = sim.getElm(e);
    		if ((ce instanceof OutputElm || ce instanceof ProbeElm) &&
    			ce != plots.get(0).elm) {
    		    yElm = ce;
    		    if (plots.size() == 1)
    			plots.add(new ScopePlot(yElm, UNITS_V));
    		    else {
    			plots.get(1).elm = yElm;
    			plots.get(1).units = UNITS_V;
    		    }
    		    return;
    		}
    	    }
    	    if (firstE == -1)
    		return;
    	    e = firstE = -1;
    	}
    	// not reached
    }
    
    void onMouseWheel(MouseWheelEvent e) {
        wheelDeltaY += e.getDeltaY();
        if (wheelDeltaY > 5) {
            slowDown();
            wheelDeltaY = 0;
        }
        if (wheelDeltaY < -5) {
            speedUp();
        	    wheelDeltaY = 0;
    	}
    }
    
    CircuitElm getElm() {
	if (selectedPlot >= 0 && visiblePlots.size() > selectedPlot)
	    return visiblePlots.get(selectedPlot).elm;
	return visiblePlots.size() > 0 ? visiblePlots.get(0).elm : plots.get(0).elm;
    }

    boolean viewingWire() {
	int i;
	for (i = 0; i != plots.size(); i++)
	    if (plots.get(i).elm instanceof WireElm)
		return true;
	return false;
    }
    
    CircuitElm getXElm() {
	return getElm();
    }
    CircuitElm getYElm() {
	if (plots.size() == 2)
	    return plots.get(1).elm;
	return null;
    }
    
    boolean needToRemove() {
	boolean ret = true;
	boolean removed = false;
	int i;
	for (i = 0; i != plots.size(); i++) {
	   ScopePlot plot = plots.get(i);
	   if (sim.locateElm(plot.elm) < 0) {
	       plots.remove(i--);
	       removed = true;
	   } else
	       ret = false;
	}
	if (removed)
	    calcVisiblePlots();
	return ret;
    }

    public boolean isManualScale() {
	return manualScale;
    }
    
    public double getManScaleFromMaxScale(int units, boolean roundUp) {
	// When the user manually switches to manual scale (and we don't already have a setting) then
	// call with "roundUp=true" to get a "sensible" suggestion for the scale. When importing from
	// a legacy file then call with "roundUp=false" to stay as close as possible to the old presentation
	double s =scale[units];
	if ( units > UNITS_A)
	    s = 0.5*s;
	if (roundUp)
	    return ScopePropertiesDialog.nextHighestScale((2*s)/(double)(manDivisions));
	else 
	    return (2*s)/(double)(manDivisions);
    }
    
    static String exportAsDecOrHex(int v, int thresh) {
	// If v>=thresh then export as hex value prefixed by "x", else export as decimal
	// Allows flags to be exported as dec if in an old value (for compatibility) or in hex if new value
	if (v>=thresh)
	    return "x"+Integer.toHexString(v);
	else
	    return Integer.toString(v);
    }
    
    static int importDecOrHex(String s) {
	if (s.charAt(0) == 'x')
	    return Integer.parseInt(s.substring(1), 16);
	else
	    return Integer.parseInt(s);
    }
}
