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
import com.google.gwt.storage.client.Storage;

import java.util.Vector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

// plot of single value on a scope
class ScopePlot {
    double minValues[], maxValues[];
    int scopePointCount;
    int ptr, ctr, value, speed, units;
    double lastValue;
    String color;
    CircuitElm elm;
    
    ScopePlot(CircuitElm e, int u) {
	elm = e;
	units = u;
    }
    
    ScopePlot(CircuitElm e, int u, int v) {
	elm = e;
	units = u;
	value = v;
    }

    int startIndex(int w) {
	return ptr + scopePointCount - w; 
    }
    
    void reset(int spc, int sp, boolean full) {
	int oldSpc = scopePointCount;
	scopePointCount = spc;
	if (speed != sp)
	    oldSpc = 0; // throw away old data
	speed = sp;
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
    	    ctr = 0;
    	ptr = 0;
    }

    void timeStep() {
	if (elm == null)
		return;
	double v = elm.getScopeValue(value);
	if (v < minValues[ptr])
		minValues[ptr] = v;
	if (v > maxValues[ptr])
		maxValues[ptr] = v;
	lastValue = v;
	ctr++;
	if (ctr >= speed) {
	    ptr = (ptr+1) & (scopePointCount-1);
	    minValues[ptr] = maxValues[ptr] = v;
	    ctr = 0;
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
	    color = "#00FF00";
	    break;
	case Scope.UNITS_A:
	    color = "#FFFF00";
	    break;
	default:
	    color = (CirSim.theSim.printableCheckItem.getState()) ? "#000000" : "#FFFFFF";
	    break;
	}
    }
}

class Scope {
    final int FLAG_YELM = 32;
    // bunch of other flags go here, see dump()
    final int FLAG_IVALUE = 2048; // Flag to indicate if IVALUE is included in dump
    final int FLAG_PLOTS = 4096; // new-style dump with multiple plots
    // other flags go here too, see dump()
    static final int VAL_POWER = 7;
    static final int VAL_POWER_OLD = 1;
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
    int scopePointCount = 128;
    FFT fft;
    int position;
    int speed;
    int stackCount; // number of scopes in this column
    String text;
    Rectangle rect;
    boolean showI, showV, showScale, showMax, showMin, showFreq, lockScale, plot2d, plotXY, maxScale;
    boolean showFFT, showNegative, showRMS, showDutyCycle;
    Vector<ScopePlot> plots, visiblePlots;
//    MemoryImageSource imageSource;
//    Image image;
    int pixels[];
    int draw_ox, draw_oy;
    float dpixels[];
    CirSim sim;
    Canvas imageCanvas;
    Context2d imageContext;
    int alphadiv =0;
    double scopeTimeStep;
    double scale[];
    boolean expandRange[];
    double scaleX, scaleY;
    int wheelDeltaY;
    int selectedPlot;
    ScopePropertiesDialog properties;
    
    Scope(CirSim s) {
    	sim = s;
    	scale = new double[UNITS_COUNT];
    	expandRange = new boolean[UNITS_COUNT];
    	
    	rect = new Rectangle(0, 0, 1, 1);
   	imageCanvas=Canvas.createIfSupported();
   	imageContext=imageCanvas.getContext2d();
	allocImage();
    	reset();
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
    	scopeTimeStep = sim.timeStep;
    	allocImage();
    }
    
    
    boolean active() { return plots.size() > 0 && plots.get(0).elm != null; }
    
