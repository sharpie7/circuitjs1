package com.lushprojects.circuitjs1.client;

public class ImportFromDropbox {

	
	static CirSim sim;
	
	ImportFromDropbox( CirSim asim ){
		sim=asim;
//		CirSim.console("importing");
		doDropboxImport();
//		CirSim.console("returned");
	}
	
	static public final native boolean isSupported() 
	/*-{
		try {
			// Bug in firefox prevents Dropbox dialog working properly in this application
			// even though Dropbox chooser supports firefox
			// See https://github.com/gwtproject/gwt/issues/7923
			if (/Firefox[\/\s](\d+\.\d+)/.test(navigator.userAgent))
				return false;
			return !!($wnd.Dropbox.isBrowserSupported());
		} 
		catch(err) {
			return false;
		}
 	}-*/;
	
	static public void doLoadCallback(String s) {
		sim.pushUndo();
		sim.readSetup(s, true);
	}
	
	
	public final native void doDropboxImport() 
	/*-{
		var options = {

		    // Required. Called when a user selects an item in the Chooser.
		    success: function(files) {
		    	try {
			        //console.log("Here's the file link: " + files[0].link);
			        if (files[0].bytes < 100000) {
				        var xhr= new XMLHttpRequest();
				        xhr.addEventListener("load", function reqListener() { 
	//			        	console.log(xhr.responseText);
				        	var text = xhr.responseText;
	      					@com.lushprojects.circuitjs1.client.ImportFromDropbox::doLoadCallback(Ljava/lang/String;)(text);
				        });
			        }
			        xhr.open("GET", files[0].link, false);
			        xhr.send();
		    	}
		        catch(err) {
		        } 
		    },
		
		    // Optional. Called when the user closes the dialog without selecting a file
		    // and does not include any parameters.
		    // cancel: function() {
		
		    //},
		
		    // Optional. "preview" (default) is a preview link to the document for sharing,
		    // "direct" is an expiring link to download the contents of the file. For more
		    // information about link types, see Link types below.
		    linkType: "direct", // "preview" or "direct"
		
		    // Optional. A value of false (default) limits selection to a single file, while
		    // true enables multiple file selection.
		    multiselect: false, // or true
		
		    // Optional. This is a list of file extensions. If specified, the user will
		    // only be able to select files with these extensions. You may also specify
		    // file types, such as "video" or "images" in the list. For more information,
		    // see File types below. By default, all extensions are allowed.
		    // extensions: ['.pdf', '.doc', '.docx'],
		};
		$wnd.Dropbox.choose(options);
	 }-*/;
}
