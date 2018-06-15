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

// GWT conversion (c) 2015 by Iain Sharp



// For information about the theory behind this, see Electronic Circuit & System Simulation Methods by Pillage



import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.lang.Math;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.PopupPanel;
import static com.google.gwt.event.dom.client.KeyCodes.*;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window.Navigator;


public class CirSim implements MouseDownHandler, MouseMoveHandler, MouseUpHandler,
ClickHandler, DoubleClickHandler, ContextMenuHandler, NativePreviewHandler,
MouseOutHandler, MouseWheelHandler {
    
    Random random;
    
    // IES - remove interaction
    Button resetButton;
    Button runStopButton;
    Button dumpMatrixButton;
    MenuItem aboutItem;
    MenuItem importFromLocalFileItem, importFromTextItem,
    	exportAsUrlItem, exportAsLocalFileItem, exportAsTextItem, printItem, recoverItem;
    MenuItem importFromDropboxItem;
    MenuItem undoItem, redoItem,
	cutItem, copyItem, pasteItem, selectAllItem, optionsItem;
    MenuBar optionsMenuBar;
    CheckboxMenuItem dotsCheckItem;
    CheckboxMenuItem voltsCheckItem;
    CheckboxMenuItem powerCheckItem;
    CheckboxMenuItem smallGridCheckItem;
    CheckboxMenuItem crossHairCheckItem;
    CheckboxMenuItem showValuesCheckItem;
    CheckboxMenuItem conductanceCheckItem;
    CheckboxMenuItem euroResistorCheckItem;
    CheckboxMenuItem euroGatesCheckItem;
    CheckboxMenuItem printableCheckItem;
    CheckboxMenuItem alternativeColorCheckItem;
    CheckboxMenuItem conventionCheckItem;
    private Label powerLabel;
    private Label titleLabel;
    private Scrollbar speedBar;
   private Scrollbar currentBar;
    private Scrollbar powerBar;
    MenuBar elmMenuBar;
    MenuItem elmEditMenuItem;
    MenuItem elmCutMenuItem;
    MenuItem elmCopyMenuItem;
    MenuItem elmDeleteMenuItem;
    MenuItem elmScopeMenuItem;
    MenuItem elmFlipMenuItem;
    MenuBar scopeMenuBar;
    MenuBar mainMenuBar;
    MenuItem scopeRemovePlotMenuItem;
    MenuItem scopeSelectYMenuItem;
    static HashMap<String,String> localizationMap;
   
    String lastCursorStyle;
    boolean mouseWasOverSplitter = false; 

//    Class addingClass;
    PopupPanel contextPanel = null;
    int mouseMode = MODE_SELECT;
    int tempMouseMode = MODE_SELECT;
    String mouseModeStr = "Select";
    static final double pi = 3.14159265358979323846;
    static final int MODE_ADD_ELM = 0;
    static final int MODE_DRAG_ALL = 1;
    static final int MODE_DRAG_ROW = 2;
    static final int MODE_DRAG_COLUMN = 3;
    static final int MODE_DRAG_SELECTED = 4;
    static final int MODE_DRAG_POST = 5;
    static final int MODE_SELECT = 6;
    static final int MODE_DRAG_SPLITTER = 7;
    static final int infoWidth = 120;
    long myframes =1;
    long mytime=0;
    long myruntime=0;
    long mydrawtime=0;
    int dragGridX, dragGridY, dragScreenX, dragScreenY, initDragGridX, initDragGridY;
    long mouseDownTime;
    long zoomTime;
    int mouseCursorX = -1;
    int mouseCursorY = -1;
    Rectangle selectedArea;
    int gridSize, gridMask, gridRound;
    boolean dragging;
    boolean analyzeFlag;
    boolean dumpMatrix;
    boolean dcAnalysisFlag;
 //   boolean useBufferedImage;
    boolean isMac;
    String ctrlMetaKey;
    double t;
    int pause = 10;
    int scopeSelected = -1;
    int menuScope = -1;
    int menuPlot = -1;
    int hintType = -1, hintItem1, hintItem2;
    String stopMessage;
    double timeStep;
    static final int HINT_LC = 1;
    static final int HINT_RC = 2;
    static final int HINT_3DB_C = 3;
    static final int HINT_TWINT = 4;
    static final int HINT_3DB_L = 5;
    Vector<CircuitElm> elmList;
//    Vector setupList;
    CircuitElm dragElm, menuElm, stopElm;
    private CircuitElm mouseElm=null;
    boolean didSwitch = false;
    int mousePost = -1;
    CircuitElm plotXElm, plotYElm;
    int draggingPost;
    SwitchElm heldSwitchElm;
    double circuitMatrix[][], circuitRightSide[],
	origRightSide[], origMatrix[][];
    RowInfo circuitRowInfo[];
    int circuitPermute[];
    boolean simRunning;
    boolean circuitNonLinear;
    int voltageSourceCount;
    int circuitMatrixSize, circuitMatrixFullSize;
    boolean circuitNeedsMap;
 //   public boolean useFrame;
    int scopeCount;
    Scope scopes[];
    boolean showResistanceInVoltageSources;
   int scopeColCount[];
    static EditDialog editDialog, customLogicEditDialog;
    static ExportAsUrlDialog exportAsUrlDialog;
    static ExportAsTextDialog exportAsTextDialog;
    static ExportAsLocalFileDialog exportAsLocalFileDialog;
    static ImportFromTextDialog importFromTextDialog;
    static ImportFromDropbox importFromDropbox;
    static ScrollValuePopup scrollValuePopup;
    static AboutBox aboutBox;
    static ImportFromDropboxDialog importFromDropboxDialog;
//    Class dumpTypes[], shortcuts[];
    String shortcuts[];
    static String muString = "\u03bc";
    static String ohmString = "\u03a9";
    String clipboard;
    String recovery;
    Rectangle circuitArea;
    Vector<String> undoStack, redoStack;
    double transform[];

	DockLayoutPanel layoutPanel;
	MenuBar menuBar;
	MenuBar fileMenuBar;
	VerticalPanel verticalPanel;
	CellPanel buttonPanel;
	private boolean mouseDragging;
	double scopeHeightFraction=0.2;
	
	Vector<CheckboxMenuItem> mainMenuItems = new Vector<CheckboxMenuItem>();
	Vector<String> mainMenuItemNames = new Vector<String>();

	LoadFile loadFileInput;
	Frame iFrame;
	
    Canvas cv;
    Context2d cvcontext;
    Canvas backcv;
    Context2d backcontext;
    static final int MENUBARHEIGHT=30;
    static int VERTICALPANELWIDTH=166; // default
    static final int POSTGRABSQ=25;
    static final int MINPOSTGRABSIZE = 256;
    final Timer timer = new Timer() {
	      public void run() {
	        updateCircuit();
	      }
	    };
	 final int FASTTIMER=16;
    
	int getrand(int x) {
		int q = random.nextInt();
		if (q < 0)
			q = -q;
		return q % x;
	}
	
	
    public void setCanvasSize(){
    	int width, height;
    	width=(int)RootLayoutPanel.get().getOffsetWidth();
    	height=(int)RootLayoutPanel.get().getOffsetHeight();
    	height=height-MENUBARHEIGHT;
    	width=width-VERTICALPANELWIDTH;
		if (cv != null) {
			cv.setWidth(width + "PX");
			cv.setHeight(height + "PX");
			cv.setCoordinateSpaceWidth(width);
			cv.setCoordinateSpaceHeight(height);
		}
		if (backcv != null) {
			backcv.setWidth(width + "PX");
			backcv.setHeight(height + "PX");
			backcv.setCoordinateSpaceWidth(width);
			backcv.setCoordinateSpaceHeight(height);
		}

    	setCircuitArea();
    }
    
    void setCircuitArea() {
    	int height = cv.getCanvasElement().getHeight();
    	int width = cv.getCanvasElement().getWidth();
		int h = (int) ((double)height * scopeHeightFraction);
		/*if (h < 128 && winSize.height > 300)
		  h = 128;*/
		circuitArea = new Rectangle(0, 0, width, height-h);
    }
    
//    Circuit applet;

    CirSim() {
//	super("Circuit Simulator v1.6d");
//	applet = a;
//	useFrame = false;
	theSim = this;
    }

    String startCircuit = null;
    String startLabel = null;
    String startCircuitText = null;
    String startCircuitLink = null;
//    String baseURL = "http://www.falstad.com/circuit/";
    
    public void init() {


	boolean printable = false;
	boolean convention = true;
	boolean euroRes = false;
	boolean usRes = false;
	MenuBar m;

	CircuitElm.initClass(this);
	readRecovery();

	QueryParameters qp = new QueryParameters();
			
	try {
		//baseURL = applet.getDocumentBase().getFile();
		// look for circuit embedded in URL
//		String doc = applet.getDocumentBase().toString();
		String cct=qp.getValue("cct");
		if (cct!=null)
			startCircuitText = cct.replace("%24", "$");
		startCircuit = qp.getValue("startCircuit");
		startLabel   = qp.getValue("startLabel");
		startCircuitLink = qp.getValue("startCircuitLink");
		euroRes = qp.getBooleanValue("euroResistors", false);
		usRes = qp.getBooleanValue("usResistors",  false);
		printable = qp.getBooleanValue("whiteBackground", getOptionFromStorage("whiteBackground", false));
		convention = qp.getBooleanValue("conventionalCurrent",
			getOptionFromStorage("conventionalCurrent", true));
	} catch (Exception e) { }
	
	boolean euroSetting = false;
	if (euroRes)
	    euroSetting = true;
	else if (usRes)
	    euroSetting = false;
	else
	    euroSetting = getOptionFromStorage("euroResistors", !weAreInUS());
	boolean euroGates = getOptionFromStorage("euroGates", weAreInGermany());
	
	transform = new double[6];
	String os = Navigator.getPlatform();
	isMac = (os.toLowerCase().contains("mac"));
	ctrlMetaKey = (isMac) ? "Cmd" : "Ctrl";

	shortcuts = new String[127];

	layoutPanel = new DockLayoutPanel(Unit.PX);
	
	  fileMenuBar = new MenuBar(true);
	  importFromLocalFileItem = new MenuItem(LS("Open File..."), new MyCommand("file","importfromlocalfile"));
	  importFromLocalFileItem.setEnabled(LoadFile.isSupported());
	  fileMenuBar.addItem(importFromLocalFileItem);
	  importFromTextItem = new MenuItem(LS("Import From Text..."), new MyCommand("file","importfromtext"));
	  fileMenuBar.addItem(importFromTextItem);
	  importFromDropboxItem = new MenuItem(LS("Import From Dropbox..."), new MyCommand("file", "importfromdropbox"));
	  fileMenuBar.addItem(importFromDropboxItem); 
	  exportAsLocalFileItem = new MenuItem(LS("Save As..."), new MyCommand("file","exportaslocalfile"));
	  exportAsLocalFileItem.setEnabled(ExportAsLocalFileDialog.downloadIsSupported());
	  fileMenuBar.addItem(exportAsLocalFileItem);
	  exportAsUrlItem = new MenuItem(LS("Export As Link..."), new MyCommand("file","exportasurl"));
	  fileMenuBar.addItem(exportAsUrlItem);
	  exportAsTextItem = new MenuItem(LS("Export As Text..."), new MyCommand("file","exportastext"));
	  fileMenuBar.addItem(exportAsTextItem);
	  fileMenuBar.addItem(new MenuItem(LS("Find DC Operating Point"), new MyCommand("file", "dcanalysis")));
	  recoverItem = new MenuItem(LS("Recover Auto-Save"), new MyCommand("file","recover"));
	  recoverItem.setEnabled(recovery != null);
	  fileMenuBar.addItem(recoverItem);
	  printItem = new MenuItem(LS("Print..."), new MyCommand("file","print"));
	  fileMenuBar.addItem(printItem);
	  fileMenuBar.addSeparator();
	  aboutItem=new MenuItem(LS("About..."),(Command)null);
	  fileMenuBar.addItem(aboutItem);
	  aboutItem.setScheduledCommand(new MyCommand("file","about"));
	  
	  int width=(int)RootLayoutPanel.get().getOffsetWidth();
	  VERTICALPANELWIDTH = width/5;
	  if (VERTICALPANELWIDTH > 166)
	      VERTICALPANELWIDTH = 166;
	  if (VERTICALPANELWIDTH < 128)
	      VERTICALPANELWIDTH = 128;

	  menuBar = new MenuBar();
	  menuBar.addItem(LS("File"), fileMenuBar);
	  verticalPanel=new VerticalPanel();
	  
	  // make buttons side by side if there's room
	  buttonPanel=(VERTICALPANELWIDTH == 166) ? new HorizontalPanel() : new VerticalPanel();
	  
	m = new MenuBar(true);
	m.addItem(undoItem = menuItemWithShortcut(LS("Undo"), LS("Ctrl-Z"), new MyCommand("edit","undo")));
	m.addItem(redoItem = menuItemWithShortcut(LS("Redo"), LS("Ctrl-Y"), new MyCommand("edit","redo")));
	m.addSeparator();
	m.addItem(cutItem = menuItemWithShortcut(LS("Cut"), LS("Ctrl-X"), new MyCommand("edit","cut")));
	m.addItem(copyItem = menuItemWithShortcut(LS("Copy"), LS("Ctrl-C"), new MyCommand("edit","copy")));
	m.addItem(pasteItem = menuItemWithShortcut(LS("Paste"), LS("Ctrl-V"), new MyCommand("edit","paste")));
	pasteItem.setEnabled(false);
	
	m.addItem(menuItemWithShortcut(LS("Duplicate"), LS("Ctrl-D"), new MyCommand("edit","duplicate")));
	
	m.addSeparator();
	m.addItem(selectAllItem = menuItemWithShortcut(LS("Select All"), LS("Ctrl-A"), new MyCommand("edit","selectAll")));
	m.addSeparator();
	m.addItem(new MenuItem(weAreInUS() ? LS("Center Circuit") : LS("Centre Circuit"), new MyCommand("edit", "centrecircuit")));
	m.addItem(menuItemWithShortcut(LS("Zoom 100%"), "0", new MyCommand("edit", "zoom100")));
	m.addItem(menuItemWithShortcut(LS("Zoom In"), "+", new MyCommand("edit", "zoomin")));
	m.addItem(menuItemWithShortcut(LS("Zoom Out"), "-", new MyCommand("edit", "zoomout")));
	menuBar.addItem(LS("Edit"),m);

	MenuBar drawMenuBar = new MenuBar(true);
	drawMenuBar.setAutoOpen(true);

	menuBar.addItem(LS("Draw"), drawMenuBar);
	
	m = new MenuBar(true);
	m.addItem(new MenuItem(LS("Stack All"), new MyCommand("scopes", "stackAll")));
	m.addItem(new MenuItem(LS("Unstack All"),new MyCommand("scopes", "unstackAll")));
	m.addItem(new MenuItem(LS("Combine All"),new MyCommand("scopes", "combineAll")));
	menuBar.addItem(LS("Scopes"), m);
		
	optionsMenuBar = m = new MenuBar(true );
	menuBar.addItem(LS("Options"), optionsMenuBar);
	m.addItem(dotsCheckItem = new CheckboxMenuItem(LS("Show Current")));
	dotsCheckItem.setState(true);
	m.addItem(voltsCheckItem = new CheckboxMenuItem(LS("Show Voltage"),
			new Command() { public void execute(){
				if (voltsCheckItem.getState())
					powerCheckItem.setState(false);
				setPowerBarEnable();
			}
			}));
	voltsCheckItem.setState(true);
	m.addItem(powerCheckItem = new CheckboxMenuItem(LS("Show Power"),
			new Command() { public void execute(){
				if (powerCheckItem.getState())
					voltsCheckItem.setState(false);
				setPowerBarEnable();
			}
	}));
	m.addItem(showValuesCheckItem = new CheckboxMenuItem(LS("Show Values")));
	showValuesCheckItem.setState(true);
	//m.add(conductanceCheckItem = getCheckItem(LS("Show Conductance")));
	m.addItem(smallGridCheckItem = new CheckboxMenuItem(LS("Small Grid"),
			new Command() { public void execute(){
				setGrid();
			}
	}));
	m.addItem(crossHairCheckItem = new CheckboxMenuItem(LS("Show Cursor Cross Hairs"),
		new Command() { public void execute(){
		    setOptionInStorage("crossHair", crossHairCheckItem.getState());
		}
	}));
	crossHairCheckItem.setState(getOptionFromStorage("crossHair", false));
	m.addItem(euroResistorCheckItem = new CheckboxMenuItem(LS("European Resistors"),
		new Command() { public void execute(){
		    setOptionInStorage("euroResistors", euroResistorCheckItem.getState());
		}
	}));
	euroResistorCheckItem.setState(euroSetting);
	m.addItem(euroGatesCheckItem = new CheckboxMenuItem(LS("IEC Gates"),
		new Command() { public void execute(){
		    setOptionInStorage("euroGates", euroGatesCheckItem.getState());
		    int i;
		    for (i = 0; i != elmList.size(); i++)
			getElm(i).setPoints();
		}
	}));
	euroGatesCheckItem.setState(euroGates);
	m.addItem(printableCheckItem = new CheckboxMenuItem(LS("White Background"),
			new Command() { public void execute(){
				int i;
				for (i=0;i<scopeCount;i++)
					scopes[i].setRect(scopes[i].rect);
				setOptionInStorage("whiteBackground", printableCheckItem.getState());
			}
	}));
	printableCheckItem.setState(printable);
	m.addItem(alternativeColorCheckItem = new CheckboxMenuItem(LS("Alt Color for Volts & Pwr"),
		new Command() { public void execute(){

			setOptionInStorage("alternativeColor", alternativeColorCheckItem.getState());
			CircuitElm.setColorScale();
		}
	}));
	alternativeColorCheckItem.setState(getOptionFromStorage("alternativeColor", false));
	
	m.addItem(conventionCheckItem = new CheckboxMenuItem(LS("Conventional Current Motion"),
		new Command() { public void execute(){
		    setOptionInStorage("conventionalCurrent", conventionCheckItem.getState());
		}
	}));
	conventionCheckItem.setState(convention);
	m.addItem(optionsItem = new CheckboxAlignedMenuItem(LS("Other Options..."),
			new MyCommand("options","other")));

	mainMenuBar = new MenuBar(true);
	mainMenuBar.setAutoOpen(true);
	composeMainMenu(mainMenuBar);
	composeMainMenu(drawMenuBar);

	  
	  layoutPanel.addNorth(menuBar, MENUBARHEIGHT);
	  layoutPanel.addEast(verticalPanel, VERTICALPANELWIDTH);
	  RootLayoutPanel.get().add(layoutPanel);
	
	cv =Canvas.createIfSupported();
	  if (cv==null) {
		  RootPanel.get().add(new Label("Not working. You need a browser that supports the CANVAS element."));
		  return;
	  }
	  
	  
	  
	    cvcontext=cv.getContext2d();
	 backcv=Canvas.createIfSupported();
	    backcontext=backcv.getContext2d();
	    setCanvasSize();
		layoutPanel.add(cv);
		verticalPanel.add(buttonPanel);
		 buttonPanel.add(resetButton = new Button(LS("Reset")));
		 resetButton.addClickHandler(new ClickHandler() {
			    public void onClick(ClickEvent event) {
			      resetAction();
			    }
			  });
		 resetButton.setStylePrimaryName("topButton");
		 buttonPanel.add(runStopButton = new Button(LSHTML("<Strong>RUN</Strong>&nbsp;/&nbsp;Stop")));
		 runStopButton.addClickHandler(new ClickHandler() {
			    public void onClick(ClickEvent event) {
			      setSimRunning(!simIsRunning());
			    }
			  });
		 
		 /*
	dumpMatrixButton = new Button("Dump Matrix");
	dumpMatrixButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) { dumpMatrix = true; }});
	verticalPanel.add(dumpMatrixButton);// IES for debugging
*/
	
	if (LoadFile.isSupported())
		verticalPanel.add(loadFileInput = new LoadFile(this));
	
	Label l;
	verticalPanel.add(l = new Label(LS("Simulation Speed")));
	l.addStyleName("topSpace");

	// was max of 140
	verticalPanel.add( speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 260));

	verticalPanel.add( l = new Label(LS("Current Speed")));
	l.addStyleName("topSpace");
	currentBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100);
	verticalPanel.add(currentBar);
	verticalPanel.add(powerLabel = new Label (LS("Power Brightness")));
	powerLabel.addStyleName("topSpace");
	verticalPanel.add(powerBar = new Scrollbar(Scrollbar.HORIZONTAL,
		    50, 1, 1, 100));
	setPowerBarEnable();
	
//	verticalPanel.add(new Label(""));
//        Font f = new Font("SansSerif", 0, 10);
        l = new Label(LS("Current Circuit:"));
	l.addStyleName("topSpace");
//        l.setFont(f);
        titleLabel = new Label("Label");
//        titleLabel.setFont(f);
        verticalPanel.add(l);
        verticalPanel.add(titleLabel);

	verticalPanel.add(iFrame = new Frame("iframe.html"));
	iFrame.setWidth(VERTICALPANELWIDTH+"px");
	iFrame.setHeight("100 px");
	iFrame.getElement().setAttribute("scrolling", "no");
	
	setGrid();
	elmList = new Vector<CircuitElm>();
