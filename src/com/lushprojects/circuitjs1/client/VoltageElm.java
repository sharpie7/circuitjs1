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

import com.google.gwt.user.client.Window;

class VoltageElm extends CircuitElm {
    static final int FLAG_COS = 2;
    static final int FLAG_PULSE_DUTY = 4;
    int waveform;
    static final int WF_DC = 0;
    static final int WF_AC = 1;
    static final int WF_SQUARE = 2;
    static final int WF_TRIANGLE = 3;
    static final int WF_SAWTOOTH = 4;
    static final int WF_PULSE = 5;
    static final int WF_NOISE = 6;
    static final int WF_VAR = 7;
    double frequency, maxVoltage, freqTimeZero, bias,
	phaseShift, dutyCycle, noiseValue;
    
    static final double defaultPulseDuty = 1/(2*Math.PI);
    
    VoltageElm(int xx, int yy, int wf) {
	super(xx, yy);
	waveform = wf;
	maxVoltage = 5;
	frequency = 40;
	dutyCycle = .5;
	reset();
    }
    public VoltageElm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	maxVoltage = 5;
	frequency = 40;
	waveform = WF_DC;
	dutyCycle = .5;
	try {
	    waveform = new Integer(st.nextToken()).intValue();
	    frequency = new Double(st.nextToken()).doubleValue();
	    maxVoltage = new Double(st.nextToken()).doubleValue();
	    bias = new Double(st.nextToken()).doubleValue();
	    phaseShift = new Double(st.nextToken()).doubleValue();
	    dutyCycle = new Double(st.nextToken()).doubleValue();
	} catch (Exception e) {
	}
	if ((flags & FLAG_COS) != 0) {
	    flags &= ~FLAG_COS;
	    phaseShift = pi/2;
	}
	
	// old circuit files have the wrong duty cycle for pulse waveforms (wasn't configurable in the past)
	if ((flags & FLAG_PULSE_DUTY) == 0 && waveform == WF_PULSE) {
	    dutyCycle = defaultPulseDuty;
	}
	
