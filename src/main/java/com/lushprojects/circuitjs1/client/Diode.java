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

// diode that can be embedded in other elements.  series resistance is handled in DiodeElm, not here.
class Diode {
    int nodes[];
    CirSim sim;
    
    Diode(CirSim s) {
	sim = s;
	nodes = new int[2];
    }
    void setup(DiodeModel model) {
	leakage = model.saturationCurrent;
	zvoltage = model.breakdownVoltage;
	vscale = model.vscale;
	vdcoef = model.vdcoef;
	
//	sim.console("setup " + leakage + " " + zvoltage + " " + model.emissionCoefficient + " " +  vdcoef);

	// critical voltage for limiting; current is vscale/sqrt(2) at
	// this voltage
	vcrit = vscale * Math.log(vscale/(Math.sqrt(2)*leakage));
	// translated, *positive* critical voltage for limiting in Zener breakdown region;
	// limitstep() uses this with translated voltages in an analogous fashion to vcrit.
	vzcrit = vt * Math.log(vt/(Math.sqrt(2)*leakage));
	if (zvoltage == 0)
	    zoffset = 0;
	else {
	    // calculate offset which will give us 5mA at zvoltage
	    double i = -.005;
	    zoffset = zvoltage-Math.log(-(1+i/leakage))/vzcoef;
	}
    }
	
    void setupForDefaultModel() {
	setup(DiodeModel.getDefaultModel());
    }
    
    void reset() {
	lastvoltdiff = 0;
    }
	
    // Electron thermal voltage at SPICE's default temperature of 27 C (300.15 K):
    static final double vt = 0.025865;
    // The diode's "scale voltage", the voltage increase which will raise current by a factor of e.
    double vscale;
    // The multiplicative equivalent of dividing by vscale (for speed).
    double vdcoef;
    // The Zener breakdown curve is represented by a steeper exponential, one like the ideal
    // Shockley curve, but flipped and translated. This curve removes the moderating influence
    // of emcoef, replacing vscale and vdcoef with vt and vzcoef.
    // vzcoef is the multiplicative equivalent of dividing by vt (for speed).
    static final double vzcoef = 1 / vt;
    // User-specified diode parameters for forward voltage drop and Zener voltage.
    double fwdrop, zvoltage;
    // The diode current's scale factor, calculated from the user-specified forward voltage drop.
    double leakage;
    // Voltage offset for Zener breakdown exponential, calculated from user-specified Zener voltage.
    double zoffset;
    // Critical voltages for limiting the normal diode and Zener breakdown exponentials.
    double vcrit, vzcrit;
    double lastvoltdiff;
    
    double limitStep(double vnew, double vold) {
	double arg;
	double oo = vnew;

	// check new voltage; has current changed by factor of e^2?
	if (vnew > vcrit && Math.abs(vnew - vold) > (vscale + vscale)) {
	    if(vold > 0) {
		arg = 1 + (vnew - vold) / vscale;
		if(arg > 0) {
		    // adjust vnew so that the current is the same
		    // as in linearized model from previous iteration.
		    // current at vnew = old current * arg
		    vnew = vold + vscale * Math.log(arg);
		} else {
		    vnew = vcrit;
		}
	    } else {
		// adjust vnew so that the current is the same
		// as in linearized model from previous iteration.
		// (1/vscale = slope of load line)
		vnew = vscale *Math.log(vnew/vscale);
	    }
	    sim.converged = false;
	    //System.out.println(vnew + " " + oo + " " + vold);
	} else if (vnew < 0 && zoffset != 0) {
	    // for Zener breakdown, use the same logic but translate the values,
	    // and replace the normal values with the Zener-specific ones to
	    // account for the steeper exponential of our Zener breakdown curve.
	    vnew = -vnew - zoffset;
	    vold = -vold - zoffset;
	    
	    if (vnew > vzcrit && Math.abs(vnew - vold) > (vt + vt)) {
		if(vold > 0) {
		    arg = 1 + (vnew - vold) / vt;
		    if(arg > 0) {
			vnew = vold + vt * Math.log(arg);
			//System.out.println(oo + " " + vnew);
		    } else {
			vnew = vzcrit;
		    }
		} else {
		    vnew = vt *Math.log(vnew/vt);
		}
		sim.converged = false;
	    }
	    vnew = -(vnew+zoffset);
	}
	return vnew;
    }
    
    void stamp(int n0, int n1) {
	nodes[0] = n0;
	nodes[1] = n1;
	sim.stampNonLinear(nodes[0]);
	sim.stampNonLinear(nodes[1]);
    }
    
    void doStep(double voltdiff) {
	// used to have .1 here, but needed .01 for peak detector
	if (Math.abs(voltdiff-lastvoltdiff) > .01)
	    sim.converged = false;
	voltdiff = limitStep(voltdiff, lastvoltdiff);
	lastvoltdiff = voltdiff;

	// To prevent a possible singular matrix or other numeric issues, put a tiny conductance
	// in parallel with each P-N junction.
	double gmin = leakage * 0.01;
	if (sim.subIterations > 100) {
	    // if we have trouble converging, put a conductance in parallel with the diode.
	    // Gradually increase the conductance value for each iteration.
	    gmin = Math.exp(-9*Math.log(10)*(1-sim.subIterations/3000.));
	    if (gmin > .1)
		gmin = .1;
	}

	if (voltdiff >= 0 || zvoltage == 0) {
	    // regular diode or forward-biased zener
	    double eval = Math.exp(voltdiff*vdcoef);
	    double geq = vdcoef*leakage*eval + gmin;
	    double nc = (eval-1)*leakage - geq*voltdiff;
	    sim.stampConductance(nodes[0], nodes[1], geq);
	    sim.stampCurrentSource(nodes[0], nodes[1], nc);
	} else {
	    // Zener diode
	    
	    // For reverse-biased Zener diodes, mimic the Zener breakdown curve with an
	    // exponential similar to the ideal Shockley curve. (The real breakdown curve
	    // isn't a simple exponential, but this approximation should be OK.)

	    /* 
	     * I(Vd) = Is * (exp[Vd*C] - exp[(-Vd-Vz)*Cz] - 1 )
	     *
	     * geq is I'(Vd)
	     * nc is I(Vd) + I'(Vd)*(-Vd)
	     */

	    double geq = leakage* ( 
		vdcoef*Math.exp(voltdiff*vdcoef) + vzcoef*Math.exp((-voltdiff-zoffset)*vzcoef)
		) + gmin;

	    double nc = leakage* (
		Math.exp(voltdiff*vdcoef) 
		- Math.exp((-voltdiff-zoffset)*vzcoef) 
		- 1
		) + geq*(-voltdiff);

	    sim.stampConductance(nodes[0], nodes[1], geq);
	    sim.stampCurrentSource(nodes[0], nodes[1],  nc);
	}
    }
    
    double calculateCurrent(double voltdiff) {
	if (voltdiff >= 0 || zvoltage == 0)
	    return leakage*(Math.exp(voltdiff*vdcoef)-1);
	return leakage* (
	    Math.exp(voltdiff*vdcoef)  
	    - Math.exp((-voltdiff-zoffset)*vzcoef)  
	    - 1
	    );
    }
}
