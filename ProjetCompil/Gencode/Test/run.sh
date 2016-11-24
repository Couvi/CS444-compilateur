#!/bin/sh

if [ ]
fich=$1
inf=$2
outf=$3

cat $inf | ima $fich > ima_temp
diff ima_temp $outf
ret=$?

rm ima_temp

exit $ret