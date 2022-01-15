package com.lushprojects.circuitjs1.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RadioButton;

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

	
Panel fp, channelButtonsp, channelSettingsp;
HorizontalPanel hp;
HorizontalPanel vModep;
CirSim sim;
//RichTextArea textBox;
TextArea textArea;
RadioButton autoButton, maxButton, manualButton;
RadioButton acButton, dcButton;
CheckBox scaleBox, voltageBox, currentBox, powerBox, peakBox, negPeakBox, freqBox, spectrumBox, manualScaleBox;
CheckBox rmsBox, dutyBox, viBox, xyBox, resistanceBox, ibBox, icBox, ieBox, vbeBox, vbcBox, vceBox, vceIcBox, logSpectrumBox, averageBox;
CheckBox elmInfoBox;
TextBox labelTextBox, manualScaleTextBox;
Button applyButton, scaleUpButton, scaleDownButton;
Scrollbar speedBar,positionBar;
Scope scope;
Grid grid, vScaleGrid, hScaleGrid;
int nx, ny;
Label scopeSpeedLabel, manualScaleLabel,vScaleList, manualScaleId, positionLabel;
expandingLabel vScaleLabel, hScaleLabel;
Vector <Button> chanButtons = new Vector <Button>();
int plotSelection = 0;
labelledGridManager gridLabels;
	
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
	    updateUi();
	}
    }
    
    class manualScaleTextHandler implements ValueChangeHandler<String> {
	
	public void onValueChange(ValueChangeEvent<String> event) {
	    apply();
	    updateUi();
	}
	
    }
    
    class downClickHandler implements ClickHandler{
	public downClickHandler() {
	}
	
	public void onClick(ClickEvent event) {
	    double lasts, s;
	if (!scope.isManualScale() || plotSelection>scope.visiblePlots.size())
		return;
	    double d = getManualScaleValue();
	    if (d==0)
		return;
	    d=d*0.999; // Go just below last check point
	    s=Scope.MIN_MAN_SCALE;
	    lasts=s;
	    for(int a=0; s<d; a++) { // Iterate until we go over the target and then use the last value
		lasts = s;
		s*=Scope.multa[a%3];
	    }
	    scope.setManualScaleValue(plotSelection, lasts);
	    updateUi();
	}
	
    }

    
    class upClickHandler implements ClickHandler{
	public upClickHandler() {
	}
	
	public void onClick(ClickEvent event) {
	    double  s;
	if (!scope.isManualScale() || plotSelection>scope.visiblePlots.size())
		return;
	    double d = getManualScaleValue();
	    if (d==0)
		return;
	    s=nextHighestScale(d);
	    scope.setManualScaleValue(plotSelection, s);
	    updateUi();
	}
	
    }
    
    static double nextHighestScale(double d) {
	    d=d*1.001; // Go just above last check point
	    double s;
	    s=Scope.MIN_MAN_SCALE;
	    for(int a=0; s<d; a++) { // Iterate until we go over the target
		s*=Scope.multa[a%3];
	    }
	    return s;
    }
    
    void positionBarChanged() {
	if (!scope.isManualScale() || plotSelection>scope.visiblePlots.size())
	    return;
	int p = positionBar.getValue();
	scope.setPlotPosition(plotSelection, p);
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
    
    class expandingLabel {
	HorizontalPanel p;
	Label l;
	Button b;
	Boolean expanded;
	
	expandingLabel(String s, Boolean ex) {
	    expanded = ex;
	    p = new HorizontalPanel();
	    b = new Button(ex?"-":"+");
	    b.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
		    expanded=!expanded;
		    b.setHTML(expanded?"-":"+");
		    updateUi();
		}
	    });
	    b.addStyleName("expand-but");
	    p.add(b);
	    l = new Label (s);
	    l.getElement().getStyle().setFontWeight(FontWeight.BOLD);
	    p.add(l);
	    p.setCellVerticalAlignment(l, HasVerticalAlignment.ALIGN_BOTTOM);
	}
	
    }

	public ScopePropertiesDialog ( CirSim asim, Scope s) {
		super();
		// We are going to try and keep the panel below the target height (defined to give some space)
		int allowedHeight = Window.getClientHeight()*4/5;
		boolean displayAll = allowedHeight > 600; // We can display everything as maximum height can be shown
		boolean displayScales = allowedHeight > 470; // We can display the scales and any one other section. So expand scales and collapse rest
		sim=asim;
		scope = s;
		Button okButton, applyButton2;
		fp=new FlowPanel();
		setWidget(fp);
		setText(CirSim.LS("Scope Properties"));

// *************** VERTICAL SCALE ***********************************************************
		Grid vSLG = new Grid(1,1); // Stupid grid to force labels to align without diving deep in to table CSS
		vScaleLabel = new expandingLabel(CirSim.LS("Vertical Scale"), displayScales);
		vSLG.setWidget(0,0,vScaleLabel.p);
		fp.add(vSLG);
		
				
		vModep = new HorizontalPanel();
		autoButton = new RadioButton("vMode", CirSim.LS("Auto"));
		autoButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	            public void onValueChange(ValueChangeEvent<Boolean> e) {
	        	scope.setManualScale(false, false);
	        	scope.setMaxScale(false);
	        	updateUi();
	            }
	        });
		maxButton = new RadioButton("vMode", CirSim.LS("Auto (Max Scale)"));
		maxButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	            public void onValueChange(ValueChangeEvent<Boolean> e) {
	        	scope.setManualScale(false, false);
	        	scope.setMaxScale(true);
	        	updateUi();
	            }
	        });
		manualButton = new RadioButton("vMode", CirSim.LS("Manual"));
		manualButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
	            public void onValueChange(ValueChangeEvent<Boolean> e) {
	        	scope.setManualScale(true, true);
	        	updateUi();
	            }
	        });
		vModep.add(autoButton);
		vModep.add(maxButton);
		vModep.add(manualButton);
		fp.add(vModep);
		channelSettingsp = new VerticalPanel();
		channelButtonsp = new FlowPanel();
		updateChannelButtons();
		channelSettingsp.add(channelButtonsp);
		fp.add(channelSettingsp);
		
		vScaleGrid = new Grid(3,5);
		dcButton= new RadioButton("acdc", CirSim.LS("DC Coupled"));
		dcButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
		    public void onValueChange(ValueChangeEvent<Boolean> e) {
		    if (plotSelection<scope.visiblePlots.size())
			scope.visiblePlots.get(plotSelection).setAcCoupled(false);
		    updateUi();
		    }
		});
		vScaleGrid.setWidget(0, 0, dcButton);
		acButton= new RadioButton("acdc", CirSim.LS("AC Coupled"));
		acButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
		    public void onValueChange(ValueChangeEvent<Boolean> e) {
		    if (plotSelection<scope.visiblePlots.size())
			scope.visiblePlots.get(plotSelection).setAcCoupled(true);
		    updateUi();
		    }
		});
		vScaleGrid.setWidget(0, 1, acButton);
		
		positionLabel= new Label(CirSim.LS("Position"));
		vScaleGrid.setWidget(1,0, positionLabel);
		vScaleGrid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		positionBar = new Scrollbar(Scrollbar.HORIZONTAL,0, 1, -Scope.V_POSITION_STEPS, Scope.V_POSITION_STEPS, new Command() {
		    public void execute() {
			positionBarChanged();
		    }
		});
		vScaleGrid.setWidget(1,1,positionBar);
		Button resetPosButton = new Button(CirSim.LS("Reset Position"));
		resetPosButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			    positionBar.setValue(0);
			    positionBarChanged();
			    updateUi();
			}
		});
		vScaleGrid.setWidget(1, 4, resetPosButton);

		manualScaleId = new Label();
		vScaleGrid.setWidget(2, 0, manualScaleId);
		Grid scaleBoxGrid=new Grid(1,3);
		scaleDownButton=new Button("&#9660;");
		scaleDownButton.addClickHandler(new downClickHandler());
		scaleBoxGrid.setWidget(0,0, scaleDownButton);
		manualScaleTextBox = new TextBox(); 
		manualScaleTextBox.addValueChangeHandler(new manualScaleTextHandler());
		manualScaleTextBox.addStyleName("scalebox");
		scaleBoxGrid.setWidget(0, 1, manualScaleTextBox);
		scaleUpButton=new Button("&#9650;");
		scaleUpButton.addClickHandler(new upClickHandler());
		scaleBoxGrid.setWidget(0,2,scaleUpButton);
		vScaleGrid.setWidget(2,1, scaleBoxGrid);
		manualScaleLabel = new Label("");
		vScaleGrid.setWidget(2,2, manualScaleLabel);
		vScaleGrid.setWidget(2,4, applyButton = new Button(CirSim.LS("Apply")));
		applyButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				apply();
			}
		});

		vScaleGrid.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		fp.add(vScaleGrid);

		// *************** HORIZONTAL SCALE ***********************************************************

		
		hScaleGrid = new Grid(2,4);
		hScaleLabel = new expandingLabel(CirSim.LS("Horizontal Scale"), displayScales);
		hScaleGrid.setWidget(0, 0, hScaleLabel.p);
		speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 2, 1, 0, 11, new Command() {
		    public void execute() {
			scrollbarChanged();
		    }
		});
		hScaleGrid.setWidget(1,0, speedBar);
		scopeSpeedLabel = new Label("");
		scopeSpeedLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hScaleGrid.setWidget(1, 1, scopeSpeedLabel);
		hScaleGrid.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_MIDDLE);

	//	speedGrid.getColumnFormatter().setWidth(0, "40%");
		fp.add(hScaleGrid);
		
		// *************** PLOTS ***********************************************************
		
		CircuitElm elm = scope.getSingleElm();
		boolean transistor = elm != null && elm instanceof TransistorElm;
		if (!transistor) {
		    grid = new Grid(11, 3);
		    gridLabels = new labelledGridManager(grid);
		    gridLabels.addLabel(CirSim.LS("Plots"), displayAll);
		    addItemToGrid(grid, voltageBox = new ScopeCheckBox(CirSim.LS("Show Voltage"), "showvoltage"));
		    voltageBox.addValueChangeHandler(this); 
		    addItemToGrid(grid, currentBox = new ScopeCheckBox(CirSim.LS("Show Current"), "showcurrent"));
		    currentBox.addValueChangeHandler(this);
		} else {
		    grid = new Grid(13,3);
		    gridLabels = new labelledGridManager(grid);
		    gridLabels.addLabel(CirSim.LS("Plots"), displayAll);
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
		
		gridLabels.addLabel(CirSim.LS("X-Y Plots"), displayAll);
		addItemToGrid(grid, viBox = new ScopeCheckBox(CirSim.LS("Show V vs I"), "showvvsi"));
		viBox.addValueChangeHandler(this); 
		addItemToGrid(grid, xyBox = new ScopeCheckBox(CirSim.LS("Plot X/Y"), "plotxy"));
		xyBox.addValueChangeHandler(this);
		if (transistor) {
		    addItemToGrid(grid, vceIcBox = new ScopeCheckBox(CirSim.LS("Show Vce vs Ic"), "showvcevsic"));
		    vceIcBox.addValueChangeHandler(this);
		}
		gridLabels.addLabel(CirSim.LS("Show Info"), displayAll);
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
		addItemToGrid(grid, elmInfoBox = new ScopeCheckBox(CirSim.LS("Show Extended Info"), "showelminfo"));
		elmInfoBox.addValueChangeHandler(this); 
		fp.add(grid);

		gridLabels.addLabel(CirSim.LS("Custom Label"), displayAll);
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
		
		updateUi();
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

	class labelledGridManager {
	    Grid g;
	    Vector <expandingLabel> labels;
	    Vector <Integer> labelRows;
	    
	    labelledGridManager(Grid gIn) {
		g=gIn;
		labels = new Vector <expandingLabel>();
		labelRows = new Vector <Integer>();
	    }
	    
	    void addLabel(String s, boolean e) {
        	    if (nx != 0)
        		ny++;
        	    nx = 0;
        	    expandingLabel l = new expandingLabel(CirSim.LS(s), e);
        	    g.setWidget(ny, nx, l.p);
        	    labels.add(l);
        	    labelRows.add(ny);
        	    ny++;
	    }
	    
	    void updateRowVisibility() {
		for (int i=0; i<labels.size(); i++) {
		    int end;
		    int start = labelRows.get(i);
		    if (i<labels.size()-1)
			end = labelRows.get(i+1);
		    else
			end = g.getRowCount();
		    for(int j=start+1; j<end; j++)
			g.getRowFormatter().setVisible(j, labels.get(i).expanded);
		}
	    }
	    
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
	
	void updateUi() {
	    vModep.setVisible(vScaleLabel.expanded);
	    gridLabels.updateRowVisibility();
	    hScaleGrid.getRowFormatter().setVisible(1, hScaleLabel.expanded);
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
	    dutyBox.setValue(scope.showDutyCycle);
	    elmInfoBox.setValue(scope.showElmInfo);
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
	    updateManualScaleUi();
	    
	    

	    // if you add more here, make sure it still works with transistor scopes
	}
	
	void updateManualScaleUi() {
	    updateChannelButtons();
	    channelSettingsp.setVisible(scope.isManualScale() && vScaleLabel.expanded);
	    vScaleGrid.setVisible(vScaleLabel.expanded);
	    if (vScaleLabel.expanded) { 
        	    vScaleGrid.getRowFormatter().setVisible(0, scope.isManualScale() && plotSelection<scope.visiblePlots.size() );
        	    vScaleGrid.getRowFormatter().setVisible(1, scope.isManualScale() && plotSelection<scope.visiblePlots.size() );
        	    vScaleGrid.getRowFormatter().setVisible(2, (!scope.isManualScale()) || plotSelection<scope.visiblePlots.size());
	    }
	    scaleUpButton.setVisible(scope.isManualScale());
	    scaleDownButton.setVisible(scope.isManualScale());
	    if (scope.isManualScale()) {
		if (plotSelection<scope.visiblePlots.size()) {
		    ScopePlot p = scope.visiblePlots.get(plotSelection);
		    manualScaleId.setText("CH "+String.valueOf(plotSelection+1)+" "+CirSim.LS("Scale"));
		    manualScaleLabel.setText(Scope.getScaleUnitsText(p.units)+CirSim.LS("/div"));
		    manualScaleTextBox.setText(EditDialog.unitString(null, p.manScale));
		    manualScaleTextBox.setEnabled(true);
		    positionLabel.setText("CH "+String.valueOf(plotSelection+1)+" "+CirSim.LS("Position"));
		    positionBar.setValue(p.manVPosition);
		    dcButton.setEnabled(true);
		    positionBar.enable();
		    dcButton.setValue(! p.isAcCoupled());
		    acButton.setEnabled(p.canAcCouple());
		    acButton.setValue(p.isAcCoupled());
		    
		} else {
		    manualScaleId.setText("");
		    manualScaleLabel.setText("");
		    manualScaleTextBox.setText("");
		    manualScaleTextBox.setEnabled(false);
		    positionLabel.setText("");
		    dcButton.setEnabled(false);
		    acButton.setEnabled(false);
		    positionBar.disable();
		    
		}
	    } else {
		manualScaleId.setText("");
		manualScaleLabel.setText(CirSim.LS("Max Value") + " (" + scope.getScaleUnitsText() + ")");
		manualScaleTextBox.setText(EditDialog.unitString(null, scope.getScaleValue()));
		manualScaleTextBox.setEnabled(false);
		positionLabel.setText("");
	    }
	    setScopeSpeedLabel();
	}
	
	void refreshDraw() {
	    // Redraw for every step of the simulation (the simulation may run in the background of this
	    // dialog and the scope may automatically rescale
	    if (! scope.isManualScale() )
		updateManualScaleUi();
	}
	
	protected void closeDialog()
	{
	    apply();
	    this.hide();
	}
	
	double getManualScaleValue()
	{
	    try {
		double d = EditDialog.parseUnits(manualScaleTextBox.getText());
		if (d< Scope.MIN_MAN_SCALE)
		    d= Scope.MIN_MAN_SCALE;
		return d;
	    } catch (Exception e) {
		return 0;
	    }
	}
	
	void apply() {
	    String label = labelTextBox.getText();
	    if (label.length() == 0)
		label = null;
	    scope.setText(label);
	    
	    if (scope.isManualScale()) {
		double d=getManualScaleValue();
		if (d>0)
		    scope.setManualScaleValue(plotSelection, d);
	    }
	}

	public void onValueChange(ValueChangeEvent<Boolean> event) {
	    ScopeCheckBox cb = (ScopeCheckBox) event.getSource();
	    scope.handleMenu(cb.menuCmd, cb.getValue());
	    updateUi();
	}


}
