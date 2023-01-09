#!/bin/sh
echo run getstrings first
for i in da de es fr it nb pl pt ru zh zh-tw
do
  python3 findmissing.py ../src/com/lushprojects/circuitjs1/public/locale_$i.txt > missing_$i.txt
done
