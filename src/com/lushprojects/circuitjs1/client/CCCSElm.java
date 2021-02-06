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

    class CCCSElm extends VCCSElm {
	public CCCSElm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
//	    exprString = CustomLogicModel.unescape(st.nextToken());
//	    inputCount = 2;
//	    parseExpr();
	    setupPins();
	}
	public CCCSElm(int xx, int yy) {
	    super(xx, yy);
	    exprString = "2*a";
	    parseExpr();
//	    setupPins();
	}
	
	int inputPairCount;
	
	void setupPins() {
            sizeX = 2;
            sizeY = inputCount > 2 ? inputCount : 2;
            pins = new Pin[inputCount+2];
            inputPairCount = inputCount/2;
            int i;
            for (i = 0; i != inputPairCount; i++) {
                pins[i*2  ] = new Pin(i*2,   SIDE_W, Character.toString((char)('A'+i)) + "+");
                pins[i*2+1] = new Pin(i*2+1, SIDE_W, Character.toString((char)('A'+i)) + "-");
                pins[i*2+1].output = true;
            }
            pins[i*2] = new Pin(0, SIDE_E, "O+");
            pins[i*2].output = true;
            pins[i*2+1] = new Pin(1, SIDE_E, "O-");
            exprState = new ExprState(inputPairCount);
            lastCurrents = new double[inputPairCount];
            allocNodes();
      	}
	String getChipName() { return "CCCS"; } 
	void stamp() {
            // voltage source (0V) between C+ and C- so we can measure current
            int i;
            for (i = 0; i != inputCount; i += 2) {
                int vn1 = pins[i+1].voltSource;
                sim.stampVoltageSource(nodes[i], nodes[i+1], vn1, 0);
            }
	    
            sim.stampNonLinear(nodes[inputCount]);
            sim.stampNonLinear(nodes[inputCount+1]);
	}

	double lastCurrents[];
	
        void doStep() {
            // no current path?  give up
            if (broken) {
        	pins[inputCount].current = 0;
        	pins[inputCount+1].current = 0;
        	// avoid singular matrix errors
        	sim.stampResistor(nodes[inputCount], nodes[inputCount+1], 1e8);
        	return;
            }

            // converged yet?
            double convergeLimit = getConvergeLimit()*.1;
            
            int i;
//          sim.debugger();
            for (i = 0; i != inputPairCount; i++) {
                double cur = pins[i*2+1].current;
                if (Math.abs(cur-lastCurrents[i]) > convergeLimit)
                    sim.converged = false;
            }

            int vn1 = pins[1].voltSource + sim.nodeList.size();
            if (expr != null) {
                // calculate output
                for (i = 0; i != inputPairCount; i++)
                    setCurrentExprValue(i, pins[i*2+1].current);
                exprState.t = sim.t;
        	double v0 = expr.eval(exprState);
        	double rs = v0;

        	pins[inputCount  ].current = v0;
        	pins[inputCount+1].current = -v0;

                for (i = 0; i != inputPairCount; i++) {
                    double cur = pins[i*2+1].current;
                    double dv = cur-lastCurrents[i];
                    int vni = pins[i*2+1].voltSource + sim.nodeList.size();
                    if (Math.abs(dv) < 1e-6)
                        dv = 1e-6;
                    setCurrentExprValue(i, cur);
                    double v = expr.eval(exprState);
                    setCurrentExprValue(i, cur-dv);
                    double v2 = expr.eval(exprState);
                    double dx = (v-v2)/dv;
                    if (Math.abs(dx) < 1e-6)
                        dx = sign(dx, 1e-6);
                    sim.stampCCCS(nodes[inputCount+1], nodes[inputCount], pins[i*2+1].voltSource, dx);
                    
                    // adjust right side
                    rs -= dx*cur;
//                    if (sim.subIterations > 1)
//                        sim.console("ccedx " + i + " " + cur + " " + dx + " " + rs + " " + sim.subIterations + " " + sim.t);
                    setCurrentExprValue(i, cur);
                }

        	sim.stampCurrentSource(nodes[inputCount+1], nodes[inputCount], rs);
            }

            for (i = 0; i != inputPairCount; i++)
                lastCurrents[i] = pins[i*2+1].current;
        }
	
        void setCurrentExprValue(int n, double cur) {
            // set i to current for backward compatibility
            if (n == 0 && inputPairCount < 9)
                exprState.values[8] = cur;
            exprState.values[n] = cur;
        }
        
	int getPostCount() { return inputCount+2; }
	int getVoltageSourceCount() { return inputPairCount; }
	int getDumpType() { return 215; }
	boolean getConnection(int n1, int n2) {
            return (n1/2 == n2/2);
	}
        boolean hasCurrentOutput() { return true; }
	
        void setCurrent(int vn, double c) {
            int i;
            for (i = 0; i != inputCount; i += 2)
                if (pins[i+1].voltSource == vn) {
                    pins[i].current = -c;
                    pins[i+1].current = c;
                    return;
                }
        }
        
        public void setEditValue(int n, EditInfo ei) {
            if (n == 1) {
                // make sure number of inputs is even
                if (ei.value < 0 || ei.value > 8 || (ei.value % 2) == 1)
                    return;
                inputCount = (int) ei.value;
                setupPins();
                allocNodes();
                setPoints();
            } else
                super.setEditValue(n, ei);
        }
    }

