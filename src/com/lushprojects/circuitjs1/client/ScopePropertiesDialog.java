package com.lushprojects.circuitjs1.client;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.CheckBox;

class ScopeCheckBox extends CheckBox {
    String menuCmd;
    
    ScopeCheckBox(String text, String menu) {
	super(text);
	menuCmd = menu;
    }
    
    void setValue(boolean x) {
	if (getValue() == x)
	    return;
	super.setValue(x);
    }
}

public class ScopePropertiesDialog extends DialogBox implements ValueChangeHandler<Boolean> {

	
Panel fp;
HorizontalPanel hp;
CirSim sim;
//RichTextArea textBox;
TextArea textArea;
CheckBox scaleBox, maxScaleBox, voltageBox, currentBox, powerBox, peakBox, negPeakBox, freqBox, spectrumBox;
CheckBox rmsBox, dutyBox, viBox, xyBox, resistanceBox, ibBox, icBox, ieBox, vbeBox, vbcBox, vceBox, vceIcBox;
Scrollbar speedBar;
Scope scope;
Grid grid;
int nx, ny;
	
	public ScopePropertiesDialog ( CirSim asim, Scope s) {
		super();
		sim=asim;
		scope = s;
		Button okButton;
		fp=new FlowPanel();
		setWidget(fp);
		setText(CirSim.LS("Scope Properties"));
		Label l = new Label(CirSim.LS("Scroll Speed"));
		l.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		fp.add(l);
		Command cmd = new Command() {
		    public void execute() {
			scrollbarChanged();
		    }
		};
		fp.add(speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 2, 1, 0, 11, cmd));

				
		CircuitElm elm = scope.getSingleElm();
		boolean transistor = elm != null && elm instanceof TransistorElm;
		if (!transistor) {
		    grid = new Grid(8, 3);
		    addLabelToGrid(grid,"Plots");
		    addItemToGrid(grid, voltageBox = new ScopeCheckBox(CirSim.LS("Show Voltage"), "showvoltage"));
		    voltageBox.addValueChangeHandler(this); 
		    addItemToGrid(grid, currentBox = new ScopeCheckBox(CirSim.LS("Show Current"), "showcurrent"));
		    currentBox.addValueChangeHandler(this);
		    addItemToGrid(grid, powerBox = new ScopeCheckBox(CirSim.LS("Show Power Consumed"), "showpower"));
		    powerBox.addValueChangeHandler(this); 
		} else {
		    grid = new Grid(9, 3);
		    addLabelToGrid(grid,"Plots");
		    addItemToGrid(grid, ibBox = new ScopeCheckBox(CirSim.LS("Show Ib"), "showib"));
		    ibBox.addValueChangeHandler(this);
		    addItemToGrid(grid, icBox = new ScopeCheckBox(CirSim.LS("Show Ic"), "showic"));
		    icBox.addValueChangeHandler(this);
		    addItemToGrid(grid, ieBox = new ScopeCheckBox(CirSim.LS("Show Ie"), "showie"));
		    ieBox.addValueChangeHandler(this);
		    addItemToGrid(grid, vbeBox = new ScopeCheckBox(CirSim.LS("Show Vbe"), "showvbe"));
		    vbeBox.addValueChangeHandler(this);
		    addItemToGrid(grid, vbcBox = new ScopeCheckBox(CirSim.LS("Show Vbc"), "showvbc"));
		    vbcBox.addValueChangeHandler(this);
		    addItemToGrid(grid, vceBox = new ScopeCheckBox(CirSim.LS("Show Vce"), "showvce"));
		    vceBox.addValueChangeHandler(this);
		}
		addItemToGrid(grid, resistanceBox = new ScopeCheckBox(CirSim.LS("Show Resistance"), "showresistance"));
		resistanceBox.addValueChangeHandler(this); 
		addItemToGrid(grid, spectrumBox = new ScopeCheckBox(CirSim.LS("Show Spectrum"), "showfft"));
		spectrumBox.addValueChangeHandler(this);

