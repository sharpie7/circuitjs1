package com.lushprojects.circuitjs1.client;

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
import com.google.gwt.user.client.ui.VerticalPanel;
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

	
Panel vp;
HorizontalPanel hp;
CirSim sim;
//RichTextArea textBox;
TextArea textArea;
CheckBox scaleBox, maxScaleBox, voltageBox, currentBox, powerBox, peakBox, negPeakBox, freqBox, spectrumBox;
CheckBox rmsBox, dutyBox, viBox, xyBox, resistanceBox, ibBox, icBox, ieBox, vbeBox, vbcBox, vceBox, vceIcBox;
Scrollbar speedBar;
Scope scope;
Grid grid;
	
	public ScopePropertiesDialog ( CirSim asim, Scope s) {
		super();
		sim=asim;
		scope = s;
		Button okButton, cancelButton;
//		vp=new VerticalPanel();
		vp=new FlowPanel();
		setWidget(vp);
		setText(sim.LS("Scope Properties"));
//		vp.add(new Label(sim.LS("Paste the text file for your circuit here...")));
//		vp.add(textBox = new RichTextArea());
		vp.add(new Label(sim.LS("Scroll Speed")));
		Command cmd = new Command() {
		    public void execute() {
			scrollbarChanged();
		    }
		};
		vp.add(speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 2, 1, 0, 11, cmd));
		CheckBox cb;
//		vp.add(maxScaleBox = new ScopeCheckBox(sim.LS("Max Scale"), "maxscale"));  // maxscale is a command in 2d
//		maxScaleBox.addValueChangeHandler(this);
				
		CircuitElm elm = scope.getSingleElm();
		boolean transistor = elm != null && elm instanceof TransistorElm;
		if (!transistor) {
		    grid = new Grid(5, 3);
		    addItem(voltageBox = new ScopeCheckBox(sim.LS("Show Voltage"), "showvoltage"));
		    voltageBox.addValueChangeHandler(this); 
		    addItem(currentBox = new ScopeCheckBox(sim.LS("Show Current"), "showcurrent"));
		    currentBox.addValueChangeHandler(this);
		    addItem(powerBox = new ScopeCheckBox(sim.LS("Show Power Consumed"), "showpower"));
		    powerBox.addValueChangeHandler(this); 
		} else {
		    grid = new Grid(6, 3);
		    addItem(ibBox = new ScopeCheckBox(sim.LS("Show Ib"), "showib"));
		    ibBox.addValueChangeHandler(this);
		    addItem(icBox = new ScopeCheckBox(sim.LS("Show Ic"), "showic"));
		    icBox.addValueChangeHandler(this);
		    addItem(ieBox = new ScopeCheckBox(sim.LS("Show Ie"), "showie"));
		    ieBox.addValueChangeHandler(this);
		    addItem(vbeBox = new ScopeCheckBox(sim.LS("Show Vbe"), "showvbe"));
		    vbeBox.addValueChangeHandler(this);
		    addItem(vbcBox = new ScopeCheckBox(sim.LS("Show Vbc"), "showvbc"));
		    vbcBox.addValueChangeHandler(this);
		    addItem(vceBox = new ScopeCheckBox(sim.LS("Show Vce"), "showvce"));
		    vceBox.addValueChangeHandler(this);
		    addItem(vceIcBox = new ScopeCheckBox(sim.LS("Show Vce vs Ic"), "showvcevsic"));
		    vceIcBox.addValueChangeHandler(this);
		}
		addItem(scaleBox = new ScopeCheckBox(sim.LS("Show Scale"), "showscale"));
		scaleBox.addValueChangeHandler(this); 
		addItem(peakBox = new ScopeCheckBox(sim.LS("Show Peak Value"), "showpeak"));
		peakBox.addValueChangeHandler(this); 
		addItem(negPeakBox = new ScopeCheckBox(sim.LS("Show Negative Peak Value"), "shownegpeak"));
		negPeakBox.addValueChangeHandler(this); 
		addItem(freqBox = new ScopeCheckBox(sim.LS("Show Frequency"), "showfreq"));
		freqBox.addValueChangeHandler(this); 
		addItem(spectrumBox = new ScopeCheckBox(sim.LS("Show Spectrum"), "showfft"));
		spectrumBox.addValueChangeHandler(this); 
		addItem(rmsBox = new ScopeCheckBox(sim.LS("Show RMS Average"), "showrms"));
		rmsBox.addValueChangeHandler(this); 
		addItem(dutyBox = new ScopeCheckBox(sim.LS("Show Duty Cycle"), "showduty"));
		dutyBox.addValueChangeHandler(this); 
		addItem(viBox = new ScopeCheckBox(sim.LS("Show V vs I"), "showvvsi"));
		viBox.addValueChangeHandler(this); 
		addItem(xyBox = new ScopeCheckBox(sim.LS("Plot X/Y"), "plotxy"));
		xyBox.addValueChangeHandler(this);
		addItem(resistanceBox = new ScopeCheckBox(sim.LS("Show Resistance"), "showresistance"));
		resistanceBox.addValueChangeHandler(this); 
		vp.add(grid);
		updateUI();
		hp = new HorizontalPanel();
		vp.add(hp);
		hp.add(okButton = new Button(sim.LS("OK")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDialog();
			}
		});
		this.center();
		show();
	}

	int nx, ny;
	
	void addItem(CheckBox scb) {
	    grid.setWidget(ny, nx, scb);
	    if (++nx >= grid.getColumnCount()) {
		nx = 0;
		ny++;
	    }
	}
	
	void scrollbarChanged() {
	    int newsp = (int)Math.pow(2,  10-speedBar.getValue());
	    sim.console("changed " + scope.speed + " " + newsp + " " + speedBar.getValue());
	    if (scope.speed != newsp)
		scope.setSpeed(newsp);
	}
	
	void updateUI() {
	    speedBar.setValue(10-(int)Math.round(Math.log(scope.speed)/Math.log(2)));
	    if (voltageBox != null) {
		voltageBox.setValue(scope.showV && !scope.showingValue(scope.VAL_POWER));
		currentBox.setValue(scope.showI && !scope.showingValue(scope.VAL_POWER));
		powerBox.setValue(scope.showingValue(Scope.VAL_POWER));
	    }
	    scaleBox.setValue(scope.showScale);
	    peakBox.setValue(scope.showMax);
	    negPeakBox.setValue(scope.showMin);
	    freqBox.setValue(scope.showFreq);
	    spectrumBox.setValue(scope.showFFT);
	    rmsBox.setValue(scope.showRMS);
	    rmsBox.setText(scope.canShowRMS() ? sim.LS("Show RMS Average") :
                					sim.LS("Show Average"));
	    viBox.setValue(scope.plot2d && !scope.plotXY);
	    xyBox.setValue(scope.plotXY);
	    resistanceBox.setValue(scope.showingValue(scope.VAL_R));
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
