package com.lushprojects.circuitjs1.client;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;

public class AudioOutputElm extends CircuitElm {
    int dataCount, dataPtr;
    double data[];
    boolean dataFull;
    Button button;
    int samplingRate;
    int labelNum;
    double duration;
    double sampleStep;
    double dataStart;
    static int lastSamplingRate = 8000;
    static boolean okToChangeTimeStep;
    
	public AudioOutputElm(int xx, int yy) {
	    super(xx, yy);
	    duration = 1;
	    samplingRate = lastSamplingRate;
	    labelNum = getNextLabelNum();
	    setDataCount();
	    createButton();
	}
	public AudioOutputElm(int xa, int ya, int xb, int yb, int f,
			 StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    duration = Double.parseDouble(st.nextToken());
	    samplingRate = Integer.parseInt(st.nextToken());
	    labelNum = Integer.parseInt(st.nextToken());
	    setDataCount();
	    createButton();
	}
	String dump() { 
	    return super.dump() + " " + duration + " " + samplingRate + " " + labelNum;
	}
	
	void draggingDone() {
	    setTimeStep();
	}
	
	// get next unused labelNum value
	int getNextLabelNum() {
	    int i;
	    int num = 1;
	    if (sim.elmList == null)
		return 0;
	    for (i = 0; i != sim.elmList.size(); i++) {
		CircuitElm ce = sim.getElm(i);
		if (!(ce instanceof AudioOutputElm))
		    continue;
		int ln = ((AudioOutputElm)ce).labelNum;
		if (ln >= num)
		    num = ln+1;
	    }
	    return num;
	}
	
	int getDumpType() { return 211; }
	int getPostCount() { return 1; }
	void reset() {
	    dataPtr = 0;
	    dataFull = false;
	    dataSampleCount = 0;
	    nextDataSample = 0;
	    dataSample = 0;
	}
	void setPoints() {
	    super.setPoints();
	    lead1 = new Point();
	}
	void draw(Graphics g) {
	    boolean selected = (needsHighlight());
	    Font f = new Font("SansSerif", selected ? Font.BOLD : 0, 14);
	    String s = "Audio Out";
	    if (labelNum > 1)
		s = "Audio " + labelNum;
	    g.setFont(f);
	    int textWidth = (int)g.context.measureText(s).getWidth();
	    g.setColor(Color.darkGray);
	    int pct = (dataFull) ? textWidth : textWidth*dataPtr/dataCount;
	    g.fillRect(x2-textWidth/2, y2-10, pct, 20);
	    g.setColor(selected ? selectColor : whiteColor);
	    interpPoint(point1, point2, lead1, 1-(textWidth/2.+8)/dn);
	    setBbox(point1, lead1, 0);
	    drawCenteredText(g, s, x2, y2, true);
	    setVoltageColor(g, volts[0]);
	    if (selected)
		g.setColor(selectColor);
	    drawThickLine(g, point1, lead1);
	    drawPosts(g);
	}
	double getVoltageDiff() { return volts[0]; }
	void getInfo(String arr[]) {
	    arr[0] = "audio output";
	    arr[1] = "V = " + getVoltageText(volts[0]);
	    int ct = (dataFull ? dataCount : dataPtr);
	    double dur = sampleStep * ct;
	    arr[2] = "start = " + getUnitText(dataFull ? sim.t-duration : dataStart, "s");
	    arr[3] = "dur = " + getUnitText(dur, "s");
	    arr[4] = "samples = " + ct + (dataFull ? "" : "/" + dataCount);
	}
	
	int dataSampleCount = 0;
	double nextDataSample = 0;
	double dataSample;
	
	void stepFinished() {
	    dataSample += volts[0];
	    dataSampleCount++;
	    if (sim.t >= nextDataSample) {
		nextDataSample += sampleStep;
		data[dataPtr++] = dataSample/dataSampleCount;
		dataSampleCount = 0;
		dataSample = 0;
		if (dataPtr >= dataCount) {
		    dataPtr = 0;
		    dataFull = true;
		}
	    }
	}
	