//	setupList = new Vector();
	undoStack = new Vector<String>();
	redoStack = new Vector<String>();


	scopes = new Scope[20];
	scopeColCount = new int[20];
	scopeCount = 0;
	
	random = new Random();
//	cv.setBackground(Color.black);
//	cv.setForeground(Color.lightGray);
	
	elmMenuBar = new MenuBar(true);
	elmMenuBar.addItem(elmEditMenuItem = new MenuItem(LS("Edit..."),new MyCommand("elm","edit")));
	elmMenuBar.addItem(elmScopeMenuItem = new MenuItem(LS("View in Scope"), new MyCommand("elm","viewInScope")));
	elmMenuBar.addItem(elmCutMenuItem = new MenuItem(LS("Cut"),new MyCommand("elm","cut")));
	elmMenuBar.addItem(elmCopyMenuItem = new MenuItem(LS("Copy"),new MyCommand("elm","copy")));
	elmMenuBar.addItem(elmDeleteMenuItem = new MenuItem(LS("Delete"),new MyCommand("elm","delete")));
	elmMenuBar.addItem(                    new MenuItem(LS("Duplicate"),new MyCommand("elm","duplicate")));
	elmMenuBar.addItem(elmFlipMenuItem = new MenuItem(LS("Swap Terminals"),new MyCommand("elm","flip")));
	
	scopeMenuBar = buildScopeMenu();

	CircuitElm.setColorScale();
	
	if (startCircuitText != null) {
		getSetupList(false);
		readSetup(startCircuitText, true);
	} else {
		if (stopMessage == null && startCircuitLink!=null) {
			readSetup(null, 0, false, true);
			getSetupList(false);
			ImportFromDropboxDialog.setSim(this);
			ImportFromDropboxDialog.doImportDropboxLink(startCircuitLink, false);
		} else {
			readSetup(null, 0, false, true);
			if (stopMessage == null && startCircuit != null) {
				getSetupList(false);
				readSetupFile(startCircuit, startLabel, true);
			}
			else
				getSetupList(true);
		}
	}

		

	
		enableUndoRedo();
		enablePaste();
		setiFrameHeight();
		cv.addMouseDownHandler(this);
		cv.addMouseMoveHandler(this);
		cv.addMouseOutHandler(this);
		cv.addMouseUpHandler(this);
		cv.addClickHandler(this);
		cv.addDoubleClickHandler(this);
		doTouchHandlers(cv.getCanvasElement());
		cv.addDomHandler(this, ContextMenuEvent.getType());	
		menuBar.addDomHandler(new ClickHandler() {
		    public void onClick(ClickEvent event) {
		        doMainMenuChecks();
		      }
		    }, ClickEvent.getType());	
		Event.addNativePreviewHandler(this);
		cv.addMouseWheelHandler(this);
		setSimRunning(true);
	    // setup timer

	    timer.scheduleRepeating(FASTTIMER);
	  

    }

    MenuItem menuItemWithShortcut(String text, String shortcut, MyCommand cmd) {
	final String edithtml="<div style=\"display:inline-block;width:80px;\">";
	String sn=edithtml + text + "</div>" + shortcut;
	return new MenuItem(SafeHtmlUtils.fromTrustedString(sn), cmd);
    }
    
    boolean getOptionFromStorage(String key, boolean val) {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return val;
        String s = stor.getItem(key);
        if (s == null)
            return val;
        return s == "true";
    }

    void setOptionInStorage(String key, boolean val) {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return;
        stor.setItem(key,  val ? "true" : "false");
    }
    
    // install touch handlers
    // don't feel like rewriting this in java.  Anyway, java doesn't let us create mouse
    // events and dispatch them.
    native void doTouchHandlers(CanvasElement cv) /*-{
	// Set up touch events for mobile, etc
	var lastTap;
	var tmout;
	var sim = this;
	cv.addEventListener("touchstart", function (e) {
        	mousePos = getTouchPos(cv, e);
  		var touch = e.touches[0];
  		var etype = "mousedown";
  		clearTimeout(tmout);
  		if (e.timeStamp-lastTap < 300) {
     		    etype = "dblclick";
  		} else {
  		    tmout = setTimeout(function() {
  		        sim.@com.lushprojects.circuitjs1.client.CirSim::longPress()();
  		    }, 500);
  		}
  		lastTap = e.timeStamp;
  		
  		var mouseEvent = new MouseEvent(etype, {
    			clientX: touch.clientX,
    			clientY: touch.clientY
  		});
  		e.preventDefault();
  		cv.dispatchEvent(mouseEvent);
	}, false);
	cv.addEventListener("touchend", function (e) {
  		var mouseEvent = new MouseEvent("mouseup", {});
  		e.preventDefault();
  		clearTimeout(tmout);
  		cv.dispatchEvent(mouseEvent);
	}, false);
	cv.addEventListener("touchmove", function (e) {
  		var touch = e.touches[0];
  		var mouseEvent = new MouseEvent("mousemove", {
    			clientX: touch.clientX,
    			clientY: touch.clientY
  		});
  		e.preventDefault();
  		clearTimeout(tmout);
  		cv.dispatchEvent(mouseEvent);
	}, false);

	// Get the position of a touch relative to the canvas
	function getTouchPos(canvasDom, touchEvent) {
  		var rect = canvasDom.getBoundingClientRect();
  		return {
    			x: touchEvent.touches[0].clientX - rect.left,
    			y: touchEvent.touches[0].clientY - rect.top
  		};
	}
	
    }-*/;
    
    boolean shown = false;
    
    public void composeMainMenu(MenuBar mainMenuBar) {
    	mainMenuBar.addItem(getClassCheckItem(LS("Add Wire"), "WireElm"));
    	mainMenuBar.addItem(getClassCheckItem(LS("Add Resistor"), "ResistorElm"));

    	MenuBar passMenuBar = new MenuBar(true);
    	passMenuBar.addItem(getClassCheckItem(LS("Add Capacitor"), "CapacitorElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Capacitor (polarized)"), "PolarCapacitorElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Inductor"), "InductorElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Switch"), "SwitchElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Push Switch"), "PushSwitchElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add SPDT Switch"), "Switch2Elm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Potentiometer"), "PotElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Transformer"), "TransformerElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Tapped Transformer"), "TappedTransformerElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Transmission Line"), "TransLineElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Relay"), "RelayElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Memristor"), "MemristorElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Spark Gap"), "SparkGapElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Passive Components")), passMenuBar);

    	MenuBar inputMenuBar = new MenuBar(true);
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Ground"), "GroundElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Voltage Source (2-terminal)"), "DCVoltageElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add A/C Voltage Source (2-terminal)"), "ACVoltageElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Voltage Source (1-terminal)"), "RailElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add A/C Voltage Source (1-terminal)"), "ACRailElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Square Wave Source (1-terminal)"), "SquareRailElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Clock"), "ClockElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add A/C Sweep"), "SweepElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Variable Voltage"), "VarRailElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Antenna"), "AntennaElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add AM Source"), "AMElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add FM Source"), "FMElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Current Source"), "CurrentElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Noise Generator"), "NoiseElm"));

    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Inputs and Sources")), inputMenuBar);
    	
    	MenuBar outputMenuBar = new MenuBar(true);
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Analog Output"), "OutputElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add LED"), "LEDElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Lamp"), "LampElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Text"), "TextElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Box"), "BoxElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Voltmeter/Scobe Probe"), "ProbeElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Ohmmeter"), "OhmMeterElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Labeled Node"), "LabeledNodeElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Test Point"), "TestPointElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Ammeter"), "AmmeterElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Data Export"), "DataRecorderElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Audio Output"), "AudioOutputElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Outputs and Labels")), outputMenuBar);
    	
    	MenuBar activeMenuBar = new MenuBar(true);
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Diode"), "DiodeElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Zener Diode"), "ZenerElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Transistor (bipolar, NPN)"), "NTransistorElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Transistor (bipolar, PNP)"), "PTransistorElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add MOSFET (N-Channel)"), "NMosfetElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add MOSFET (P-Channel)"), "PMosfetElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add JFET (N-Channel)"), "NJfetElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add JFET (P-Channel)"), "PJfetElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add SCR"), "SCRElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Darlington Pair (NPN)"), "NDarlingtonElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Darlington Pair (PNP)"), "PDarlingtonElm"));
    	//    	activeMenuBar.addItem(getClassCheckItem("Add Varactor/Varicap", "VaractorElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Tunnel Diode"), "TunnelDiodeElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Triode"), "TriodeElm"));
    	//    	activeMenuBar.addItem(getClassCheckItem("Add Diac", "DiacElm"));
    	//    	activeMenuBar.addItem(getClassCheckItem("Add Triac", "TriacElm"));
    	//    	activeMenuBar.addItem(getClassCheckItem("Add Photoresistor", "PhotoResistorElm"));
    	//    	activeMenuBar.addItem(getClassCheckItem("Add Thermistor", "ThermistorElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Active Components")), activeMenuBar);

    	MenuBar activeBlocMenuBar = new MenuBar(true);
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Op Amp (- on top)"), "OpAmpElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Op Amp (+ on top)"), "OpAmpSwapElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Analog Switch (SPST)"), "AnalogSwitchElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Analog Switch (SPDT)"), "AnalogSwitch2Elm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Tristate Buffer"), "TriStateElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Schmitt Trigger"), "SchmittElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Schmitt Trigger (Inverting)"), "InvertingSchmittElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add CCII+"), "CC2Elm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add CCII-"), "CC2NegElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Comparator (Hi-Z/GND output)"), "ComparatorElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add OTA (LM13700 style)"), "OTAElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Voltage-Controlled Voltage Source"), "VCVSElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Voltage-Controlled Current Source"), "VCCSElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Current-Controlled Voltage Source"), "CCVSElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Current-Controlled Current Source"), "CCCSElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Active Building Blocks")), activeBlocMenuBar);
    	
    	MenuBar gateMenuBar = new MenuBar(true);
    	gateMenuBar.addItem(getClassCheckItem(LS("Add Logic Input"), "LogicInputElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add Logic Output"), "LogicOutputElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add Inverter"), "InverterElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add NAND Gate"), "NandGateElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add NOR Gate"), "NorGateElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add AND Gate"), "AndGateElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add OR Gate"), "OrGateElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add XOR Gate"), "XorGateElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Logic Gates, Input and Output")), gateMenuBar);

    	MenuBar chipMenuBar = new MenuBar(true);
    	chipMenuBar.addItem(getClassCheckItem(LS("Add D Flip-Flop"), "DFlipFlopElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add JK Flip-Flop"), "JKFlipFlopElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add T Flip-Flop"), "TFlipFlopElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add 7 Segment LED"), "SevenSegElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add 7 Segment Decoder"), "SevenSegDecoderElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Multiplexer"), "MultiplexerElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Demultiplexer"), "DeMultiplexerElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add SIPO shift register"), "SipoShiftElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add PISO shift register"), "PisoShiftElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Counter"), "CounterElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Decade Counter"), "DecadeElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Latch"), "LatchElm"));
    	//chipMenuBar.addItem(getClassCheckItem("Add Static RAM", "SRAMElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Sequence generator"), "SeqGenElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Full Adder"), "FullAdderElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Half Adder"), "HalfAdderElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Custom Logic"), "UserDefinedLogicElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Digital Chips")), chipMenuBar);
    	
    	MenuBar achipMenuBar = new MenuBar(true);
    	achipMenuBar.addItem(getClassCheckItem(LS("Add 555 Timer"), "TimerElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add Phase Comparator"), "PhaseCompElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add DAC"), "DACElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add ADC"), "ADCElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add VCO"), "VCOElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add Monostable"), "MonostableElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Analog and Hybrid Chips")), achipMenuBar);
    	
    	MenuBar otherMenuBar = new MenuBar(true);
    	CheckboxMenuItem mi;
    	otherMenuBar.addItem(mi=getClassCheckItem(LS("Drag All"), "DragAll"));
    	mi.addShortcut(LS("(Alt-drag)"));
    	otherMenuBar.addItem(mi=getClassCheckItem(LS("Drag Row"), "DragRow"));
    	mi.addShortcut(LS("(A-S-drag)"));
    	otherMenuBar.addItem(mi=getClassCheckItem(LS("Drag Column"), "DragColumn"));
    	mi.addShortcut(isMac ? LS("(A-Cmd-drag)") : LS("(A-M-drag)"));
    	otherMenuBar.addItem(getClassCheckItem(LS("Drag Selected"), "DragSelected"));
    	otherMenuBar.addItem(mi=getClassCheckItem(LS("Drag Post"), "DragPost"));
    	mi.addShortcut("(" + ctrlMetaKey + "-drag)");

    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Drag")), otherMenuBar);

    	mainMenuBar.addItem(mi=getClassCheckItem(LS("Select/Drag Sel"), "Select"));
    	mi.addShortcut(LS("(space or Shift-drag)"));
    }
    
    public void setiFrameHeight() {
    	if (iFrame==null)
    		return;
    	int i;
    	int cumheight=0;
    	for (i=0; i < verticalPanel.getWidgetIndex(iFrame); i++) {
    		if (verticalPanel.getWidget(i) !=loadFileInput) {
    			cumheight=cumheight+verticalPanel.getWidget(i).getOffsetHeight();
    			if (verticalPanel.getWidget(i).getStyleName().contains("topSpace"))
    					cumheight+=12;
    		}
    	}
    	int ih=RootLayoutPanel.get().getOffsetHeight()-MENUBARHEIGHT-cumheight;
    	if (ih<0)
    		ih=0;
    	iFrame.setHeight(ih+"px");
    }
    

    MenuBar buildScopeMenu() {
    	MenuBar m = new MenuBar(true);
    	m.addItem(new CheckboxAlignedMenuItem(LS("Remove Scope"),new MyCommand("scopepop", "remove")));
    	CheckboxMenuItem mi;
    	m.addItem(mi = new CheckboxMenuItem(LS("Max Scale"), new MyCommand("scopepop", "maxscale")));
    	m.addItem(new CheckboxAlignedMenuItem(LS("Stack"), new MyCommand("scopepop", "stack")));
    	m.addItem(new CheckboxAlignedMenuItem(LS("Unstack"), new MyCommand("scopepop", "unstack")));
    	m.addItem(new CheckboxAlignedMenuItem(LS("Combine"), new MyCommand("scopepop", "combine")));
    	m.addItem(scopeRemovePlotMenuItem = new CheckboxAlignedMenuItem(LS("Remove Plot"),new MyCommand("scopepop", "removeplot")));
    	m.addItem(new CheckboxAlignedMenuItem(LS("Reset"), new MyCommand("scopepop", "reset")));
    	m.addItem(new CheckboxAlignedMenuItem(LS("Properties..."), new MyCommand("scopepop", "properties")));
    	return m;
    }
    


    CheckboxMenuItem getClassCheckItem(String s, String t) {
    	// try {
    	//   Class c = Class.forName(t);
    	String shortcut="";
    	CircuitElm elm = constructElement(t, 0, 0);
    	CheckboxMenuItem mi;
    	//  register(c, elm);
    	if ( elm!=null ) {
    		if (elm.needsShortcut() ) {
    			shortcut += (char)elm.getShortcut();
    			shortcuts[elm.getShortcut()]=t;
    		}
    		elm.delete();
    	}
//    	else
//    		GWT.log("Coudn't create class: "+t);
    	//	} catch (Exception ee) {
    	//	    ee.printStackTrace();
    	//	}
    	if (shortcut=="")
    		mi= new CheckboxMenuItem(s);
    	else
    		mi = new CheckboxMenuItem(s, shortcut);
    	mi.setScheduledCommand(new MyCommand("main", t) );
    	mainMenuItems.add(mi);
    	mainMenuItemNames.add(t);
    	return mi;
    }
    
    

    
    void centreCircuit() {
	Rectangle bounds = getCircuitBounds();
	
    	double scale = 1;
    	
    	if (bounds != null)
    	    // add some space on edges because bounds calculation is not perfect
    	    scale = Math.min(circuitArea.width /(double)(bounds.width+140),
    			     circuitArea.height/(double)(bounds.height+100));
    	scale = Math.min(scale, 1.5); // Limit scale so we don't create enormous circuits in big windows

    	// calculate transform so circuit fills most of screen
    	transform[0] = transform[3] = scale;
    	transform[1] = transform[2] = transform[4] = transform[5] = 0;
    	if (bounds != null) {
    	    transform[4] = (circuitArea.width -bounds.width *scale)/2 - bounds.x*scale;
    	    transform[5] = (circuitArea.height-bounds.height*scale)/2 - bounds.y*scale;
    	}
    }

    // get circuit bounds.  remember this doesn't use setBbox().  That is calculated when we draw
    // the circuit, but this needs to be ready before we first draw it, so we use this crude method
    Rectangle getCircuitBounds() {
    	int i;
    	int minx = 1000, maxx = 0, miny = 1000, maxy = 0;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		// centered text causes problems when trying to center the circuit,
    		// so we special-case it here
    		if (!ce.isCenteredText()) {
    			minx = min(ce.x, min(ce.x2, minx));
    			maxx = max(ce.x, max(ce.x2, maxx));
    		}
    		miny = min(ce.y, min(ce.y2, miny));
    		maxy = max(ce.y, max(ce.y2, maxy));
    	}
    	if (minx > maxx)
    	    return null;
    	return new Rectangle(minx, miny, maxx-minx, maxy-miny);
    }

    long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0;
    int frames = 0;
    int steps = 0;
    int framerate = 0, steprate = 0;
    static CirSim theSim;

    
    public void setSimRunning(boolean s) {
    	if (s) {
    		simRunning = true;
    		runStopButton.setHTML(LSHTML("<strong>RUN</strong>&nbsp;/&nbsp;Stop"));
    		runStopButton.setStylePrimaryName("topButton");
    	} else {
    		simRunning = false;
    		runStopButton.setHTML(LSHTML("Run&nbsp;/&nbsp;<strong>STOP</strong>"));
    		runStopButton.setStylePrimaryName("topButton-red");
    	}
    }
    
    public boolean simIsRunning() {
    	return simRunning;
    }
    
    
