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

//import java.awt.*;
//import java.awt.image.*;
//import java.awt.event.*;
//import java.util.StringTokenizer;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Method;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.user.client.ui.MenuBar;

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
    
    void reset(int spc, int sp) {
	scopePointCount = spc;
	speed = sp;
    	minValues = new double[scopePointCount];
    	maxValues = new double[scopePointCount];
    	ptr = ctr = 0;
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
    static final int VAL_POWER = 1;
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
    int scopePointCount = 128;
    FFT fft;
    int position;
    int speed;
    String text;
    Rectangle rect;
    boolean showI, showV, showScale, showMax, showMin, showFreq, lockScale, plot2d, plotXY;
    boolean showFFT;
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
	if (b && !showingValue(0))
	    setValue(0);
	calcVisiblePlots();
    }
    void showVoltage(boolean b) {
	showV = b;
	if (b && !showingValue(0))
	    setValue(0);
	calcVisiblePlots();
    }

    void showMax    (boolean b) { showMax = b; }
    void showScale    (boolean b) { showScale = b; }
    void showMin    (boolean b) { showMin = b; }
    void showFreq   (boolean b) { showFreq = b; }
    void setLockScale  (boolean b) { lockScale = b; }
    void showFFT(boolean b) {
      showFFT = b;
      if (!showFFT)
    	  fft = null;
    }
    
    void resetGraph() {
    	scopePointCount = 1;
    	while (scopePointCount <= rect.width)
    		scopePointCount *= 2;
    	if (plots == null)
    	    plots = new Vector<ScopePlot>();
    	int i;
    	for (i = 0; i != plots.size(); i++)
    	    plots.get(i).reset(scopePointCount, speed);
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
    
    boolean showingValue(int v) {
	int i;
	for (i = 0; i != plots.size(); i++) {
	    ScopePlot sp = plots.get(i);
	    if (sp.value != v)
		return false;
	}
	return true;
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
	
    void adjustScale(double x) {
	scale[UNITS_V] *= x;
	scale[UNITS_A] *= x;
	scale[UNITS_OHMS] *= x;
	scale[UNITS_W] *= x;
	scaleX *= x;
	scaleY *= x;
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
      int y = (rect.height - 1) / 2;
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
      double magnitude = fft.magnitude(real[0], imag[0]);
      int prevHeight = (int) ((magnitude * y) / maxM);
      int prevX = 0;
      g.setColor("#FF0000");
      for (int i = 1; i < scopePointCount / 2; i++) {
        int x = 2 * i * rect.width / scopePointCount;
        // rect.width may be greater than or less than scopePointCount/2,
        // so x may be greater than or equal to prevX.
        if (x == prevX) continue;
        magnitude = fft.magnitude(real[i], imag[i]);
        int height = (int) ((magnitude * y) / maxM);
        g.drawLine(prevX, y - prevHeight, x, y - height);
        prevHeight = height;
        prevX = x;
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
    }
	
    boolean drawGridLines;
    boolean somethingSelected;
    
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
//    	if (pixels == null)
//    		return;
    	g.context.save();
    	g.setColor(Color.red);
    	g.context.translate(rect.x, rect.y);

        if (showFFT) {
            drawFFTVerticalGridLines(g);
            drawFFT(g);
        }

    	int i;
    	for (i = 0; i != UNITS_COUNT; i++)
    	    expandRange[i] = false;
    	
    	int si;
    	somethingSelected = false;  // is one of our plots selected?
    	
    	for (si = 0; si != visiblePlots.size(); si++) {
    	    ScopePlot plot = visiblePlots.get(si);
    	    calcPlotScale(plot);
    	    if (sim.scopeSelected == -1 && plot.elm.isMouseElm())
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
    	
    	if (plots.get(0).ptr > 5 && !lockScale) {
    	    for (i = 0; i != UNITS_COUNT; i++)
    		if (scale[i] > 1e-4 && expandRange[i])
    		    scale[i] /= 2;
	}

    }
    
    String curColor, voltColor;
    double gridStepX, gridStepY;
    double maxValue, minValue;
    
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
    
    void calcPlotScale(ScopePlot plot) {
    	int i;
    	int ipa = plot.startIndex(rect.width);
    	double maxV[] = plot.maxValues;
    	double minV[] = plot.minValues;
    	double gridMax = scale[plot.units];
    	for (i = 0; i != rect.width; i++) {
    	    int ip = (i+ipa) & (scopePointCount-1);
    	    while (maxV[ip] > gridMax)
    		gridMax *= 2;
    	    while (minV[ip] < -gridMax)
    		gridMax *= 2;
    	}
    	scale[plot.units] = gridMax;
    }

    void drawPlot(Graphics g, ScopePlot plot, boolean drawHGridLines, boolean selected) {
    	int i;
    	String col;
//    	int col = (sim.printableCheckItem.getState()) ? 0xFFFFFFFF : 0;
//    	for (i = 0; i != pixels.length; i++)
//    		pixels[i] = col;
    	
    	double multa[] = {2.0, 2.5, 2.0};
    	int multptr=0;
    	int x = 0;
    	int maxy = (rect.height-1)/2;
    	int y = maxy;
    	int minRange = 4;

    	String color = (somethingSelected) ? "#A0A0A0" : plot.color;
	if (sim.scopeSelected == -1 && plot.elm.isMouseElm())
    	    color = "#00FFFF";
	else if (selected)
	    color = plot.color;
    	int ipa = plot.startIndex(rect.width);
    	double maxV[] = plot.maxValues;
    	double minV[] = plot.minValues;
    	double gridMax = scale[plot.units];

    	gridStepY = 1e-8;    	
    	while (gridStepY < 20*gridMax/maxy) {
  		gridStepY *=multa[(multptr++)%3];
    	}

    	// Horizontal gridlines
    	int ll;
    	String minorDiv = "#707070";
    	String majorDiv = "#A0A0A0";
    	if (sim.printableCheckItem.getState()) {
    	    minorDiv = "#D0D0D0";
    	    majorDiv = "#808080";
    	    curColor = "#A0A000";
    	}
    	
    	// Vertical (T) gridlines
    	gridStepX = 1e-15;

    	double ts = sim.timeStep*speed;
    	//    	while (gridStep < ts*5)
    	//    		gridStep *= 10;
    	multptr=0;
    	while (gridStepX < ts*20) {
    	    gridStepX *=multa[(multptr++)%3];
    	}

    	if (drawGridLines) {
    	    // horizontal gridlines
    	    
    	    // don't show gridlines if lines are too close together (except for center line)
    	    boolean showGridLines = (gridStepY != 0) && drawHGridLines;
    	    for (ll = -100; ll <= 100; ll++) {
    		if (ll != 0 && !showGridLines)
    		    continue;
    		int yl = maxy-(int) (maxy*ll*gridStepY/gridMax);
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
            int minvy = (int) ((maxy/gridMax)*minV[ip]);
            int maxvy = (int) ((maxy/gridMax)*maxV[ip]);
            if (minvy <= maxy) {
        	if (minvy < -minRange || maxvy > minRange) {
        	    // we got a value outside min range, so we don't need to rescale later
        	    expandRange[plot.units] = false;
        	    minRange = 1000; // avoid triggering this test again
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
	if (selectedPlot < 0)
	    return;
	String info[] = new String[4];
	int ipa = plots.get(0).startIndex(rect.width);
	int ip = (sim.mouseCursorX-rect.x+ipa) & (scopePointCount-1);
	int ct = 0;
    	int maxy = (rect.height-1)/2;
    	int y = maxy;
    	ScopePlot plot = visiblePlots.get(selectedPlot);
    	info[ct++] = plot.getUnitText(plot.maxValues[ip]);
    	int maxvy = (int) ((maxy/scale[plot.units])*plot.maxValues[ip]);
    	g.setColor(plot.color);
    	g.fillOval(sim.mouseCursorX-2, rect.y+y-maxvy-2, 5, 5);
        if (showFFT) {
    		double maxFrequency = 1 / (sim.timeStep * speed * 2);
    		info[ct++] = CircuitElm.getUnitText(maxFrequency*(sim.mouseCursorX-rect.x)/rect.width, "Hz");
        }
	if (visiblePlots.size() > 0) {
	    double t = sim.t-sim.timeStep*speed*(rect.x+rect.width-sim.mouseCursorX);
	    info[ct++] = CircuitElm.getUnitText(t, "s");
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
    	if (showMax || showMin)
    	    calcMaxAndMin(plot.units);
    	if (showMax)
    	    drawInfoText(g, plot.getUnitText(maxValue));
    	if (showMin) {
    	    int ym=rect.height-5;
    	    g.drawString(plot.getUnitText(minValue), 0, ym);
    	}
    	if (text != null)
    	    drawInfoText(g, text);
    	if (showFreq)
    	    drawFrequency(g);
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
    
    MenuBar getMenu() {
	CircuitElm elm = plots.get(0).elm;
    	if (elm == null)
    	    return null;
    	elm = getSingleElm();
    	if (elm != null && elm instanceof TransistorElm) {
    		sim.scopeIbMenuItem.setState(showingValue(VAL_IB));
    		sim.scopeIcMenuItem.setState(showingValue(VAL_IC));
    		sim.scopeIeMenuItem.setState(showingValue(VAL_IE));
    		sim.scopeVbeMenuItem.setState(showingValue(VAL_VBE));
    		sim.scopeVbcMenuItem.setState(showingValue(VAL_VBC));
    		sim.scopeVceMenuItem.setState(showingValue(VAL_VCE));
    		sim.scopeVceIcMenuItem.setState(isShowingVceAndIc());
    		return sim.transScopeMenuBar;
    	} else {
    	    	sim.scopeRemovePlotMenuItem.setEnabled(visiblePlots.size() > 1 && !plot2d);
    		sim.scopeVMenuItem    .setState(showV && !showingValue(VAL_POWER));
    		sim.scopeIMenuItem    .setState(showI && !showingValue(VAL_POWER));
    		sim.scopeScaleMenuItem.setState(showScale);
    		sim.scopeMaxMenuItem  .setState(showMax);
    		sim.scopeMinMenuItem  .setState(showMin);
    		sim.scopeFreqMenuItem .setState(showFreq);
    		sim.scopePowerMenuItem.setState(showingValue(VAL_POWER));
    		sim.scopeVIMenuItem   .setState(plot2d && !plotXY);
    		sim.scopeXYMenuItem   .setState(plotXY);
    		sim.scopeSelectYMenuItem.setEnabled(plotXY);
    		sim.scopeFFTMenuItem.setState(showFFT);
    		sim.scopeResistMenuItem.setState(showingValue(VAL_R));
    		sim.scopeResistMenuItem.setEnabled(elm != null && elm instanceof MemristorElm);
    		return sim.scopeMenuBar;
    	}
    }
    
    boolean isShowingVceAndIc() {
	return plot2d && plots.size() == 2 && plots.get(0).value == VAL_VCE && plots.get(1).value == VAL_IC;
    }
    
    String dump() {
	ScopePlot vPlot = plots.get(0);
	
	CircuitElm elm = vPlot.elm;
    	if (elm == null)
    		return null;
    	int flags = (showI ? 1 : 0) | (showV ? 2 : 0) |
    			(showMax ? 0 : 4) |   // showMax used to be always on
    			(showFreq ? 8 : 0) |
    			(lockScale ? 16 : 0) | (plot2d ? 64 : 0) |
    			(plotXY ? 128 : 0) | (showMin ? 256 : 0) | (showScale? 512:0) |
    			(showFFT ? 1024 : 0);
    	flags |= FLAG_PLOTS;
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
    		x += " " + text;
    	return x;
    }
    
    void undump(StringTokenizer st) {
    	reset();
    	int e = new Integer(st.nextToken()).intValue();
    	if (e == -1)
    		return;
    	setElm(sim.getElm(e));
    	speed = new Integer(st.nextToken()).intValue();
    	int value = new Integer(st.nextToken()).intValue();
    	int flags = new Integer(st.nextToken()).intValue();
    	scale[UNITS_V] = new Double(st.nextToken()).doubleValue();
    	scale[UNITS_A] = new Double(st.nextToken()).doubleValue();
    	if (scale[UNITS_V] == 0)
    	    scale[UNITS_V] = .5;
    	if (scale[UNITS_A] == 0)
    	    scale[UNITS_A] = 1;
    	scaleX = scale[UNITS_V];
    	scaleY = scale[UNITS_A];
    	scale[UNITS_OHMS] = scale[UNITS_W] = 5;
    	text = null;
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
    	showI = (flags & 1) != 0;
    	showV = (flags & 2) != 0;
    	showMax = (flags & 4) == 0;
    	showFreq = (flags & 8) != 0;
    	lockScale = (flags & 16) != 0;
    	plot2d = (flags & 64) != 0;
    	plotXY = (flags & 128) != 0;
    	showMin = (flags & 256) != 0;
    	showScale = (flags & 512) !=0;
    	showFFT((flags & 1024) != 0);
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
    
    void handleMenu(String mi) {
    	if (mi == "showvoltage")
    		showVoltage(sim.scopeVMenuItem.getState());
    	if (mi == "showcurrent")
    		showCurrent(sim.scopeIMenuItem.getState());
    	if (mi=="showscale")
    		showScale(sim.scopeScaleMenuItem.getState());
    	if (mi == "showpeak")
    		showMax(sim.scopeMaxMenuItem.getState());
    	if (mi == "shownegpeak")
    		showMin(sim.scopeMinMenuItem.getState());
    	if (mi == "showfreq")
    		showFreq(sim.scopeFreqMenuItem.getState());
    	if (mi == "showfft")
    		showFFT(sim.scopeFFTMenuItem.getState());
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
    		plot2d = sim.scopeVIMenuItem.getState();
    		plotXY = false;
    		resetGraph();
    	}
    	if (mi == "plotxy") {
    		plotXY = plot2d = sim.scopeXYMenuItem.getState();
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
	if (wheelDeltaY < -5)
	    speedUp();
	    wheelDeltaY = 0;
    }
    
    CircuitElm getElm() {
	if (selectedPlot >= 0 && visiblePlots.size() > selectedPlot)
	    return visiblePlots.get(selectedPlot).elm;
	return visiblePlots.size() > 0 ? visiblePlots.get(0).elm : plots.get(0).elm;
    }

    CircuitElm getXElm() {
	return getElm();
    }
    CircuitElm getYElm() {
	if (plots.size() == 2)
	    return plots.get(1).elm;
	return null;
    }
}