	reset();
    }
    int getDumpType() { return 'v'; }
    
    String dump() {
	// set flag so we know if duty cycle is correct for pulse waveforms
	if (waveform == WF_PULSE)
	    flags |= FLAG_PULSE_DUTY;
	else
	    flags &= ~FLAG_PULSE_DUTY;
	
	return super.dump() + " " + waveform + " " + frequency + " " +
	    maxVoltage + " " + bias + " " + phaseShift + " " +
	    dutyCycle;
	// VarRailElm adds text at the end
    }

    void reset() {
	freqTimeZero = 0;
	curcount = 0;
    }
    double triangleFunc(double x) {
	if (x < pi)
	    return x*(2/pi)-1;
	return 1-(x-pi)*(2/pi);
    }
    void stamp() {
	if (waveform == WF_DC)
	    sim.stampVoltageSource(nodes[0], nodes[1], voltSource,
			       getVoltage());
	else
	    sim.stampVoltageSource(nodes[0], nodes[1], voltSource);
    }
    void doStep() {
	if (waveform != WF_DC)
	    sim.updateVoltageSource(nodes[0], nodes[1], voltSource,
				getVoltage());
    }
    void stepFinished() {
	if (waveform == WF_NOISE)
	    noiseValue = (sim.random.nextDouble()*2-1) * maxVoltage + bias;
    }
    double getVoltage() {
	if (waveform != WF_DC && sim.dcAnalysisFlag)
	    return bias;
	
	double w = 2*pi*(sim.t-freqTimeZero)*frequency + phaseShift;
	switch (waveform) {
	case WF_DC: return maxVoltage+bias;
	case WF_AC: return Math.sin(w)*maxVoltage+bias;
	case WF_SQUARE:
	    return bias+((w % (2*pi) > (2*pi*dutyCycle)) ?
			 -maxVoltage : maxVoltage);
	case WF_TRIANGLE:
	    return bias+triangleFunc(w % (2*pi))*maxVoltage;
	case WF_SAWTOOTH:
	    return bias+(w % (2*pi))*(maxVoltage/pi)-maxVoltage;
	case WF_PULSE:
	    return ((w % (2*pi)) < (2*pi*dutyCycle)) ? maxVoltage+bias : bias;
	case WF_NOISE:
	    return noiseValue;
	default: return 0;
	}
    }
    final int circleSize = 17;
    void setPoints() {
	super.setPoints();
	calcLeads((waveform == WF_DC || waveform == WF_VAR) ? 8 : circleSize*2);
    }
    void draw(Graphics g) {
	setBbox(x, y, x2, y2);
	draw2Leads(g);
	if (waveform == WF_DC) {
	    setVoltageColor(g, volts[0]);
	    setPowerColor(g, false);
	    interpPoint2(lead1, lead2, ps1, ps2, 0, 10);
	    drawThickLine(g, ps1, ps2);
	    setVoltageColor(g, volts[1]);
	    setPowerColor(g, false);
	    int hs = 16;
	    setBbox(point1, point2, hs);
	    interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
	    drawThickLine(g, ps1, ps2);
	} else {
	    setBbox(point1, point2, circleSize);
	    interpPoint(lead1, lead2, ps1, .5);
	    drawWaveform(g, ps1);
	    String inds;
	    if (bias>0 || (bias==0 && waveform == WF_PULSE))
               inds="+";
	    else
               inds="*";
	    g.setColor(Color.white);
	    g.setFont(unitsFont);
	    Point plusPoint = interpPoint(point1, point2, (dn/2+circleSize+4)/dn, 10*dsign );
            plusPoint.y += 4;
	    int w = (int)g.context.measureText(inds).getWidth();;
	    g.drawString(inds, plusPoint.x-w/2, plusPoint.y);
	}
	updateDotCount();
	if (sim.dragElm != this) {
	    if (waveform == WF_DC)
		drawDots(g, point1, point2, curcount);
	    else {
		drawDots(g, point1, lead1, curcount);
		drawDots(g, point2, lead2, -curcount);
	    }
	}
	drawPosts(g);
    }
	
    void drawWaveform(Graphics g, Point center) {
	g.setColor(needsHighlight() ? selectColor : Color.gray);
	setPowerColor(g, false);
	int xc = center.x; int yc = center.y;
	if (waveform != WF_NOISE)
	    drawThickCircle(g, xc, yc, circleSize);
	int wl = 8;
	adjustBbox(xc-circleSize, yc-circleSize,
		   xc+circleSize, yc+circleSize);
	int xc2;
	switch (waveform) {
	case WF_DC:
	{
	    break;
	}
	case WF_SQUARE:
	    xc2 = (int) (wl*2*dutyCycle-wl+xc);
	    xc2 = max(xc-wl+3, min(xc+wl-3, xc2));
	    drawThickLine(g, xc-wl, yc-wl, xc-wl, yc   );
	    drawThickLine(g, xc-wl, yc-wl, xc2  , yc-wl);
	    drawThickLine(g, xc2  , yc-wl, xc2  , yc+wl);
	    drawThickLine(g, xc+wl, yc+wl, xc2  , yc+wl);
	    drawThickLine(g, xc+wl, yc   , xc+wl, yc+wl);
	    break;
	case WF_PULSE:
	    yc += wl/2;
	    drawThickLine(g, xc-wl, yc-wl, xc-wl, yc   );
	    drawThickLine(g, xc-wl, yc-wl, xc-wl/2, yc-wl);
	    drawThickLine(g, xc-wl/2, yc-wl, xc-wl/2, yc);
	    drawThickLine(g, xc-wl/2, yc, xc+wl, yc);
	    break;
	case WF_SAWTOOTH:
	    drawThickLine(g, xc   , yc-wl, xc-wl, yc   );
	    drawThickLine(g, xc   , yc-wl, xc   , yc+wl);
	    drawThickLine(g, xc   , yc+wl, xc+wl, yc   );
	    break;
	case WF_TRIANGLE:
	{
	    int xl = 5;
	    drawThickLine(g, xc-xl*2, yc   , xc-xl, yc-wl);
	    drawThickLine(g, xc-xl, yc-wl, xc, yc);
	    drawThickLine(g, xc   , yc, xc+xl, yc+wl);
	    drawThickLine(g, xc+xl, yc+wl, xc+xl*2, yc);
	    break;
	}
	case WF_NOISE:
	{
	    g.setColor(needsHighlight() ? selectColor : whiteColor);
	    setPowerColor(g, false);
	    drawCenteredText(g, sim.LS("Noise"), xc, yc, true);
	    break;
	}
	case WF_AC:
	{
	    int i;
	    int xl = 10;
	    g.context.beginPath();
	    g.context.setLineWidth(3.0);

	    for (i = -xl; i <= xl; i++) {
		int yy = yc+(int) (.95*Math.sin(i*pi/xl)*wl);
		if (i == -xl)
		    g.context.moveTo(xc+i, yy);
		else
		    g.context.lineTo(xc+i, yy);
	    }
	    g.context.stroke();
	    g.context.setLineWidth(1.0);
	    break;
	}
	}
	if (sim.showValuesCheckItem.getState() && waveform != WF_NOISE) {
	    String s = getShortUnitText(frequency, "Hz");
	    if (dx == 0 || dy == 0)
		drawValues(g, s, circleSize);
	}
    }
	
    int getVoltageSourceCount() {
	return 1;
    }
    double getPower() { return -getVoltageDiff()*current; }
    double getVoltageDiff() { return volts[1] - volts[0]; }
    void getInfo(String arr[]) {
	switch (waveform) {
	case WF_DC: case WF_VAR:
	    arr[0] = "voltage source"; break;
	case WF_AC:       arr[0] = "A/C source"; break;
	case WF_SQUARE:   arr[0] = "square wave gen"; break;
	case WF_PULSE:    arr[0] = "pulse gen"; break;
	case WF_SAWTOOTH: arr[0] = "sawtooth gen"; break;
	case WF_TRIANGLE: arr[0] = "triangle gen"; break;
	case WF_NOISE:    arr[0] = "noise gen"; break;
	}
	arr[1] = "I = " + getCurrentText(getCurrent());
	arr[2] = ((this instanceof RailElm) ? "V = " : "Vd = ") +
	    getVoltageText(getVoltageDiff());
	int i = 3;
	if (waveform != WF_DC && waveform != WF_VAR && waveform != WF_NOISE) {
	    arr[i++] = "f = " + getUnitText(frequency, "Hz");
	    arr[i++] = "Vmax = " + getVoltageText(maxVoltage);
	    if (waveform == WF_AC && bias == 0)
		arr[i++] = "V(rms) = " + getVoltageText(maxVoltage/1.41421356);
	    if (bias != 0)
		arr[i++] = "Voff = " + getVoltageText(bias);
	    else if (frequency > 500)
		arr[i++] = "wavelength = " +
		    getUnitText(2.9979e8/frequency, "m");
	}
	if (waveform == WF_DC && current != 0 && sim.showResistanceInVoltageSources)
	    arr[i++] = "(R = " + getUnitText(maxVoltage/current, sim.ohmString) + ")";
	arr[i++] = "P = " + getUnitText(getPower(), "W");
    }
    public EditInfo getEditInfo(int n) {
	if (n == 0)
	    return new EditInfo(waveform == WF_DC ? "Voltage" :
				"Max Voltage", maxVoltage, -20, 20);
	if (n == 1) {
	    EditInfo ei =  new EditInfo("Waveform", waveform, -1, -1);
	    ei.choice = new Choice();
	    ei.choice.add("D/C");
	    ei.choice.add("A/C");
	    ei.choice.add("Square Wave");
	    ei.choice.add("Triangle");
	    ei.choice.add("Sawtooth");
	    ei.choice.add("Pulse");
	    ei.choice.add("Noise");
	    ei.choice.select(waveform);
	    return ei;
	}
	if (n == 2)
	    return new EditInfo("DC Offset (V)", bias, -20, 20);
	if (waveform == WF_DC || waveform == WF_NOISE)
	    return null;
	if (n == 3)
	    return new EditInfo("Frequency (Hz)", frequency, 4, 500);
	if (n == 4)
	    return new EditInfo("Phase Offset (degrees)", phaseShift*180/pi,
				-180, 180).setDimensionless();
	if (n == 5 && (waveform == WF_PULSE || waveform == WF_SQUARE))
	    return new EditInfo("Duty Cycle", dutyCycle*100, 0, 100).
		setDimensionless();
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    maxVoltage = ei.value;
	if (n == 2)
	    bias = ei.value;
	if (n == 3) {
	    // adjust time zero to maintain continuity ind the waveform
	    // even though the frequency has changed.
	    double oldfreq = frequency;
	    frequency = ei.value;
	    double maxfreq = 1/(8*sim.timeStep);
	    if (frequency > maxfreq) {
		if (Window.confirm(sim.LS("Adjust timestep to allow for higher frequencies?")))
		    sim.timeStep = 1/(32*frequency);
		else
		    frequency = maxfreq;
	    }
	    double adj = frequency-oldfreq;
	    freqTimeZero = sim.t-oldfreq*(sim.t-freqTimeZero)/frequency;
	}
	if (n == 1) {
	    int ow = waveform;
	    waveform = ei.choice.getSelectedIndex();
	    if (waveform == WF_DC && ow != WF_DC) {
		ei.newDialog = true;
		bias = 0;
	    } else if (waveform != ow)
		ei.newDialog = true;
	    
	    // change duty cycle if we're changing to or from pulse
	    if (waveform == WF_PULSE && ow != WF_PULSE)
		dutyCycle = defaultPulseDuty;
	    else if (ow == WF_PULSE && waveform != WF_PULSE)
		dutyCycle = .5;
	    
	    setPoints();
	}
	if (n == 4)
	    phaseShift = ei.value*pi/180;
	if (n == 5)
	    dutyCycle = ei.value*.01;
    }
}
