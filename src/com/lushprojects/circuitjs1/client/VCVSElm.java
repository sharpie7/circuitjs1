/*    
    Copyright (C) Paul Falstad
    
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

    class VCVSElm extends VCCSElm {
	public VCVSElm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
//	    inputCount = Integer.parseInt(st.nextToken());
//	    exprString = CustomLogicModel.unescape(st.nextToken());
//	    parseExpr();
//	    setupPins();
	}
	public VCVSElm(int xx, int yy) {
	    super(xx, yy);
	    exprString = "2*(a-b)";
//	    inputCount = 2;
//	    exprString = "a+b";
//	    parseExpr();
//	    setupPins();
	}
	
	void setupPins() {
	    sizeX = 2;
            sizeY = inputCount > 2 ? inputCount : 2;
            pins = new Pin[inputCount+2];
	    int i;
	    for (i = 0; i != inputCount; i++)
		pins[i] = new Pin(i, SIDE_W, Character.toString((char)('A'+i)));
	    pins[inputCount] = new Pin(0, SIDE_E, "V+");
	    pins[inputCount].output = true;
            pins[inputCount+1] = new Pin(1, SIDE_E, "V-");
	    lastVolts = new double[inputCount];
	    exprState = new ExprState(inputCount);
	}
	String getChipName() { return "VCVS"; } 
	void stamp() {
            int vn = pins[inputCount].voltSource + sim.nodeList.size();
            sim.stampNonLinear(vn);
            sim.stampVoltageSource(nodes[inputCount+1], nodes[inputCount], pins[inputCount].voltSource);
	}

        void doStep() {
            int i;
            // converged yet?
            double limitStep = getLimitStep();
            double convergeLimit = getConvergeLimit();
            for (i = 0; i != inputCount; i++) {
        	if (Math.abs(volts[i]-lastVolts[i]) > convergeLimit)
        	    sim.converged = false;
        	if (Double.isNaN(volts[i]))
        	    volts[i] = 0;
        	if (Math.abs(volts[i]-lastVolts[i]) > limitStep)
        	    volts[i] = lastVolts[i] + sign(volts[i]-lastVolts[i], limitStep);
            }
            int vn = pins[inputCount].voltSource + sim.nodeList.size();
            if (expr != null) {
        	// calculate output
        	for (i = 0; i != inputCount; i++)
        	    exprState.values[i] = volts[i];
        	exprState.t = sim.t;
        	double v0 = expr.eval(exprState);
        	if (Math.abs(volts[inputCount]-volts[inputCount+1]-v0) > Math.abs(v0)*.01 && sim.subIterations < 100)
        	    sim.converged = false;
        	double rs = v0;
        	
        	// calculate and stamp output derivatives
        	for (i = 0; i != inputCount; i++) {
        	    double dv = 1e-6;
        	    exprState.values[i] = volts[i]+dv;
        	    double v = expr.eval(exprState);
        	    exprState.values[i] = volts[i]-dv;
        	    double v2 = expr.eval(exprState);
        	    double dx = (v-v2)/(dv*2);
        	    if (Math.abs(dx) < 1e-6)
        		dx = sign(dx, 1e-6);
//        	    sim.console("cae_ " + i + " " + dx);
        	    sim.stampMatrix(vn,  nodes[i], -dx);
        	    // adjust right side
        	    rs -= dx*volts[i];
        	    exprState.values[i] = volts[i];
        	}
        	sim.stampRightSide(vn, rs);
            }

            for (i = 0; i != inputCount; i++)
        	lastVolts[i] = volts[i];
        }
	int getPostCount() { return inputCount+2; }
	int getVoltageSourceCount() { return 1; }
	int getDumpType() { return 212; }
        boolean hasCurrentOutput() { return false; }

        void setCurrent(int vn, double c) {
            if (pins[inputCount].voltSource == vn) {
                pins[inputCount].current = c;
                pins[inputCount+1].current = -c;
            }
        }

    }

