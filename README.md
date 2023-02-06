# CircuitJS1

## Introduction

CircuitJS1 is an electronic circuit simulator that runs in the browser. It was originally written by Paul Falstad as a Java Applet. It was adapted by Iain Sharp to run in the browser using GWT.

For a hosted version of the application see:

* Paul's Page: [http://www.falstad.com/circuit/](http://www.falstad.com/circuit/)
* Iain's Page: [http://lushprojects.com/circuitjs/](http://lushprojects.com/circuitjs/)

Thanks to: Edward Calver for 15 new components and other improvements; Rodrigo Hausen for file import/export and many other UI improvements; J. Mike Rollins for the Zener diode code; Julius Schmidt for the spark gap code and some examples; Dustin Soodak for help with the user interface improvements; Jacob Calvert for the T Flip Flop; Ben Hayden for scope spectrum; Thomas Reitinger, Krystian Sławiński, Usevalad Khatkevich, Lucio Sciamanna, Mauro Hemerly Gazzani, J. Miguel Silva, and Franck Viard for translations; Andre Adrian for improved emitter coupled oscillator; Felthry for many examples; Colin Howell for code improvements. LZString (c) 2013 pieroxy.

## Building the web application

The web application can be compiled and run locally using Eclipse, or in a cloud development container like Github Codespaces or gitpod.io. Both of these services provide a number of free usage hours every month. You can also use the cloud tools from `./dev.sh` on your local Linux machine or in a local docker container.

### Development using Eclipse

The tools you will need to build the project are:

* Eclipse, Oxygen version.
* GWT plugin for Eclipse.

