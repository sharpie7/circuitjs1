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

//import java.awt.*;
//import java.util.StringTokenizer;

    class CustomAnalogElm extends ChipElm {
	double gain;
	int inputCount;
	Expr expr;
	ExprState exprState;
	String exprString;
	public CustomAnalogElm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    inputCount = Integer.parseInt(st.nextToken());
	    exprString = CustomLogicModel.unescape(st.nextToken());
	    parseExpr();
	    setupPins();
	}
	public CustomAnalogElm(int xx, int yy) {
	    super(xx, yy);
	    inputCount = 2;
	    exprString = "a+b";
	    parseExpr();
	    setupPins();
	}
	
	String dump() {
	    return super.dump() + " " + inputCount + " " + CustomLogicModel.escape(exprString);
	}
	
	double lastVolts[];
	void setupPins() {
	    sizeX = 2;
	    sizeY = inputCount > 1 ? inputCount : 1;
	    pins = new Pin[inputCount+1];
	    int i;
	    for (i = 0; i != inputCount; i++)
		pins[i] = new Pin(i, SIDE_W, Character.toString((char)('A'+i)));
	    pins[inputCount] = new Pin(0, SIDE_E, "Vo");
	    pins[inputCount].output = true;
	    lastVolts = new double[inputCount];
	    exprState = new ExprState(inputCount);
	}
	String getChipName() { return "custom device"; } 
	boolean nonLinear() { return true; }
	void stamp() {
            int vn = pins[inputCount].voltSource + sim.nodeList.size();
            sim.stampNonLinear(vn);
            sim.stampVoltageSource(0, nodes[inputCount], pins[inputCount].voltSource);
	}
        double lastvd;

        void doStep() {
            int i;
            // converged yet?
            for (i = 0; i != inputCount; i++) {
        	if (Math.abs(volts[i]-lastVolts[i]) > .1)
        	    sim.converged = false;
        	if (Double.isNaN(volts[i]))
        	    volts[i] = 0;
            }
            int vn = pins[inputCount].voltSource + sim.nodeList.size();
            if (expr != null) {
        	// calculate output
        	for (i = 0; i != inputCount; i++)
        	    exprState.values[i] = volts[i];
        	exprState.t = sim.t;
        	double v0 = expr.eval(exprState);
        	if (Math.abs(volts[inputCount]-v0) > Math.abs(v0)*.01)
        	    sim.converged = false;
        	double rs = v0;
        	
        	// calculate and stamp output derivatives
        	for (i = 0; i != inputCount; i++) {
        	    double dv = .01;
        	    exprState.values[i] = volts[i]+dv;
        	    double v = expr.eval(exprState);
        	    double dx = (v-rs)/dv;
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
	void draw(Graphics g) {
	    drawChip(g);
	}
	int getPostCount() { return inputCount+1; }
	int getVoltageSourceCount() { return 1; }
	int getDumpType() { return 212; }
	
        public EditInfo getEditInfo(int n) {
            if (n == 0)
                return new EditInfo("# of Inputs", inputCount, 1, 8).
                    setDimensionless();
            if (n == 1) {
                EditInfo ei = new EditInfo("Output Function", 0, -1, -1);
                ei.text = exprString;
                return ei;
            }
            return null;
        }
        public void setEditValue(int n, EditInfo ei) {
            if (n == 0) {
        	if (ei.value < 0 || ei.value > 8)
        	    return;
                inputCount = (int) ei.value;
                setupPins();
                allocNodes();
                setPoints();
            }
            if (n == 1) {
        	exprString = ei.textf.getText();
        	parseExpr();
        	return;
            }
        }

        void parseExpr() {
            ExprParser parser = new ExprParser(exprString);
            expr = parser.parseExpression();
        }
    }

