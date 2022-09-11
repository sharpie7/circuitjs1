
package com.lushprojects.circuitjs1.client;

import com.google.gwt.user.client.ui.Label;
import com.lushprojects.circuitjs1.client.util.Locale;
import com.google.gwt.user.client.Command;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;

/*Bill Collis - June 2015

datasheet for Vishay thermistor ntcle100
thermistor model
view-source:http://www.giangrandi.ch/electronics/ntc/ntc.shtml
http://www.giangrandi.ch/electronics/ntc/ntcparam.html
no heating effects
ID = 350
add id to CirSim.constructElement and part to CirSim.createCe and to CirSimcomposeMainMenu
 */
class ThermistorNTCElm extends CircuitElm implements Command, MouseWheelHandler {
    double position; //of the slider 0.005 to 0.995
    double resistance; //based upon slider position
    double minTempr, maxTempr; //Celsius - note min is -40, max is +150 degC
    double temperature; //calculated from slider value (0.005 - 0.995) ratios of minTempr - maxTempr
    double r25, r50; //the values that a user can input from a datsheet r 225 degc and r at 50degC
    double rneg40; //maximum resistance - will be at -40 degC
    double b25100; // constant based upon 2 values of R for 2 temperatures
    double t0 = 273.15;
    double t25 = t0 + 25;

    Scrollbar slider; //from Pot
    Label label;
    String sliderText;

    //constructor - when initially created
    public ThermistorNTCElm(int xx, int yy) {
	super(xx, yy);
	//setup();
	minTempr = -40;//celsius
	maxTempr = 150; 
	r25 = 10000; //default 10k thermistor e.g. NTCLE100E3010 Vishay
	r50 = 3605;
	position = .34; //25 degC for -40 to 150 degC
	//thermistor calcs
	rneg40 = calcResistance(minTempr); //for 10k ntc about 400k	
	b25100 = calcB25100(); //	
	temperature = temprFromSliderPos();
	resistance = calcResistance(temperature); 
	sliderText = "Temperature";
	createSlider();
    }

    //constructor - when read in from file
    public ThermistorNTCElm(int xa, int ya, int xb, int yb, int f,
	    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	r25 = new Double(st.nextToken()).doubleValue();
	r50 = new Double(st.nextToken()).doubleValue();
	minTempr = new Double(st.nextToken()).doubleValue();
	maxTempr = new Double(st.nextToken()).doubleValue();
	position = new Double(st.nextToken()).doubleValue();
	//thermistor calcs
	rneg40 = calcResistance(minTempr); //for 10k ntc about 400k	
	b25100 = calcB25100(); //
	temperature = temprFromSliderPos();
	resistance = calcResistance(temperature); 
	sliderText = CustomLogicModel.unescape(st.nextToken());
	createSlider(); //uses position to set the slider
    }

    //void setup() {
    //}

    int getPostCount() { return 2; }
    int getDumpType() { return 350; } //NTC thermistor

    //data for file saving - make sure it matches order of items in file input constructor
    String dump() { 
	return super.dump() + " " + r25 + " " + r50 + " " + minTempr + " " + maxTempr +" " + position  + " " + CustomLogicModel.escape(sliderText); 
    }

    void createSlider() {
	sim.addWidgetToVerticalPanel(label = new Label(sliderText));
	label.addStyleName("topSpace");
	int value = (int) (position*100);
	sim.addWidgetToVerticalPanel(slider = new Scrollbar(Scrollbar.HORIZONTAL, value, 1, 0, 101, this, this));
    }

    public void execute() {
	sim.analyzeFlag = true;
	setPoints();
    }

    void delete() {
	sim.removeWidgetFromVerticalPanel(label);
	sim.removeWidgetFromVerticalPanel(slider);
    }
    Point ps3, ps4;   

    //called straight after constructor when txt file is loaded
    void setPoints() {
	super.setPoints();
	calcLeads(32);
	position = slider.getValue()*.0099+.005;
	temperature = temprFromSliderPos();
	resistance = calcResistance(temperature); 
	ps3 = new Point();
	ps4 = new Point();
    }

