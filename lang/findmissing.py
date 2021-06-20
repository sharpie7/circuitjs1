import os
import re
import sys

missing = {}

with open("all_strings.txt") as stream:
  for line in stream.read().splitlines():
    missing[line[1:-1]] = True

with open(sys.argv[1], 'r') as stream:
  text = stream.read().splitlines()

for line in text:
  a = line.find('"="')
  if a >= 0:
    missing[line[1:a]] = False

for key in sorted(missing.keys()):
  if missing[key]:
    print('"'+key+'"')

