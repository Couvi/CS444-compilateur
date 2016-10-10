# Makefile pour la passe 2

all : 
	cd ../../..; javac ProjetCompil/Verif/Src/*.java

html :
	cd ../Doc; javadoc ..*/Src/*.java
clean : 
	/bin/rm -f *.class
	
testTypage : all
	java ProjetCompil.Verif.Src.TestReglesTypage

veriftest: 
	../Test/veriftest

test-% : ../Test/success/%.cas 
	java ProjetCompil.Verif.Src.TestVerif $^