    void draw(Graphics g) { //used Resistor draw
	//int segments = 16;
	int i;
	//int ox = 0;
	int hs=6; //is this a width 
	double v1 = volts[0];
	double v2 = volts[1];
	setBbox(point1, point2, hs); //the two points that are there when the device is being created
	draw2Leads(g); //from point1 to lead1 and lead1 to point2 (lead1&2 are on the body) 
	setPowerColor(g, true);
	double len = distance(lead1, lead2);
	g.context.save();
	g.context.setLineWidth(3.0);
	g.context.transform(((double)(lead2.x-lead1.x))/len, ((double)(lead2.y-lead1.y))/len, -((double)(lead2.y-lead1.y))/len,((double)(lead2.x-lead1.x))/len,lead1.x,lead1.y);
	CanvasGradient grad = g.context.createLinearGradient(0,0,len,0);
	grad.addColorStop(0, getVoltageColor(g,v1).getHexValue());
	grad.addColorStop(1.0, getVoltageColor(g,v2).getHexValue());
	g.context.setStrokeStyle(grad);
	if (!sim.euroResistorCheckItem.getState()) {
	    g.context.beginPath();
	    g.context.moveTo(0,0);
	    for (i=0;i<4;i++){
		g.context.lineTo((1+4*i)*len/16, hs);
		g.context.lineTo((3+4*i)*len/16, -hs);
	    }
	    g.context.lineTo(len, 0);
	    g.context.stroke();

	} else    {
	    g.context.strokeRect(0, -hs, len, 2.0*hs); //draw the box for the euro resistor
	}

	g.context.beginPath(); //thermistor symbol lines 0 is in the middle of the left handside of the resistor box
	g.context.moveTo(0-hs,hs*2);
	g.context.lineTo(hs,hs*2);
	g.context.lineTo(len,-hs*2);
	g.context.stroke();


	g.context.restore();
	if (sim.showValuesCheckItem.getState()) {
	    temperature = temprFromSliderPos();
	    resistance = calcResistance(temperature);
	    String s = getShortUnitText(resistance, "");
	    String t = Character.toString((char)176);
	    //drawValues(g, "-t:"+s, hs);
	    drawValues(g, temperature+t+"C="+s+"\u03A9", hs);
	}
	doDots(g);
	drawPosts(g);
    }

    void calculateCurrent() {
	current = (volts[0]-volts[1])/resistance;
    }
    void stamp() {
	temperature = temprFromSliderPos(); //e.g. 190 - 40 for range -40 to +150
	resistance = calcResistance(temperature); 
	sim.stampResistor(nodes[0], nodes[1], resistance); //show temperature as well??
    }

    void getInfo(String arr[]) {
	arr[0] = "thermistor";
	arr[1] = "I = "+ getCurrentDText(current); //getBasicInfo(arr);
	arr[2] = "Vd = "+ getVoltageDText(getVoltageDiff());
	arr[3] = "R = " + getUnitText(resistance, Locale.ohmString);
	arr[4] = "P = " + getUnitText(getPower(), "W");
	arr[5] = "T = " + getUnitText(temperature, "\u00b0C");
    }
    public EditInfo getEditInfo(int n) {
	// ohmString doesn't work here on linux
	if (n == 0)
	    return new EditInfo("R at 25\u00b0C", r25, r50+100, 100000); //limits: r25 must be > r50
	if (n == 1)
	    return new EditInfo("R at 50\u00b0C", r50, 100, r25-100);
	if (n == 2)
	    return new EditInfo("Slider min temp (\u00b0C)", minTempr, -40, maxTempr); //limits: maxTempr must be > minTempr
	if (n == 3)
	    return new EditInfo("Slider max temp (\u00b0C)", maxTempr, minTempr, 150);
	if (n == 4) {
	    EditInfo ei = new EditInfo("Slider Text", 0, -1, -1);
	    ei.text = sliderText;
	    return ei;
	}
	return null;
    }
    //component edited
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    r25 = ei.value; 
	if (n == 1)
	    r50 = ei.value; 
	if (n == 2)
	    minTempr = ei.value; 
	if (n == 3)
	    maxTempr = ei.value; 
	if (n == 4) {
	    sliderText = ei.textf.getText();
	    label.setText(sliderText);
	    sim.setiFrameHeight();
	}
	rneg40 = calcResistance(minTempr);
	b25100 = calcB25100(); //	
	temperature = temprFromSliderPos();
	resistance = calcResistance(temperature); 
    }
    void setMouseElm(boolean v) {
	super.setMouseElm(v);
	if (slider!=null)
	    slider.draw();
    }

    public void onMouseWheel(MouseWheelEvent e) {
	if (slider!=null)
	    slider.onMouseWheel(e);
    }

    double calcResistance(double tempr) //knowing the temperature
    {
	return Math.round(r25 * Math.exp(b25100 * ((1 / (tempr + t0)) - (1 / t25))));
    }
    double temprFromSliderPos() //knowing slider position etc
    {
	return Math.round( position * (maxTempr - minTempr) + minTempr);
    }
    //determine constant B25100 - when knowing two R values at two temperatures
    double calcB25100() //given R25=10000 and R50=3605  B25100 will be 3932
    {
	double kelvin1 = t0 + 25;
	double kelvin2 = t0 + 50;
	return ( Math.log(r25) - Math.log(r50) ) / ( (1/kelvin1) - (1/kelvin2) );

    }
}

