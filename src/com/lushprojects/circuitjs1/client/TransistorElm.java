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

import java.util.Vector;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.lushprojects.circuitjs1.client.util.Locale;

class TransistorElm extends CircuitElm {
	// node 0 = base
	// node 1 = collector
	// node 2 = emitter
	int pnp;
	double beta;
//	double fgain, inv_fgain;
	double gmin;
	String modelName;
	TransistorModel model;
	static String lastModelName = "default";
	final int FLAG_FLIP = 1;
	final int FLAG_CIRCLE = 2;
	final int FLAGS_GLOBAL = FLAG_CIRCLE;
	static int globalFlags;
	int badIters;
	
	TransistorElm(int xx, int yy, boolean pnpflag) {
	    super(xx, yy);
	    pnp = (pnpflag) ? -1 : 1;
	    beta = 100;
	    modelName = lastModelName;
	    setup();
	}
	public TransistorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    pnp = new Integer(st.nextToken()).intValue();
	    beta = 100;
	    try {
		lastvbe = new Double(st.nextToken()).doubleValue();
		lastvbc = new Double(st.nextToken()).doubleValue();
		volts[0] = 0;
		volts[1] = -lastvbe;
		volts[2] = -lastvbc;
		beta = new Double(st.nextToken()).doubleValue();
		modelName = CustomLogicModel.unescape(st.nextToken());
	    } catch (Exception e) {
		modelName = "default";
	    }
            globalFlags = flags & (FLAGS_GLOBAL);
	    setup();
	}
	void setup() {
	    model = TransistorModel.getModelWithNameOrCopy(modelName, model);
	    modelName = model.name;   // in case we couldn't find that model    
	    vcrit = vt * Math.log(vt/(Math.sqrt(2)*model.satCur));
	    noDiagonal = true;
	}
	boolean nonLinear() { return true; }
	void reset() {
	    volts[0] = volts[1] = volts[2] = 0;
	    lastvbc = lastvbe = curcount_c = curcount_e = curcount_b = 0;
	    badIters = 0;
	}
	int getDumpType() { return 't'; }
	String dump() {
	    return super.dump() + " " + pnp + " " + (volts[0]-volts[1]) + " " +
		(volts[0]-volts[2]) + " " + beta + " " + CustomLogicModel.escape(modelName);
	}
	
	    public void updateModels() {
	        setup();
	    }

	    String dumpModel() {
	        if (model.builtIn || model.dumped)
	            return null;
	        return model.dump();
	    }
	    
	
	double ic, ie, ib, curcount_c, curcount_e, curcount_b;
	
	Polygon rectPoly, arrowPoly;
	Point circleCenter;	
	
	boolean hasCircle() { return (globalFlags & FLAG_CIRCLE) != 0; }
	
	void draw(Graphics g) {
            // pick up global flags changes
            if ((flags & FLAGS_GLOBAL) != globalFlags)
                setPoints();

	    setBbox(point1, point2, 16);
	    if (hasCircle()) {
		g.setColor(Color.gray);
		drawThickCircle(g, circleCenter.x, circleCenter.y, 20);
	    }
	    setPowerColor(g, true);
	    // draw collector
	    setVoltageColor(g, volts[1]);
	    drawThickLine(g, coll[0], coll[1]);
	    // draw emitter
	    setVoltageColor(g, volts[2]);
	    drawThickLine(g, emit[0], emit[1]);
	    // draw arrow
	    g.setColor(lightGrayColor);
	    g.fillPolygon(arrowPoly);
	    // draw base
	    setVoltageColor(g, volts[0]);
	    if (sim.powerCheckItem.getState())
		g.setColor(Color.gray);
	    drawThickLine(g, point1, base);
	    // draw dots
	    curcount_b = updateDotCount(-ib, curcount_b);
	    drawDots(g, base, point1, curcount_b);
	    curcount_c = updateDotCount(-ic, curcount_c);
	    drawDots(g, coll[1], coll[0], curcount_c);
	    curcount_e = updateDotCount(-ie, curcount_e);
	    drawDots(g, emit[1], emit[0], curcount_e);
	    // draw base rectangle
	    setVoltageColor(g, volts[0]);
	    setPowerColor(g, true);
	    g.fillPolygon(rectPoly);

	    if ((needsHighlight() || sim.dragElm == this) && dy == 0) {
		g.setColor(whiteColor);
// IES
//		g.setFont(unitsFont);
		int ds = sign(dx);
		g.drawString("B", base.x-10*ds, base.y-5);
		g.drawString("C", coll[0].x-3+9*ds, coll[0].y+4); // x+6 if ds=1, -12 if -1
		g.drawString("E", emit[0].x-3+9*ds, emit[0].y+4);
	    }
	    drawPosts(g);
	}
	Point getPost(int n) {
	    return (n == 0) ? point1 : (n == 1) ? coll[0] : emit[0];
	}
	
	int getPostCount() { return 3; }
	double getPower() {
	    return (volts[0]-volts[2])*ib + (volts[1]-volts[2])*ic;
	}

	Point rect[], coll[], emit[], base;
	void setPoints() {
            // these flags apply to all transistors
            flags &= ~FLAGS_GLOBAL;
            flags |= globalFlags;

	    super.setPoints();
	    int hs = 16;
	    if ((flags & FLAG_FLIP) != 0)
		dsign = -dsign;
	    int hs2 = hs*dsign*pnp;
	    // calc collector, emitter posts
	    coll = newPointArray(2);
	    emit = newPointArray(2);
	    interpPoint2(point1, point2, coll[0], emit[0], 1, hs2);
	    // calc rectangle edges
	    rect = newPointArray(4);
	    interpPoint2(point1, point2, rect[0], rect[1], 1-16/dn, hs);
	    interpPoint2(point1, point2, rect[2], rect[3], 1-13/dn, hs);
	    // calc points where collector/emitter leads contact rectangle
	    interpPoint2(point1, point2, coll[1], emit[1], 1-13/dn, 6*dsign*pnp);
	    // calc point where base lead contacts rectangle
	    base = new Point();
	    interpPoint (point1, point2, base, 1-16/dn);
	    // rectangle
	    rectPoly = createPolygon(rect[0], rect[2], rect[3], rect[1]);

	    // arrow
	    if (pnp == 1)
		arrowPoly = calcArrow(emit[1], emit[0], 8, 4);
	    else {
		Point pt = interpPoint(point1, point2, 1-11/dn, -5*dsign*pnp);
		arrowPoly = calcArrow(emit[0], pt, 8, 4);
	    }
	    
	    circleCenter = interpPoint(base, point2, .5);
	}
	
	static final double leakage = 1e-13; // 1e-6;
	// Electron thermal voltage at SPICE's default temperature of 27 C (300.15 K):
	static final double vt = 0.025865;
	double vcrit;
	double lastvbc, lastvbe;
	double limitStep(double vnew, double vold) {
	    double arg;
	    double oo = vnew;
	    
	    if (vnew > vcrit && Math.abs(vnew - vold) > (vt + vt)) {
		if(vold > 0) {
		    arg = 1 + (vnew - vold) / vt;
		    if(arg > 0) {
			vnew = vold + vt * Math.log(arg);
		    } else {
			vnew = vcrit;
		    }
		} else {
		    vnew = vt *Math.log(vnew/vt);
		}
		sim.converged = false;
		//System.out.println(vnew + " " + oo + " " + vold);
	    }
	    return(vnew);
	}
	void stamp() {
	    sim.stampNonLinear(nodes[0]);
	    sim.stampNonLinear(nodes[1]);
	    sim.stampNonLinear(nodes[2]);
	}
	void doStep() {
	    double vbc = pnp*(volts[0]-volts[1]); // typically negative
	    double vbe = pnp*(volts[0]-volts[2]); // typically positive
	    if (Math.abs(vbc-lastvbc) > .01 || // .01
		Math.abs(vbe-lastvbe) > .01)
		sim.converged = false;

	    // To prevent a possible singular matrix, put a tiny conductance in parallel
	    // with each P-N junction.
//	    gmin = leakage * 0.01;
	    gmin = 1e-12;
	    
	    if (sim.subIterations > 100 && badIters < 5) {
		// if we have trouble converging, put a conductance in parallel with all P-N junctions.
		// Gradually increase the conductance value for each iteration.
		gmin = Math.exp(-9*Math.log(10)*(1-sim.subIterations/300.));
		if (gmin > .1)
		    gmin = .1;
	    }
	    
	    //System.out.print("T " + vbc + " " + vbe + "\n");
	    vbc = limitStep(vbc, lastvbc);
	    vbe = limitStep(vbe, lastvbe);
	    lastvbc = vbc;
	    lastvbe = vbe;

            /*
             *   dc model paramters (from Spice 3f5, bjtload.c)
             */
            double csat=model.satCur;
            double oik=model.invRollOffF;
            double c2=model.BEleakCur;
            double vte=model.leakBEemissionCoeff*vt;
            double oikr=model.invRollOffR;
            double c4=model.BCleakCur;
            double vtc=model.leakBCemissionCoeff*vt;
            
//          double rbpr=model.minBaseResist;
//          double rbpi=model.baseResist-rbpr;
//          double xjrb=model.baseCurrentHalfResist;
            
            double vtn=vt*model.emissionCoeffF;
            double evbe, cbe, gbe, cben, gben, evben, evbc, cbc, gbc, cbcn, gbcn, evbcn;
            double qb, dqbdve, dqbdvc, q2, sqarg, arg;
            if(vbe > -5*vtn){
                evbe=Math.exp(vbe/vtn);
                cbe=csat*(evbe-1)+gmin*vbe;
                gbe=csat*evbe/vtn+gmin;
                if (c2 == 0) {
                    cben=0;
                    gben=0;
                } else {
                    evben=Math.exp(vbe/vte);
                    cben=c2*(evben-1);
                    gben=c2*evben/vte;
                }
            } else {
                gbe = -csat/vbe+gmin;
                cbe=gbe*vbe;
                gben = -c2/vbe;
                cben=gben*vbe;
            }
            vtn=vt*model.emissionCoeffR;
            if(vbc > -5*vtn) {
                evbc=Math.exp(vbc/vtn);
                cbc=csat*(evbc-1)+gmin*vbc;
                gbc=csat*evbc/vtn+gmin;
                if (c4 == 0) {
                    cbcn=0;
                    gbcn=0;
                } else {
                    evbcn=Math.exp(vbc/vtc);
                    cbcn=c4*(evbcn-1);
                    gbcn=c4*evbcn/vtc;
                }
            } else {
                gbc = -csat/vbc+gmin;
                cbc = gbc*vbc;
                gbcn = -c4/vbc;
                cbcn=gbcn*vbc;
            }
            /*
             *   determine base charge terms
             */
            double q1=1/(1-model.invEarlyVoltF*vbc-model.invEarlyVoltR*vbe);
            if(oik == 0 && oikr == 0) {
                qb=q1;
                dqbdve=q1*qb*model.invEarlyVoltR;
                dqbdvc=q1*qb*model.invEarlyVoltF;
            } else {
                q2=oik*cbe+oikr*cbc;
                arg=Math.max(0,1+4*q2);
                sqarg=1;
                if(arg != 0) sqarg=Math.sqrt(arg);
                qb=q1*(1+sqarg)/2;
                dqbdve=q1*(qb*model.invEarlyVoltR+oik*gbe/sqarg);
                dqbdvc=q1*(qb*model.invEarlyVoltF+oikr*gbc/sqarg);
            }

            double cc=0;
            double cex=cbe;
            double gex=gbe;
            /*
             *   determine dc incremental conductances
             */
            cc=cc+(cex-cbc)/qb-cbc/model.betaR-cbcn;
            double cb=cbe/beta+cben+cbc/model.betaR+cbcn;
            
            // get currents
	    ic = pnp*cc;
	    ib = pnp*cb;
	    ie = pnp*(-cc-cb);
	    
/*            double gx=rbpr+rbpi/qb;   // base resistance commented out for now
            if(xjrb != 0) {
                double arg1=Math.max(cb/xjrb,1e-9);
                double arg2=(-1+Math.sqrt(1+14.59025*arg1))/2.4317/Math.sqrt(arg1);
                arg1=Math.tan(arg2);
                gx=rbpr+3*rbpi*(arg1-arg2)/arg2/arg1/arg1;
            }
            if(gx != 0) gx=1/gx;*/
            double gpi=gbe/beta+gben;
            double gmu=gbc/model.betaR+gbcn;
            double go=(gbc+(cex-cbc)*dqbdvc/qb)/qb;
            double gm=(gex-(cex-cbc)*dqbdve/qb)/qb-go;

            double ceqbe=pnp * (cc + cb - vbe * (gm + go + gpi) + vbc * go);
            double ceqbc=pnp * (-cc + vbe * (gm + go) - vbc * (gmu + go));

            if (Double.isInfinite(ib) || Double.isNaN(ic))
        	sim.stop("infinite transistor current", this);
            
            // stamp matrix.
	    // Node 0 is the base, node 1 the collector, node 2 the emitter.
	    sim.stampMatrix(nodes[1], nodes[1], gmu+go);
	    sim.stampMatrix(nodes[1], nodes[0], -gmu+gm);
	    sim.stampMatrix(nodes[1], nodes[2], -gm-go);
	    sim.stampMatrix(nodes[0], nodes[0], gpi+gmu);
	    sim.stampMatrix(nodes[0], nodes[2], -gpi);
	    sim.stampMatrix(nodes[0], nodes[1], -gmu);
	    sim.stampMatrix(nodes[2], nodes[0], -gpi-gm);
	    sim.stampMatrix(nodes[2], nodes[1], -go);
	    sim.stampMatrix(nodes[2], nodes[2], gpi+gm+go);

            /*
             *  load current excitation vector (right side)
             */
	    sim.stampRightSide(nodes[0], -ceqbe-ceqbc);
	    sim.stampRightSide(nodes[1], ceqbc);
	    sim.stampRightSide(nodes[2], ceqbe);

	}
	
	@Override String getScopeText(int x) {
	    String t ="";
	    switch (x) {
	    case Scope.VAL_IB: t = "Ib"; break; 
	    case Scope.VAL_IC: t = "Ic"; break;
	    case Scope.VAL_IE: t = "Ie"; break;
	    case Scope.VAL_VBE: t = "Vbe"; break;
	    case Scope.VAL_VBC: t = "Vbc"; break;
	    case Scope.VAL_VCE: t = "Vce"; break;
	    case Scope.VAL_POWER: t = "P"; break;
	    }
	    return Locale.LS("transistor") + ", " + t;
	}
	
	void getInfo(String arr[]) {
	    arr[0] = Locale.LS("transistor") + " (" + ((pnp == -1) ? "PNP" : "NPN") + ", " + model.name + ", \u03b2=" + showFormat.format(beta) + ")";
	    double vbc = volts[0]-volts[1];
	    double vbe = volts[0]-volts[2];
	    double vce = volts[1]-volts[2];
	    if (vbc*pnp > .2)
		arr[1] = vbe*pnp > .2 ? "saturation" : "reverse active";
	    else
		arr[1] = vbe*pnp > .2 ? "fwd active" : "cutoff";
	    arr[1] = Locale.LS(arr[1]);
	    arr[2] = "Ic = " + getCurrentText(ic);
	    arr[3] = "Ib = " + getCurrentText(ib);
	    arr[4] = "Vbe = " + getVoltageText(vbe);
	    arr[5] = "Vbc = " + getVoltageText(vbc);
	    arr[6] = "Vce = " + getVoltageText(vce);
	    arr[7] = "P = " + getUnitText(getPower(), "W");
	}
	
	double getScopeValue(int x) {
	    switch (x) {
	    case Scope.VAL_IB: return ib;
	    case Scope.VAL_IC: return ic;
	    case Scope.VAL_IE: return ie;
	    case Scope.VAL_VBE: return volts[0]-volts[2];
	    case Scope.VAL_VBC: return volts[0]-volts[1];
	    case Scope.VAL_VCE: return volts[1]-volts[2];
	    case Scope.VAL_POWER: return getPower(); 
	    }
	    return 0;
	}
	
	int getScopeUnits(int x) {
	    switch (x) {
	    case Scope.VAL_IB: case Scope.VAL_IC:
	    case Scope.VAL_IE: return Scope.UNITS_A;
	    case Scope.VAL_POWER: return Scope.UNITS_W;
	    default: return Scope.UNITS_V;
	    }
	}
	
	    Vector<TransistorModel> models;

	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Beta/hFE", beta, 10, 1000).
		    setDimensionless();
	    if (n == 1) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Swap E/C", (flags & FLAG_FLIP) != 0);
		return ei;
	    }
	    if (n == 2) {
		EditInfo ei = EditInfo.createCheckbox("Draw Circle", hasCircle());
		return ei;
	    }
	    if (n == 3) {
		EditInfo ei =  new EditInfo("Model", 0, -1, -1);
		models = TransistorModel.getModelList();
		ei.choice = new Choice();
		int i;
		for (i = 0; i != models.size(); i++) {
		    TransistorModel dm = models.get(i);
		    ei.choice.add(dm.getDescription());
		    if (dm == model)
			ei.choice.select(i);
		}
		return ei;
	    }
	    if (n == 4) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.button = new Button(Locale.LS("Create New Model"));
		return ei;
	    }
	    if (n == 5) {
		if (model.readOnly)
		    return null;
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.button = new Button(Locale.LS("Edit Model"));
		return ei;
	    }
	    return null;
	}
	
	    public void newModelCreated(TransistorModel tm) {
	        model = tm;
	        modelName = model.name;
	        setup();
	    }
	    

	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0) {
		beta = ei.value;
		setup();
	    }
	    if (n == 1) {
		if (ei.checkbox.getState())
		    flags |= FLAG_FLIP;
		else
		    flags &= ~FLAG_FLIP;
		setPoints();
	    }
	    if (n == 2) {
		globalFlags = ei.changeFlag(globalFlags, FLAG_CIRCLE);
		return;
	    }
	    if (n == 3) {
		model = models.get(ei.choice.getSelectedIndex());
		modelName = model.name;
		setup();
		ei.newDialog = true;
		return;
	    }
	    if (n == 4) {
		TransistorModel newModel = new TransistorModel(model);
		EditDialog editDialog = new EditTransistorModelDialog(newModel, sim, this);
		CirSim.diodeModelEditDialog = editDialog;
		editDialog.show();
		return;
	    }
	    if (n == 5) {
		if (model.readOnly) {
		    // probably never reached
		    Window.alert(Locale.LS("This model cannot be modified.  Change the model name to allow customization."));
		    return;
		}
		EditDialog editDialog = new EditTransistorModelDialog(model, sim, null);
		CirSim.diodeModelEditDialog = editDialog;
		editDialog.show();
		return;
	    }
	}
	
	void setBeta(double b) {
	    beta = b;
	    setup();
	}
	
        void stepFinished() {
            // stop for huge currents that make simulator act weird
            if (Math.abs(ic) > 1e12 || Math.abs(ib) > 1e12)
                sim.stop("max current exceeded", this);

            // if we needed to add a conductance to all junctions, this was a bad iteration.
            // If we have 5 of those in a row, give up
	    if (sim.subIterations > 100)
		badIters++;
	    else
		badIters = 0;
        }

	boolean canViewInScope() { return true; }
	
	double getCurrentIntoNode(int n) {
	    if (n==0)
		return -ib;
	    if (n==1)
		return -ic;
	    return -ie;
	}
    }
