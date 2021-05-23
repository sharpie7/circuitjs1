import sys

with open("all_strings.txt") as stream:
  data = stream.read()

input = data.splitlines()

for line in input:
  print(line + "=" + line)