// *****************************************************************
//                     UPDATE CIRCUIT
    
    public void updateCircuit() {
    long mystarttime;
    long myrunstarttime;
    long mydrawstarttime;
//	if (winSize == null || winSize.width == 0)
//	    return;
	mystarttime=System.currentTimeMillis();
	boolean didAnalyze = analyzeFlag;
	if (analyzeFlag || dcAnalysisFlag) {
	    analyzeCircuit();
	    analyzeFlag = false;
	}
//	if (editDialog != null && editDialog.elm instanceof CircuitElm)
//	    mouseElm = (CircuitElm) (editDialog.elm);
	if (stopElm != null && stopElm != mouseElm)
	    stopElm.setMouseElm(true);
	setupScopes();

	Graphics g=new Graphics(backcontext);
	
	CircuitElm.selectColor = Color.cyan;
	if (printableCheckItem.getState()) {
  	    CircuitElm.whiteColor = Color.black;
  	    CircuitElm.lightGrayColor = Color.black;
  	    g.setColor(Color.white);
	} else {
	    CircuitElm.whiteColor = Color.white;
	    CircuitElm.lightGrayColor = Color.lightGray;
	    g.setColor(Color.black);
	}
	g.fillRect(0, 0, g.context.getCanvas().getWidth(), g.context.getCanvas().getHeight());
	myrunstarttime=System.currentTimeMillis();
	if (simRunning) {
	    try {
		runCircuit(didAnalyze);
	    } catch (Exception e) {
		console("exception in runCircuit " + e);
		e.printStackTrace();
		return;
	    }
	 myruntime+=System.currentTimeMillis()-myrunstarttime;
}
	long sysTime = System.currentTimeMillis();
		if (simRunning) {
			
			if (lastTime != 0) {
				int inc = (int) (sysTime - lastTime);
				double c = currentBar.getValue();
				c = java.lang.Math.exp(c / 3.5 - 14.2);
				CircuitElm.currentMult = 1.7 * inc * c;
				 if (!conventionCheckItem.getState())
				 CircuitElm.currentMult = -CircuitElm.currentMult;
			}

			lastTime = sysTime;
		} else
			lastTime = 0;
		
		if (sysTime - secTime >= 1000) {
			framerate = frames;
			steprate = steps;
			frames = 0;
			steps = 0;
			secTime = sysTime;
		}
	   CircuitElm.powerMult = Math.exp(powerBar.getValue()/4.762-7);
	   
	
	int i;
//	Font oldfont = g.getFont();
	Font oldfont = CircuitElm.unitsFont;
	g.setFont(oldfont);
	
	// this causes bad behavior on Chrome 55
//	g.clipRect(0, 0, circuitArea.width, circuitArea.height);
	
	mydrawstarttime=System.currentTimeMillis();
	
	g.context.setLineCap(LineCap.ROUND);

	// draw elements
	backcontext.setTransform(transform[0], transform[1], transform[2],
				 transform[3], transform[4], transform[5]);
	for (i = 0; i != elmList.size(); i++) {
	    if (powerCheckItem.getState())
	    	g.setColor(Color.gray);
	    /*else if (conductanceCheckItem.getState())
	      g.setColor(Color.white);*/
	    getElm(i).draw(g);
	}
	mydrawtime+=System.currentTimeMillis()-mydrawstarttime;
	
	// draw posts normally
	if (mouseMode != CirSim.MODE_DRAG_ROW && mouseMode != CirSim.MODE_DRAG_COLUMN) {
	    for (i = 0; i != postDrawList.size(); i++)
		CircuitElm.drawPost(g, postDrawList.get(i));
	}
	
	// for some mouse modes, what matters is not the posts but the endpoints (which are only
	// the same for 2-terminal elements).  We draw those now if needed
	if (tempMouseMode == MODE_DRAG_ROW || tempMouseMode == MODE_DRAG_COLUMN ||
			tempMouseMode == MODE_DRAG_POST || tempMouseMode == MODE_DRAG_SELECTED)
		for (i = 0; i != elmList.size(); i++) {

			CircuitElm ce = getElm(i);
//			ce.drawPost(g, ce.x , ce.y );
//			ce.drawPost(g, ce.x2, ce.y2);
			if (ce!=mouseElm || tempMouseMode!=MODE_DRAG_POST) {
				g.setColor(Color.gray);
				g.fillOval(ce.x-3, ce.y-3, 7, 7);
				g.fillOval(ce.x2-3, ce.y2-3, 7, 7);
			} else {
				ce.drawHandles(g, Color.cyan);
			}
		}
	// draw handles for elm we're creating
	if (tempMouseMode==MODE_SELECT && mouseElm!=null) {
		mouseElm.drawHandles(g, Color.cyan);
	}
	
	// draw handles for elm we're dragging
	if (dragElm != null &&
		      (dragElm.x != dragElm.x2 || dragElm.y != dragElm.y2)) {
		    	dragElm.draw(g);
		    	dragElm.drawHandles(g, Color.cyan);
		}

	// draw bad connections.  do this last so they will not be overdrawn.
	for (i = 0; i != badConnectionList.size(); i++) {
	    Point cn = badConnectionList.get(i);
	    g.setColor(Color.red);
	    g.fillOval(cn.x-3, cn.y-3, 7, 7);
	}
	
	if (selectedArea != null) {
	    g.setColor(CircuitElm.selectColor);
	    g.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);
	}

	if (crossHairCheckItem.getState() && mouseCursorX>=0
		&& mouseCursorX <= circuitArea.width && mouseCursorY <= circuitArea.height) {
	    g.setColor(Color.gray);
	    int x = snapGrid(inverseTransformX(mouseCursorX));
	    int y = snapGrid(inverseTransformY(mouseCursorY));
	    g.drawLine(x, inverseTransformY(0), x, inverseTransformY(circuitArea.height));
	    g.drawLine(inverseTransformX(0), y, inverseTransformX(circuitArea.width), y);
	}

	backcontext.setTransform(1, 0, 0, 1, 0, 0);

	if (printableCheckItem.getState())
	    g.setColor(Color.white);
	else
	    g.setColor(Color.black);
	g.fillRect(0, circuitArea.height, circuitArea.width, cv.getCoordinateSpaceHeight()-circuitArea.height);
//	g.restore();
	g.setFont(oldfont);
	int ct = scopeCount;
	if (stopMessage != null)
	    ct = 0;
	for (i = 0; i != ct; i++)
	    scopes[i].draw(g);
	if (mouseWasOverSplitter) {
		g.setColor(Color.cyan);
		g.setLineWidth(4.0);
		g.drawLine(0, circuitArea.height-2, circuitArea.width, circuitArea.height-2);
		g.setLineWidth(1.0);
	}
	g.setColor(CircuitElm.whiteColor);

	if (stopMessage != null) {
	    g.drawString(stopMessage, 10, circuitArea.height-10);
	} else {
	    String info[] = new String[10];
	    if (mouseElm != null) {
		if (mousePost == -1) {
		    mouseElm.getInfo(info);
		    info[0] = LS(info[0]);
		    if (info[1] != null)
			info[1] = LS(info[1]);
		} else
		    info[0] = "V = " +
			CircuitElm.getUnitText(mouseElm.getPostVoltage(mousePost), "V");
		/* //shownodes
		for (i = 0; i != mouseElm.getPostCount(); i++)
		    info[0] += " " + mouseElm.nodes[i];
		if (mouseElm.getVoltageSourceCount() > 0)
		    info[0] += ";" + (mouseElm.getVoltageSource()+nodeList.size());
		*/
		
	    } else {
	    	info[0] = "t = " + CircuitElm.getUnitText(t, "s");
	    	info[1] = LS("time step = ") + CircuitElm.getUnitText(timeStep, "s");
	    }
	    if (hintType != -1) {
		for (i = 0; info[i] != null; i++)
		    ;
		String s = getHint();
		if (s == null)
		    hintType = -1;
		else
		    info[i] = s;
	    }
	    int x = 0;
	    if (ct != 0)
		x = scopes[ct-1].rightEdge() + 20;
	    x = max(x, cv.getCoordinateSpaceWidth()*2/3);
	  //  x=cv.getCoordinateSpaceWidth()*2/3;
	    
	    // count lines of data
	    for (i = 0; info[i] != null; i++)
		;
	    int badnodes = badConnectionList.size();
	    if (badnodes > 0)
		info[i++] = badnodes + ((badnodes == 1) ?
					LS(" bad connection") : LS(" bad connections"));
	    
	    int ybase = circuitArea.height;
	    for (i = 0; info[i] != null; i++)
		g.drawString(info[i], x,
			     ybase+15*(i+1));
	}
	if (stopElm != null && stopElm != mouseElm)
	    stopElm.setMouseElm(false);
	frames++;
	
	g.setColor(Color.white);
//	g.drawString("Framerate: " + CircuitElm.showFormat.format(framerate), 10, 10);
//	g.drawString("Steprate: " + CircuitElm.showFormat.format(steprate),  10, 30);
//	g.drawString("Steprate/iter: " + CircuitElm.showFormat.format(steprate/getIterCount()),  10, 50);
//	g.drawString("iterc: " + CircuitElm.showFormat.format(getIterCount()),  10, 70);
//	g.drawString("Frames: "+ frames,10,90);
//	g.drawString("ms per frame (other): "+ CircuitElm.showFormat.format((mytime-myruntime-mydrawtime)/myframes),10,110);
//	g.drawString("ms per frame (sim): "+ CircuitElm.showFormat.format((myruntime)/myframes),10,130);
//	g.drawString("ms per frame (draw): "+ CircuitElm.showFormat.format((mydrawtime)/myframes),10,150);
	
	cvcontext.drawImage(backcontext.getCanvas(), 0.0, 0.0);
	
	// if we did DC analysis, we need to re-analyze the circuit with that flag cleared. 
	if (dcAnalysisFlag) {
	    dcAnalysisFlag = false;
	    analyzeFlag = true;
	}

	lastFrameTime = lastTime;
	mytime=mytime+System.currentTimeMillis()-mystarttime;
	myframes++;
    }

    
    void setupScopes() {
    	int i;

    	// check scopes to make sure the elements still exist, and remove
    	// unused scopes/columns
    	int pos = -1;
    	for (i = 0; i < scopeCount; i++) {
    	    	if (scopes[i].needToRemove()) {
    			int j;
    			for (j = i; j != scopeCount; j++)
    				scopes[j] = scopes[j+1];
    			scopeCount--;
    			i--;
    			continue;
    		}
    		if (scopes[i].position > pos+1)
    			scopes[i].position = pos+1;
    		pos = scopes[i].position;
    	}
    	while (scopeCount > 0 && scopes[scopeCount-1].getElm() == null)
    		scopeCount--;
    	int h = cv.getCoordinateSpaceHeight() - circuitArea.height;
    	pos = 0;
    	for (i = 0; i != scopeCount; i++)
    		scopeColCount[i] = 0;
    	for (i = 0; i != scopeCount; i++) {
    		pos = max(scopes[i].position, pos);
    		scopeColCount[scopes[i].position]++;
    	}
    	int colct = pos+1;
    	int iw = infoWidth;
    	if (colct <= 2)
    		iw = iw*3/2;
    	int w = (cv.getCoordinateSpaceWidth()-iw) / colct;
    	int marg = 10;
    	if (w < marg*2)
    		w = marg*2;
    	pos = -1;
    	int colh = 0;
    	int row = 0;
    	int speed = 0;
    	for (i = 0; i != scopeCount; i++) {
    		Scope s = scopes[i];
    		if (s.position > pos) {
    			pos = s.position;
    			colh = h / scopeColCount[pos];
    			row = 0;
    			speed = s.speed;
    		}
    		s.stackCount = scopeColCount[pos];
    		if (s.speed != speed) {
    			s.speed = speed;
    			s.resetGraph();
    		}
    		Rectangle r = new Rectangle(pos*w, cv.getCoordinateSpaceHeight()-h+colh*row,
    				w-marg, colh);
    		row++;
    		if (!r.equals(s.rect))
    			s.setRect(r);
    	}
    }
    
    String getHint() {
	CircuitElm c1 = getElm(hintItem1);
	CircuitElm c2 = getElm(hintItem2);
	if (c1 == null || c2 == null)
	    return null;
	if (hintType == HINT_LC) {
	    if (!(c1 instanceof InductorElm))
		return null;
	    if (!(c2 instanceof CapacitorElm))
		return null;
	    InductorElm ie = (InductorElm) c1;
	    CapacitorElm ce = (CapacitorElm) c2;
	    return LS("res.f = ") + CircuitElm.getUnitText(1/(2*pi*Math.sqrt(ie.inductance*
						    ce.capacitance)), "Hz");
	}
	if (hintType == HINT_RC) {
	    if (!(c1 instanceof ResistorElm))
		return null;
	    if (!(c2 instanceof CapacitorElm))
		return null;
	    ResistorElm re = (ResistorElm) c1;
	    CapacitorElm ce = (CapacitorElm) c2;
	    return "RC = " + CircuitElm.getUnitText(re.resistance*ce.capacitance,
					 "s");
	}
	if (hintType == HINT_3DB_C) {
	    if (!(c1 instanceof ResistorElm))
		return null;
	    if (!(c2 instanceof CapacitorElm))
		return null;
	    ResistorElm re = (ResistorElm) c1;
	    CapacitorElm ce = (CapacitorElm) c2;
	    return LS("f.3db = ") +
		CircuitElm.getUnitText(1/(2*pi*re.resistance*ce.capacitance), "Hz");
	}
	if (hintType == HINT_3DB_L) {
	    if (!(c1 instanceof ResistorElm))
		return null;
	    if (!(c2 instanceof InductorElm))
		return null;
	    ResistorElm re = (ResistorElm) c1;
	    InductorElm ie = (InductorElm) c2;
	    return LS("f.3db = ") +
		CircuitElm.getUnitText(re.resistance/(2*pi*ie.inductance), "Hz");
	}
	if (hintType == HINT_TWINT) {
	    if (!(c1 instanceof ResistorElm))
		return null;
	    if (!(c2 instanceof CapacitorElm))
		return null;
	    ResistorElm re = (ResistorElm) c1;
	    CapacitorElm ce = (CapacitorElm) c2;
	    return LS("fc = ") +
		CircuitElm.getUnitText(1/(2*pi*re.resistance*ce.capacitance), "Hz");
	}
	return null;
    }

//    public void toggleSwitch(int n) {
//	int i;
//	for (i = 0; i != elmList.size(); i++) {
//	    CircuitElm ce = getElm(i);
//	    if (ce instanceof SwitchElm) {
//		n--;
//		if (n == 0) {
//		    ((SwitchElm) ce).toggle();
//		    analyzeFlag = true;
//		    cv.repaint();
//		    return;
//		}
//	    }
//	}
//    }
    
    void needAnalyze() {
	analyzeFlag = true;
	//cv.repaint();
    }
    
    Vector<CircuitNode> nodeList;
    Vector<Point> postDrawList = new Vector<Point>();
    Vector<Point> badConnectionList = new Vector<Point>();
    CircuitElm voltageSources[];

    public CircuitNode getCircuitNode(int n) {
	if (n >= nodeList.size())
	    return null;
	return nodeList.elementAt(n);
    }

    public CircuitElm getElm(int n) {
	if (n >= elmList.size())
	    return null;
	return elmList.elementAt(n);
    }
    
    public static native void console(String text)
    /*-{
	    console.log(text);
	}-*/;

    class NodeMapEntry {
	int node;
	NodeMapEntry() { node = -1; }
	NodeMapEntry(int n) { node = n; }
    }
    // map points to node numbers
    HashMap<Point,NodeMapEntry> nodeMap;
    HashMap<Point,Integer> postCountMap;
    
    class WireInfo {
	WireElm wire;
	Vector<CircuitElm> neighbors;
	int post;
	WireInfo(WireElm w) {
	    wire = w;
	}
    }
    // info about each wire and its neighbors, used to calculate wire currents
    Vector<WireInfo> wireInfoList;
    
    // find groups of nodes connected by wires and map them to the same node.  this speeds things
    // up considerably by reducing the size of the matrix
    void calculateWireClosure() {
	int i;
	nodeMap = new HashMap<Point,NodeMapEntry>();
//	int mergeCount = 0;
	wireInfoList = new Vector<WireInfo>();
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (!(ce instanceof WireElm))
		continue;
	    WireElm we = (WireElm) ce;
	    we.hasWireInfo = false;
	    wireInfoList.add(new WireInfo(we));
	    NodeMapEntry cn  = nodeMap.get(ce.getPost(0));
	    NodeMapEntry cn2 = nodeMap.get(ce.getPost(1));
	    if (cn != null && cn2 != null) {
		// merge nodes; go through map and change all keys pointing to cn2 to point to cn
		for (Map.Entry<Point, NodeMapEntry> entry : nodeMap.entrySet()) {
		    if (entry.getValue() == cn2)
			entry.setValue(cn);
		}
//		mergeCount++;
		continue;
	    }
	    if (cn != null) {
		nodeMap.put(ce.getPost(1), cn);
		continue;
	    }
	    if (cn2 != null) {
		nodeMap.put(ce.getPost(0), cn2);
		continue;
	    }
	    // new entry
	    cn = new NodeMapEntry();
	    nodeMap.put(ce.getPost(0), cn);
	    nodeMap.put(ce.getPost(1), cn);
	}
	