Install "Eclipse for Java developers" from [here](https://www.eclipse.org/downloads/packages/). To add the GWT plugin for Eclipse follow the instructions [here](https://gwt-plugins.github.io/documentation/gwt-eclipse-plugin/Download.html).

This repository is a project folder for your Eclipse project space. Once you have a local copy you can then build and run in development mode or build for deployment. Running in super development mode is done by clicking on the "run" icon on the toolbar and choosing http://127.0.0.1:8888/circuitjs.html from the "Development Mode" tab which appears. Building for deployment is done by selecting the project root node and using the GWT button on the Eclipse taskbar and choosing "GWT Compile Project...".

GWT will build its output in to the "war" directory. In the "war" directory the file "iframe.html" is loaded as an iFrame in to the spare space at the bottom of the right hand pannel. It can be used for branding etc.

### Development using cloud containers

1. Install [Visual Studio Code](https://code.visualstudio.com/) and the appropriate remote extension: either [Gitpod Extension](https://marketplace.visualstudio.com/items?itemName=gitpod.gitpod-desktop) or [Codespaces Extension](https://marketplace.visualstudio.com/items?itemName=GitHub.codespaces).
2. Open your fork of the `circuitjs1` repository in your chosen provider's dev container.
3. This should open a new tab in your browser showing VS Code. Click in the green button in the bottom left corner, then select "Open in VS Code Desktop" in the popup menu that opened. Click "Allow" in all URL popups and authenticate using github if asked.

Once you have successfully connected your local VS Code to the remote workspace, you should be able to see the content of the remote container in your local VS Code. You can now continue with the setup:

4. Open a shell inside the dev container by pressing `Ctrl+Backtick` or pressing `F1` and typing "Create new Terminal".
5. Make sure you are in the folder `/workspaces/circuitjs1` inside the container (necessary only once per newly created container).
6. Run `./dev.sh setup` to install all development dependencies, including GWT and Java.
7. Run `./dev.sh start` to start the web server and the GWT code server. This will start two services: http://localhost:8000 and http://localhost:9876.
8. Make sure both port and 8000 and 9876 are forwarded in the "Ports" tab (next to "Terminal").
9. If you edit a Java file in VS Code and reload http://localhost:8000, it should recompile the file automatically. It will then load the compiled JavaScript and the corresponding source map from the code server running on http://localhost:9876. You should be able to see the your changes in the web application.

> ***Note:*** When running the web application server inside a remote dev container, port forwarding is necessary in order to access the remote server from your own computer. This port forwarding is provided by Visual Studio Code running on your local computer.
>
> Theoretically, it would be possible to use the browser-based VS Code interface. However, both Gitpod and Codespaces map forwarded ports to different domain names instead of different ports, which confuses the GWT code loader. It is possible to fix this by live-patching the `serverUrl` variable in `circuitjs1.nocache.js` using a custom HTTP server, but it also requires setting the port visibility to "Public" to avoid CORS errors due to redirects. Using a local installation of VS Code is much simpler.


## Deployment of the web application

* "GWT Compile Project..." as explained above or run `./dev.sh compile`. This will put the outputs in to the "war" directory in the Eclipse project folder. You then need to copy everything in the "war" directory, except the "WEB-INF" directory, on to your web server.
* Customize the header of the file "circuitjs1.html" to include your tracking, favicon etc.
* Customize the "iframe.html" file to include any branding you want in the right hand panel of the application
* The optional file "shortrelay.php" is a server-side script to act as a relay to a URL shortening service to avoid cross-origin problems with a purely client solution. You may want to customize this for your site. If you don't want to use this feature edit the circuitjs1.java file before compiling.
* If you wish to enable dropbox loading and saving a dropbox API app-key is needed. This should be edited in to the circuitjs.html file where needed. If this is not included the relevant features will be disabled.


The link for the full-page version of the application is now:
`http://<your host>/<your path>/circuitjs1.html`
(you can rename the "circuitjs1.html" file if you want too though you should also update "shortrelay.php" if you do).

Just for reference the files should look like this

```
-+ Directory containing the front page (eg "circuitjs")
  +- circuitjs.html - full page version of application
  +- iframe.html - see notes above
  +- shortrelay.php - see notes above
  ++ circuitjs1 (directory)
   +- various files built by GWT
   +- circuits (directory, containing example circuits)
   +- setuplist.txt (index in to example circuit directory)
```

## Embedding

You can link to the full page version of the application using the link shown above.

If you want to embed the application in another page then use an iframe with the src being the full-page version.

You can add query parameters to link to change the applications startup behaviour. The following are supported:
```
.../circuitjs.html?cct=<string> // Load the circuit from the URL (like the # in the Java version)
.../circuitjs.html?ctz=<string> // Load the circuit from compressed data in the URL
.../circuitjs.html?startCircuit=<filename> // Loads the circuit named "filename" from the "Circuits" directory
.../circuitjs.html?startCircuitLink=<URL> // Loads the circuit from the specified URL. CURRENTLY THE URL MUST BE A DROPBOX SHARED FILE OR ANOTHER URL THAT SUPPORTS CORS ACCESS FROM THE CLIENT
.../circuitjs.html?euroResistors=true // Set to true to force "Euro" style resistors. If not specified the resistor style will be based on the user's browser's language preferences
.../circuitjs.html?IECGates=true // Set to true to force IEC logic gates. If not specified the gate style will be based on the user's browser's language preferences
.../circuitjs.html?usResistors=true // Set to true to force "US" style resistors. If not specified the resistor style will be based on the user's browser's language preferences
.../circuitjs.html?whiteBackground=<true|false>
.../circuitjs.html?conventionalCurrent=<true|false>
.../circuitjs.html?running=<true|false> // Start the app without the simulation running, default true
.../circuitjs.html?hideSidebar=<true|false> // Hide the sidebar, default false
.../circuitjs.html?hideMenu=<true|false> // Hide the menu, default false
.../circuitjs.html?editable=<true|false> // Allow circuit editing, default true
.../circuitjs.html?positiveColor=%2300ff00 // change positive voltage color (rrggbb)
.../circuitjs.html?negativeColor=%23ff0000 // change negative voltage color
.../circuitjs.html?selectColor=%2300ffff // change selection color
.../circuitjs.html?currentColor=%23ffff00 // change current color
.../circuitjs.html?mouseWheelEdit=<true|false> // allow changing of values by mouse wheel
.../circuitjs.html?mouseMode=<item> // set the initial mouse mode.  can also initially perform other UI actions, such as opening the 'about' menu, running 'importfromlocalfile', etc.
.../circuitjs.html?hideInfoBox=<true|false>
```
The simulator can also interface with your javascript code.  See [war/jsinterface.html](http://www.falstad.com/circuit/jsinterface.html) for an example.

## Building an Electron application

The [Electron](https://electronjs.org/) project allows web applications to be distributed as local executables for a variety of platforms. This repository contains the additional files needed to build circuitJS1 as an Electron application.

The general approach to building an Electron application for a particular platform is documented [here](https://electronjs.org/docs/tutorial/application-distribution). The following instructions apply this approach to circuit JS.

To build the Electron application:
* Compile the application using GWT, as above.
* Download and unpack a [pre-built Electron binary directory](https://github.com/electron/electron/releases) version 9.3.2 for the target platform.
* Copy the "app" directory from this repository to the location specified [here](https://electronjs.org/docs/tutorial/application-distribution) in the Electron binary directory structure.
* Copy the "war" directory, containing the compiled CircuitJS1 application, in to the "app" directory the Electron binary directory structure.
* Run the "Electron" executable file. It should automatically load CircuitJS1.

Known limitations of the Electron application:
* "Create short URL" on "Export as URL" doesn't work as it relies on server support.

Thanks to @Immortalin for the initial work in applying Electron to CircuitJS1.

## License

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