	void setDataCount() {
	    dataCount = (int) (samplingRate * duration);
	    data = new double[dataCount];
	    dataStart = sim.t;
	    dataPtr = 0;
	    dataFull = false;
	    sampleStep = 1./samplingRate;
	    nextDataSample = sim.t+sampleStep;
	}
	
	int samplingRateChoices[] = { 8000, 11025, 16000, 22050, 44100 };
	
	public EditInfo getEditInfo(int n) {
	    if (n == 0) {
		EditInfo ei = new EditInfo("Duration (s)", duration, 0, 5);
		return ei;
	    }
	    if (n == 1) {
		EditInfo ei =  new EditInfo("Sampling Rate", 0, -1, -1);
		ei.choice = new Choice();
		int i;
		for (i = 0; i != samplingRateChoices.length; i++) {
		    ei.choice.add(samplingRateChoices[i] + "");
		    if (samplingRateChoices[i] == samplingRate)
			ei.choice.select(i);
		}
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0 && ei.value > 0) {
		duration = ei.value;
		setDataCount();
	    }
	    if (n == 1) {
		int nsr = samplingRateChoices[ei.choice.getSelectedIndex()];
		if (nsr != samplingRate) {
		    samplingRate = nsr;
		    lastSamplingRate = nsr;
		    setDataCount();
		    setTimeStep();
		}
	    }
	}
	
	void setTimeStep() {
	    /*
	    // timestep must be smaller than 1/sampleRate
	    if (sim.timeStep > sampleStep)
		sim.timeStep = sampleStep;
	    else {
		// make sure sampleStep/timeStep is an integer.  otherwise we get distortion
//		int frac = (int)Math.round(sampleStep/sim.timeStep);
//		sim.timeStep = sampleStep / frac;
		
		// actually, just make timestep = 1/sampleRate
		sim.timeStep = sampleStep;
	    }
	    */
	    
//	    int frac = (int)Math.round(Math.max(sampleStep*33000, 1));
	    double target = sampleStep/8;
	    if (sim.timeStep != target) {
                if (okToChangeTimeStep || Window.confirm(sim.LS("Adjust timestep for best audio quality and performance?"))) {
                    sim.timeStep = target;
                    okToChangeTimeStep = true;
                }
	    }
	}
	
