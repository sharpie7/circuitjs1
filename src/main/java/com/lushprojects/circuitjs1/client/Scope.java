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
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

class Scope {
    final int FLAG_YELM = 32;
    static final int VAL_POWER = 1;
    static final int VAL_IB = 1;
    static final int VAL_IC = 2;
    static final int VAL_IE = 3;
    static final int VAL_VBE = 4;
    static final int VAL_VBC = 5;
    static final int VAL_VCE = 6;
    static final int VAL_R = 2;
    double minV[], maxV[], minMaxV;
    double minI[], maxI[], minMaxI;
    int scopePointCount = 128;
    int ptr, ctr, speed, position;
    int value, ivalue;
    String text;
    Rectangle rect;
    boolean showI, showV, showScale, showMax, showMin, showFreq, lockScale, plot2d, plotXY;
    CircuitElm elm, xElm, yElm;
//    MemoryImageSource imageSource;
//    Image image;
    int pixels[];
    int draw_ox, draw_oy;
    float dpixels[];
    CirSim sim;
    Canvas imageCanvas;
    Context2d imageContext;
    int alphadiv =0;

    
    Scope(CirSim s) {
    	sim = s;
    	rect = new Rectangle(0, 0, 1, 1);
   	 	imageCanvas=Canvas.createIfSupported();
	    imageContext=imageCanvas.getContext2d();
	    allocImage();
    	reset();

    }
    
    void showCurrent(boolean b) { showI = b; value = ivalue = 0; }
    void showVoltage(boolean b) { showV = b; value = ivalue = 0; }
    void showMax    (boolean b) { 
    	showMax = b; 
    	if (b) 
    		showScale=false; 
    }
    void showScale    (boolean b) { 
    	showScale = b; 
    	if (b) showMax=false;
    	}
    void showMin    (boolean b) { showMin = b; }
    void showFreq   (boolean b) { showFreq = b; }
    void setLockScale  (boolean b) { lockScale = b; }
    
    void resetGraph() {
    	scopePointCount = 1;
    	while (scopePointCount <= rect.width)
    		scopePointCount *= 2;
    	minV = new double[scopePointCount];
    	maxV = new double[scopePointCount];
    	minI = new double[scopePointCount];
    	maxI = new double[scopePointCount];
    	ptr = ctr = 0;
    	allocImage();
    }
    
    
    boolean active() { return elm != null; }
    
    void reset() {
    	resetGraph();
    	minMaxV = 5;
    	minMaxI = .1;
    	speed = 64;
    	showI = showV = showScale = true;
    	showFreq = lockScale = showMin = showMax = false;
    	plot2d = false;
    	// no showI for Output
    		if (elm != null && (elm instanceof OutputElm ||
    				    elm instanceof LogicOutputElm ||
    				    elm instanceof ProbeElm))
    		    showI = false;
    	
    	value = ivalue = 0;
    	if (elm instanceof TransistorElm)
    		value = VAL_VCE;
    }
    
    void setRect(Rectangle r) {
    	this.rect = r;
    	resetGraph();
    }
    
    int getWidth() { return rect.width; }
    
    int rightEdge() { return rect.x+rect.width; }
	
    void setElm(CircuitElm ce) {
    	elm = ce;
    	reset();
    }
	
