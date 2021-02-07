package com.lushprojects.circuitjs1.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.ListBox;

import java.util.Vector;

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

	
Panel fp, channelButtonsp;
HorizontalPanel hp;
CirSim sim;
//RichTextArea textBox;
TextArea textArea;
RadioButton autoButton, maxButton, manualButton;
CheckBox scaleBox, voltageBox, currentBox, powerBox, peakBox, negPeakBox, freqBox, spectrumBox, manualScaleBox;
CheckBox rmsBox, dutyBox, viBox, xyBox, resistanceBox, ibBox, icBox, ieBox, vbeBox, vbcBox, vceBox, vceIcBox, logSpectrumBox, averageBox;
TextBox labelTextBox, manualScaleTextBox;
Button applyButton;
Scrollbar speedBar;
Scope scope;
Grid grid, vScaleGrid, hScaleGrid;
int nx, ny;
Label scopeSpeedLabel, manualScaleLabel, vScaleLabel,vScaleList, manualScaleId;
Vector <Button> chanButtons = new Vector <Button>();
int plotSelection = 0;
	
    class PlotClickHandler implements ClickHandler {
	int num;

	public PlotClickHandler(int n) {
	    num = n;
	}

	public void onClick(ClickEvent event) {
	    plotSelection = num;
	    for (int i =0; i < chanButtons.size(); i++) {
		if (i==num)
		    chanButtons.get(i).addStyleName("chsel");
		else
		    chanButtons.get(i).removeStyleName("chsel");
	    }
	    updateUI();
	}
    }
    
    class manualScaleTextHandler implements ValueChangeHandler<String> {
	
	public void onValueChange(ValueChangeEvent<String> event) {
	    apply();
	    updateUI();
	}
	
    }

    
    String getChannelButtonLabel(int i) {
	    ScopePlot p = scope.visiblePlots.get(i);
	    String l = "<span style=\"color: "+p.color+";\">&#x25CF;</span>&nbsp;CH "+String.valueOf(i+1);
	    switch (p.units) {
	    	case Scope.UNITS_V: 
	    	    l += " (V)";
	    	    break;
	    	case Scope.UNITS_A:
	    	    l += " (I)";
	    	    break;
	    	case Scope.UNITS_OHMS:
	    	    l += " (R)";
	    	    break;
	    	case Scope.UNITS_W:
	    	    l += " (P)";
	    	    break;
	    }
	    return l;
	
    }
    
    void updateChannelButtons() {
	if (plotSelection >= scope.visiblePlots.size())
	    plotSelection = 0;
	// More buttons than plots - remove extra buttons
	for (int i = chanButtons.size()-1; i >= scope.visiblePlots.size(); i--) {
	    channelButtonsp.remove(chanButtons.get(i));
	    chanButtons.remove(i);
	}
	// Now go though all the channels, adding new buttons if necessary
	for (int i=0; i<scope.visiblePlots.size(); i++) {
	    if (i>=chanButtons.size()) {
		Button b = new Button();
		chanButtons.add(b);
		chanButtons.get(i).addClickHandler(new PlotClickHandler(i));
		b.addStyleName("chbut");
		if (CircuitElm.whiteColor == Color.white)
			b.addStyleName("chbut-black");
		    else
			b.addStyleName("chbut-white");
		channelButtonsp.add(b);
	    }
	    Button b = chanButtons.get(i);
	    b.setHTML(getChannelButtonLabel(i));
	    if (i==plotSelection)
		b.addStyleName("chsel");
	    else
		b.removeStyleName("chsel");
	}
    }

	public ScopePropertiesDialog ( CirSim asim, Scope s) {
		super();
		HorizontalPanel vModeP;
		sim=asim;
		scope = s;
		Button okButton, applyButton2;
		fp=new FlowPanel();
		setWidget(fp);
		setText(CirSim.LS("Scope Properties"));
		Command cmd = new Command() {
		    public void execute() {
			scrollbarChanged();
		    }
		};
// *************** VERTICAL SCALE ***********************************************************
		vScaleLabel = new Label (CirSim.LS("Vertical Scale"));
		vScaleLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);

		fp.add(vScaleLabel);