//	console("got " + (groupCount-mergeCount) + " groups with " + nodeMap.size() + " nodes " + mergeCount);
    }
    
    // generate info we need to calculate wire currents.  Most other elements calculate currents using
    // the voltage on their terminal nodes.  But wires have the same voltage at both ends, so we need
    // to use the neighbors' currents instead.
    boolean calcWireInfo() {
	int i;
	int moved = 0;
	for (i = 0; i != wireInfoList.size(); i++) {
	    WireInfo wi = wireInfoList.get(i);
	    WireElm wire = wi.wire;
	    CircuitNode cn1 = nodeList.get(wire.getNode(0));  // both ends of wire have same node #
	    int j;

	    Vector<CircuitElm> neighbors0 = new Vector<CircuitElm>();
	    Vector<CircuitElm> neighbors1 = new Vector<CircuitElm>();
	    boolean isReady0 = true, isReady1 = true;
	    
	    // go through elements sharing a node with this wire (may be connected indirectly
	    // by other wires, but at least it's faster than going through all elements)
	    for (j = 0; j != cn1.links.size(); j++) {
		CircuitNodeLink cnl = cn1.links.get(j);
		CircuitElm ce = cnl.elm;
		if (ce == wire)
		    continue;
		Point pt = cnl.elm.getPost(cnl.num);
		
		// is this a wire that doesn't have wire info yet?  If so we can't use it.
		// That would create a circular dependency
		boolean notReady = (ce instanceof WireElm && !((WireElm) ce).hasWireInfo);
		
		// which post does this element connect to, if any?
		if (pt.x == wire.x && pt.y == wire.y) {
		    neighbors0.add(ce);
		    if (notReady) isReady0 = false;
		} else if (pt.x == wire.x2 && pt.y == wire.y2) {
		    neighbors1.add(ce);
		    if (notReady) isReady1 = false;
		}
	    }

	    // does one of the posts have all information necessary to calculate current
	    if (isReady0) {
		wi.neighbors = neighbors0;
		wi.post = 0;
		wire.hasWireInfo = true;
	    } else if (isReady1) {
		wi.neighbors = neighbors1;
		wi.post = 1;
		wire.hasWireInfo = true;
	    } else {
		// move to the end of the list and try again later
		wireInfoList.add(wireInfoList.remove(i--));
		moved++; 
//		console("moved to end " + moved);
		if (moved > wireInfoList.size() * 2) {
		    stop("wire loop detected", wire);
		    return false;
		}
	    }
	}
	
	return true;
    }
    
    void analyzeCircuit() {
	if (elmList.isEmpty()) {
	    postDrawList = new Vector<Point>();
	    badConnectionList = new Vector<Point>();
	    return;
	}
	stopMessage = null;
	stopElm = null;
	int i, j;
	int vscount = 0;
	nodeList = new Vector<CircuitNode>();
	postCountMap = new HashMap<Point,Integer>();
	boolean gotGround = false;
	boolean gotRail = false;
	CircuitElm volt = null;

	calculateWireClosure();
	
	//System.out.println("ac1");
	// look for voltage or ground element
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (ce instanceof GroundElm) {
		gotGround = true;
		break;
	    }
	    if (ce instanceof RailElm)
	    	gotRail = true;
	    if (volt == null && ce instanceof VoltageElm)
	    	volt = ce;
	}

	// if no ground, and no rails, then the voltage elm's first terminal
	// is ground
	if (!gotGround && volt != null && !gotRail) {
	    CircuitNode cn = new CircuitNode();
	    Point pt = volt.getPost(0);
	    nodeList.addElement(cn);

	    // update node map
	    NodeMapEntry cln = nodeMap.get(pt);
	    if (cln != null)
		cln.node = 0;
	    else
		nodeMap.put(pt, new NodeMapEntry(0));
	} else {
	    // otherwise allocate extra node for ground
	    CircuitNode cn = new CircuitNode();
	    nodeList.addElement(cn);
	}
	//System.out.println("ac2");

	// allocate nodes and voltage sources
	LabeledNodeElm.resetNodeList();
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    int inodes = ce.getInternalNodeCount();
	    int ivs = ce.getVoltageSourceCount();
	    int posts = ce.getPostCount();
	    
	    // allocate a node for each post and match posts to nodes
	    for (j = 0; j != posts; j++) {
		Point pt = ce.getPost(j);
		Integer g = postCountMap.get(pt);
		postCountMap.put(pt, g == null ? 1 : g+1);
		NodeMapEntry cln = nodeMap.get(pt);
		
		// is this node not in map yet?  or is the node number unallocated?
		// (we don't allocate nodes before this because changing the allocation order
		// of nodes changes circuit behavior and breaks backward compatibility;
		// the code below to connect unconnected nodes may connect a different node to ground) 
		if (cln == null || cln.node == -1) {
		    CircuitNode cn = new CircuitNode();
		    CircuitNodeLink cnl = new CircuitNodeLink();
		    cnl.num = j;
		    cnl.elm = ce;
		    cn.links.addElement(cnl);
		    ce.setNode(j, nodeList.size());
		    if (cln != null)
			cln.node = nodeList.size();
		    else
			nodeMap.put(pt, new NodeMapEntry(nodeList.size()));
		    nodeList.addElement(cn);
		} else {
		    int n = cln.node;
		    CircuitNodeLink cnl = new CircuitNodeLink();
		    cnl.num = j;
		    cnl.elm = ce;
		    getCircuitNode(n).links.addElement(cnl);
		    ce.setNode(j, n);
		    // if it's the ground node, make sure the node voltage is 0,
		    // cause it may not get set later
		    if (n == 0)
			ce.setNodeVoltage(j, 0);
		}
	    }
	    for (j = 0; j != inodes; j++) {
		CircuitNode cn = new CircuitNode();
		cn.internal = true;
		CircuitNodeLink cnl = new CircuitNodeLink();
		cnl.num = j+posts;
		cnl.elm = ce;
		cn.links.addElement(cnl);
		ce.setNode(cnl.num, nodeList.size());
		nodeList.addElement(cn);
	    }
	    vscount += ivs;
	}
	
	makePostDrawList();
	if (!calcWireInfo())
	    return;
	nodeMap = null; // done with this
	
	voltageSources = new CircuitElm[vscount];
	vscount = 0;
	circuitNonLinear = false;
	//System.out.println("ac3");

	// determine if circuit is nonlinear
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (ce.nonLinear())
		circuitNonLinear = true;
	    int ivs = ce.getVoltageSourceCount();
	    for (j = 0; j != ivs; j++) {
		voltageSources[vscount] = ce;
		ce.setVoltageSource(j, vscount++);
	    }
	}
	voltageSourceCount = vscount;

	int matrixSize = nodeList.size()-1 + vscount;
	circuitMatrix = new double[matrixSize][matrixSize];
	circuitRightSide = new double[matrixSize];
	origMatrix = new double[matrixSize][matrixSize];
	origRightSide = new double[matrixSize];
	circuitMatrixSize = circuitMatrixFullSize = matrixSize;
	circuitRowInfo = new RowInfo[matrixSize];
	circuitPermute = new int[matrixSize];
	for (i = 0; i != matrixSize; i++)
	    circuitRowInfo[i] = new RowInfo();
	circuitNeedsMap = false;
	
	// stamp linear circuit elements
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    ce.stamp();
	}
	//System.out.println("ac4");

	// determine nodes that are not connected indirectly to ground
	boolean closure[] = new boolean[nodeList.size()];
	boolean changed = true;
	closure[0] = true;
	while (changed) {
	    changed = false;
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		if (ce instanceof WireElm)
		    continue;
		// loop through all ce's nodes to see if they are connected
		// to other nodes not in closure
		for (j = 0; j < ce.getConnectionNodeCount(); j++) {
		    if (!closure[ce.getConnectionNode(j)]) {
			if (ce.hasGroundConnection(j))
			    closure[ce.getConnectionNode(j)] = changed = true;
			continue;
		    }
		    int k;
		    for (k = 0; k != ce.getConnectionNodeCount(); k++) {
			if (j == k)
			    continue;
			int kn = ce.getConnectionNode(k);
			if (ce.getConnection(j, k) && !closure[kn]) {
			    closure[kn] = true;
			    changed = true;
			}
		    }
		}
	    }
	    if (changed)
		continue;

	    // connect one of the unconnected nodes to ground with a big resistor, then try again
	    for (i = 0; i != nodeList.size(); i++)
		if (!closure[i] && !getCircuitNode(i).internal) {
		    console("node " + i + " unconnected");
		    stampResistor(0, i, 1e8);
		    closure[i] = true;
		    changed = true;
		    break;
		}
	}
	//System.out.println("ac5");

	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    // look for inductors with no current path
	    if (ce instanceof InductorElm) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
						    ce.getNode(1));
		// first try findPath with maximum depth of 5, to avoid slowdowns
		if (!fpi.findPath(ce.getNode(0), 5) &&
		    !fpi.findPath(ce.getNode(0))) {
//		    console(ce + " no path");
		    ce.reset();
		}
	    }
	    // look for current sources with no current path
	    if (ce instanceof CurrentElm) {
		CurrentElm cur = (CurrentElm) ce;
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
						    ce.getNode(1));
		// first try findPath with maximum depth of 5, to avoid slowdowns
		if (!fpi.findPath(ce.getNode(0), 5) &&
		    !fpi.findPath(ce.getNode(0))) {
		    cur.stampCurrentSource(true);
		} else
		    cur.stampCurrentSource(false);
	    }
	    if (ce instanceof VCCSElm) {
		VCCSElm cur = (VCCSElm) ce;
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
						    cur.getOutputNode(0));
		if (cur.hasCurrentOutput() && !fpi.findPath(cur.getOutputNode(1))) {
		    cur.broken = true;
		} else
		    cur.broken = false;
	    }
	    // look for voltage source loops
	    // IES
	    if ((ce instanceof VoltageElm && ce.getPostCount() == 2) /*|| ce instanceof WireElm*/) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce,
						    ce.getNode(1));
		if (fpi.findPath(ce.getNode(0))) {
		    stop("Voltage source/wire loop with no resistance!", ce);
		    return;
		}
	    }
	    // look for shorted caps, or caps w/ voltage but no R
	    if (ce instanceof CapacitorElm) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce,
						    ce.getNode(1));
		if (fpi.findPath(ce.getNode(0))) {
		    console(ce + " shorted");
		    ce.reset();
		} else {
		    // a capacitor loop used to cause a matrix error. but we changed the capacitor model
		    // so it works fine now. The only issue is if a capacitor is added in parallel with
		    // another capacitor with a nonzero voltage; in that case we will get oscillation unless
		    // we reset both capacitors to have the same voltage. Rather than check for that, we just
		    // give an error.
		    fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1));
		    if (fpi.findPath(ce.getNode(0))) {
			stop("Capacitor loop with no resistance!", ce);
			return;
		    }
		}
	    }
	}
	//System.out.println("ac6");

	simplifyMatrix(matrixSize);
	/*
	System.out.println("matrixSize = " + matrixSize + " " + circuitNonLinear);
	for (j = 0; j != circuitMatrixSize; j++) {
	    for (i = 0; i != circuitMatrixSize; i++)
		System.out.print(circuitMatrix[j][i] + " ");
	    System.out.print("  " + circuitRightSide[j] + "\n");
	}
	System.out.print("\n");*/

	// if a matrix is linear, we can do the lu_factor here instead of
	// needing to do it every frame
	if (!circuitNonLinear) {
	    if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
		stop("Singular matrix!", null);
		return;
	    }
	}
	
	// show resistance in voltage sources if there's only one
	boolean gotVoltageSource = false;
	showResistanceInVoltageSources = true;
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (ce instanceof VoltageElm) {
		if (gotVoltageSource)
		    showResistanceInVoltageSources = false;
		else
		    gotVoltageSource = true;
	    }
	}

    }

    // simplify the matrix; this speeds things up quite a bit, especially for
    // digital circuits
    void simplifyMatrix(int matrixSize) {
	int i, j;
	for (i = 0; i != matrixSize; i++) {
	    int qp = -1;
	    double qv = 0;
	    RowInfo re = circuitRowInfo[i];
	    /*System.out.println("row " + i + " " + re.lsChanges + " " + re.rsChanges + " " +
			       re.dropRow);*/
	    if (re.lsChanges || re.dropRow || re.rsChanges)
		continue;
	    double rsadd = 0;

	    // look for rows that can be removed
	    for (j = 0; j != matrixSize; j++) {
		double q = circuitMatrix[i][j];
		if (circuitRowInfo[j].type == RowInfo.ROW_CONST) {
		    // keep a running total of const values that have been
		    // removed already
		    rsadd -= circuitRowInfo[j].value*q;
		    continue;
		}
		// ignore zeroes
		if (q == 0)
		    continue;
		// keep track of first nonzero element that is not ROW_CONST
		if (qp == -1) {
		    qp = j;
		    qv = q;
		    continue;
		}
		// more than one nonzero element?  give up
		break;
	    }
	    if (j == matrixSize) {
		if (qp == -1) {
		    stop("Matrix error", null);
		    return;
		}
		RowInfo elt = circuitRowInfo[qp];
		// we found a row with only one nonzero nonconst entry; that value
		// is a constant
		if (elt.type != RowInfo.ROW_NORMAL) {
		    System.out.println("type already " + elt.type + " for " + qp + "!");
		    continue;
		}
		elt.type = RowInfo.ROW_CONST;
//		console("ROW_CONST " + i + " " + rsadd);
		elt.value = (circuitRightSide[i]+rsadd)/qv;
		circuitRowInfo[i].dropRow = true;
		i = -1; // start over from scratch
	    }
	}
	//System.out.println("ac7");

	// find size of new matrix
	int nn = 0;
	for (i = 0; i != matrixSize; i++) {
	    RowInfo elt = circuitRowInfo[i];
	    if (elt.type == RowInfo.ROW_NORMAL) {
		elt.mapCol = nn++;
		//System.out.println("col " + i + " maps to " + elt.mapCol);
		continue;
	    }
	    if (elt.type == RowInfo.ROW_CONST)
		elt.mapCol = -1;
	}

	// make the new, simplified matrix
	int newsize = nn;
	double newmatx[][] = new double[newsize][newsize];
	double newrs  []   = new double[newsize];
	int ii = 0;
	for (i = 0; i != matrixSize; i++) {
	    RowInfo rri = circuitRowInfo[i];
	    if (rri.dropRow) {
		rri.mapRow = -1;
		continue;
	    }
	    newrs[ii] = circuitRightSide[i];
	    rri.mapRow = ii;
	    //System.out.println("Row " + i + " maps to " + ii);
	    for (j = 0; j != matrixSize; j++) {
		RowInfo ri = circuitRowInfo[j];
		if (ri.type == RowInfo.ROW_CONST)
		    newrs[ii] -= ri.value*circuitMatrix[i][j];
		else
		    newmatx[ii][ri.mapCol] += circuitMatrix[i][j];
	    }
	    ii++;
	}

//	console("old size = " + matrixSize + " new size = " + newsize);
	
	circuitMatrix = newmatx;
	circuitRightSide = newrs;
	matrixSize = circuitMatrixSize = newsize;
	for (i = 0; i != matrixSize; i++)
	    origRightSide[i] = circuitRightSide[i];
	for (i = 0; i != matrixSize; i++)
	    for (j = 0; j != matrixSize; j++)
		origMatrix[i][j] = circuitMatrix[i][j];
	circuitNeedsMap = true;
    }
    
    // make list of posts we need to draw.  posts shared by 2 elements should be hidden, all
    // others should be drawn.  We can't use the node list anymore because wires have the same
    // node number at both ends.
    void makePostDrawList() {
	postDrawList = new Vector<Point>();
	badConnectionList = new Vector<Point>();
	for (Map.Entry<Point, Integer> entry : postCountMap.entrySet()) {
	    if (entry.getValue() != 2)
		postDrawList.add(entry.getKey());
	    
	    // look for bad connections, posts not connected to other elements which intersect
	    // other elements' bounding boxes
	    if (entry.getValue() == 1) {
		int j;
		boolean bad = false;
		Point cn = entry.getKey();
		for (j = 0; j != elmList.size() && !bad; j++) {
		    CircuitElm ce = getElm(j);
		    if ( ce instanceof GraphicElm )
			continue;
		    // does this post intersect elm's bounding box?
		    if (!ce.boundingBox.contains(cn.x, cn.y))
			continue;
		    int k;
		    // does this post belong to the elm?
		    int pc = ce.getPostCount();
		    for (k = 0; k != pc; k++)
			if (ce.getPost(k).equals(cn))
			    break;
		    if (k == pc)
			bad = true;
		}
		if (bad)
		    badConnectionList.add(cn);
	    }
	}
	postCountMap = null;
    }

    class FindPathInfo {
	static final int INDUCT  = 1;
	static final int VOLTAGE = 2;
	static final int SHORT   = 3;
	static final int CAP_V   = 4;
	boolean used[];
	int dest;
	CircuitElm firstElm;
	int type;
	FindPathInfo(int t, CircuitElm e, int d) {
	    dest = d;
	    type = t;
	    firstElm = e;
	    used = new boolean[nodeList.size()];
	}
	boolean findPath(int n1) { return findPath(n1, -1); }
	boolean findPath(int n1, int depth) {
	    if (n1 == dest)
		return true;
	    if (depth-- == 0)
		return false;
	    if (used[n1]) {
		//System.out.println("used " + n1);
		return false;
	    }
	    used[n1] = true;
	    int i;
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		if (ce == firstElm)
		    continue;
		if (type == INDUCT) {
		    // inductors need a path free of current sources
		    if (ce instanceof CurrentElm)
			continue;
		}
		if (type == VOLTAGE) {
		    // when checking for voltage loops, we only care about voltage sources/wires
		    if (!(ce.isWire() || ce instanceof VoltageElm))
			continue;
		}
		// when checking for shorts, just check wires
		if (type == SHORT && !ce.isWire())
		    continue;
		if (type == CAP_V) {
		    // checking for capacitor/voltage source loops
		    if (!(ce.isWire() || ce instanceof CapacitorElm || ce instanceof VoltageElm))
			continue;
		}
		if (n1 == 0) {
		    // look for posts which have a ground connection;
		    // our path can go through ground
		    int j;
		    for (j = 0; j != ce.getConnectionNodeCount(); j++)
			if (ce.hasGroundConnection(j) &&
			    findPath(ce.getConnectionNode(j), depth)) {
			    used[n1] = false;
			    return true;
			}
		}
		int j;
		for (j = 0; j != ce.getConnectionNodeCount(); j++) {
		    //System.out.println(ce + " " + ce.getNode(j));
		    if (ce.getConnectionNode(j) == n1)
			break;
		}
		if (j == ce.getConnectionNodeCount())
		    continue;
		if (ce.hasGroundConnection(j) && findPath(0, depth)) {
		    //System.out.println(ce + " has ground");
		    used[n1] = false;
		    return true;
		}
		if (type == INDUCT && ce instanceof InductorElm) {
		    // inductors can use paths with other inductors of matching current
		    double c = ce.getCurrent();
		    if (j == 0)
			c = -c;
		    //System.out.println("matching " + c + " to " + firstElm.getCurrent());
		    //System.out.println(ce + " " + firstElm);
		    if (Math.abs(c-firstElm.getCurrent()) > 1e-10)
			continue;
		}
		int k;
		for (k = 0; k != ce.getConnectionNodeCount(); k++) {
		    if (j == k)
			continue;
//		    console(ce + " " + ce.getNode(j) + "-" + ce.getNode(k));
		    if (ce.getConnection(j, k) && findPath(ce.getConnectionNode(k), depth)) {
			//System.out.println("got findpath " + n1);
			used[n1] = false;
			return true;
		    }
		    //System.out.println("back on findpath " + n1);
		}
	    }
	    used[n1] = false;
	    //System.out.println(n1 + " failed");
	    return false;
	}
    }

    void stop(String s, CircuitElm ce) {
	stopMessage = LS(s);
	circuitMatrix = null;  // causes an exception
	stopElm = ce;
	setSimRunning(false);
	analyzeFlag = false;
//	cv.repaint();
    }
    
    // control voltage source vs with voltage from n1 to n2 (must
    // also call stampVoltageSource())
    void stampVCVS(int n1, int n2, double coef, int vs) {
	int vn = nodeList.size()+vs;
	stampMatrix(vn, n1, coef);
	stampMatrix(vn, n2, -coef);
    }
    
    // stamp independent voltage source #vs, from n1 to n2, amount v
    void stampVoltageSource(int n1, int n2, int vs, double v) {
	int vn = nodeList.size()+vs;
	stampMatrix(vn, n1, -1);
	stampMatrix(vn, n2, 1);
	stampRightSide(vn, v);
	stampMatrix(n1, vn, 1);
	stampMatrix(n2, vn, -1);
    }

    // use this if the amount of voltage is going to be updated in doStep()
    void stampVoltageSource(int n1, int n2, int vs) {
	int vn = nodeList.size()+vs;
	stampMatrix(vn, n1, -1);
	stampMatrix(vn, n2, 1);
	stampRightSide(vn);
	stampMatrix(n1, vn, 1);
	stampMatrix(n2, vn, -1);
    }
    
    void updateVoltageSource(int n1, int n2, int vs, double v) {
	int vn = nodeList.size()+vs;
	stampRightSide(vn, v);
    }
    
    void stampResistor(int n1, int n2, double r) {
	double r0 = 1/r;
	if (Double.isNaN(r0) || Double.isInfinite(r0)) {
	    System.out.print("bad resistance " + r + " " + r0 + "\n");
	    int a = 0;
	    a /= a;
	}
	stampMatrix(n1, n1, r0);
	stampMatrix(n2, n2, r0);
	stampMatrix(n1, n2, -r0);
	stampMatrix(n2, n1, -r0);
    }

    void stampConductance(int n1, int n2, double r0) {
	stampMatrix(n1, n1, r0);
	stampMatrix(n2, n2, r0);
	stampMatrix(n1, n2, -r0);
	stampMatrix(n2, n1, -r0);
    }

    // current from cn1 to cn2 is equal to voltage from vn1 to 2, divided by g
    void stampVCCurrentSource(int cn1, int cn2, int vn1, int vn2, double g) {
	stampMatrix(cn1, vn1, g);
	stampMatrix(cn2, vn2, g);
	stampMatrix(cn1, vn2, -g);
	stampMatrix(cn2, vn1, -g);
    }

    void stampCurrentSource(int n1, int n2, double i) {
	stampRightSide(n1, -i);
	stampRightSide(n2, i);
    }

    // stamp a current source from n1 to n2 depending on current through vs
    void stampCCCS(int n1, int n2, int vs, double gain) {
	int vn = nodeList.size()+vs;
	stampMatrix(n1, vn, gain);
	stampMatrix(n2, vn, -gain);
    }

    // stamp value x in row i, column j, meaning that a voltage change
    // of dv in node j will increase the current into node i by x dv.
    // (Unless i or j is a voltage source node.)
    void stampMatrix(int i, int j, double x) {
	if (i > 0 && j > 0) {
	    if (circuitNeedsMap) {
		i = circuitRowInfo[i-1].mapRow;
		RowInfo ri = circuitRowInfo[j-1];
		if (ri.type == RowInfo.ROW_CONST) {
		    //System.out.println("Stamping constant " + i + " " + j + " " + x);
		    circuitRightSide[i] -= x*ri.value;
		    return;
		}
		j = ri.mapCol;
		//System.out.println("stamping " + i + " " + j + " " + x);
	    } else {
		i--;
		j--;
	    }
	    circuitMatrix[i][j] += x;
	}
    }

    // stamp value x on the right side of row i, representing an
    // independent current source flowing into node i
    void stampRightSide(int i, double x) {
	if (i > 0) {
	    if (circuitNeedsMap) {
		i = circuitRowInfo[i-1].mapRow;
		//System.out.println("stamping " + i + " " + x);
	    } else
		i--;
	    circuitRightSide[i] += x;
	}
    }

    // indicate that the value on the right side of row i changes in doStep()
    void stampRightSide(int i) {
	//System.out.println("rschanges true " + (i-1));
	if (i > 0)
	    circuitRowInfo[i-1].rsChanges = true;
    }
    
    // indicate that the values on the left side of row i change in doStep()
    void stampNonLinear(int i) {
	if (i > 0)
	    circuitRowInfo[i-1].lsChanges = true;
    }

    double getIterCount() {
    	// IES - remove interaction
	if (speedBar.getValue() == 0)
	   return 0;

	 return .1*Math.exp((speedBar.getValue()-61)/24.);

    }

    // we need to calculate wire currents for every iteration if someone is viewing a wire in the
    // scope.  Otherwise we can do it only once per frame.
    boolean canDelayWireProcessing() {
	int i;
	for (i = 0; i != scopeCount; i++)
	    if (scopes[i].viewingWire())
		return false;
	return true;
    }
    
    boolean converged;
    int subIterations;
    void runCircuit(boolean didAnalyze) {
	if (circuitMatrix == null || elmList.size() == 0) {
	    circuitMatrix = null;
	    return;
	}
	int iter;
	//int maxIter = getIterCount();
	boolean debugprint = dumpMatrix;
	dumpMatrix = false;
	long steprate = (long) (160*getIterCount());
	long tm = System.currentTimeMillis();
	long lit = lastIterTime;
	if (lit == 0) {
	    lastIterTime = tm;
	    return;
	}
	
	// Check if we don't need to run simulation (for very slow simulation speeds).
	// If the circuit changed, do at least one iteration to make sure everything is consistent.
	if (1000 >= steprate*(tm-lastIterTime) && !didAnalyze)
	    return;
	
	boolean delayWireProcessing = canDelayWireProcessing();
	
	for (iter = 1; ; iter++) {
	    int i, j, k, subiter;
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		ce.startIteration();
	    }
	    steps++;
	    final int subiterCount = 5000;
	    for (subiter = 0; subiter != subiterCount; subiter++) {
		converged = true;
		subIterations = subiter;
		for (i = 0; i != circuitMatrixSize; i++)
		    circuitRightSide[i] = origRightSide[i];
		if (circuitNonLinear) {
		    for (i = 0; i != circuitMatrixSize; i++)
			for (j = 0; j != circuitMatrixSize; j++)
			    circuitMatrix[i][j] = origMatrix[i][j];
		}
		for (i = 0; i != elmList.size(); i++) {
		    CircuitElm ce = getElm(i);
		    ce.doStep();
		}
		if (stopMessage != null)
		    return;
		boolean printit = debugprint;
		debugprint = false;
		for (j = 0; j != circuitMatrixSize; j++) {
		    for (i = 0; i != circuitMatrixSize; i++) {
			double x = circuitMatrix[i][j];
			if (Double.isNaN(x) || Double.isInfinite(x)) {
			    stop("nan/infinite matrix!", null);
			    return;
			}
		    }
		}
		if (printit) {
		    for (j = 0; j != circuitMatrixSize; j++) {
			String x = "";
			for (i = 0; i != circuitMatrixSize; i++)
			    x += circuitMatrix[j][i] + ",";
			x += "\n";
			console(x);
		    }
		    console("");
		}
		if (circuitNonLinear) {
		    if (converged && subiter > 0)
			break;
		    if (!lu_factor(circuitMatrix, circuitMatrixSize,
				  circuitPermute)) {
			stop("Singular matrix!", null);
			return;
		    }
		}
		lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute,
			 circuitRightSide);
		
		for (j = 0; j != circuitMatrixFullSize; j++) {
		    RowInfo ri = circuitRowInfo[j];
		    double res = 0;
		    if (ri.type == RowInfo.ROW_CONST)
			res = ri.value;
		    else
			res = circuitRightSide[ri.mapCol];
		    /*System.out.println(j + " " + res + " " +
		      ri.type + " " + ri.mapCol);*/
		    if (Double.isNaN(res)) {
			converged = false;
			//debugprint = true;
			break;
		    }
		    if (j < nodeList.size()-1) {
			CircuitNode cn = getCircuitNode(j+1);
			for (k = 0; k != cn.links.size(); k++) {
			    CircuitNodeLink cnl = (CircuitNodeLink)
				cn.links.elementAt(k);
			    cnl.elm.setNodeVoltage(cnl.num, res);
			}
		    } else {
			int ji = j-(nodeList.size()-1);
			//System.out.println("setting vsrc " + ji + " to " + res);
			voltageSources[ji].setCurrent(ji, res);
		    }
		}
		if (!circuitNonLinear)
		    break;
	    }
	    if (subiter > 5)
		console("converged after " + subiter + " iterations\n");
	    if (subiter == subiterCount) {
		stop("Convergence failed!", null);
		break;
	    }
	    t += timeStep;
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		ce.stepFinished();
	    }
	    if (!delayWireProcessing)
		calcWireCurrents();
	    for (i = 0; i != scopeCount; i++)
	    	scopes[i].timeStep();
	    tm = System.currentTimeMillis();
	    lit = tm;
	    // Check whether enough time has elapsed to perform an *additional* iteration after
	    // those we have already completed.
	    if ((iter+1)*1000 >= steprate*(tm-lastIterTime) || (tm-lastFrameTime > 500))
		break;
	} // for (iter = 1; ; iter++)
	lastIterTime = lit;
	if (delayWireProcessing)
	    calcWireCurrents();
