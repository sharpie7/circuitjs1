const { remote, dialog } = require('electron');
const fs = require('fs');

let currWindow = remote.BrowserWindow.getFocusedWindow();

var lastSavedFilePath = null;

window.showSaveDialog = function () { return remote.dialog.showSaveDialog(null); } 
window.saveFile = function (file, text) {
  var path;
  if (!file)
    path = lastSavedFilePath;
  else {
    path = file.filePath.toString();
    lastSavedFilePath = path;
  }
  fs.writeFile(path, text, function (err) { if (err) window.alert(err); });
}

window.openFile = function (callback) {
  remote.dialog.showOpenDialog({ properties: ['openFile']}).then(function(result) {
    if (result == undefined) return;
    var fileName = result.filePaths[0];
    fs.readFile(fileName, 'utf-8', function (err, data) {
      if (err) { if (err) window.alert(err); return; }
      lastSavedFilePath = fileName;
      var shortName = fileName.substring(fileName.lastIndexOf('/')+1);
      shortName = shortName.substring(shortName.lastIndexOf("\\")+1);
      callback(data, shortName);
    });
  });
}

window.toggleDevTools = function () {
  remote.getCurrentWindow().toggleDevTools();
}