		addLabelToGrid(grid,"X-Y Plots");
		addItemToGrid(grid, viBox = new ScopeCheckBox(CirSim.LS("Show V vs I"), "showvvsi"));
		viBox.addValueChangeHandler(this); 
		addItemToGrid(grid, xyBox = new ScopeCheckBox(CirSim.LS("Plot X/Y"), "plotxy"));
		xyBox.addValueChangeHandler(this);
		if (transistor) {
		    addItemToGrid(grid, vceIcBox = new ScopeCheckBox(CirSim.LS("Show Vce vs Ic"), "showvcevsic"));
		    vceIcBox.addValueChangeHandler(this);
		}
		addLabelToGrid(grid, "Show Info");
		addItemToGrid(grid, scaleBox = new ScopeCheckBox(CirSim.LS("Show Scale"), "showscale"));
		scaleBox.addValueChangeHandler(this); 
		addItemToGrid(grid, peakBox = new ScopeCheckBox(CirSim.LS("Show Peak Value"), "showpeak"));
		peakBox.addValueChangeHandler(this); 
		addItemToGrid(grid, negPeakBox = new ScopeCheckBox(CirSim.LS("Show Negative Peak Value"), "shownegpeak"));
		negPeakBox.addValueChangeHandler(this); 
		addItemToGrid(grid, freqBox = new ScopeCheckBox(CirSim.LS("Show Frequency"), "showfreq"));
		freqBox.addValueChangeHandler(this); 
		addItemToGrid(grid, rmsBox = new ScopeCheckBox(CirSim.LS("Show RMS Average"), "showrms"));
		rmsBox.addValueChangeHandler(this); 
		addItemToGrid(grid, dutyBox = new ScopeCheckBox(CirSim.LS("Show Duty Cycle"), "showduty"));
		dutyBox.addValueChangeHandler(this); 
		fp.add(grid);

		
		updateUI();
		hp = new HorizontalPanel();
		fp.add(hp);
		hp.add(okButton = new Button(CirSim.LS("OK")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDialog();
			}
		});
		this.center();
		show();
	}

	
	
	void addLabelToGrid(Grid g, String s) {
	    if (nx !=0)
		ny++;
	    nx=0;
	    Label l = new Label(CirSim.LS(s));
	    l.getElement().getStyle().setFontWeight(FontWeight.BOLD);
	    g.setWidget(ny, nx, l);
	    ny++;
	    
	}
	
	void addItemToGrid(Grid g, CheckBox scb) {
	    g.setWidget(ny, nx, scb);
	    if (++nx >= grid.getColumnCount()) {
		nx = 0;
		ny++;
	    }
	}
	
	void scrollbarChanged() {
	    int newsp = (int)Math.pow(2,  10-speedBar.getValue());
	    CirSim.console("changed " + scope.speed + " " + newsp + " " + speedBar.getValue());
	    if (scope.speed != newsp)
		scope.setSpeed(newsp);
	}
	
	void updateUI() {
	    speedBar.setValue(10-(int)Math.round(Math.log(scope.speed)/Math.log(2)));
	    if (voltageBox != null) {
		voltageBox.setValue(scope.showV && !scope.showingValue(Scope.VAL_POWER));
		currentBox.setValue(scope.showI && !scope.showingValue(Scope.VAL_POWER));
		powerBox.setValue(scope.showingValue(Scope.VAL_POWER));
	    }
	    scaleBox.setValue(scope.showScale);
	    peakBox.setValue(scope.showMax);
	    negPeakBox.setValue(scope.showMin);
	    freqBox.setValue(scope.showFreq);
	    spectrumBox.setValue(scope.showFFT);
	    rmsBox.setValue(scope.showRMS);
	    rmsBox.setText(scope.canShowRMS() ? CirSim.LS("Show RMS Average") :
                					CirSim.LS("Show Average"));
	    viBox.setValue(scope.plot2d && !scope.plotXY);
	    xyBox.setValue(scope.plotXY);
	    resistanceBox.setValue(scope.showingValue(Scope.VAL_R));
	    resistanceBox.setEnabled(scope.canShowResistance());
	    if (vbeBox != null) {
                ibBox.setValue(scope.showingValue(Scope.VAL_IB));
                icBox.setValue(scope.showingValue(Scope.VAL_IC));
                ieBox.setValue(scope.showingValue(Scope.VAL_IE));
                vbeBox.setValue(scope.showingValue(Scope.VAL_VBE));
                vbcBox.setValue(scope.showingValue(Scope.VAL_VBC));
                vceBox.setValue(scope.showingValue(Scope.VAL_VCE));
                vceIcBox.setValue(scope.isShowingVceAndIc());
	    }
	}
	
	protected void closeDialog()
	{
		this.hide();
	}

	public void onValueChange(ValueChangeEvent<Boolean> event) {
	    ScopeCheckBox cb = (ScopeCheckBox) event.getSource();
	    scope.handleMenu(cb.menuCmd, cb.getValue());
	    updateUI();
	}


}
