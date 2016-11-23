#!/bin/sh

fich=$1
in=$2
out=$3

cat $in | ima $fich > ima_temp
diff ima_temp out
ret=$?

rm ima_temp

exit ret