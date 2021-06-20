const electron = require('electron')
// Module to control application life.
const app = electron.app
// Module to create native browser window.
const BrowserWindow = electron.BrowserWindow
const Menu = electron.Menu

const path = require('path')
const url = require('url')

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
var windows = [];

Menu.setApplicationMenu(false);

// save arguments
global.sharedObject = {prop1: process.argv};

function createWindow () {
  // Create the browser window.
  var mainWindow = new BrowserWindow({width: 800, 
    height: 600,
    webPreferences: { nativeWindowOpen: true,
                      preload: path.join(__dirname, 'preload.js')
    }
  })
  windows.push(mainWindow);

  // and load the index.html of the app.
  mainWindow.loadURL(url.format({
    // pathname: path.join(__dirname, 'index.html'),
    pathname: path.join(__dirname, 'war/circuitjs.html'),
    protocol: 'file:',
    slashes: true
  }))


  // Open the DevTools.
  // mainWindow.webContents.openDevTools()

  // Emitted when the window is closed.
  mainWindow.on('closed', function () {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    var i = windows.indexOf(mainWindow);
    if (i >= 0)
      windows.splice(i, 1);
  })

  mainWindow.webContents.on('new-window', (evt, url, frameName, disposition, options) => {
	if (disposition == 'save-to-disk')
		return;
	if (!url.endsWith("circuitjs.html"))
		return;
        // app is opening a new window.  override it by creating a BrowserWindow to work around an electron bug (11128)
	evt.preventDefault();
	createWindow();
  });

}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow)

// Quit when all windows are closed.
app.on('window-all-closed', function () {
  // On OS X it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', function () {
  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (windows.length == 0) {
    createWindow()
  }
})

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.