//		vScaleList = new Label("");
//		fp.add(vScaleList);
		
				
		vModeP = new HorizontalPanel();
		vModeP.setStyleName("radioPanel");
		autoButton = new RadioButton("vMode", CirSim.LS("Auto"));
		autoButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	            public void onValueChange(ValueChangeEvent<Boolean> e) {
	        	scope.setManualScale(false);
	        	scope.setMaxScale(false);
	        	updateUI();
	            }
	        });
		maxButton = new RadioButton("vMode", CirSim.LS("Auto (Max Scale)"));
		maxButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	            public void onValueChange(ValueChangeEvent<Boolean> e) {
	        	scope.setManualScale(false);
	        	scope.setMaxScale(true);
	        	updateUI();
	            }
	        });
		manualButton = new RadioButton("vMode", CirSim.LS("Manual"));
		manualButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	            public void onValueChange(ValueChangeEvent<Boolean> e) {
	        	scope.setManualScale(true);
	        	updateUI();
	            }
	        });
		vModeP.add(autoButton);
		vModeP.add(maxButton);
		vModeP.add(manualButton);
		fp.add(vModeP);
		channelButtonsp = new FlowPanel();
		channelButtonsp.setVisible(scope.isManualScale());
		updateChannelButtons();
		
		fp.add(channelButtonsp);
		vScaleGrid = new Grid(1,5);
		manualScaleId = new Label();
		vScaleGrid.setWidget(0, 0, manualScaleId);
		manualScaleTextBox = new TextBox(); 
		manualScaleTextBox.addValueChangeHandler(new manualScaleTextHandler());
		vScaleGrid.setWidget(0,1, manualScaleTextBox);
		manualScaleLabel = new Label("");
		vScaleGrid.setWidget(0,2, manualScaleLabel);
		vScaleGrid.setWidget(0,3, applyButton = new Button(CirSim.LS("Apply")));
		applyButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				apply();
			}
		});
		vScaleGrid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		fp.add(vScaleGrid);

		
		hScaleGrid = new Grid(2,4);
		Label l = new Label(CirSim.LS("Horizontal Scale"));
		l.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		hScaleGrid.setWidget(0, 0, l);
		speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 2, 1, 0, 11, cmd);
		hScaleGrid.setWidget(1,0, speedBar);
		scopeSpeedLabel = new Label("");
		scopeSpeedLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hScaleGrid.setWidget(1, 1, scopeSpeedLabel);
		hScaleGrid.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_MIDDLE);

	//	speedGrid.getColumnFormatter().setWidth(0, "40%");
		fp.add(hScaleGrid);
		

				
		CircuitElm elm = scope.getSingleElm();
		boolean transistor = elm != null && elm instanceof TransistorElm;
		if (!transistor) {
		    grid = new Grid(11, 3);
		    addLabelToGrid(grid,"Plots");
		    addItemToGrid(grid, voltageBox = new ScopeCheckBox(CirSim.LS("Show Voltage"), "showvoltage"));
		    voltageBox.addValueChangeHandler(this); 
		    addItemToGrid(grid, currentBox = new ScopeCheckBox(CirSim.LS("Show Current"), "showcurrent"));
		    currentBox.addValueChangeHandler(this);
		} else {
		    grid = new Grid(13,3);
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
		addItemToGrid(grid, powerBox = new ScopeCheckBox(CirSim.LS("Show Power Consumed"), "showpower"));
		powerBox.addValueChangeHandler(this); 
		addItemToGrid(grid, resistanceBox = new ScopeCheckBox(CirSim.LS("Show Resistance"), "showresistance"));
		resistanceBox.addValueChangeHandler(this); 
		addItemToGrid(grid, spectrumBox = new ScopeCheckBox(CirSim.LS("Show Spectrum"), "showfft"));
		spectrumBox.addValueChangeHandler(this);
		addItemToGrid(grid, logSpectrumBox = new ScopeCheckBox(CirSim.LS("Log Spectrum"), "logspectrum"));
		logSpectrumBox.addValueChangeHandler(this);
		
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
		addItemToGrid(grid, averageBox = new ScopeCheckBox(CirSim.LS("Show Average"), "showaverage"));
		averageBox.addValueChangeHandler(this); 
		addItemToGrid(grid, rmsBox = new ScopeCheckBox(CirSim.LS("Show RMS Average"), "showrms"));
		rmsBox.addValueChangeHandler(this); 
		addItemToGrid(grid, dutyBox = new ScopeCheckBox(CirSim.LS("Show Duty Cycle"), "showduty"));
		dutyBox.addValueChangeHandler(this); 
		fp.add(grid);

		addLabelToGrid(grid, CirSim.LS("Custom Label"));
		labelTextBox = new TextBox();
		addItemToGrid(grid, labelTextBox);
		String labelText = scope.getText();
		if (labelText != null)
		    labelTextBox.setText(labelText);
		addItemToGrid(grid, applyButton2= new Button(CirSim.LS("Apply")));
		applyButton2.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				apply();
			}
		});
		
		updateUI();
		hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hp.setStyleName("topSpace");
		fp.add(hp);
		hp.add(okButton = new Button(CirSim.LS("OK")));
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDialog();
			}
		});