//	System.out.println((System.currentTimeMillis()-lastFrameTime)/(double) iter);
    }

    // we removed wires from the matrix to speed things up.  in order to display wire currents,
    // we need to calculate them now.
    void calcWireCurrents() {
	int i;
	
	// for debugging
	//for (i = 0; i != wireInfoList.size(); i++)
	 //   wireInfoList.get(i).wire.setCurrent(-1, 1.23);
	
	for (i = 0; i != wireInfoList.size(); i++) {
	    WireInfo wi = wireInfoList.get(i);
	    double cur = 0;
	    int j;
	    Point p = wi.wire.getPost(wi.post);
	    for (j = 0; j != wi.neighbors.size(); j++) {
		CircuitElm ce = wi.neighbors.get(j);
		cur += ce.getCurrentIntoPoint(p.x, p.y);
	    }
	    if (wi.post == 0)
		wi.wire.setCurrent(-1, cur);
	    else
		wi.wire.setCurrent(-1, -cur);
	}
    }
    
    int min(int a, int b) { return (a < b) ? a : b; }
    int max(int a, int b) { return (a > b) ? a : b; }

    
    

    
    public void resetAction(){
    	int i;
    	for (i = 0; i != elmList.size(); i++)
    		getElm(i).reset();
    	for (i = 0; i != scopeCount; i++)
    		scopes[i].resetGraph(true);
    	// TODO: Will need to do IE bug fix here?
    	analyzeFlag = true;
    	if (t == 0)
    	    setSimRunning(true);
    	else
    	    t=0;
    }
    
    
    public void menuPerformed(String menu, String item) {
    	if (item=="about")
    		aboutBox = new AboutBox(circuitjs1.versionString);
    	if (item=="importfromlocalfile") {
    		pushUndo();
    		loadFileInput.click();
    	}
    	if (item=="importfromtext") {
    		importFromTextDialog = new ImportFromTextDialog(this);
    	}
    	if (item=="importfromdropbox") {
    		importFromDropboxDialog = new ImportFromDropboxDialog(this);
    	}
    	if (item=="exportasurl") {
    		doExportAsUrl();
    	}
    	if (item=="exportaslocalfile")
    		doExportAsLocalFile();
    	if (item=="exportastext")
    		doExportAsText();
    	if (item=="dcanalysis")
    	    	doDCAnalysis();
    	if (item=="print")
    	    	doPrint();
    	if (item=="recover")
    	    	doRecover();

    	if ((menu=="elm" || menu=="scopepop") && contextPanel!=null)
    		contextPanel.hide();
    	if (menu=="options" && item=="other")
    		doEdit(new EditOptions(this));
    	if (item=="undo")
    		doUndo();
    	if (item=="redo")
    		doRedo();
    	if (item == "cut") {
    		if (menu!="elm")
    			menuElm = null;
    		doCut();
    	}
    	if (item == "copy") {
    		if (menu!="elm")
    			menuElm = null;
    		doCopy();
    	}
    	if (item=="paste")
    		doPaste(null);
    	if (item=="duplicate") {
		if (menu!="elm")
			menuElm = null;
    	    	doDuplicate();
    	}
    	if (item=="flip")
    	    doFlip();
    	if (item=="selectAll")
    		doSelectAll();
    	//	if (e.getSource() == exitItem) {
    	//	    destroyFrame();
    	//	    return;
    	//	}
    	
    	if (item=="centrecircuit") {
    		pushUndo();
    		centreCircuit();
    	}
    	if (item=="stackAll")
    		stackAll();
    	if (item=="unstackAll")
    		unstackAll();
    	if (item=="combineAll")
		combineAll();
    	if (item=="zoomin")
    	    zoomCircuit(20);
    	if (item=="zoomout")
    	    zoomCircuit(-20);
    	if (item=="zoom100")
    	    setCircuitScale(1);
    	if (menu=="elm" && item=="edit")
    		doEdit(menuElm);
    	if (item=="delete") {
    		if (menu!="elm")
    			menuElm = null;
    		doDelete();
    	}

    	if (item=="viewInScope" && menuElm != null) {
    		int i;
    		for (i = 0; i != scopeCount; i++)
    			if (scopes[i].getElm() == null)
    				break;
    		if (i == scopeCount) {
    			if (scopeCount == scopes.length)
    				return;
    			scopeCount++;
    			scopes[i] = new Scope(this);
    			scopes[i].position = i;
    			//handleResize();
    		}
    		scopes[i].setElm(menuElm);
    		if (i > 0)
    		    scopes[i].speed = scopes[i-1].speed;
    	}
    	if (menu=="scopepop") {
    		pushUndo();
    		if (item=="remove")
    			scopes[menuScope].setElm(null);
    		if (item=="removeplot")
			scopes[menuScope].removePlot(menuPlot);
    		if (item=="speed2")
    			scopes[menuScope].speedUp();
    		if (item=="speed1/2")
    			scopes[menuScope].slowDown();
//    		if (item=="scale")
//    			scopes[menuScope].adjustScale(.5);
    		if (item=="maxscale")
    			scopes[menuScope].maxScale();
    		if (item=="stack")
    			stackScope(menuScope);
    		if (item=="unstack")
    			unstackScope(menuScope);
    		if (item=="combine")
			combineScope(menuScope);
    		if (item=="selecty")
    			scopes[menuScope].selectY();
    		if (item=="reset")
    			scopes[menuScope].resetGraph(true);
    		if (item=="properties")
			scopes[menuScope].properties();
    		//cv.repaint();
    	}
    	if (menu=="circuits" && item.indexOf("setup ") ==0) {
    		pushUndo();
    		int sp = item.indexOf(' ', 6);
    		readSetupFile(item.substring(6, sp), item.substring(sp+1), true);
    	}
    		
    	//	if (ac.indexOf("setup ") == 0) {
    	//	    pushUndo();
    	//	    readSetupFile(ac.substring(6),
    	//			  ((MenuItem) e.getSource()).getLabel());
    	//	}

    	// IES: Moved from itemStateChanged()
    	if (menu=="main") {
    		if (contextPanel!=null)
    			contextPanel.hide();
    		//	MenuItem mmi = (MenuItem) mi;
    		//		int prevMouseMode = mouseMode;
    		setMouseMode(MODE_ADD_ELM);
    		String s = item;
    		if (s.length() > 0)
    			mouseModeStr = s;
    		if (s.compareTo("DragAll") == 0)
    			setMouseMode(MODE_DRAG_ALL);
    		else if (s.compareTo("DragRow") == 0)
    			setMouseMode(MODE_DRAG_ROW);
    		else if (s.compareTo("DragColumn") == 0)
    			setMouseMode(MODE_DRAG_COLUMN);
    		else if (s.compareTo("DragSelected") == 0)
    			setMouseMode(MODE_DRAG_SELECTED);
    		else if (s.compareTo("DragPost") == 0)
    			setMouseMode(MODE_DRAG_POST);
    		else if (s.compareTo("Select") == 0)
    			setMouseMode(MODE_SELECT);
    		//		else if (s.length() > 0) {
    		//			try {
    		//				addingClass = Class.forName(s);
    		//			} catch (Exception ee) {
    		//				ee.printStackTrace();
    		//			}
    		//		}
    		//		else
    		//			setMouseMode(prevMouseMode);
    		tempMouseMode = mouseMode;
    	}
    }
    

    void stackScope(int s) {
    	if (s == 0) {
    		if (scopeCount < 2)
    			return;
    		s = 1;
    	}
    	if (scopes[s].position == scopes[s-1].position)
    		return;
    	scopes[s].position = scopes[s-1].position;
    	for (s++; s < scopeCount; s++)
    		scopes[s].position--;
    }

    void unstackScope(int s) {
    	if (s == 0) {
    		if (scopeCount < 2)
    			return;
    		s = 1;
    	}
    	if (scopes[s].position != scopes[s-1].position)
    		return;
    	for (; s < scopeCount; s++)
    		scopes[s].position++;
    }

    void combineScope(int s) {
    	if (s == 0) {
    		if (scopeCount < 2)
    			return;
    		s = 1;
    	}
    	scopes[s-1].combine(scopes[s]);
    	scopes[s].setElm(null);
    }
    

    void stackAll() {
    	int i;
    	for (i = 0; i != scopeCount; i++) {
    		scopes[i].position = 0;
    		scopes[i].showMax = scopes[i].showMin = false;
    	}
    }

    void unstackAll() {
    	int i;
    	for (i = 0; i != scopeCount; i++) {
    		scopes[i].position = i;
    		scopes[i].showMax = true;
    	}
    }

    void combineAll() {
    	int i;
    	for (i = scopeCount-2; i >= 0; i--) {
    	    scopes[i].combine(scopes[i+1]);
    	    scopes[i+1].setElm(null);
    	}
    }
    

    void doEdit(Editable eable) {
    	clearSelection();
    	pushUndo();
    	if (editDialog != null) {
    //		requestFocus();
    		editDialog.setVisible(false);
    		editDialog = null;
    	}
    	editDialog = new EditDialog(eable, this);
    	editDialog.show();
    }
    


    void doExportAsUrl()
    {
    	String dump = dumpCircuit();
//	if (expDialog == null) {
//	    expDialog = ImportExportDialogFactory.Create(this,
//		 ImportExportDialog.Action.EXPORT);
////	    expDialog = new ImportExportClipboardDialog(this,
////		 ImportExportDialog.Action.EXPORT);
//	}
//        expDialog.setDump(dump);
//	expDialog.execute();
	    exportAsUrlDialog = new ExportAsUrlDialog(dump);
	    exportAsUrlDialog.show();
    }
    
    
    void doExportAsText()
    {
    	String dump = dumpCircuit();
	    exportAsTextDialog = new ExportAsTextDialog(this, dump);
	    exportAsTextDialog.show();
    }
    
    
    
    void doExportAsLocalFile() {
    	String dump = dumpCircuit();
    	exportAsLocalFileDialog = new ExportAsLocalFileDialog(dump);
    	exportAsLocalFileDialog.show();
    }
    

    String dumpCircuit() {
	int i;
	CustomLogicModel.clearDumpedFlags();
	int f = (dotsCheckItem.getState()) ? 1 : 0;
	f |= (smallGridCheckItem.getState()) ? 2 : 0;
	f |= (voltsCheckItem.getState()) ? 0 : 4;
	f |= (powerCheckItem.getState()) ? 8 : 0;
	f |= (showValuesCheckItem.getState()) ? 0 : 16;
	// 32 = linear scale in afilter
	String dump = "$ " + f + " " +
	    timeStep + " " + getIterCount() + " " +
	    currentBar.getValue() + " " + CircuitElm.voltageRange + " " +
	    powerBar.getValue() + "\n";
		
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (ce instanceof CustomLogicElm) {
		String m = ((CustomLogicElm)ce).dumpModel();
		if (!m.isEmpty())
		    dump += m + "\n";
	    }
	    dump += ce.dump() + "\n";
	}
	for (i = 0; i != scopeCount; i++) {
	    String d = scopes[i].dump();
	    if (d != null)
		dump += d + "\n";
	}
	if (hintType != -1)
	    dump += "h " + hintType + " " + hintItem1 + " " +
		hintItem2 + "\n";
	return dump;
    }

    void getSetupList(final boolean openDefault) {

    	String url;
    	url = GWT.getModuleBaseURL()+"setuplist.txt"+"?v="+random.nextInt(); 
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("File Error Response", exception);
				}

				public void onResponseReceived(Request request, Response response) {
					// processing goes here
					if (response.getStatusCode()==Response.SC_OK) {
					String text = response.getText();
					processSetupList(text.getBytes(), text.length(), openDefault);
					// end or processing
					}
					else 
						GWT.log("Bad file server response:"+response.getStatusText() );
				}
			});
		} catch (RequestException e) {
			GWT.log("failed file reading", e);
		}
    }
		
    void processSetupList(byte b[], int len, final boolean openDefault) {
    	MenuBar currentMenuBar;
    	MenuBar stack[] = new MenuBar[6];
    	int stackptr = 0;
    	currentMenuBar=new MenuBar(true);
    	currentMenuBar.setAutoOpen(true);
    	menuBar.addItem(LS("Circuits"), currentMenuBar);
    	stack[stackptr++] = currentMenuBar;
    	int p;
    	for (p = 0; p < len; ) {
    		int l;
    		for (l = 0; l != len-p; l++)
    			if (b[l+p] == '\n') {
    				l++;
    				break;
    			}
    		String line = new String(b, p, l-1);
    		if (line.charAt(0) == '#')
    			;
    		else if (line.charAt(0) == '+') {
    		//	MenuBar n = new Menu(line.substring(1));
    			MenuBar n = new MenuBar(true);
    			n.setAutoOpen(true);
    			currentMenuBar.addItem(LS(line.substring(1)),n);
    			currentMenuBar = stack[stackptr++] = n;
    		} else if (line.charAt(0) == '-') {
    			currentMenuBar = stack[--stackptr-1];
    		} else {
    			int i = line.indexOf(' ');
    			if (i > 0) {
    				String title = LS(line.substring(i+1));
    				boolean first = false;
    				if (line.charAt(0) == '>')
    					first = true;
    				String file = line.substring(first ? 1 : 0, i);
    				currentMenuBar.addItem(new MenuItem(title,
    					new MyCommand("circuits", "setup "+file+" " + title)));
    				if (file.equals(startCircuit) && startLabel == null) {
    				    startLabel = title;
    				    titleLabel.setText(title);
    				}
    				if (first && startCircuit == null) {
    					startCircuit = file;
    					startLabel = title;
    					if (openDefault && stopMessage == null)
    						readSetupFile(startCircuit, startLabel, true);
    				}
    			}
    		}
    		p += l;
    	}
}

    
    
    

    void readSetup(String text, boolean centre) {
	readSetup(text, false, centre);
	titleLabel.setText(null);
    }
    
    void readSetup(String text, boolean retain, boolean centre) {
	readSetup(text.getBytes(), text.length(), retain, centre);
	titleLabel.setText(null);
    }


	void readSetupFile(String str, String title, boolean centre) {
		t = 0;
		System.out.println(str);
		// TODO: Maybe think about some better approach to cache management!
		String url=GWT.getModuleBaseURL()+"circuits/"+str+"?v="+random.nextInt(); 
		loadFileFromURL(url, centre);
		if (title != null)
		    titleLabel.setText(title);
	}
	
	void loadFileFromURL(String url, final boolean centre) {
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("File Error Response", exception);
				}

				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode()==Response.SC_OK) {
					String text = response.getText();
					readSetup(text.getBytes(), text.length(), false, centre);
					}
					else 
						GWT.log("Bad file server response:"+response.getStatusText() );
				}
			});
		} catch (RequestException e) {
			GWT.log("failed file reading", e);
		}
		
	}

    void readSetup(byte b[], int len, boolean retain, boolean centre) {
	int i;
	if (!retain) {
	    clearMouseElm();
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		ce.delete();
	    }
	    elmList.removeAllElements();
	    hintType = -1;
	    timeStep = 5e-6;
	    dotsCheckItem.setState(false);
	    smallGridCheckItem.setState(false);
	    powerCheckItem.setState(false);
	    voltsCheckItem.setState(true);
	    showValuesCheckItem.setState(true);
	    setGrid();
	    speedBar.setValue(117); // 57
	    currentBar.setValue(50);
	    powerBar.setValue(50);
	    CircuitElm.voltageRange = 5;
	    scopeCount = 0;
	    lastIterTime = 0;
	}
	//cv.repaint();
	int p;
	for (p = 0; p < len; ) {
	    int l;
	    int linelen = len-p; // IES - changed to allow the last line to not end with a delim.
	    for (l = 0; l != len-p; l++)
		if (b[l+p] == '\n' || b[l+p] == '\r') {
		    linelen = l++;
		    if (l+p < b.length && b[l+p] == '\n')
			l++;
		    break;
		}
	    String line = new String(b, p, linelen);
	    StringTokenizer st = new StringTokenizer(line, " +\t\n\r\f");
	    while (st.hasMoreTokens()) {
		String type = st.nextToken();
		int tint = type.charAt(0);
		try {
		    if (tint == 'o') {
			Scope sc = new Scope(this);
			sc.position = scopeCount;
			sc.undump(st);
			scopes[scopeCount++] = sc;
			break;
		    }
		    if (tint == 'h') {
			readHint(st);
			break;
		    }
		    if (tint == '$') {
			readOptions(st);
			break;
		    }
		    if (tint == '!') {
			new CustomLogicModel(st);
			break;
		    }
		    if (tint == '%' || tint == '?' || tint == 'B') {
			// ignore afilter-specific stuff
			break;
		    }
		    if (tint >= '0' && tint <= '9')
			tint = new Integer(type).intValue();
		    int x1 = new Integer(st.nextToken()).intValue();
		    int y1 = new Integer(st.nextToken()).intValue();
		    int x2 = new Integer(st.nextToken()).intValue();
		    int y2 = new Integer(st.nextToken()).intValue();
		    int f  = new Integer(st.nextToken()).intValue();
		    // The following lines are functionally replaced by the call to
		    // createCe below
//		    Class cls = dumpTypes[tint];
//		    if (cls == null) {
//			System.out.println("unrecognized dump type: " + type);
//			break;
//		    }
//		    // find element class
//		    Class carr[] = new Class[6];
//		    //carr[0] = getClass();
//		    carr[0] = carr[1] = carr[2] = carr[3] = carr[4] =
//			int.class;
//		    carr[5] = StringTokenizer.class;
//		    Constructor cstr = null;
//		    cstr = cls.getConstructor(carr);
//		
//		    // invoke constructor with starting coordinates
//		    Object oarr[] = new Object[6];
//		    //oarr[0] = this;
//		    oarr[0] = new Integer(x1);
//		    oarr[1] = new Integer(y1);
//		    oarr[2] = new Integer(x2);
//		    oarr[3] = new Integer(y2);
//		    oarr[4] = new Integer(f );
//		    oarr[5] = st;
//		    ce = (CircuitElm) cstr.newInstance(oarr);
		    CircuitElm newce = createCe(tint, x1, y1, x2, y2, f, st);
		    if (newce==null) {
				System.out.println("unrecognized dump type: " + type);
				break;
			    }
		    newce.setPoints();
		    elmList.addElement(newce);
//		} catch (java.lang.reflect.InvocationTargetException ee) {
//		    ee.getTargetException().printStackTrace();
//		    break;
		} catch (Exception ee) {
		    ee.printStackTrace();
		    console("exception while undumping " + ee);
		    break;
		}
		break;
	    }
	    p += l;
	    
	}
	setPowerBarEnable();
	enableItems();
