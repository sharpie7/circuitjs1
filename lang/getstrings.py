import os
import re

with open("ignorelist.txt") as stream:
  ignorelist = stream.read().splitlines()

dir = "../src/com/lushprojects/circuitjs1/client"
files = [fn for fn in os.listdir(dir) if fn.endswith(".java")]

output = []

def checkString(bstr, str, astr, line):
  if re.search("\+ *$", bstr):
    return
  if re.search("^ *\+", astr):
    return
  if re.search("Elm$", str):
    return
  if re.search("iconMenuItem.$", bstr):
    return
  if re.search("menuItemWithShortcut.$", bstr):
    return
  if re.search("^#[0-9a-fA-F][0-9a-fA-F]", str):
    return
  if re.search("^###", str):
    return
  if ".html" in str:
    return
  if re.search('item==$', bstr):
    return
  if re.search('menu==$', bstr):
    return
  if not re.search('[a-zA-Z]', str):
    return
  if str in ignorelist:
    return
  output.append(str)

breaklist = ["MyCommand", "qp.get", "EventListener", "MouseEvent", "DependentName", "//", "new Pin",
        "replaceAll", "setStrokeStyle", "setFillStyle", "setWidth", "setHeight", "StyleName", "getUnitText",
        "setAttribute", "menuPerformed", 'skip(', "console", "createElement", "langString", "versionString",
        "getOptionFromStorage", "setOptionInStorage", "SuppressWarnings", "System.out.println",
        "setTextBaseline", "setColor", "setTextAlign"]

for fn in files:
  with open(dir + "/" + fn) as stream:
    content = stream.read().splitlines()
    for line in content:
      start = 0
      while True:
        a = line.find('"', start)
        if a < 0:
          break
        b = line.find('"', a+1)
        if b < 0:
          break
        bstr = line[0:a]
        astr = line[b+1:]
        str = line[a+1:b]
        if "\\" in str:
          break
        if any(ele in bstr for ele in breaklist):
          break
        checkString(bstr, str, astr, line)

        if "getClassCheckItem" in bstr:
          break
        start = b+1

with open("../src/com/lushprojects/circuitjs1/public/setuplist.txt") as stream:
  content = stream.read().splitlines()
  for line in content:
    if line.startswith("+"):
      output.append(line[1:])
    elif not line.startswith("#") and not line.startswith("-"):
      ix = line.index(" ")
      output.append(line[ix+1:])


dir = "../src/com/lushprojects/circuitjs1/public/circuits"
files = [fn for fn in os.listdir(dir) if fn.endswith(".txt")]

for fn in files:
  with open(dir + "/" + fn) as stream:
    content = stream.read().splitlines()
    for line in content:
      if line.startswith("x "):
        line2 = re.sub("^x ([-0-9]+ )*", "", line)
        line2 = re.sub("\\\\s", " ", line2)
        if len(line2) > 2 and not line2 in ignorelist:
          output.append(line2)

for str in sorted(set(output)):
  if len(str) > 2:
    print('"' + str + '"')