    void timeStep() {
    	if (elm == null)
    		return;
    	double v = elm.getScopeValue(value);
    	if (v < minV[ptr])
    		minV[ptr] = v;
    	if (v > maxV[ptr])
    		maxV[ptr] = v;
    	double i = 0;
    	if (value == 0 || ivalue != 0) {
    		i = (ivalue == 0) ? elm.getCurrent() : elm.getScopeValue(ivalue);
    		if (i < minI[ptr])
    			minI[ptr] = i;
    		if (i > maxI[ptr])
    			maxI[ptr] = i;
    	}

 //   	if (plot2d && dpixels != null) {
    	if (plot2d && imageContext!=null) {
    		boolean newscale = false;
    		while (v > minMaxV || v < -minMaxV) {
    			minMaxV *= 2;
    			newscale = true;
    		}
    		double yval = i;
    		if (plotXY)
    			yval = (yElm == null) ? 0 : yElm.getVoltageDiff();
    		while (yval > minMaxI || yval < -minMaxI) {
    			minMaxI *= 2;
    			newscale = true;
    		}
    		if (newscale)
    			clear2dView();
    		double xa = v/minMaxV;
    		double ya = yval/minMaxI;
    		int x = (int) (rect.width *(1+xa)*.499);
    		int y = (int) (rect.height*(1-ya)*.499);
    		drawTo(x, y);
    	} else {
    		ctr++;
    		if (ctr >= speed) {
    			ptr = (ptr+1) & (scopePointCount-1);
    			minV[ptr] = maxV[ptr] = v;
    			minI[ptr] = maxI[ptr] = i;
    			ctr = 0;
    		}
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
//    	// need to draw a line from x1,y1 to x2,y2
//    	if (draw_ox == x2 && draw_oy == y2) {
//    		dpixels[x2+r.width*y2] = 1;
//    	} else if (CircuitElm.abs(y2-draw_oy) > CircuitElm.abs(x2-draw_ox)) {
//    		// y difference is greater, so we step along y's
//    		// from min to max y and calculate x for each step
//    		double sgn = CircuitElm.sign(y2-draw_oy);
//    		int x, y;
//    		for (y = draw_oy; y != y2+sgn; y += sgn) {
//    			x = draw_ox+(x2-draw_ox)*(y-draw_oy)/(y2-draw_oy);
//    			dpixels[x+r.width*y] = 1;
//    		}
//    	} else {
//    		// x difference is greater, so we step along x's
//    		// from min to max x and calculate y for each step
//    		double sgn = CircuitElm.sign(x2-draw_ox);
//    		int x, y;
//    		for (x = draw_ox; x != x2+sgn; x += sgn) {
//    			y = draw_oy+(y2-draw_oy)*(x-draw_ox)/(x2-draw_ox);
//    			dpixels[x+r.width*y] = 1;
//    		}
//    	}
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
    	minMaxV *= x;
    	minMaxI *= x;
    }

    void draw2d(Graphics g) {
    	if (imageContext==null)
    		return;
    	g.context.save();
    	g.context.translate(rect.x, rect.y);
//   	int i;
//    	if (pixels == null || dpixels == null)
//    		return;
//    	int col = (sim.printableCheckItem.getState()) ? 0xFFFFFFFF : 0;
//    	for (i = 0; i != pixels.length; i++)
//    		pixels[i] = col;
//    	for (i = 0; i != r.width; i++)
//    		pixels[i+r.width*(r.height/2)] = 0xFF00FF00;
//    	int ycol = (plotXY) ? 0xFF00FF00 : 0xFFFFFF00;
//    	for (i = 0; i != r.height; i++)
//    		pixels[r.width/2+r.width*i] = ycol;
    	
//    	for (i = 0; i != pixels.length; i++) {
//    		int q = (int) (255*dpixels[i]);
//    		if (q > 0)
//    			pixels[i] = 0xFF000000 | (0x10101*q);
//    		dpixels[i] *= .997;
//    	}
    	
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
    	g.setColor(elm.whiteColor);
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
	
    void draw(Graphics g) {


    	if (elm == null)
    		return;
    	if (plot2d) {
    		draw2d(g);
    		return;
    	}
//    	if (pixels == null)
//    		return;
    	g.context.save();
    	g.setColor(Color.red);
    	g.context.translate(rect.x, rect.y);
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

    	boolean gotI = false;
    	boolean gotV = false;
    	int minRange = 4;
    	double realMaxV = -1e8;
    	double realMaxI = -1e8;
    	double realMinV =  1e8;
    	double realMinI =  1e8;
    	String curColor = "#FFFF00";
    	String voltColor = (value > 0) ? "#FFFFFF" : "#00FF00";
    	if (sim.scopeSelected == -1 && elm.isMouseElm())
    		curColor = voltColor = "#00FFFF";
    	int ipa = ptr+scopePointCount-rect.width;
    	for (i = 0; i != rect.width; i++) {
    		int ip = (i+ipa) & (scopePointCount-1);
    		while (maxV[ip] > minMaxV)
    			minMaxV *= 2;
    		while (minV[ip] < -minMaxV)
    			minMaxV *= 2;
    		while (maxI[ip] > minMaxI)
    			minMaxI *= 2;
    		while (minI[ip] < -minMaxI)
    			minMaxI *= 2;
    	}

    	double gridStepY = 1e-8;
    	double gridMax = (showI ? minMaxI : minMaxV);
//    	while (gridStep*100 < gridMax)
//    		gridStep *= 10;
//    	if (maxy*gridStep/gridMax < .3)
//    		gridStep = 0;
    	
    	while (gridStepY < 20*gridMax/maxy) {
    		gridStepY *=multa[(multptr++)%3];
    	}

    	// Horizontal gridlines
    	int ll;
 //   	boolean sublines = (maxy*gridStep/gridMax > 3);
    	for (ll = -100; ll <= 100; ll++) {
    		// don't show gridlines if plotting multiple values,
    		// or if lines are too close together (except for center line)
    		if (ll != 0 && ((showI && showV) || gridStepY == 0))
    			continue;
    		int yl = maxy-(int) (maxy*ll*gridStepY/gridMax);
    		if (yl < 0 || yl >= rect.height-1)
    			continue;
    		col = ll == 0 ? "#909090" : "#404040";
//    		if (ll % 10 != 0) {
//    			col = "#101010";
//    			if (!sublines)
//    				continue;
//    		}
    	g.setColor(col);
    		g.drawLine(0,yl,rect.width-1,yl);
//    		for (i = 0; i != r.width; i++)
//    			pixels[i+yl*r.width] = col;
    	}

    	// Vertical (T) gridlines
    	double gridStepX = 1e-15;

    	
    	double ts = sim.timeStep*speed;
//    	while (gridStep < ts*5)
//    		gridStep *= 10;
    	multptr=0;
    	while (gridStepX < ts*20) {
    		gridStepX *=multa[(multptr++)%3];
    	}
    	double tstart = sim.t-sim.timeStep*speed*rect.width;
    	double tx = sim.t-(sim.t % gridStepX);
    	// int first = 1;
    	for (ll = 0; ; ll++) {
    		double tl = tx-gridStepX*ll;
    		int gx = (int) ((tl-tstart)/ts);
    		if (gx < 0)
    			break;
    		if (gx >= rect.width)
    			continue;
    		if (tl < 0)
    			continue;
    		col = "#202020";
    		// first = 0;
    		if (((tl+gridStepX/4) % (gridStepX*10)) < gridStepX) {
    			col = "#909090";
    			if (((tl+gridStepX/4) % (gridStepX*100)) < gridStepX)
    				col = "#4040D0";
    		}
    	g.setColor(col);
    		g.drawLine(gx,0,gx,rect.height-1);
//    		for (i = 0; i < pixels.length; i += r.width)
//    			pixels[i+gx] = col;
    	}

    	// these two loops are pretty much the same, and should be
    	// combined!
    g.setColor(curColor);
    	if (value == 0 && showI) {
    		int ox = -1, oy = -1;
    		int j;
    		for (i = 0; i != rect.width; i++) {
    			int ip = (i+ipa) & (scopePointCount-1);
    			int miniy = (int) ((maxy/minMaxI)*minI[ip]);
    			int maxiy = (int) ((maxy/minMaxI)*maxI[ip]);
    			if (maxI[ip] > realMaxI)
    				realMaxI = maxI[ip];
    			if (minI[ip] < realMinI)
    				realMinI = minI[ip];
    			if (miniy <= maxy) {
    				if (miniy < -minRange || maxiy > minRange)
    					gotI = true;
    				if (ox != -1) {
    					if (miniy == oy && maxiy == oy)
    						continue;
    					// Horizontal line from (ox,y-oy) to (x+i-1,y-oy)
    					
//    					for (j = ox; j != x+i; j++)
//    						pixels[j+r.width*(y-oy)] = curColor;
    					g.drawLine(ox, y-oy, x+i-1, y-oy);
    					ox = oy = -1;
    				}
    				if (miniy == maxiy) {
    					ox = x+i;
    					oy = miniy;
    					continue;
    				}
    				// Vertical line from (x+i,y-miniy) to (x+i,y-maxiy-1)
//    				for (j = miniy; j <= maxiy; j++)
//    					pixels[x+i+r.width*(y-j)] = curColor;
    				g.drawLine(x+i, y-miniy, x+i, y-maxiy-1);
    			}
    		}
    		if (ox != -1)
    			// Horizontal line from (ox,y-oy) to (x+i-1,y-oy)
//    			for (j = ox; j != x+i; j++)
//    				pixels[j+r.width*(y-oy)] = curColor;
    			g.drawLine(ox, y-oy, x+i-1, y-oy);
    	}
    g.setColor(voltColor);
    	if (value != 0 || showV) {
    		int ox = -1, oy = -1, j;
    		for (i = 0; i != rect.width; i++) {
    			int ip = (i+ipa) & (scopePointCount-1);
    			int minvy = (int) ((maxy/minMaxV)*minV[ip]);
    			int maxvy = (int) ((maxy/minMaxV)*maxV[ip]);
    			if (maxV[ip] > realMaxV)
    				realMaxV = maxV[ip];
    			if (minV[ip] < realMinV)
    				realMinV = minV[ip];
    			if ((value != 0 || showV) && minvy <= maxy) {
    				if (minvy < -minRange || maxvy > minRange)
    					gotV = true;
    				if (ox != -1) {
    					if (minvy == oy && maxvy == oy)
    						continue;
//    					for (j = ox; j != x+i; j++)
//    						pixels[j+r.width*(y-oy)] = voltColor;
    					g.drawLine(ox, y-oy, x+i-1, y-oy);
    					ox = oy = -1;
    				}
    				if (minvy == maxvy) {
    					ox = x+i;
    					oy = minvy;
    					continue;
    				}
//    				for (j = minvy; j <= maxvy; j++)
//    					pixels[x+i+r.width*(y-j)] = voltColor;
    				g.drawLine(x+i, y-minvy, x+i, y-maxvy-1);
    			}
    		} // for (i=0...)
    		if (ox != -1)
//    			for (j = ox; j != x+i; j++)
//    				pixels[j+r.width*(y-oy)] = voltColor;
    			g.drawLine(ox, y-oy, x+i-1, y-oy); // Horizontal
    	}
    	double freq = 0;
    	if (showFreq) {
    		// try to get frequency
    		// get average
    		double avg = 0;
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
    		freq = 1/(avperiod*sim.timeStep*speed);
    		// don't show freq if standard deviation is too great
    		if (periodct < 1 || periodstd > 2)
    			freq = 0;
    		// System.out.println(freq + " " + periodstd + " " + periodct);
    	}
//    	g.drawImage(image, r.x, r.y, null);
    	g.setColor(elm.whiteColor);
//    	int yt = r.y+10;
//    	x += r.x;
    	int yt=10;
    	if (showScale) {
    		String unitText="";
    		String vScaleText="";
    		if ( gridStepY!=0 && (!(showV && showI))) {
    			if (value!=0)
    				unitText=elm.getScopeUnits(value);
    			else if (showV)
    				unitText="V";
    			else if (showI)
    				unitText="A";
    			vScaleText=" V="+CircuitElm.getShortUnitText(gridStepY, unitText)+"/div";
    		}
    		g.drawString("H="+CircuitElm.getShortUnitText(gridStepX, "s")+"/div" +
    				vScaleText, x,yt);
    	}
    	if (showMax) {
    		if (value != 0)
    			g.drawString(elm.getUnitText(realMaxV, elm.getScopeUnits(value)),
    					x, yt);
    		else if (showV)
    			g.drawString(elm.getVoltageText(realMaxV), x, yt);
    		else if (showI)
    			g.drawString(elm.getCurrentText(realMaxI), x, yt);
    		yt += 15;
    	}
    	if (showMin) {
    	//	int ym = r.y+r.height-5;
    		int ym=rect.height-5;
    		if (value != 0)
    			g.drawString(elm.getUnitText(realMinV,
    					elm.getScopeUnits(value)),
    					x, ym);
    		else if (showV)
    			g.drawString(elm.getVoltageText(realMinV), x, ym);
    		else if (showI)
    			g.drawString(elm.getCurrentText(realMinI), x, ym);
    	}
    	if (text != null && rect.y + rect.height > yt+5) {
    		g.drawString(text, x, yt);
    		yt += 15;
    	}
    	if (showFreq && freq != 0 && rect.y + rect.height > yt+5)
    		g.drawString(elm.getUnitText(freq, "Hz"), x, yt);
    	if (ptr > 5 && !lockScale) {
    		if (!gotI && minMaxI > 1e-4)
    			minMaxI /= 2;
    		if (!gotV && minMaxV > 1e-4)
    			minMaxV /= 2;
    	}
    	g.context.restore();
    }
	
    void speedUp() {
    	if (speed > 1) {
    		speed /= 2;
    		resetGraph();
    	}
    }
    
    void slowDown() {
    	speed *= 2;
    	resetGraph();
    }
	
    MenuBar getMenu() {
    	if (elm == null)
    		return null;
    	if (elm instanceof TransistorElm) {
    		sim.scopeIbMenuItem.setState(value == VAL_IB);
    		sim.scopeIcMenuItem.setState(value == VAL_IC);
    		sim.scopeIeMenuItem.setState(value == VAL_IE);
    		sim.scopeVbeMenuItem.setState(value == VAL_VBE);
    		sim.scopeVbcMenuItem.setState(value == VAL_VBC);
    		sim.scopeVceMenuItem.setState(value == VAL_VCE && ivalue != VAL_IC);
    		sim.scopeVceIcMenuItem.setState(value == VAL_VCE && ivalue == VAL_IC);
    		return sim.transScopeMenuBar;
    	} else {
    		sim.scopeVMenuItem    .setState(showV && value == 0);
    		sim.scopeIMenuItem    .setState(showI && value == 0);
    		sim.scopeScaleMenuItem.setState(showScale);
    		sim.scopeMaxMenuItem  .setState(showMax);
    		sim.scopeMinMenuItem  .setState(showMin);
    		sim.scopeFreqMenuItem .setState(showFreq);
    		sim.scopePowerMenuItem.setState(value == VAL_POWER);
    		sim.scopeVIMenuItem   .setState(plot2d && !plotXY);
    		sim.scopeXYMenuItem   .setState(plotXY);
    		sim.scopeSelectYMenuItem.setEnabled(plotXY);
    		sim.scopeResistMenuItem.setState(value == VAL_R);
    		sim.scopeResistMenuItem.setEnabled(elm instanceof MemristorElm);
    		return sim.scopeMenuBar;
    	}
    }
    
    void setValue(int x) { reset(); value = x; }
    
    String dump() {
    	if (elm == null)
    		return null;
    	int flags = (showI ? 1 : 0) | (showV ? 2 : 0) |
    			(showMax ? 0 : 4) |   // showMax used to be always on
    			(showFreq ? 8 : 0) |
    			(lockScale ? 16 : 0) | (plot2d ? 64 : 0) |
    			(plotXY ? 128 : 0) | (showMin ? 256 : 0) | (showScale? 512:0);
    	flags |= FLAG_YELM; // yelm present
    	int eno = sim.locateElm(elm);
    	if (eno < 0)
    		return null;
    	int yno = yElm == null ? -1 : sim.locateElm(yElm);
    	String x = "o " + eno + " " +
    			speed + " " + value + " " + flags + " " +
    			minMaxV + " " + minMaxI + " " + position + " " + yno;
    	if (text != null)
    		x += " " + text;
    	return x;
    }
    
    void undump(StringTokenizer st) {
    	reset();
    	int e = new Integer(st.nextToken()).intValue();
    	if (e == -1)
    		return;
    	elm = sim.getElm(e);
    	speed = new Integer(st.nextToken()).intValue();
    	value = new Integer(st.nextToken()).intValue();
    	int flags = new Integer(st.nextToken()).intValue();
    	minMaxV = new Double(st.nextToken()).doubleValue();
    	minMaxI = new Double(st.nextToken()).doubleValue();
    	if (minMaxV == 0)
    		minMaxV = .5;
    	if (minMaxI == 0)
    		minMaxI = 1;
    	text = null;
    	yElm = null;
    	try {
    		position = new Integer(st.nextToken()).intValue();
    		int ye = -1;
    		if ((flags & FLAG_YELM) != 0) {
    			ye = new Integer(st.nextToken()).intValue();
    			if (ye != -1)
    				yElm = sim.getElm(ye);
    		}
    		while (st.hasMoreTokens()) {
    			if (text == null)
    				text = st.nextToken();
    			else
    				text += " " + st.nextToken();
    		}
    	} catch (Exception ee) {
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
    
//    void allocImage() {
//    	pixels = null;
//    	int w = r.width;
//    	int h = r.height;
//    	if (w == 0 || h == 0)
//    		return;
//    	if (sim.useBufferedImage) {
//    		try {
//    			/* simulate the following code using reflection:
//		   dbimage = new BufferedImage(d.width, d.height,
//		   BufferedImage.TYPE_INT_RGB);
//		   DataBuffer db = (DataBuffer)(((BufferedImage)dbimage).
//		   getRaster().getDataBuffer());
//		   DataBufferInt dbi = (DataBufferInt) db;
//		   pixels = dbi.getData();
//    			 */
//    			Class biclass = Class.forName("java.awt.image.BufferedImage");
//    			Class dbiclass = Class.forName("java.awt.image.DataBufferInt");
//    			Class rasclass = Class.forName("java.awt.image.Raster");
//    			Constructor cstr = biclass.getConstructor(
//    					new Class[] { int.class, int.class, int.class });
//    			image = (Image) cstr.newInstance(new Object[] {
//    					new Integer(w), new Integer(h),
//    					new Integer(BufferedImage.TYPE_INT_RGB)});
//    			Method m = biclass.getMethod("getRaster");
//    			Object ras = m.invoke(image);
//    			Object db = rasclass.getMethod("getDataBuffer").invoke(ras);
//    			pixels = (int[])
//    					dbiclass.getMethod("getData").invoke(db);
//    		} catch (Exception ee) {
//    			// ee.printStackTrace();
//    			System.out.println("BufferedImage failed");
//    		}
//    	}
//    	if (pixels == null) {
//    		pixels = new int[w*h];
//    		int i;
//    		for (i = 0; i != w*h; i++)
//    			pixels[i] = 0xFF000000;
//    		imageSource = new MemoryImageSource(w, h, pixels, 0, w);
//    		imageSource.setAnimated(true);
//    		imageSource.setFullBufferUpdates(true);
//    		image = sim.cv.createImage(imageSource);
//    	}
//    	dpixels = new float[w*h];
//    	draw_ox = draw_oy = -1;
//    }

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
    		value = VAL_VCE;
    		ivalue = VAL_IC;
    		resetGraph();
    	}

    	if (mi == "showvvsi") {
    		plot2d = sim.scopeVIMenuItem.getState();
    		plotXY = false;
    		resetGraph();
    	}
    	if (mi == "plotxy") {
    		plotXY = plot2d = sim.scopeXYMenuItem.getState();
    		if (yElm == null)
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
    	int e = yElm == null ? -1 : sim.locateElm(yElm);
    	int firstE = e;
    	while (true) {
    		for (e++; e < sim.elmList.size(); e++) {
    			CircuitElm ce = sim.getElm(e);
    			if ((ce instanceof OutputElm || ce instanceof ProbeElm) &&
    					ce != elm) {
    				yElm = ce;
    				return;
    			}
    			
    		}
    		if (firstE == -1)
    			return;
    		e = firstE = -1;
    	}
    }
}