//	if (!retain)
	//    handleResize(); // for scopes
	needAnalyze();
	if (centre)
		centreCircuit();
    }

    void readHint(StringTokenizer st) {
	hintType  = new Integer(st.nextToken()).intValue();
	hintItem1 = new Integer(st.nextToken()).intValue();
	hintItem2 = new Integer(st.nextToken()).intValue();
    }

    void readOptions(StringTokenizer st) {
	int flags = new Integer(st.nextToken()).intValue();
	dotsCheckItem.setState((flags & 1) != 0);
	smallGridCheckItem.setState((flags & 2) != 0);
	voltsCheckItem.setState((flags & 4) == 0);
	powerCheckItem.setState((flags & 8) == 8);
	showValuesCheckItem.setState((flags & 16) == 0);
	timeStep = new Double (st.nextToken()).doubleValue();
	double sp = new Double(st.nextToken()).doubleValue();
	int sp2 = (int) (Math.log(10*sp)*24+61.5);
	//int sp2 = (int) (Math.log(sp)*24+1.5);
	speedBar.setValue(sp2);
	currentBar.setValue(new Integer(st.nextToken()).intValue());
	CircuitElm.voltageRange = new Double (st.nextToken()).doubleValue();

	try {
	    powerBar.setValue(new Integer(st.nextToken()).intValue());
	} catch (Exception e) {
	}
	setGrid();
    }
    
    int snapGrid(int x) {
	return (x+gridRound) & gridMask;
    }

	boolean doSwitch(int x, int y) {
		if (mouseElm == null || !(mouseElm instanceof SwitchElm))
			return false;
		SwitchElm se = (SwitchElm) mouseElm;
		if (!se.getSwitchRect().contains(x, y))
		    return false;
		se.toggle();
		if (se.momentary)
			heldSwitchElm = se;
		needAnalyze();
		return true;
	}

    int locateElm(CircuitElm elm) {
	int i;
	for (i = 0; i != elmList.size(); i++)
	    if (elm == elmList.elementAt(i))
		return i;
	return -1;
    }
    
    public void mouseDragged(MouseMoveEvent e) {
    	// ignore right mouse button with no modifiers (needed on PC)
    	if (e.getNativeButton()==NativeEvent.BUTTON_RIGHT) {
    		if (!(e.isMetaKeyDown() ||
    				e.isShiftKeyDown() ||
    				e.isControlKeyDown() ||
    				e.isAltKeyDown()))
    			return;
    	}
    	
    	if (tempMouseMode==MODE_DRAG_SPLITTER) {
    		dragSplitter(e.getX(), e.getY());
    		return;
    	}
    	int gx = inverseTransformX(e.getX());
    	int gy = inverseTransformY(e.getY());
    	if (!circuitArea.contains(e.getX(), e.getY()))
    	    return;
    	boolean changed = false;
    	if (dragElm != null)
    	    dragElm.drag(gx, gy);
    	boolean success = true;
    	switch (tempMouseMode) {
    	case MODE_DRAG_ALL:
    		dragAll(e.getX(), e.getY());
    		break;
    	case MODE_DRAG_ROW:
    		dragRow(snapGrid(gx), snapGrid(gy));
    		changed = true;
    		break;
    	case MODE_DRAG_COLUMN:
		dragColumn(snapGrid(gx), snapGrid(gy));
    		changed = true;
    		break;
    	case MODE_DRAG_POST:
    		if (mouseElm != null) {
    		    dragPost(snapGrid(gx), snapGrid(gy));
    		    changed = true;
    		}
    		break;
    	case MODE_SELECT:
    		if (mouseElm == null)
    		    selectArea(gx, gy);
    		else {
    		    // wait short delay before dragging.  This is to fix problem where switches were accidentally getting
    		    // dragged when tapped on mobile devices
    		    if (System.currentTimeMillis()-mouseDownTime < 150)
    			return;
    		
    		    tempMouseMode = MODE_DRAG_SELECTED;
    		    changed = success = dragSelected(gx, gy);
    		}
    		break;
    	case MODE_DRAG_SELECTED:
    		changed = success = dragSelected(gx, gy);
    		break;

    	}
    	dragging = true;
    	if (success) {
    	    dragScreenX = e.getX();
    	    dragScreenY = e.getY();
    //	    console("setting dragGridx in mousedragged");
    	    dragGridX = inverseTransformX(dragScreenX);
    	    dragGridY = inverseTransformY(dragScreenY);
    	    if (!(tempMouseMode == MODE_DRAG_SELECTED && onlyGraphicsElmsSelected())) {
    		dragGridX = snapGrid(dragGridX);
    		dragGridY = snapGrid(dragGridY);
    	    }
   	}
    	if (changed)
    	    writeRecoveryToStorage();
    }
    
    void dragSplitter(int x, int y) {
    	double h = (double) cv.getCanvasElement().getHeight();
    	if (h<1)
    		h=1;
    	scopeHeightFraction=1.0-(((double)y)/h);
    	if (scopeHeightFraction<0.1)
    		scopeHeightFraction=0.1;
    	if (scopeHeightFraction>0.9)
    		scopeHeightFraction=0.9;
    	setCircuitArea();
    	
    }

    void dragAll(int x, int y) {
    	int dx = x-dragScreenX;
    	int dy = y-dragScreenY;
    	if (dx == 0 && dy == 0)
    		return;
    	transform[4] += dx;
    	transform[5] += dy;
    	dragScreenX = x;
    	dragScreenY = y;
    }

    void dragRow(int x, int y) {
    	int dy = y-dragGridY;
    	if (dy == 0)
    		return;
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		if (ce.y  == dragGridY)
    			ce.movePoint(0, 0, dy);
    		if (ce.y2 == dragGridY)
    			ce.movePoint(1, 0, dy);
    	}
    	removeZeroLengthElements();
    }

    void dragColumn(int x, int y) {
    	int dx = x-dragGridX;
    	if (dx == 0)
    		return;
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		if (ce.x  == dragGridX)
    			ce.movePoint(0, dx, 0);
    		if (ce.x2 == dragGridX)
    			ce.movePoint(1, dx, 0);
    	}
    	removeZeroLengthElements();
    }

    boolean onlyGraphicsElmsSelected() {
	if (mouseElm!=null && !(mouseElm instanceof GraphicElm))
	    return false;
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    	    CircuitElm ce = getElm(i);
    	    if ( ce.isSelected() && !(ce instanceof GraphicElm) )
    		return false;
    	}
    	return true;
    }
    
    boolean dragSelected(int x, int y) {
    	boolean me = false;
    	int i;
    	if (mouseElm != null && !mouseElm.isSelected())
    	    mouseElm.setSelected(me = true);

    	if (! onlyGraphicsElmsSelected()) {
    //	    console("Snapping x and y");
    	    x = snapGrid(x);
    	    y = snapGrid(y);
    	}

    	int dx = x-dragGridX;
  //  	console("dx="+dx+"dragGridx="+dragGridX);
    	int dy = y-dragGridY;
    	if (dx == 0 && dy == 0) {
    	    // don't leave mouseElm selected if we selected it above
    	    if (me)
    		mouseElm.setSelected(false);
    	    return false;
    	}
    	boolean allowed = true;

    	// check if moves are allowed
    	for (i = 0; allowed && i != elmList.size(); i++) {
    	    CircuitElm ce = getElm(i);
    	    if (ce.isSelected() && !ce.allowMove(dx, dy))
    		allowed = false;
    	}

    	if (allowed) {
    	    for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		if (ce.isSelected())
    		    ce.move(dx, dy);
    	    }
    	    needAnalyze();
    	}

    	// don't leave mouseElm selected if we selected it above
    	if (me)
    		mouseElm.setSelected(false);

    	return allowed;
    }

    void dragPost(int x, int y) {
    	if (draggingPost == -1) {
    		draggingPost =
    				(Graphics.distanceSq(mouseElm.x , mouseElm.y , x, y) >
    				Graphics.distanceSq(mouseElm.x2, mouseElm.y2, x, y)) ? 1 : 0;
    	}
    	int dx = x-dragGridX;
    	int dy = y-dragGridY;
    	if (dx == 0 && dy == 0)
    		return;
    	mouseElm.movePoint(draggingPost, dx, dy);
    	needAnalyze();
    }

    void doFlip() {
	menuElm.flipPosts();
    	needAnalyze();
    }
    
    void selectArea(int x, int y) {
    	int x1 = min(x, initDragGridX);
    	int x2 = max(x, initDragGridX);
    	int y1 = min(y, initDragGridY);
    	int y2 = max(y, initDragGridY);
    	selectedArea = new Rectangle(x1, y1, x2-x1, y2-y1);
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		ce.selectRect(selectedArea);
    	}
    }

//    void setSelectedElm(CircuitElm cs) {
//    	int i;
//    	for (i = 0; i != elmList.size(); i++) {
//    		CircuitElm ce = getElm(i);
//    		ce.setSelected(ce == cs);
//    	}
//    	mouseElm = cs;
//    }
    
    void setMouseElm(CircuitElm ce) {
    	if (ce!=mouseElm) {
    		if (mouseElm!=null)
    			mouseElm.setMouseElm(false);
    		if (ce!=null)
    			ce.setMouseElm(true);
    		mouseElm=ce;
    	}
    }

    void removeZeroLengthElements() {
    	int i;
    	boolean changed = false;
    	for (i = elmList.size()-1; i >= 0; i--) {
    		CircuitElm ce = getElm(i);
    		if (ce.x == ce.x2 && ce.y == ce.y2) {
    			elmList.removeElementAt(i);
    			ce.delete();
    			changed = true;
    		}
    	}
    	needAnalyze();
    }
    
    boolean mouseIsOverSplitter(int x, int y) {
    	boolean isOverSplitter;
    	isOverSplitter =((x>=0) && (x<circuitArea.width) && 
    			(y>=circuitArea.height-5) && (y<circuitArea.height));
    	if (isOverSplitter!=mouseWasOverSplitter){
    		if (isOverSplitter)
    			setCursorStyle("cursorSplitter");
    		else
    			setMouseMode(mouseMode);
    	}
    	mouseWasOverSplitter=isOverSplitter;
    	return isOverSplitter;
    }

    public void onMouseMove(MouseMoveEvent e) {
    	e.preventDefault();
    	mouseCursorX=e.getX();
    	mouseCursorY=e.getY();
    	if (mouseDragging) {
    		mouseDragged(e);
    		return;
    	}
    	mouseSelect(e);
    }
    
    // convert screen coordinates to grid coordinates by inverting circuit transform
    int inverseTransformX(double x) {
	return (int) ((x-transform[4])/transform[0]);
    }

    int inverseTransformY(double y) {
	return (int) ((y-transform[5])/transform[3]);
    }

    // need to break this out into a separate routine to handle selection,
    // since we don't get mouse move events on mobile
    public void mouseSelect(MouseEvent<?> e) {
    	//	The following is in the original, but seems not to work/be needed for GWT
    	//    	if (e.getNativeButton()==NativeEvent.BUTTON_LEFT)
    	//	    return;
    	CircuitElm newMouseElm=null;
    	mouseCursorX=e.getX();
    	mouseCursorY=e.getY();
    	int sx = e.getX();
    	int sy = e.getY();
    	int gx = inverseTransformX(sx);
    	int gy = inverseTransformY(sy);
   // 	console("Settingd draggridx in mouseEvent");
    	dragGridX = snapGrid(gx);
    	dragGridY = snapGrid(gy);
    	dragScreenX = sx;
    	dragScreenY = sy;
    	draggingPost = -1;
    	int i;
    	//	CircuitElm origMouse = mouseElm;

    	mousePost = -1;
    	plotXElm = plotYElm = null;
    	
    	if (mouseIsOverSplitter(sx, sy)) {
    		setMouseElm(null);
    		return;
    	}
    	
    	if (mouseElm!=null && ( mouseElm.getHandleGrabbedClose(gx, gy, POSTGRABSQ, MINPOSTGRABSIZE)>=0)) {
    		newMouseElm=mouseElm;
    	} else {
    		int bestDist = 100000000;
    		int bestArea = 100000000;
    		for (i = 0; i != elmList.size(); i++) {
    			CircuitElm ce = getElm(i);
    			if (ce.boundingBox.contains(gx, gy)) {
    				int j;
    				int area = ce.boundingBox.width * ce.boundingBox.height;
    				int jn = ce.getPostCount();
    				if (jn > 2)
    					jn = 2;
    				for (j = 0; j != jn; j++) {
    					Point pt = ce.getPost(j);
    					int dist = Graphics.distanceSq(gx, gy, pt.x, pt.y);

    					// if multiple elements have overlapping bounding boxes,
    					// we prefer selecting elements that have posts close
    					// to the mouse pointer and that have a small bounding
    					// box area.
    					if (dist <= bestDist && area <= bestArea) {
    						bestDist = dist;
    						bestArea = area;
    						newMouseElm = ce;
    					}
    				}
    				// prefer selecting elements that have small bounding box area (for
    				// elements with no posts)
    				if (ce.getPostCount() == 0 && area <= bestArea) {
    				    newMouseElm = ce;
    				    bestArea = area;
    				}
    			}
    		} // for
    	}
    	scopeSelected = -1;
    	if (newMouseElm == null) {
    		for (i = 0; i != scopeCount; i++) {
    			Scope s = scopes[i];
    			if (s.rect.contains(sx, sy)) {
    				newMouseElm=s.getElm();
    		    	if (s.plotXY) {
    		    		plotXElm = s.getXElm();
    		    		plotYElm = s.getYElm();
    		    	}
    				scopeSelected = i;
    			}
    		}
    		//	    // the mouse pointer was not in any of the bounding boxes, but we
    		//	    // might still be close to a post
    		for (i = 0; i != elmList.size(); i++) {
    			CircuitElm ce = getElm(i);
    			if (mouseMode==MODE_DRAG_POST ) {
    				if (ce.getHandleGrabbedClose(gx, gy, POSTGRABSQ, 0)> 0)
    				{
    					newMouseElm = ce;
    					break;
    				}
    			}
    			int j;
    			int jn = ce.getPostCount();
    			for (j = 0; j != jn; j++) {
    				Point pt = ce.getPost(j);
    				//   int dist = Graphics.distanceSq(x, y, pt.x, pt.y);
    				if (Graphics.distanceSq(pt.x, pt.y, gx, gy) < 26) {
    					newMouseElm = ce;
    					mousePost = j;
    					break;
    				}
    			}
    		}
    	} else {
    		mousePost = -1;
    		// look for post close to the mouse pointer
    		for (i = 0; i != newMouseElm.getPostCount(); i++) {
    			Point pt = newMouseElm.getPost(i);
    			if (Graphics.distanceSq(pt.x, pt.y, gx, gy) < 26)
    				mousePost = i;
    		}
    	}
    	//	if (mouseElm != origMouse)
    	//	    cv.repaint();
    	setMouseElm(newMouseElm);
    }



    public void onContextMenu(ContextMenuEvent e) {
    	e.preventDefault();
    	menuX = e.getNativeEvent().getClientX();
    	menuY = e.getNativeEvent().getClientY();
    	doPopupMenu();
    }
    
    void doPopupMenu() {
    	menuElm = mouseElm;
    	menuScope=-1;
    	menuPlot=-1;
    	int x, y;
    	if (scopeSelected!=-1) {
    		MenuBar m=scopes[scopeSelected].getMenu();
    		menuScope=scopeSelected;
    		menuPlot=scopes[scopeSelected].selectedPlot;
    		if (m!=null) {
    			contextPanel=new PopupPanel(true);
    			contextPanel.add(m);
    			y=Math.max(0, Math.min(menuY,cv.getCoordinateSpaceHeight()-160));
    			contextPanel.setPopupPosition(menuX, y);
    			contextPanel.show();
    		}
    	} else if (mouseElm != null) {
    		elmScopeMenuItem.setEnabled(mouseElm.canViewInScope());
    		elmEditMenuItem .setEnabled(mouseElm.getEditInfo(0) != null);
    		elmFlipMenuItem .setEnabled(mouseElm.getPostCount() == 2);
    		contextPanel=new PopupPanel(true);
    		contextPanel.add(elmMenuBar);
    		contextPanel.setPopupPosition(menuX, menuY);
    		contextPanel.show();
    	} else {
    		doMainMenuChecks();
    		contextPanel=new PopupPanel(true);
    		contextPanel.add(mainMenuBar);
    		x=Math.max(0, Math.min(menuX, cv.getCoordinateSpaceWidth()-400));
    		y=Math.max(0, Math.min(menuY,cv.getCoordinateSpaceHeight()-450));
    		contextPanel.setPopupPosition(x,y);
    		contextPanel.show();
    	}
    }
    
    void longPress() {
	doPopupMenu();
    }
    
//    public void mouseClicked(MouseEvent e) {
    public void onClick(ClickEvent e) {
    	e.preventDefault();
//    	//IES - remove inteaction
////	if ( e.getClickCount() == 2 && !didSwitch )
////	    doEditMenu(e);
//	if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
//	    if (mouseMode == MODE_SELECT || mouseMode == MODE_DRAG_SELECTED)
//		clearSelection();
//	}	
    	if ((e.getNativeButton() == NativeEvent.BUTTON_MIDDLE))
    		scrollValues(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY(), 0);
    }
    
    public void onDoubleClick(DoubleClickEvent e){
    	e.preventDefault();
 //   	if (!didSwitch && mouseElm != null)
    	if (mouseElm != null && !(mouseElm instanceof SwitchElm))
    		doEdit(mouseElm);
    }
    
//    public void mouseEntered(MouseEvent e) {
//    }
    
    public void onMouseOut(MouseOutEvent e) {
    	mouseCursorX=-1;
    }

    void clearMouseElm() {
    	scopeSelected = -1;
    	mouseElm = plotXElm = plotYElm = null;
    }
    
    int menuX, menuY;
    
    public void onMouseDown(MouseDownEvent e) {
//    public void mousePressed(MouseEvent e) {
    	e.preventDefault();
    	menuX = e.getX();
    	menuY = e.getY();
    	mouseDownTime = System.currentTimeMillis();
    	
    	// maybe someone did copy in another window?  should really do this when
    	// window receives focus
    	enablePaste();
    	
    	if (e.getNativeButton() != NativeEvent.BUTTON_LEFT && e.getNativeButton() != NativeEvent.BUTTON_MIDDLE)
    		return;
    	
    	// set mouseElm in case we are on mobile
    	mouseSelect(e);
    	
    	mouseDragging=true;
    	didSwitch = false;
	
    	if (mouseWasOverSplitter) {
    		tempMouseMode = MODE_DRAG_SPLITTER;
    		return;
    	}
	if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
//	    // left mouse
	    tempMouseMode = mouseMode;
	    if (e.isAltKeyDown() && e.isMetaKeyDown())
		tempMouseMode = MODE_DRAG_COLUMN;
	    else if (e.isAltKeyDown() && e.isShiftKeyDown())
		tempMouseMode = MODE_DRAG_ROW;
	    else if (e.isShiftKeyDown())
		tempMouseMode = MODE_SELECT;
	    else if (e.isAltKeyDown())
		tempMouseMode = MODE_DRAG_ALL;
	    else if (e.isControlKeyDown() || e.isMetaKeyDown())
		tempMouseMode = MODE_DRAG_POST;
	} else
	    tempMouseMode = MODE_DRAG_ALL;

	int gx = inverseTransformX(e.getX());
	int gy = inverseTransformY(e.getY());
	if (doSwitch(gx, gy)) {
	    // do this BEFORE we change the mouse mode to MODE_DRAG_POST!  Or else logic inputs
	    // will add dots to the whole circuit when we click on them!
            didSwitch = true;
	    return;
	}
	
	// IES - Grab resize handles in select mode if they are far enough apart and you are on top of them
	if (tempMouseMode == MODE_SELECT && mouseElm!=null && 
			mouseElm.getHandleGrabbedClose(gx, gy, POSTGRABSQ, MINPOSTGRABSIZE) >=0 &&
		    !anySelectedButMouse() )
		tempMouseMode = MODE_DRAG_POST;


	
	if (tempMouseMode != MODE_SELECT && tempMouseMode != MODE_DRAG_SELECTED)
	    clearSelection();

	pushUndo();
	initDragGridX = gx;
	initDragGridY = gy;
	dragging = true;
	if (tempMouseMode !=MODE_ADD_ELM)
		return;
