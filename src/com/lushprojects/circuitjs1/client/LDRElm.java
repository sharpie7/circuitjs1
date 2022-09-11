package com.lushprojects.circuitjs1.client;

import com.google.gwt.user.client.ui.Label;
import com.lushprojects.circuitjs1.client.util.Locale;
import com.google.gwt.user.client.Command;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;

/*Bill Collis - June 2015 */

class LDRElm extends CircuitElm implements Command, MouseWheelHandler {
    double position; //of the slider 0.005 to 0.995
    double resistance; //based upon slider position
    double minLux, maxLux;
    double lux;

    Scrollbar slider; 
    Label label;
    String sliderText;

    //constructor - when initially created
    public LDRElm(int xx, int yy) {
	super(xx, yy);
	//setup();
	minLux = 0.1; //dark
	maxLux = 10000; // sunlight
	position = .34; 

	lux = LuxFromSliderPos();
	resistance = calcResistance(lux); 
	sliderText = "Light Brightness";
	createSlider();
    }

    //constructor - when read in from file
    public LDRElm(int xa, int ya, int xb, int yb, int f,
	    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	minLux = 0.1; //dark
	maxLux = 10000; // sunlight
	position = new Double(st.nextToken()).doubleValue();
	lux = LuxFromSliderPos();
	resistance = calcResistance(lux); 
	sliderText = CustomLogicModel.unescape(st.nextToken());
	createSlider(); //uses position to set the slider   
    }

    //void setup() {
    //}

    int getPostCount() { return 2; }
    int getDumpType() { return 374; } //LDR

    //data for file saving - make sure it matches order of items in file input constructor
    String dump() { 
	return super.dump() + " " + position  + " " + CustomLogicModel.escape(sliderText); 
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
	position = slider.getValue()*.0099+.0001;
	lux = LuxFromSliderPos();
	resistance = calcResistance(lux); 
	ps3 = new Point();
	ps4 = new Point();
    }
    Polygon arrowPoly;
    void draw(Graphics g) { //used Resistor draw
	//int segments = 16;
	int i;
	//int ox = 0;
	int hs=6; //width 
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
	//upper arrow
	g.context.moveTo(-8,26);   //arrow1 start   (y,x coordinates from center?)
	g.context.lineTo(8,12);		//arrow end point   
	g.context.moveTo(2,12);  	//arrow 1 head
	g.context.lineTo(8,12);		//arrow end point
	g.context.lineTo(8,18);	
	g.context.moveTo(12,26);   //arrow2 start   (y,x coordinates from center?)
	g.context.lineTo(26,12);		//arrow end point   
	g.context.moveTo(20,12);  	//arrow 1 head
	g.context.lineTo(26,12);		//arrow end point
	g.context.lineTo(26,18);	

	g.context.stroke();


	g.context.restore();
	if (sim.showValuesCheckItem.getState()) {
	    lux = LuxFromSliderPos();
	    resistance = calcResistance(lux);
	    String s = getShortUnitText(resistance, "");
	    drawValues(g, s+"\u03A9", hs);
	}
	doDots(g);
	drawPosts(g);
    }

    void calculateCurrent() {
	current = (volts[0]-volts[1])/resistance;
    }
    void stamp() {
	lux = LuxFromSliderPos();
	resistance = calcResistance(lux); 
	sim.stampResistor(nodes[0], nodes[1], resistance); 
    }

    void getInfo(String arr[]) {
	arr[0] = "photoresistor";
	arr[1] = "I = "+ getCurrentDText(current); //getBasicInfo(arr);
	arr[2] = "Vd = "+ getVoltageDText(getVoltageDiff());
	arr[3] = "R = " + getUnitText(resistance, Locale.ohmString);
	arr[4] = "P = " + getUnitText(getPower(), "W");
    }
    public EditInfo getEditInfo(int n) {
	if (n == 0) {
	    EditInfo ei = new EditInfo("Slider Text", 0, -1, -1);
	    ei.text = sliderText;
	    return ei;
	}
	return null;
    }
    //component edited
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0) {
	    sliderText = ei.textf.getText();
	    label.setText(sliderText);
	    sim.setiFrameHeight();
	}
	lux = LuxFromSliderPos();
	resistance = calcResistance(lux); 
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

    double calcResistance(double lux) //knowing the lux
    {
	//double loglux = Math.log10(lux);
	//double slope = -1.4;
	//double intercept = 7.1;
	//double logR = 	(loglux-intercept)/slope;

	//return Math.round(Math.pow(10, logR));
	double r = (maxLux-lux+1)*10;

	r = Math.round(r);
	return r;
    }
    double LuxFromSliderPos() //knowing slider position etc
    {
	return maxLux * position + minLux ;
    }

}

