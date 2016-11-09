package com.lushprojects.circuitjs1.client;

import java.util.Date;

import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;

public class ExportToDropbox {
	
	static public final native boolean isSupported() 
	/*-{
		try {
			// Bug in firefox prevents Dropbox dialog working properly in this application
			// even though Dropbox saver supports firefox
			// See https://github.com/gwtproject/gwt/issues/7923
			if (/Firefox[\/\s](\d+\.\d+)/.test(navigator.userAgent))
				return false;
			return !!($wnd.Dropbox.isBrowserSupported());
		} 
		catch(err) {
			return false;
		}
 	}-*/;
	
	public ExportToDropbox(String data) {
		String url="data:text/plain,"+URL.encode(data);
		Date date = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd-HHmm");
		String fname = "circuit-"+ dtf.format(date) + ".circuitjs.txt";
		doDropboxExport(url, fname);
	}
	

	
	public final native void doDropboxExport(String url, String name) 
	/*-{
		var options = {
		    files: [
		        // You can specify up to 100 files.
		        {'url': url, 'filename': name}
		    ],
		
		    // Success is called once all files have been successfully added to the user's
		    // Dropbox, although they may not have synced to the user's devices yet.
		    success: function () {
		        // Indicate to the user that the files have been saved.
		        alert("Success! Files saved to your Dropbox.");
		    },
		
		    // Progress is called periodically to update the application on the progress
		    // of the user's downloads. The value passed to this callback is a float
		    // between 0 and 1. The progress callback is guaranteed to be called at least
		    // once with the value 1.
		    progress: function (progress) {},
		
		    // Cancel is called if the user presses the Cancel button or closes the Saver.
		    cancel: function () {},
		
		    // Error is called in the event of an unexpected response from the server
		    // hosting the files, such as not being able to find a file. This callback is
		    // also called if there is an error on Dropbox or if the user is over quota.
		    error: function (errorMessage) {
		    	alert("Dropbox error "+errorMessage);
		    }
		};
		$wnd.Dropbox.save(options);
	 }-*/;

}