//	
	int x0 = snapGrid(gx);
	int y0 = snapGrid(gy);
	if (!circuitArea.contains(e.getX(), e.getY()))
	    return;

	dragElm = constructElement(mouseModeStr, x0, y0);
    }

 
    
    
    
    void doMainMenuChecks() {
    	int c = mainMenuItems.size();
    	int i;
    	for (i=0; i<c ; i++)
    		mainMenuItems.get(i).setState(mainMenuItemNames.get(i)==mouseModeStr);
    }
    
 
    public void onMouseUp(MouseUpEvent e) {
    	e.preventDefault();
    	mouseDragging=false;
    	
    	// click to clear selection
    	if (tempMouseMode == MODE_SELECT && selectedArea == null)
    	    clearSelection();
    	
    	tempMouseMode = mouseMode;
    	selectedArea = null;
    	dragging = false;
    	boolean circuitChanged = false;
    	if (heldSwitchElm != null) {
    		heldSwitchElm.mouseUp();
    		heldSwitchElm = null;
    		circuitChanged = true;
    	}
    	if (dragElm != null) {
    		// if the element is zero size then don't create it
    		// IES - and disable any previous selection
    	    	if (dragElm.creationFailed()) {
    			dragElm.delete();
    			if (mouseMode == MODE_SELECT || mouseMode == MODE_DRAG_SELECTED)
    				clearSelection();
    		}
    		else {
    			elmList.addElement(dragElm);
    			dragElm.draggingDone();
    			circuitChanged = true;
    			writeRecoveryToStorage();
    		}
    		dragElm = null;
    	}
    	if (circuitChanged)
    		needAnalyze();
    	if (dragElm != null)
    		dragElm.delete();
    	dragElm = null;
    	//	cv.repaint();
    }
    
    public void onMouseWheel(MouseWheelEvent e) {
    	e.preventDefault();
    	
    	// once we start zooming, don't allow other uses of mouse wheel for a while
    	// so we don't accidentally edit a resistor value while zooming
    	boolean zoomOnly = System.currentTimeMillis() < zoomTime+1000;
    	
    	if (!zoomOnly)
    	    scrollValues(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY(), e.getDeltaY());
    	
    	if (mouseElm instanceof MouseWheelHandler && !zoomOnly)
    		((MouseWheelHandler) mouseElm).onMouseWheel(e);
    	else if (scopeSelected != -1)
    	    scopes[scopeSelected].onMouseWheel(e);
    	else if (!dialogIsShowing()) {
    	    zoomCircuit(e.getDeltaY());
    	    zoomTime = System.currentTimeMillis();
   	}
    }

    void zoomCircuit(int dy) {
	double newScale;
    	double oldScale = transform[0];
    	double val = dy*.01;
    	newScale = Math.max(oldScale+val, .2);
    	newScale = Math.min(newScale, 2.5);
    	setCircuitScale(newScale);
    }
    
    void setCircuitScale(double newScale) {
	int cx = inverseTransformX(circuitArea.width/2);
	int cy = inverseTransformY(circuitArea.height/2);
	transform[0] = transform[3] = newScale;

	// adjust translation to keep center of screen constant
	// inverse transform = (x-t4)/t0
	transform[4] = circuitArea.width /2 - cx*newScale;
	transform[5] = circuitArea.height/2 - cy*newScale;
    }
    
    void setPowerBarEnable() {
    	if (powerCheckItem.getState()) {
    	    powerLabel.setStyleName("disabled", false);
    	    powerBar.enable();
    	} else {
    	    powerLabel.setStyleName("disabled", true);
    	    powerBar.disable();
    	}
    }

    void scrollValues(int x, int y, int deltay) {
    	if (mouseElm!=null && !dialogIsShowing() && scopeSelected == -1)
    		if (mouseElm instanceof ResistorElm || mouseElm instanceof CapacitorElm ||  mouseElm instanceof InductorElm) {
    			scrollValuePopup = new ScrollValuePopup(x, y, deltay, mouseElm, this);
    		}
    }
    
    void enableItems() {
    }
    
    void setGrid() {
	gridSize = (smallGridCheckItem.getState()) ? 8 : 16;
	gridMask = ~(gridSize-1);
	gridRound = gridSize/2-1;
    }

    void pushUndo() {
    	redoStack.removeAllElements();
    	String s = dumpCircuit();
    	if (undoStack.size() > 0 &&
    			s.compareTo(undoStack.lastElement()) == 0)
    		return;
    	undoStack.add(s);
    	enableUndoRedo();
    }

    void doUndo() {
    	if (undoStack.size() == 0)
    		return;
    	redoStack.add(dumpCircuit());
    	String s = undoStack.remove(undoStack.size()-1);
    	readSetup(s, false);
    	enableUndoRedo();
    }

    void doRedo() {
    	if (redoStack.size() == 0)
    		return;
    	undoStack.add(dumpCircuit());
    	String s = redoStack.remove(redoStack.size()-1);
    	readSetup(s, false);
    	enableUndoRedo();
    }

    void doRecover() {
	pushUndo();
	readSetup(recovery, false);
	recoverItem.setEnabled(false);
    }
    
    void enableUndoRedo() {
    	redoItem.setEnabled(redoStack.size() > 0);
    	undoItem.setEnabled(undoStack.size() > 0);
    }

    void setMouseMode(int mode)
    {
    	mouseMode = mode;
    	if ( mode == MODE_ADD_ELM ) {
    		setCursorStyle("cursorCross");
    	} else {
    		setCursorStyle("cursorPointer");
    	}
    }
    
    void setCursorStyle(String s) {
    	if (lastCursorStyle!=null)
    		cv.removeStyleName(lastCursorStyle);
    	cv.addStyleName(s);
    	lastCursorStyle=s;
    }
    


    void setMenuSelection() {
    	if (menuElm != null) {
    		if (menuElm.selected)
    			return;
    		clearSelection();
    		menuElm.setSelected(true);
    	}
    }

    void doCut() {
    	int i;
    	pushUndo();
    	setMenuSelection();
    	clipboard = "";
    	for (i = elmList.size()-1; i >= 0; i--) {
    		CircuitElm ce = getElm(i);
    		if (ce.isSelected()) {
    			clipboard += ce.dump() + "\n";
    			ce.delete();
    			elmList.removeElementAt(i);
    		}
    	}
    	writeClipboardToStorage();
    	writeRecoveryToStorage();
    	enablePaste();
    	needAnalyze();
    }

    void writeClipboardToStorage() {
    	Storage stor = Storage.getLocalStorageIfSupported();
    	if (stor == null)
    		return;
    	stor.setItem("circuitClipboard", clipboard);
    }
    
    void readClipboardFromStorage() {
    	Storage stor = Storage.getLocalStorageIfSupported();
    	if (stor == null)
    		return;
    	clipboard = stor.getItem("circuitClipboard");
    }

    void writeRecoveryToStorage() {
	console("write recovery");
    	Storage stor = Storage.getLocalStorageIfSupported();
    	if (stor == null)
    		return;
    	String s = dumpCircuit();
    	stor.setItem("circuitRecovery", s);
    }

    void readRecovery() {
	Storage stor = Storage.getLocalStorageIfSupported();
	if (stor == null)
		return;
	recovery = stor.getItem("circuitRecovery");
    }


    void doDelete() {
    	int i;
    	pushUndo();
    	setMenuSelection();
    	boolean hasDeleted = false;

    	for (i = elmList.size()-1; i >= 0; i--) {
    		CircuitElm ce = getElm(i);
    		if (ce.isSelected()) {
    			ce.delete();
    			elmList.removeElementAt(i);
    			hasDeleted = true;
    		}
    	}

    	if ( !hasDeleted )
    	{
    		for (i = elmList.size()-1; i >= 0; i--) {
    			CircuitElm ce = getElm(i);
    			if (ce == mouseElm) {
    				ce.delete();
    				elmList.removeElementAt(i);
    				hasDeleted = true;
    				setMouseElm(null);
    				break;
    			}
    		}
    	}

    	if ( hasDeleted ) {
    	    needAnalyze();
    	    writeRecoveryToStorage();
    	}    
    }

    void doCopy() {
    	int i;
    	clipboard = "";
    	
    	// clear selection when we're done if we're copying a single element using the context menu
    	boolean clearSel = (menuElm != null && !menuElm.selected);
    	
    	setMenuSelection();
    	for (i = elmList.size()-1; i >= 0; i--) {
    		CircuitElm ce = getElm(i);
    		if (ce.isSelected())
    			clipboard += ce.dump() + "\n";
    	}
    	
    	if (clearSel)
    	    clearSelection();
    	
    	writeClipboardToStorage();
    	enablePaste();
    }

    void enablePaste() {
    	if (clipboard == null || clipboard.length() == 0)
    		readClipboardFromStorage();
    	pasteItem.setEnabled(clipboard != null && clipboard.length() > 0);
    }

    void doDuplicate() {
    	int i;
    	String s = "";
    	setMenuSelection();
    	for (i = elmList.size()-1; i >= 0; i--) {
    		CircuitElm ce = getElm(i);
    		if (ce.isSelected())
    			s += ce.dump() + "\n";
    	}
    	doPaste(s);
    }
    
    void doPaste(String dump) {
    	pushUndo();
    	clearSelection();
    	int i;
    	Rectangle oldbb = null;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		Rectangle bb = ce.getBoundingBox();
    		if (oldbb != null)
    			oldbb = oldbb.union(bb);
    		else
    			oldbb = bb;
    	}
    	int oldsz = elmList.size();
    	if (dump != null)
    	    readSetup(dump, true, false);
    	else {
    	    readClipboardFromStorage();
    	    readSetup(clipboard, true, false);
    	}

    	// select new items
    	Rectangle newbb = null;
    	for (i = oldsz; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		ce.setSelected(true);
    		Rectangle bb = ce.getBoundingBox();
    		if (newbb != null)
    			newbb = newbb.union(bb);
    		else
    			newbb = bb;
    	}
    	if (oldbb != null && newbb != null && oldbb.intersects(newbb)) {
    		// find a place for new items
    		int dx = 0, dy = 0;
    		int spacew = circuitArea.width - oldbb.width - newbb.width;
    		int spaceh = circuitArea.height - oldbb.height - newbb.height;
    		if (spacew > spaceh)
    			dx = snapGrid(oldbb.x + oldbb.width  - newbb.x + gridSize);
    		else
    			dy = snapGrid(oldbb.y + oldbb.height - newbb.y + gridSize);
    		for (i = oldsz; i != elmList.size(); i++) {
    			CircuitElm ce = getElm(i);
    			ce.move(dx, dy);
    		}
    		// center circuit
    	//	handleResize();
    	}
    	needAnalyze();
    	writeRecoveryToStorage();
    }

    void clearSelection() {
	int i;
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    ce.setSelected(false);
	}
    }
    
    void doSelectAll() {
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		ce.setSelected(true);
    	}
    }
    
    boolean anySelectedButMouse() {
    	for (int i=0; i != elmList.size(); i++)
    		if (getElm(i)!= mouseElm && getElm(i).selected)
    			return true;
    	return false;
    }