//		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		Button saveAsDefaultButton;
		hp.add(saveAsDefaultButton = new Button(CirSim.LS("Save as Default")));
		saveAsDefaultButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				scope.saveAsDefault();
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
	
	void setScopeSpeedLabel() {
	    scopeSpeedLabel.setText(CircuitElm.getUnitText(scope.calcGridStepX(), "s")+"/div");
	}
	
	void addItemToGrid(Grid g, FocusWidget scb) {
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
	    setScopeSpeedLabel();
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
	    logSpectrumBox.setValue(scope.logSpectrum);
	    rmsBox.setValue(scope.showRMS);
	    averageBox.setValue(scope.showAverage);
	    rmsBox.setEnabled(scope.canShowRMS());
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
	    if (scope.isManualScale()) {
		manualButton.setValue(true);
		autoButton.setValue(false);
		maxButton.setValue(false);
		applyButton.setVisible(true);
	    }
	    else {
		manualButton.setValue(false);
		autoButton.setValue(! scope.maxScale);
		maxButton.setValue(scope.maxScale);
		applyButton.setVisible(false);
	    }
	    updateChannelButtons();
	    channelButtonsp.setVisible(scope.isManualScale());
	    updateScaleTextBox();
	    
	    

	    // if you add more here, make sure it still works with transistor scopes
	}
	
	void updateScaleTextBox() {
	    if (scope.isManualScale()) {
		if (plotSelection<scope.visiblePlots.size()) {
		    manualScaleId.setText("CH "+String.valueOf(plotSelection+1));
		    manualScaleLabel.setText(Scope.getScaleUnitsText(scope.visiblePlots.get(plotSelection).units)+CirSim.LS("/div"));
		    manualScaleTextBox.setText(EditDialog.unitString(null, scope.visiblePlots.get(plotSelection).manScale));
		    manualScaleTextBox.setEnabled(true);
		} else {
		    manualScaleId.setText("");
		    manualScaleLabel.setText("");
		    manualScaleTextBox.setText("");
		    manualScaleTextBox.setEnabled(false);
		}
	    } else {
		manualScaleId.setText("");
		manualScaleLabel.setText(CirSim.LS("Max Value") + " (" + scope.getScaleUnitsText() + ")");
		manualScaleTextBox.setText(EditDialog.unitString(null, scope.getScaleValue()));
		manualScaleTextBox.setEnabled(false);
	    }
	    setScopeSpeedLabel();
	}
	
	void refreshDraw() {
	    // Redraw for every step of the simulation (the simulation may run in the background of this
	    // dialog and the scope may automatically rescale
	    if (! scope.isManualScale() )
		updateScaleTextBox();
	}
	
	protected void closeDialog()
	{
	    apply();
	    this.hide();
	}
	
	void apply() {
	    String label = labelTextBox.getText();
	    if (label.length() == 0)
		label = null;
	    scope.setText(label);

	    try {
		double d = EditDialog.parseUnits(manualScaleTextBox.getText());
		scope.setManualScaleValue(d);
	    } catch (Exception e) {}
	}

	public void onValueChange(ValueChangeEvent<Boolean> event) {
	    ScopeCheckBox cb = (ScopeCheckBox) event.getSource();
	    scope.handleMenu(cb.menuCmd, cb.getValue());
	    updateUI();
	}


}