        void createButton() {
            String label = "&#9654; " + sim.LS("Play Audio");
            if (labelNum > 1)
        	label += " " + labelNum;
            sim.addWidgetToVerticalPanel(button = new Button(label));
            button.setStylePrimaryName("topButton");
            button.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        	    play();
        	}
            });
            
        }
        void delete() {
            sim.removeWidgetFromVerticalPanel(button);
            super.delete();
        }
        
        public static native void playJS(JsArrayInteger samples, int sampleRate)
            /*-{
            var Wav = function(opt_params){
        	this._sampleRate = opt_params && opt_params.sampleRate ? opt_params.sampleRate : 44100;
        	this._channels = opt_params && opt_params.channels ? opt_params.channels : 2;  
        	this._eof = true;
        	this._bufferNeedle = 0;
        	this._buffer;
        	
        	};

        Wav.prototype.setBuffer = function(buffer){
        	this._buffer = this.getWavInt16Array(buffer);
        	this._bufferNeedle = 0;
        	this._internalBuffer = '';
        	this._hasOutputHeader = false;
        	this._eof = false;
        };

        Wav.prototype.getBuffer = function(len){
        	
        	var rt;
        	if( this._bufferNeedle + len >= this._buffer.length ){
        		rt = new Int16Array(this._buffer.length - this._bufferNeedle);
        		this._eof = true;
        	}
        	else {
        		rt = new Int16Array(len);
        	}
        	
        	for(var i=0; i<rt.length; i++){
        		rt[i] = this._buffer[i+this._bufferNeedle];
        	}
        	this._bufferNeedle += rt.length;
        	
        	return  rt.buffer;
        	

        };

        Wav.prototype.eof = function(){
        	return this._eof;
        };

        Wav.prototype.getWavInt16Array = function(buffer){
        		
        	var intBuffer = new Int16Array(buffer.length + 23), tmp;
        	
        	intBuffer[0] = 0x4952; // "RI"
        	intBuffer[1] = 0x4646; // "FF"
        	
        	intBuffer[2] = (2*buffer.length + 15) & 0x0000ffff; // RIFF size
        	intBuffer[3] = ((2*buffer.length + 15) & 0xffff0000) >> 16; // RIFF size
        	
        	intBuffer[4] = 0x4157; // "WA"
        	intBuffer[5] = 0x4556; // "VE"
        		
        	intBuffer[6] = 0x6d66; // "fm"
        	intBuffer[7] = 0x2074; // "t "
        		
        	intBuffer[8] = 0x0012; // fmt chunksize: 18
        	intBuffer[9] = 0x0000; //
        		
        	intBuffer[10] = 0x0001; // format tag : 1 
        	intBuffer[11] = this._channels; // channels: 2
        	
        	intBuffer[12] = this._sampleRate & 0x0000ffff; // sample per sec
        	intBuffer[13] = (this._sampleRate & 0xffff0000) >> 16; // sample per sec
        	
        	intBuffer[14] = (2*this._channels*this._sampleRate) & 0x0000ffff; // byte per sec
        	intBuffer[15] = ((2*this._channels*this._sampleRate) & 0xffff0000) >> 16; // byte per sec
        	
        	intBuffer[16] = 0x0004; // block align
        	intBuffer[17] = 0x0010; // bit per sample
        	intBuffer[18] = 0x0000; // cb size
        	intBuffer[19] = 0x6164; // "da"
        	intBuffer[20] = 0x6174; // "ta"
        	intBuffer[21] = (2*buffer.length) & 0x0000ffff; // data size[byte]
        	intBuffer[22] = ((2*buffer.length) & 0xffff0000) >> 16; // data size[byte]	

        	for (var i = 0; i < buffer.length; i++)
        	    intBuffer[i+23] = buffer[i];
        	
        	return intBuffer;
        };
        var i=0,
            wav = new Wav({sampleRate: sampleRate, channels: 1});
        wav.setBuffer(samples);

        var srclist = [];
        while( !wav.eof() ){
            srclist.push(wav.getBuffer(1000));
        }

	var oldblob = $doc.audioBlob;
	var oldobj = $doc.audioObject;
	// remove old blob and audio obj if any.  We should do this when audio is done playing, but this is easier
	if (oldblob) {
	    oldobj.parentNode.removeChild(oldobj);
            URL.revokeObjectURL(oldblob);
	}

        var b = new Blob(srclist, {type:'audio/wav'});
//        var URLObject = $wnd.webkitURL || $wnd.URL;
//        var url = URLObject.createObjectURL(b);
        var url = URL.createObjectURL(b);
        $doc.audioBlob = url;
//        console.log(url);
	var audio = $doc.createElement("audio");
	$doc.audioObject = audio;
	audio.src = url;
	$doc.body.appendChild(audio);
	audio.play();
}-*/;

        void play() {
            int i;
            JsArrayInteger arr = (JsArrayInteger)JsArrayInteger.createArray();
            int ct = dataPtr;
            int base = 0;
            if (dataFull) {
        	ct = dataCount;
        	base = dataPtr;
            }
            if (ct * sampleStep < .05) {
        	Window.alert(sim.LS("Audio data is not ready yet.  Increase simulation speed to make data ready sooner."));
        	return;
            }
            
            // rescale data to maximize
            double max = -1e8;
            double min =  1e8;
            for (i = 0; i != ct; i++) {
        	if (data[i] > max) max = data[i];
        	if (data[i] < min) min = data[i];
            }
            
            double adj = -(max+min)/2;
            double mult = (.25*32766)/(max+adj);
            
            // fade in over 1/20 sec
	    int fadeLen = samplingRate/20;
	    int fadeOut = ct-fadeLen;
	    
	    double fadeMult = mult/fadeLen;
            for (i = 0; i != ct; i++) {
		double fade = (i < fadeLen) ? i*fadeMult : (i > fadeOut) ? (ct-i)*fadeMult : mult;
        	int s = (int)((data[(i+base)%dataCount]+adj)*fade);
        	arr.push(s);
            }
            playJS(arr, samplingRate);
        }
}