    void reset() {
    	resetGraph();
    	scale[UNITS_W] = scale[UNITS_OHMS] = scale[UNITS_V] = 5;
    	scale[UNITS_A] = .1;
    	scaleX = 5;
    	scaleY = .1;
    	speed = 64;
    	showMax = true;
    	showV = showI = false;
    	showScale = showFreq = lockScale = showMin = false;
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
    	reset();
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
    
    void setValue(int val, CircuitElm ce) {
	plots = new Vector<ScopePlot>();
	if (val == 0) {
	    plots.add(new ScopePlot(ce, UNITS_V, 0));
	    
	    // create plot for current if applicable
	    if (ce != null && !(ce instanceof OutputElm ||
		    ce instanceof LogicOutputElm ||
		    ce instanceof AudioOutputElm ||
		    ce instanceof ProbeElm))
		plots.add(new ScopePlot(ce, UNITS_A, VAL_CURRENT));
	} else {
	    int u = ce.getScopeUnits(val);
	    plots.add(new ScopePlot(ce, u, val));
	    if (u == UNITS_V)
		showV = true;
	    if (u == UNITS_A)
		showI = true;
	}
	calcVisiblePlots();
	resetGraph();
//    	reset();
    }

    void setValues(int val, int ival, CircuitElm ce, CircuitElm yelm) {
	if (ival > 0) {
	    plots = new Vector<ScopePlot>();
	    plots.add(new ScopePlot(ce, ce.getScopeUnits( val),  val));
	    plots.add(new ScopePlot(ce, ce.getScopeUnits(ival), ival));
	    return;
	}
	if (yelm != null) {
	    plots = new Vector<ScopePlot>();
	    plots.add(new ScopePlot(ce,   ce.getScopeUnits( val), 0));
	    plots.add(new ScopePlot(yelm, ce.getScopeUnits(ival), 0));
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
	    if (sp.value == 0)
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
    }
    
    void removePlot(int plot) {
	if (plot < visiblePlots.size()) {
	    ScopePlot p = visiblePlots.get(plot);
	    plots.remove(p);
	    calcVisiblePlots();
	}
    }
    
    void timeStep() {
	int i;
	for (i = 0; i != plots.size(); i++)
	    plots.get(i).timeStep();

    	if (plot2d && imageContext!=null) {
    	    boolean newscale = false;
    	    if (plots.size() < 2)
    		return;
    	    double v = plots.get(0).lastValue;
    	    while (v > scaleX || v < -scaleX) {
    		scaleX *= 2;
    		newscale = true;
    	    }
    	    double yval = plots.get(1).lastValue;
    	    while (yval > scaleY || yval < -scaleY) {
    		scaleY *= 2;
    		newscale = true;
    	    }
    	    if (newscale)
    		clear2dView();
    	    double xa = v   /scaleX;
    	    double ya = yval/scaleY;
    	    int x = (int) (rect.width *(1+xa)*.499);
    	    int y = (int) (rect.height*(1-ya)*.499);
    	    drawTo(x, y);
    	}
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
    void maxScale() {
	if (plot2d) {
	    double x = 1e-8;
	    scale[UNITS_V] *= x;
	    scale[UNITS_A] *= x;
	    scale[UNITS_OHMS] *= x;
	    scale[UNITS_W] *= x;
	    scaleX *= x;
	    scaleY *= x;
	    return;
	}
	// toggle max scale.  Why isn't this on by default?  For the examples, we sometimes want two plots
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
      double maxFrequency = 1 / (sim.timeStep * speed * divs * 2);
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
      int y = (rect.height - 1) - 12;
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
      int prevHeight = 0;
      int prevX = 0;
      g.setColor("#FF0000");
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
    	int yt = 10;
    	int x = 0;
    	if (text != null &&  rect.height > yt+5) {
    		g.drawString(text, x, yt);
    		yt += 15;
    	}
    	g.setColor(Color.green);
    	g.drawLine(0, rect.height/2, rect.width-1, rect.height/2);
    	if (!plotXY)
    		g.setColor(Color.yellow);
    	g.drawLine(rect.width/2, 0, rect.width/2, rect.height-1);
    	g.context.restore();
    	drawSettingsWheel(g);
    }
	
    boolean drawGridLines;
    boolean somethingSelected;
    
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
    	if (scopeTimeStep != sim.timeStep) {
    	    scopeTimeStep = sim.timeStep;
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

        if (showFFT) {
            drawFFTVerticalGridLines(g);
            drawFFT(g);
        }

    	int i;
    	for (i = 0; i != UNITS_COUNT; i++) {
    	    expandRange[i] = false;
    	    if (maxScale)
    		scale[i] = 1e-4;
    	}
    	
    	int si;
    	somethingSelected = false;  // is one of our plots selected?
    	
    	for (si = 0; si != visiblePlots.size(); si++) {
    	    ScopePlot plot = visiblePlots.get(si);
    	    calcPlotScale(plot);
    	    if (sim.scopeSelected == -1 && plot.elm !=null && plot.elm.isMouseElm())
    		somethingSelected = true;
    	    expandRange[plot.units] = true;
    	}
    	
    	checkForSelection();
    	if (selectedPlot >= 0)
    	    somethingSelected = true;

    	drawGridLines = true;
    	boolean hGridLines = true;
    	for (i = 1; i < visiblePlots.size(); i++) {
    	    if (visiblePlots.get(i).units != visiblePlots.get(0).units)
    		hGridLines = false;
    	}
    	
    	if ((hGridLines || showMax || showMin) && visiblePlots.size() > 0)
    	    calcMaxAndMin(visiblePlots.firstElement().units);
    	
    	// draw volts on top (last), then current underneath, then everything else
    	for (i = 0; i != visiblePlots.size(); i++) {
    	    if (visiblePlots.get(i).units > UNITS_A && i != selectedPlot)
    		drawPlot(g, visiblePlots.get(i), hGridLines, false);
    	}
    	for (i = 0; i != visiblePlots.size(); i++) {
    	    if (visiblePlots.get(i).units == UNITS_A && i != selectedPlot)
    		drawPlot(g, visiblePlots.get(i), hGridLines, false);
    	}
    	for (i = 0; i != visiblePlots.size(); i++) {
    	    if (visiblePlots.get(i).units == UNITS_V && i != selectedPlot)
    		drawPlot(g, visiblePlots.get(i), hGridLines, false);
    	}
    	// draw selection on top.  only works if selection chosen from scope
    	if (selectedPlot >= 0)
    	    drawPlot(g, visiblePlots.get(selectedPlot), hGridLines, true);
    	
        if (visiblePlots.size() > 0)
            drawInfoTexts(g);
    	
    	g.context.restore();
    	
    	drawCrosshairs(g);
    	
    	// there is no UI for setting lockScale but it's used in some of the example circuits (like crossover)
    	if (plots.get(0).ptr > 5 && !lockScale) {
    	    for (i = 0; i != UNITS_COUNT; i++)
    		if (scale[i] > 1e-4 && expandRange[i])
    		    scale[i] /= 2;
    	}

    }
    
    String curColor, voltColor;
    double gridStepX, gridStepY;
    double maxValue, minValue;
    
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

    	double ts = sim.timeStep*speed;
    	while (gsx < ts*20) {
    	    gsx *=multa[(multptr++)%3];
    	}
    	return gsx;
    }

    double mainGridMult, mainGridMid;
    
    void drawPlot(Graphics g, ScopePlot plot, boolean drawHGridLines, boolean selected) {
	if (plot.elm == null)
	    return;
    	int i;
    	String col;
//    	int col = (sim.printableCheckItem.getState()) ? 0xFFFFFFFF : 0;
//    	for (i = 0; i != pixels.length; i++)
//    		pixels[i] = col;
    	

    	int multptr=0;
    	int x = 0;
    	int maxy = (rect.height-1)/2;
    	int y = maxy;

    	String color = (somethingSelected) ? "#A0A0A0" : plot.color;
	if (sim.scopeSelected == -1  && plot.elm.isMouseElm())
    	    color = "#00FFFF";
	else if (selected)
	    color = plot.color;
    	int ipa = plot.startIndex(rect.width);
    	double maxV[] = plot.maxValues;
    	double minV[] = plot.minValues;
    	double gridMax = scale[plot.units];
    	double gridMid = 0;
    	if (drawHGridLines) {
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
    	double gridMult = maxy/gridMax;
    	if (selected) {
    	    mainGridMult = gridMult;
    	    mainGridMid  = gridMid;
    	}
    	int minRangeLo = -10-(int) (gridMid*gridMult);
    	int minRangeHi =  10-(int) (gridMid*gridMult);

    	gridStepY = 1e-8;    	
    	while (gridStepY < 20*gridMax/maxy) {
  		gridStepY *=multa[(multptr++)%3];
    	}

    	// Horizontal gridlines
    	int ll;
    	String minorDiv = "#303030";
    	String majorDiv = "#A0A0A0";
    	if (sim.printableCheckItem.getState()) {
    	    minorDiv = "#D0D0D0";
    	    majorDiv = "#808080";
    	    curColor = "#A0A000";
    	}
    	
    	// Vertical (T) gridlines
    	double ts = sim.timeStep*speed;
    	gridStepX = calcGridStepX();

    	if (drawGridLines) {
    	    // horizontal gridlines
    	    
    	    // don't show gridlines if lines are too close together (except for center line)
    	    boolean showGridLines = (gridStepY != 0) && drawHGridLines;
    	    for (ll = -100; ll <= 100; ll++) {
    		if (ll != 0 && !showGridLines)
    		    continue;
    		int yl = maxy-(int) ((ll*gridStepY-gridMid)*gridMult);
    		if (yl < 0 || yl >= rect.height-1)
    		    continue;
    		col = ll == 0 ? majorDiv : minorDiv;
    		g.setColor(col);
    		g.drawLine(0,yl,rect.width-1,yl);
    	    }
    	    
    	    // vertical gridlines
    	    double tstart = sim.t-sim.timeStep*speed*rect.width;
    	    double tx = sim.t-(sim.t % gridStepX);

    	    for (ll = 0; ; ll++) {
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
        
        int ox = -1, oy = -1;
        for (i = 0; i != rect.width; i++) {
            int ip = (i+ipa) & (scopePointCount-1);
            int minvy = (int) (gridMult*(minV[ip]-gridMid));
            int maxvy = (int) (gridMult*(maxV[ip]-gridMid));
            if (minvy <= maxy) {
        	if (minvy < minRangeLo || maxvy > minRangeHi) {
        	    // we got a value outside min range, so we don't need to rescale later
        	    expandRange[plot.units] = false;
        	    minRangeLo = -1000;
        	    minRangeHi = 1000; // avoid triggering this test again
        	}
        	if (ox != -1) {
        	    if (minvy == oy && maxvy == oy)
        		continue;
        	    g.drawLine(ox, y-oy, x+i-1, y-oy);
        	    ox = oy = -1;
        	}
        	if (minvy == maxvy) {
        	    ox = x+i;
        	    oy = minvy;
        	    continue;
        	}
        	g.drawLine(x+i, y-minvy, x+i, y-maxvy-1);
            }
        } // for (i=0...)
        if (ox != -1)
            g.drawLine(ox, y-oy, x+i-1, y-oy); // Horizontal
        
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
    	    int maxvy = (int) ((maxy/scale[plot.units])*plot.maxValues[ip]);
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
    	    int maxvy = (int) (mainGridMult*(plot.maxValues[ip]-mainGridMid));
    	    g.setColor(plot.color);
    	    g.fillOval(sim.mouseCursorX-2, rect.y+y-maxvy-2, 5, 5);
    	}
        if (showFFT) {
    		double maxFrequency = 1 / (sim.timeStep * speed * 2);
    		info[ct++] = CircuitElm.getUnitText(maxFrequency*(sim.mouseCursorX-rect.x)/rect.width, "Hz");
        }
	if (visiblePlots.size() > 0) {
	    double t = sim.t-sim.timeStep*speed*(rect.x+rect.width-sim.mouseCursorX);
	    info[ct++] = CircuitElm.getTimeText(t);
	}
	int szw = 0, szh = 15*ct;
	int i;
	for (i = 0; i != ct; i++) {
	    int w=(int)g.context.measureText(info[i]).getWidth();
	    if (w > szw)
		szw = w;
	}

	g.setColor(CircuitElm.whiteColor);
	g.drawLine(sim.mouseCursorX, rect.y, sim.mouseCursorX, rect.y+rect.height);
	//	    g.drawLine(rect.x, sim.mouseCursorY, rect.x+rect.width, sim.mouseCursorY);
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
    
    void drawRMS(Graphics g) {
	if (!canShowRMS()) {
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
	    drawInfoText(g, plot.getUnitText(avg) + sim.LS(" average"));
	}
    }

    void drawDutyCycle(Graphics g) {
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
	    drawInfoText(g, sim.LS("Duty cycle ") + duty + "%");
	}
    }

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
	double freq = 1/(avperiod*sim.timeStep*speed);
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
    	ScopePlot plot = visiblePlots.firstElement();
    	if (showScale) {
    	    String vScaleText="";
    	    if ( gridStepY!=0 && (!(showV && showI)))
    		vScaleText=" V=" + plot.getUnitText(gridStepY)+"/div";
    	    drawInfoText(g, "H="+CircuitElm.getUnitText(gridStepX, "s")+"/div" +
    		    vScaleText);
    	}
//    	if (showMax || showMin)
//    	    calcMaxAndMin(plot.units);
    	if (showMax)
    	    drawInfoText(g, plot.getUnitText(maxValue));
    	if (showMin) {
    	    int ym=rect.height-5;
    	    g.drawString(plot.getUnitText(minValue), 0, ym);
    	}
    	if (showRMS)
    	    drawRMS(g);
    	if (showDutyCycle)
    	    drawDutyCycle(g);
    	String t = text;
    	if (t == null)
    	    t = getScopeText();
    	t = CirSim.LS(t);
    	if (t != null)
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
			(lockScale ? 16 : 0) | (plot2d ? 64 : 0) |
			(plotXY ? 128 : 0) | (showMin ? 256 : 0) | (showScale? 512:0) |
			(showFFT ? 1024 : 0) | (maxScale ? 8192 : 0) | (showRMS ? 16384 : 0) |
			(showDutyCycle ? 32768 : 0);
	flags |= FLAG_PLOTS;
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
    			vPlot.speed + " " + vPlot.value + " " + flags + " " +
    			scale[UNITS_V] + " " + scale[UNITS_A] + " " + position + " " +
    			plots.size();
    	int i;
    	for (i = 0; i < plots.size(); i++) {
    	    ScopePlot p = plots.get(i);
    	    if (i > 0)
    		x += " " + sim.locateElm(p.elm) + " " + p.value;
    	    // dump scale if units are not V or A
    	    if (p.units > UNITS_A)
    		x += " " + scale[p.units];
    	}
    	if (text != null)
    	    	x += " " + CustomLogicModel.escape(text);
    	return x;
    }
    
    void undump(StringTokenizer st) {
    	reset();
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
    	
    	int flags = new Integer(st.nextToken()).intValue();
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
    	if ((flags & FLAG_PLOTS) != 0) {
    	    // new-style dump
    	    try {
    		position = Integer.parseInt(st.nextToken());
    		int sz = Integer.parseInt(st.nextToken());
    		int i;
    		setValue(value);
    		// setValue(0) creates an extra plot for current, so remove that
    		while (plots.size() > 1)
    		    plots.removeElementAt(1);
    		
    		int u = plots.get(0).units;
		if (u > UNITS_A)
		    scale[u] = Double.parseDouble(st.nextToken());
    		
    		for (i = 1; i != sz; i++) {
    		    int ne = Integer.parseInt(st.nextToken());
    		    int val = Integer.parseInt(st.nextToken());
    		    CircuitElm elm = sim.getElm(ne);
    		    u = elm.getScopeUnits(val);
    		    if (u > UNITS_A)
    			scale[u] = Double.parseDouble(st.nextToken());
    		    plots.add(new ScopePlot(elm, u, val));
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
    	lockScale = (flags & 16) != 0;
    	plotXY = (flags & 128) != 0;
    	showMin = (flags & 256) != 0;
    	showScale = (flags & 512) !=0;
    	showFFT((flags & 1024) != 0);
    	maxScale = (flags & 8192) != 0;
    	showRMS = (flags & 16384) != 0;
    	showDutyCycle = (flags & 32768) != 0;
    }
    
    void saveAsDefault() {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return;
	ScopePlot vPlot = plots.get(0);
    	int flags = getFlags();
    	
    	// store current scope settings as default.  1 is a version code
    	stor.setItem("scopeDefaults", "1 " + flags + " " + vPlot.speed);
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
    	if (mi == "showrms")
    	    	showRMS = state;
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
}
