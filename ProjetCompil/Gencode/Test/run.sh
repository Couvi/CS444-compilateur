#!/bin/sh

RED="$(tput bold ; tput setaf 1)"
GREEN="$(tput bold ; tput setaf 2)"
NC="$(tput sgr0)" # No Color



fich=$1
inf=$2
outf=$3
ret=0

if [ ! -f $inf ] || [ ! -f $outf ]; then
    echo "pas de fichier in/out pour $fich execution simple"
    ima $fich > ima_temp
else
	cat $inf | ima $fich > ima_temp
	diff ima_temp $outf
	ret=$?
fi
errima=$(cat ima_temp)
if expr match "$errima" "*****"; then
		ret=$(($ret + 1))
		cat ima_temp
fi
if [ $ret -ne 0 ]
then
	echo "${RED}gentest $fich FAILED${NC}"
else
	echo "${GREEN}gentest $fich OK${NC}"
fi
if [ $4 ]
then
	cat ima_temp
fi
rm ima_temp
echo ""
exit $ret
