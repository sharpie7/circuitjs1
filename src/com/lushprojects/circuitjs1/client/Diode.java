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

class Diode {
    int nodes[];
    CirSim sim;

    
    Diode(CirSim s) {
	sim = s;
	nodes = new int[2];
    }
    void setup(double fw, double zv) {
	fwdrop = fw;
	zvoltage = zv;
	vdcoef = Math.log(1/leakage + 1)/fwdrop;
	vt = 1/vdcoef;
	// critical voltage for limiting; current is vt/sqrt(2) at
	// this voltage
	vcrit = vt * Math.log(vt/(Math.sqrt(2)*leakage));
	if (zvoltage == 0)
	    zoffset = 0;
	else {
	    // calculate offset which will give us 5mA at zvoltage
	    double i = -.005;
	    zoffset = zvoltage-Math.log(-(1+i/leakage))/vdcoef;
	}
    }
	
    void reset() {
	lastvoltdiff = 0;
    }
	
    public double leakage = 1e-14; // was 1e-9;
    double vt, vdcoef, fwdrop, zvoltage, zoffset;
    double lastvoltdiff;
    double vcrit;
    
    double limitStep(double vnew, double vold) {
	double arg;
	double oo = vnew;

	// check new voltage; has current changed by factor of e^2?
	if (vnew > vcrit && Math.abs(vnew - vold) > (vt + vt)) {
	    if(vold > 0) {
		arg = 1 + (vnew - vold) / vt;
		if(arg > 0) {
		    // adjust vnew so that the current is the same
		    // as in linearized model from previous iteration.
		    // current at vnew = old current * arg
		    vnew = vold + vt * Math.log(arg);
		    // current at v0 = 1uA
		    double v0 = Math.log(1e-6/leakage)*vt;
		    vnew = Math.max(v0, vnew);
		} else {
		    vnew = vcrit;
		}
	    } else {
		// adjust vnew so that the current is the same
		// as in linearized model from previous iteration.
		// (1/vt = slope of load line)
		vnew = vt *Math.log(vnew/vt);
	    }
	    sim.converged = false;
	    //System.out.println(vnew + " " + oo + " " + vold);
	} else if (vnew < 0 && zoffset != 0) {
	    // for Zener breakdown, use the same logic but translate the values
	    vnew = -vnew - zoffset;
	    vold = -vold - zoffset;
	    
	    if (vnew > vcrit && Math.abs(vnew - vold) > (vt + vt)) {
		if(vold > 0) {
		    arg = 1 + (vnew - vold) / vt;
		    if(arg > 0) {
			vnew = vold + vt * Math.log(arg);
			double v0 = Math.log(1e-6/leakage)*vt;
			vnew = Math.max(v0, vnew);
			//System.out.println(oo + " " + vnew);
		    } else {
			vnew = vcrit;
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

	double gmin = 0;
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
	    // make diode linear with negative voltages; aids convergence
	    if (voltdiff < 0)
		eval = 1;
	    double geq = vdcoef*leakage*eval + gmin;
	    double nc = (eval-1)*leakage - geq*voltdiff;
	    sim.stampConductance(nodes[0], nodes[1], geq);
	    sim.stampCurrentSource(nodes[0], nodes[1], nc);
	} else {
	    // Zener diode
	    
	    /* 
	     * I(Vd) = Is * (exp[Vd*C] - exp[(-Vd-Vz)*C] - 1 )
	     *
	     * geq is I'(Vd)
	     * nc is I(Vd) + I'(Vd)*(-Vd)
	     */

	    double geq = leakage*vdcoef* ( 
		Math.exp(voltdiff*vdcoef) + Math.exp((-voltdiff-zoffset)*vdcoef)
		) + gmin;

	    double nc = leakage* (
		Math.exp(voltdiff*vdcoef) 
		- Math.exp((-voltdiff-zoffset)*vdcoef) 
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
	    - Math.exp((-voltdiff-zoffset)*vdcoef)  
	    - 1
	    );
    }
}
