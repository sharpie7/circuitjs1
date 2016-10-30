#CircuitJS1

##Introduction

CircuitJS1 is an electronic circuit simulator that runs in the browser. It was originally written by Paul Falstad as a Java Applet. It was adapted by Iain Sharp to run in the browser using GWT.

For a hosted version of the application see:

* Paul's Page: [http://www.falstad.com/circuit/](http://www.falstad.com/circuit/)
* Iain's Page: [http://lushprojects.com/circuitjs/](http://lushprojects.com/circuitjs/)

Thanks to Edward Calver for 15 new components and other improvements. Thanks to Rodrigo Hausen for file import/export and many other UI improvements. Thanks to J. Mike Rollins for the Zener diode code. Thanks to Julius Schmidt for the spark gap code and some examples. Thanks to Dustin Soodak for help with the user interface improvements. Thanks to Jacob Calvert for the T Flip Flop. 

##Building the application

The tools you will need to build the project are:

* Java JDK 8 or newer
* Maven 3 or newer

1) Checkout the project and open a command line in the project directory

2) Build the project via:
> mvn clean install

3) GWT will build it's output in to the "target/circuitjs1-1.0" directory. You can open the "circuitjs.html" file with a browser and run the simulator. "iframe.html" is loaded as an iFrame in to the spare space at the bottom of the right hand panel. It can be used for branding etc.

##Deployment

* The build procedure explained above will output everything you need in the "target/circuitjs1-1.0" directory. You can deploy that directory to a web server, skipping the WEB-INF sub-directory.
* Customize the header of the file "circuitjs1.html" to include your tracking, favicon etc.
* Customize the "iframe.html" file to include any branding you want in the right hand panel of the application

The link for the full-page version of the application is now:
`http://<your host>/<your path>/circuitjs1.html`
(you can rename the "circuitjs1.html" file if you want too).

Just for reference the files should look like this

```
-+ Directory containing the front page (eg "circuitjs")
  +- circuitjs.html - full page version of application
  +- iframe.html - see notes above
  ++ circuitjs1 (directory)
   +- various files built by GWT
   +- circuits (directory, containing example circuits)
   +- setuplist.txt (index in to example circuit directory)
```
   
## Embedding

You can link to the full page version of the application using the link shown above.

If you want to embed the application in another page then use an iframe with the src being the full-page version.

You can add query parameters to link to the full page version to change it's startup behaviour. The following are supported:
```
.../circuitjs1.html?cct=<string> // Load the circuit from the URL (like the # in the Java version)
.../circuitjs1.html?startCircuit=<filename> // Loads the circuit named "filename" from the "Circuits" directory
.../circuitjs1.html?euroResistors=<true|false>
.../circuitjs1.html?whiteBackground=<true|false>
.../circuitjs1.html?conventionalCurrent=<true|false>
```

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