//    public void keyPressed(KeyEvent e) {}
//    public void keyReleased(KeyEvent e) {}
    
    boolean dialogIsShowing() {
    	if (editDialog!=null && editDialog.isShowing())
    		return true;
    	if (customLogicEditDialog!=null && customLogicEditDialog.isShowing())
		return true;
    	if (exportAsUrlDialog != null && exportAsUrlDialog.isShowing())
    		return true;
    	if (exportAsTextDialog != null && exportAsTextDialog.isShowing())
    		return true;
       	if (exportAsLocalFileDialog != null && exportAsLocalFileDialog.isShowing())
       		return true;
    	if (contextPanel!=null && contextPanel.isShowing())
    		return true;
    	if (scrollValuePopup != null && scrollValuePopup.isShowing())
    		return true;
    	if (aboutBox !=null && aboutBox.isShowing())
    		return true;
    	if (importFromTextDialog !=null && importFromTextDialog.isShowing())
    		return true;
    	if (importFromDropboxDialog != null && importFromDropboxDialog.isShowing())
    		return true;
    	return false;
    }
    
    public void onPreviewNativeEvent(NativePreviewEvent e) {
    	int cc=e.getNativeEvent().getCharCode();
    	int t=e.getTypeInt();
    	int code=e.getNativeEvent().getKeyCode();
    	if (dialogIsShowing()) {
    		if (scrollValuePopup != null && scrollValuePopup.isShowing() &&
    				(t & Event.ONKEYDOWN)!=0) {
    			if (code==KEY_ESCAPE || code==KEY_SPACE)
    				scrollValuePopup.close(false);
    			if (code==KEY_ENTER)
    				scrollValuePopup.close(true);
    		}
    		if (editDialog!=null && editDialog.isShowing() &&
    				(t & Event.ONKEYDOWN)!=0) {
    			if (code==KEY_ESCAPE)
    				editDialog.closeDialog();
    			if (code==KEY_ENTER) {
    				editDialog.apply();
    				editDialog.closeDialog();
    			}  			
    		}
    		return;
    	}
    	if ((t & Event.ONKEYDOWN)!=0) {
    		if (code==KEY_BACKSPACE || code==KEY_DELETE) {
    		    	menuElm = null;
    			doDelete();
    			e.cancel();
    		}
    		if (code==KEY_ESCAPE){
    			setMouseMode(MODE_SELECT);
    			mouseModeStr = "Select";
    			tempMouseMode = mouseMode;
    			e.cancel();
    		}

    		if (e.getNativeEvent().getCtrlKey() || e.getNativeEvent().getMetaKey()) {
    			if (code==KEY_C) {
    				menuPerformed("key", "copy");
    				e.cancel();
    			}
    			if (code==KEY_X) {
    				menuPerformed("key", "cut");
    				e.cancel();
    			}
    			if (code==KEY_V) {
    				menuPerformed("key", "paste");
    				e.cancel();
    			}
    			if (code==KEY_Z) {
    				menuPerformed("key", "undo");
    				e.cancel();
    			}
    			if (code==KEY_Y) {
    				menuPerformed("key", "redo");
    				e.cancel();
    			}
    			if (code==KEY_D) {
    			    	menuPerformed("key", "duplicate");
    			    	e.cancel();
    			}
    			if (code==KEY_A) {
    				menuPerformed("key", "selectAll");
    				e.cancel();
    			}
    		}
    	}
    	if ((t&Event.ONKEYPRESS)!=0) {
		if (cc=='-') {
    		    menuPerformed("key", "zoomout");
    		    e.cancel();
    		}
    		if (cc=='+' || cc == '=') {
    		    menuPerformed("key", "zoomin");
    		    e.cancel();
    		}
		if (cc=='0') {
    		    menuPerformed("key", "zoom100");
    		    e.cancel();
		}

    		if (cc>32 && cc<127){
    			String c=shortcuts[cc];
    			e.cancel();
    			if (c==null)
    				return;
    			setMouseMode(MODE_ADD_ELM);
    			mouseModeStr=c;
    			tempMouseMode = mouseMode;
    		}
    		if (cc==32) {
			    setMouseMode(MODE_SELECT);
			    mouseModeStr = "Select";
			    tempMouseMode = mouseMode;
			    e.cancel();    			
    		}
    	}
    }
    
    // factors a matrix into upper and lower triangular matrices by
    // gaussian elimination.  On entry, a[0..n-1][0..n-1] is the
    // matrix to be factored.  ipvt[] returns an integer vector of pivot
    // indices, used in the lu_solve() routine.
    boolean lu_factor(double a[][], int n, int ipvt[]) {
	int i,j,k;
	
	// check for a possible singular matrix by scanning for rows that
	// are all zeroes
	for (i = 0; i != n; i++) { 
	    boolean row_all_zeros = true;
	    for (j = 0; j != n; j++) {
		if (a[i][j] != 0) {
		    row_all_zeros = false;
		    break;
		}
	    }
	    // if all zeros, it's a singular matrix
	    if (row_all_zeros)
		return false;
	}
	
        // use Crout's method; loop through the columns
	for (j = 0; j != n; j++) {
	    
	    // calculate upper triangular elements for this column
	    for (i = 0; i != j; i++) {
		double q = a[i][j];
		for (k = 0; k != i; k++)
		    q -= a[i][k]*a[k][j];
		a[i][j] = q;
	    }

	    // calculate lower triangular elements for this column
	    double largest = 0;
	    int largestRow = -1;
	    for (i = j; i != n; i++) {
		double q = a[i][j];
		for (k = 0; k != j; k++)
		    q -= a[i][k]*a[k][j];
		a[i][j] = q;
		double x = Math.abs(q);
		if (x >= largest) {
		    largest = x;
		    largestRow = i;
		}
	    }
	    
	    // pivoting
	    if (j != largestRow) {
		double x;
		for (k = 0; k != n; k++) {
		    x = a[largestRow][k];
		    a[largestRow][k] = a[j][k];
		    a[j][k] = x;
		}
	    }

	    // keep track of row interchanges
	    ipvt[j] = largestRow;

	    // avoid zeros
	    if (a[j][j] == 0.0) {
		System.out.println("avoided zero");
		a[j][j]=1e-18;
	    }

	    if (j != n-1) {
		double mult = 1.0/a[j][j];
		for (i = j+1; i != n; i++)
		    a[i][j] *= mult;
	    }
	}
	return true;
    }

    // Solves the set of n linear equations using a LU factorization
    // previously performed by lu_factor.  On input, b[0..n-1] is the right
    // hand side of the equations, and on output, contains the solution.
    void lu_solve(double a[][], int n, int ipvt[], double b[]) {
	int i;

	// find first nonzero b element
	for (i = 0; i != n; i++) {
	    int row = ipvt[i];

	    double swap = b[row];
	    b[row] = b[i];
	    b[i] = swap;
	    if (swap != 0)
		break;
	}
	
	int bi = i++;
	for (; i < n; i++) {
	    int row = ipvt[i];
	    int j;
	    double tot = b[row];
	    
	    b[row] = b[i];
	    // forward substitution using the lower triangular matrix
	    for (j = bi; j < i; j++)
		tot -= a[i][j]*b[j];
	    b[i] = tot;
	}
	for (i = n-1; i >= 0; i--) {
	    double tot = b[i];
	    
	    // back-substitution using the upper triangular matrix
	    int j;
	    for (j = i+1; j != n; j++)
		tot -= a[i][j]*b[j];
	    b[i] = tot/a[i][i];
	}
    }

    
    void createNewLoadFile() {
    	// This is a hack to fix what IMHO is a bug in the <INPUT FILE element
    	// reloading the same file doesn't create a change event so importing the same file twice
    	// doesn't work unless you destroy the original input element and replace it with a new one
    	int idx=verticalPanel.getWidgetIndex(loadFileInput);
    	LoadFile newlf=new LoadFile(this);
    	verticalPanel.insert(newlf, idx);
    	verticalPanel.remove(idx+1);
    	loadFileInput=newlf;
    }

    void addWidgetToVerticalPanel(Widget w) {
    	if (iFrame!=null) {
    		int i=verticalPanel.getWidgetIndex(iFrame);
    		verticalPanel.insert(w, i);
    		setiFrameHeight();
    	}
    	else
    		verticalPanel.add(w);
    }
    
    void removeWidgetFromVerticalPanel(Widget w){
    	verticalPanel.remove(w);
    	if (iFrame!=null)
    		setiFrameHeight();
    }
    
    public static CircuitElm createCe(int tint, int x1, int y1, int x2, int y2, int f, StringTokenizer st) {
    	if (tint=='g')
    		return (CircuitElm) new GroundElm(x1, y1, x2, y2, f, st);
    	if (tint=='r')
    		return (CircuitElm) new ResistorElm(x1, y1, x2, y2, f, st);
    	if (tint=='R')
    		return (CircuitElm) new RailElm(x1, y1, x2, y2, f, st);
    	if (tint=='s')
    		return (CircuitElm) new SwitchElm(x1, y1, x2, y2, f, st);
    	if (tint=='S')
    		return (CircuitElm) new Switch2Elm(x1, y1, x2, y2, f, st);
    	if (tint=='t')
    		return (CircuitElm) new TransistorElm(x1, y1, x2, y2, f, st);
    	if (tint=='w')
    		return (CircuitElm) new WireElm(x1, y1, x2, y2, f, st);
    	if (tint=='c')
    		return (CircuitElm) new CapacitorElm(x1, y1, x2, y2, f, st);   	
    	if (tint==209)
		return (CircuitElm) new PolarCapacitorElm(x1, y1, x2, y2, f, st);   	
    	if (tint=='l')
    		return (CircuitElm) new InductorElm(x1, y1, x2, y2, f, st);
    	if (tint=='v')
    		return (CircuitElm) new VoltageElm(x1, y1, x2, y2, f, st);
    	if (tint==172)
    		return (CircuitElm) new VarRailElm(x1, y1, x2, y2, f, st);
    	if (tint==174)
    		return (CircuitElm) new PotElm(x1, y1, x2, y2, f, st);
    	if (tint=='O')
    		return (CircuitElm) new OutputElm(x1, y1, x2, y2, f, st);
    	if (tint=='i')
    		return (CircuitElm) new CurrentElm(x1, y1, x2, y2, f, st);
    	if (tint=='p')
    		return (CircuitElm) new ProbeElm(x1, y1, x2, y2, f, st);
    	if (tint=='d')
    		return (CircuitElm) new DiodeElm(x1, y1, x2, y2, f, st);
    	if (tint=='z')
    		return (CircuitElm) new ZenerElm(x1, y1, x2, y2, f, st);
    	if (tint==170)
    		return (CircuitElm) new SweepElm(x1, y1, x2, y2, f, st);
    	if (tint==162)
    		return (CircuitElm) new LEDElm(x1, y1, x2, y2, f, st);
    	if (tint=='A')
    		return (CircuitElm) new AntennaElm(x1, y1, x2, y2, f, st);
    	if (tint=='L')
    		return (CircuitElm) new LogicInputElm(x1, y1, x2, y2, f, st);
    	if (tint=='M')
    		return (CircuitElm) new LogicOutputElm(x1, y1, x2, y2, f, st);
    	if (tint=='T')
    		return (CircuitElm) new TransformerElm(x1, y1, x2, y2, f, st);
    	if (tint==169)
    		return (CircuitElm) new TappedTransformerElm(x1, y1, x2, y2, f, st);
    	if (tint==171)
    		return (CircuitElm) new TransLineElm(x1, y1, x2, y2, f, st);
    	if (tint==178)
    		return (CircuitElm) new RelayElm(x1, y1, x2, y2, f, st);
    	if (tint=='m')
    		return (CircuitElm) new MemristorElm(x1, y1, x2, y2, f, st);
    	if (tint==187)
    		return (CircuitElm) new SparkGapElm(x1, y1, x2, y2, f, st);
    	if (tint==200)
    		return (CircuitElm) new AMElm(x1, y1, x2, y2, f, st);
    	if (tint==201)
    		return (CircuitElm) new FMElm(x1, y1, x2, y2, f, st);
    	if (tint=='n')
		return (CircuitElm) new NoiseElm(x1, y1, x2, y2, f, st);
    	if (tint==181)
    		return (CircuitElm) new LampElm(x1, y1, x2, y2, f, st);
    	if (tint=='a')
    		return (CircuitElm) new OpAmpElm(x1, y1, x2, y2, f, st);
    	if (tint=='f')
    		return (CircuitElm) new MosfetElm(x1, y1, x2, y2, f, st);
    	if (tint=='j')
    		return (CircuitElm) new JfetElm(x1, y1, x2, y2, f, st);
    	if (tint==159)
    		return (CircuitElm) new AnalogSwitchElm(x1, y1, x2, y2, f, st);
    	if (tint==160)
    		return (CircuitElm) new AnalogSwitch2Elm(x1, y1, x2, y2, f, st);
    	if (tint==180)
    		return (CircuitElm) new TriStateElm(x1, y1, x2, y2, f, st);
    	if (tint==182)
    		return (CircuitElm) new SchmittElm(x1, y1, x2, y2, f, st);
    	if (tint==183)
    		return (CircuitElm) new InvertingSchmittElm(x1, y1, x2, y2, f, st);
    	if (tint==177)
    		return (CircuitElm) new SCRElm(x1, y1, x2, y2, f, st);
    	if (tint==203)
    		return (CircuitElm) new DiacElm(x1, y1, x2, y2, f, st);
    	if (tint==206)
    		return (CircuitElm) new TriacElm(x1, y1, x2, y2, f, st);
    	if (tint==173)
    		return (CircuitElm) new TriodeElm(x1, y1, x2, y2, f, st);
    	if (tint==175)
    		return (CircuitElm) new TunnelDiodeElm(x1, y1, x2, y2, f, st);
    	if (tint==179)
    		return (CircuitElm) new CC2Elm(x1, y1, x2, y2, f, st);
    	if (tint=='I')
    		return (CircuitElm) new InverterElm(x1, y1, x2, y2, f, st);
    	if (tint==151)
    		return (CircuitElm) new NandGateElm(x1, y1, x2, y2, f, st);
    	if (tint==153)
    		return (CircuitElm) new NorGateElm(x1, y1, x2, y2, f, st);
    	if (tint==150)
    		return (CircuitElm) new AndGateElm(x1, y1, x2, y2, f, st);
    	if (tint==152)
    		return (CircuitElm) new OrGateElm(x1, y1, x2, y2, f, st);
    	if (tint==154)
    		return (CircuitElm) new XorGateElm(x1, y1, x2, y2, f, st);
    	if (tint==155)
    		return (CircuitElm) new DFlipFlopElm(x1, y1, x2, y2, f, st);
    	if (tint==156)
    		return (CircuitElm) new JKFlipFlopElm(x1, y1, x2, y2, f, st);
    	if (tint==157)
    		return (CircuitElm) new SevenSegElm(x1, y1, x2, y2, f, st);
    	if (tint==184)
    		return (CircuitElm) new MultiplexerElm(x1, y1, x2, y2, f, st);
    	if (tint==185)
    		return (CircuitElm) new DeMultiplexerElm(x1, y1, x2, y2, f, st);
    	if (tint==189)
    		return (CircuitElm) new SipoShiftElm(x1, y1, x2, y2, f, st);
    	if (tint==186)
    		return (CircuitElm) new PisoShiftElm(x1, y1, x2, y2, f, st);
    	if (tint==161)
    		return (CircuitElm) new PhaseCompElm(x1, y1, x2, y2, f, st);
    	if (tint==164)
    		return (CircuitElm) new CounterElm(x1, y1, x2, y2, f, st);
    	if (tint==163)
    		return (CircuitElm) new DecadeElm(x1, y1, x2, y2, f, st);
    	if (tint==165)
    		return (CircuitElm) new TimerElm(x1, y1, x2, y2, f, st);
    	if (tint==166)
    		return (CircuitElm) new DACElm(x1, y1, x2, y2, f, st);
    	if (tint==167)
    		return (CircuitElm) new ADCElm(x1, y1, x2, y2, f, st);
    	if (tint==168)
    		return (CircuitElm) new LatchElm(x1, y1, x2, y2, f, st);
    	if (tint==188)
    		return (CircuitElm) new SeqGenElm(x1, y1, x2, y2, f, st);
    	if (tint==158)
    		return (CircuitElm) new VCOElm(x1, y1, x2, y2, f, st);
    	if (tint=='b')
    		return (CircuitElm) new BoxElm(x1, y1, x2, y2, f, st);
    	if (tint=='x')
    		return (CircuitElm) new TextElm(x1, y1, x2, y2, f, st);
    	if (tint==193)
    		return (CircuitElm) new TFlipFlopElm(x1, y1, x2, y2, f, st);
    	if (tint==197)
    		return (CircuitElm) new SevenSegDecoderElm(x1, y1, x2, y2, f, st);
    	if (tint==196)
    		return (CircuitElm) new FullAdderElm(x1, y1, x2, y2, f, st);
    	if (tint==195)
    		return (CircuitElm) new HalfAdderElm(x1, y1, x2, y2, f, st);
    	if (tint==194)
    		return (CircuitElm) new MonostableElm(x1, y1, x2, y2, f, st);
    	if (tint==207)
    		return (CircuitElm) new LabeledNodeElm(x1, y1, x2, y2, f, st);
    	if (tint==208)
    	    return (CircuitElm) new CustomLogicElm(x1, y1, x2, y2, f, st);
    	if (tint==210)
    	    return (CircuitElm) new DataRecorderElm(x1, y1, x2, y2, f, st);
    	if (tint==211)
    	    return (CircuitElm) new AudioOutputElm(x1, y1, x2, y2, f, st);
    	if (tint==212)
    	    return (CircuitElm) new VCVSElm(x1, y1, x2, y2, f, st);
    	if (tint==213)
    	    return (CircuitElm) new VCCSElm(x1, y1, x2, y2, f, st);
    	if (tint==214)
    	    return (CircuitElm) new CCVSElm(x1, y1, x2, y2, f, st);
    	if (tint==215)
    	    return (CircuitElm) new CCCSElm(x1, y1, x2, y2, f, st);
    	if (tint==216)
    	    return (CircuitElm) new OhmMeterElm(x1, y1, x2, y2, f, st);
    	if (tint==368)
    	    return new TestPointElm(x1, y1, x2, y2, f, st);
    	if (tint==370)
    	    return new AmmeterElm(x1, y1, x2, y2, f, st);
    	if (tint==400)
    	    return new DarlingtonElm(x1, y1, x2, y2, f, st);
    	if (tint==401)
    	    return new ComparatorElm(x1, y1, x2, y2, f, st);
    	if (tint==402)
    	    return new OTAElm(x1, y1, x2, y2, f, st);
    	return
    			null;
    }

    public static CircuitElm constructElement(String n, int x1, int y1){
    	if (n=="GroundElm")
    		return (CircuitElm) new GroundElm(x1, y1);
    	if (n=="ResistorElm")
    		return (CircuitElm) new ResistorElm(x1, y1);
    	if (n=="RailElm")
    		return (CircuitElm) new RailElm(x1, y1);
    	if (n=="SwitchElm")
    		return (CircuitElm) new SwitchElm(x1, y1);
    	if (n=="Switch2Elm")
    		return (CircuitElm) new Switch2Elm(x1, y1);
    	if (n=="NTransistorElm")
    		return (CircuitElm) new NTransistorElm(x1, y1);
    	if (n=="PTransistorElm")
    		return (CircuitElm) new PTransistorElm(x1, y1);
    	if (n=="WireElm")
    		return (CircuitElm) new WireElm(x1, y1);
    	if (n=="CapacitorElm")
    		return (CircuitElm) new CapacitorElm(x1, y1);
    	if (n=="PolarCapacitorElm")
		return (CircuitElm) new PolarCapacitorElm(x1, y1);
    	if (n=="InductorElm")
    		return (CircuitElm) new InductorElm(x1, y1);
    	if (n=="DCVoltageElm")
    		return (CircuitElm) new DCVoltageElm(x1, y1);
    	if (n=="VarRailElm")
    		return (CircuitElm) new VarRailElm(x1, y1);
    	if (n=="PotElm")
    		return (CircuitElm) new PotElm(x1, y1);
    	if (n=="OutputElm")
    		return (CircuitElm) new OutputElm(x1, y1);
    	if (n=="CurrentElm")
    		return (CircuitElm) new CurrentElm(x1, y1);
    	if (n=="ProbeElm")
    		return (CircuitElm) new ProbeElm(x1, y1);
    	if (n=="DiodeElm")
    		return (CircuitElm) new DiodeElm(x1, y1);
    	if (n=="ZenerElm")
    		return (CircuitElm) new ZenerElm(x1, y1);
    	if (n=="ACVoltageElm")
    		return (CircuitElm) new ACVoltageElm(x1, y1);
    	if (n=="ACRailElm")
    		return (CircuitElm) new ACRailElm(x1, y1);
    	if (n=="SquareRailElm")
    		return (CircuitElm) new SquareRailElm(x1, y1);
    	if (n=="SweepElm")
    		return (CircuitElm) new SweepElm(x1, y1);
    	if (n=="LEDElm")
    		return (CircuitElm) new LEDElm(x1, y1);
    	if (n=="AntennaElm")
    		return (CircuitElm) new AntennaElm(x1, y1);
    	if (n=="LogicInputElm")
    		return (CircuitElm) new LogicInputElm(x1, y1);
    	if (n=="LogicOutputElm")
    		return (CircuitElm) new LogicOutputElm(x1, y1);
    	if (n=="TransformerElm")
    		return (CircuitElm) new TransformerElm(x1, y1);
    	if (n=="TappedTransformerElm")
    		return (CircuitElm) new TappedTransformerElm(x1, y1);
    	if (n=="TransLineElm")
    		return (CircuitElm) new TransLineElm(x1, y1);
    	if (n=="RelayElm")
    		return (CircuitElm) new RelayElm(x1, y1);
    	if (n=="MemristorElm")
    		return (CircuitElm) new MemristorElm(x1, y1);
    	if (n=="SparkGapElm")
    		return (CircuitElm) new SparkGapElm(x1, y1);
    	if (n=="ClockElm")
    		return (CircuitElm) new ClockElm(x1, y1);
    	if (n=="AMElm")
    		return (CircuitElm) new AMElm(x1, y1);
    	if (n=="FMElm")
    		return (CircuitElm) new FMElm(x1, y1);
    	if (n=="LampElm")
    		return (CircuitElm) new LampElm(x1, y1);
    	if (n=="PushSwitchElm")
    		return (CircuitElm) new PushSwitchElm(x1, y1);
    	if (n=="OpAmpElm")
    		return (CircuitElm) new OpAmpElm(x1, y1);
    	if (n=="OpAmpSwapElm")
    		return (CircuitElm) new OpAmpSwapElm(x1, y1);
    	if (n=="NMosfetElm")
    		return (CircuitElm) new NMosfetElm(x1, y1);
    	if (n=="PMosfetElm")
    		return (CircuitElm) new PMosfetElm(x1, y1);
    	if (n=="NJfetElm")
    		return (CircuitElm) new NJfetElm(x1, y1);
    	if (n=="PJfetElm")
    		return (CircuitElm) new PJfetElm(x1, y1);
    	if (n=="AnalogSwitchElm")
    		return (CircuitElm) new AnalogSwitchElm(x1, y1);
    	if (n=="AnalogSwitch2Elm")
    		return (CircuitElm) new AnalogSwitch2Elm(x1, y1);
    	if (n=="SchmittElm")
    		return (CircuitElm) new SchmittElm(x1, y1);
    	if (n=="InvertingSchmittElm")
    		return (CircuitElm) new InvertingSchmittElm(x1, y1);
    	if (n=="TriStateElm")
    		return (CircuitElm) new TriStateElm(x1, y1);
    	if (n=="SCRElm")
    		return (CircuitElm) new SCRElm(x1, y1);
    	if (n=="DiacElm")
    		return (CircuitElm) new DiacElm(x1, y1);
    	if (n=="TriacElm")
    		return (CircuitElm) new TriacElm(x1, y1);
    	if (n=="TriodeElm")
    		return (CircuitElm) new TriodeElm(x1, y1);
    	if (n=="TunnelDiodeElm")
    		return (CircuitElm) new TunnelDiodeElm(x1, y1);
    	if (n=="CC2Elm")
    		return (CircuitElm) new CC2Elm(x1, y1);
    	if (n=="CC2NegElm")
    		return (CircuitElm) new CC2NegElm(x1, y1);
    	if (n=="InverterElm")
    		return (CircuitElm) new InverterElm(x1, y1);
    	if (n=="NandGateElm")
    		return (CircuitElm) new NandGateElm(x1, y1);
    	if (n=="NorGateElm")
    		return (CircuitElm) new NorGateElm(x1, y1);
    	if (n=="AndGateElm")
    		return (CircuitElm) new AndGateElm(x1, y1);
    	if (n=="OrGateElm")
    		return (CircuitElm) new OrGateElm(x1, y1);
    	if (n=="XorGateElm")
    		return (CircuitElm) new XorGateElm(x1, y1);
    	if (n=="DFlipFlopElm")
    		return (CircuitElm) new DFlipFlopElm(x1, y1);
    	if (n=="JKFlipFlopElm")
    		return (CircuitElm) new JKFlipFlopElm(x1, y1);
    	if (n=="SevenSegElm")
    		return (CircuitElm) new SevenSegElm(x1, y1);
    	if (n=="MultiplexerElm")
    		return (CircuitElm) new MultiplexerElm(x1, y1);
    	if (n=="DeMultiplexerElm")
    		return (CircuitElm) new DeMultiplexerElm(x1, y1);
    	if (n=="SipoShiftElm")
    		return (CircuitElm) new SipoShiftElm(x1, y1);
    	if (n=="PisoShiftElm")
    		return (CircuitElm) new PisoShiftElm(x1, y1);
    	if (n=="PhaseCompElm")
    		return (CircuitElm) new PhaseCompElm(x1, y1);
    	if (n=="CounterElm")
    		return (CircuitElm) new CounterElm(x1, y1);
    	if (n=="DecadeElm")
    		return (CircuitElm) new DecadeElm(x1, y1);
    	if (n=="TimerElm")
    		return (CircuitElm) new TimerElm(x1, y1);
    	if (n=="DACElm")
    		return (CircuitElm) new DACElm(x1, y1);
    	if (n=="ADCElm")
    		return (CircuitElm) new ADCElm(x1, y1);
    	if (n=="LatchElm")
    		return (CircuitElm) new LatchElm(x1, y1);
    	if (n=="SeqGenElm")
    		return (CircuitElm) new SeqGenElm(x1, y1);
    	if (n=="VCOElm")
    		return (CircuitElm) new VCOElm(x1, y1);
    	if (n=="BoxElm")
    		return (CircuitElm) new BoxElm(x1, y1);
    	if (n=="TextElm")
    		return (CircuitElm) new TextElm(x1, y1);
    	if (n=="TFlipFlopElm")
    		return (CircuitElm) new TFlipFlopElm(x1, y1);
    	if (n=="SevenSegDecoderElm")
    		return (CircuitElm) new SevenSegDecoderElm(x1, y1);
    	if (n=="FullAdderElm")
    		return (CircuitElm) new FullAdderElm(x1, y1);
    	if (n=="HalfAdderElm")
    		return (CircuitElm) new HalfAdderElm(x1, y1);
    	if (n=="MonostableElm")
    		return (CircuitElm) new MonostableElm(x1, y1);
    	if (n=="LabeledNodeElm")
    		return (CircuitElm) new LabeledNodeElm(x1, y1);
    	if (n=="UserDefinedLogicElm")
    	    	return (CircuitElm) new CustomLogicElm(x1, y1);
    	if (n=="TestPointElm")
    	    	return new TestPointElm(x1, y1);
    	if (n=="AmmeterElm")
	    	return new AmmeterElm(x1, y1);
    	if (n=="DataRecorderElm")
		return (CircuitElm) new DataRecorderElm(x1, y1);
    	if (n=="AudioOutputElm")
		return (CircuitElm) new AudioOutputElm(x1, y1);
    	if (n=="NDarlingtonElm")
		return (CircuitElm) new NDarlingtonElm(x1, y1);
    	if (n=="PDarlingtonElm")
		return (CircuitElm) new PDarlingtonElm(x1, y1);
    	if (n=="ComparatorElm")
		return (CircuitElm) new ComparatorElm(x1, y1);
    	if (n=="OTAElm")
		return (CircuitElm) new OTAElm(x1, y1);
    	if (n=="NoiseElm")
		return (CircuitElm) new NoiseElm(x1, y1);
    	if (n=="VCVSElm")
		return (CircuitElm) new VCVSElm(x1, y1);
    	if (n=="VCCSElm")
		return (CircuitElm) new VCCSElm(x1, y1);
    	if (n=="CCVSElm")
		return (CircuitElm) new CCVSElm(x1, y1);
    	if (n=="CCCSElm")
		return (CircuitElm) new CCCSElm(x1, y1);
    	if (n=="OhmMeterElm")
		return (CircuitElm) new OhmMeterElm(x1, y1);
    	return null;
    }
    
    public void updateModels() {
	int i;
	for (i = 0; i != elmList.size(); i++)
	    elmList.get(i).updateModels();
    }
    

    
    
    native boolean weAreInUS() /*-{
    try {
	l = navigator.languages ? navigator.languages[0] : (navigator.language || navigator.userLanguage) ;  
    	if (l.length > 2) {
    		l = l.slice(-2).toUpperCase();
    		return (l == "US" || l=="CA");
    	} else {
    		return 0;
    	}

    } catch (e) { return 0;
    }
    }-*/;

    native boolean weAreInGermany() /*-{
    try {
	l = navigator.languages ? navigator.languages[0] : (navigator.language || navigator.userLanguage) ;
	return (l.toUpperCase().startsWith("DE"));
    } catch (e) { return 0;
    }
    }-*/;
    
    static String LS(String s) {
	if (s == null)
	    return null;
	String sm = localizationMap.get(s);
	if (sm != null)
	    return sm;
	
	// use trailing ~ to differentiate strings that are the same in English but need different translations.
	// remove these if there's no translation.
	int ix = s.indexOf('~');
	if (ix < 0)
	    return s;
	s = s.substring(0, ix);
	sm = localizationMap.get(s);
	if (sm != null)
	    return sm;
	return s;
    }
    static SafeHtml LSHTML(String s) { return SafeHtmlUtils.fromTrustedString(LS(s)); }
    
    
    // For debugging
    void dumpNodelist() {

	CircuitNode nd;
	CircuitElm e;
	int i,j;
	String s;
	String cs;
//
//	for(i=0; i<nodeList.size(); i++) {
//	    s="Node "+i;
//	    nd=nodeList.get(i);
//	    for(j=0; j<nd.links.size();j++) {
//		s=s+" " + nd.links.get(j).num + " " +nd.links.get(j).elm.getDumpType();
//	    }
//	    console(s);
//	}
	console("Elm list Dump");
	for (i=0;i<elmList.size(); i++) {
	    e=elmList.get(i);
	    cs = e.getDumpClass().toString();
	    int p = cs.lastIndexOf('.');
	    cs = cs.substring(p+1);
	    if (cs=="WireElm") 
		continue;
	    if (cs=="LabeledNodeElm")
		cs = cs+" "+((LabeledNodeElm)e).text;
	    if (cs=="TransistorElm") {
		if (((TransistorElm)e).pnp == -1)
		    cs= "PTransistorElm";
		else
		    cs = "NTransistorElm";
	    }
	    s=cs;
	    for(j=0; j<e.getPostCount(); j++) {
		s=s+" "+e.nodes[j];
	    }
	    console(s);
	}
    }
    
	native void printCanvas(CanvasElement cv) /*-{
	    var img    = cv.toDataURL("image/png");
	    var win = window.open("", "print", "height=500,width=500,status=yes,location=no");
	    win.document.title = "Print Circuit";
	    win.document.open();
	    win.document.write('<img src="'+img+'"/>');
	    win.document.close();
	    win.print();
	}-*/;

	void doDCAnalysis() {
	    dcAnalysisFlag = true;
	    resetAction();
	}
	
	void doPrint() {
	    	// create canvas to draw circuit into
	    	Canvas cv = Canvas.createIfSupported();
	    	Rectangle bounds = getCircuitBounds();
	    	int w = bounds.width * 2;
	    	int h = bounds.height * 2;
	    	cv.setCoordinateSpaceWidth(w);
	    	cv.setCoordinateSpaceHeight(h);
	    
		Context2d context = cv.getContext2d();
		Graphics g = new Graphics(context);
		context.setTransform(1, 0, 0, 1, 0, 0);
	        
	        double scale = 1;
	        
	        if (bounds != null)
		    // add some space on edges because bounds calculation is not perfect
	            scale = Math.min(w /(double)(bounds.width+140),
	                             h/(double)(bounds.height+100));
	        scale = Math.min(scale, 1.5); // Limit scale so we don't create enormous circuits in big windows
//	        console("scaling to " + scale + " " + cv.getOffsetWidth() + " " + bounds + " " + w + " " + h);
		context.scale(scale, scale);
		context.translate(-(bounds.x-70), -(bounds.y-50));
		
		// turn on white background, turn off current display
		boolean p = printableCheckItem.getState();
		boolean c = dotsCheckItem.getState();
		printableCheckItem.setState(true);
		dotsCheckItem.setState(false);
	  	CircuitElm.whiteColor = Color.black;
	  	CircuitElm.lightGrayColor = Color.black;
	  	g.setColor(Color.white);
		
		int i;
		for (i = 0; i != elmList.size(); i++) {
		    getElm(i).draw(g);
		}
		printableCheckItem.setState(p);
		dotsCheckItem.setState(c);
		
		printCanvas(cv.getCanvasElement());
	}
}
