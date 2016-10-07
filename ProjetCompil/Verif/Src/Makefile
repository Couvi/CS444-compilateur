# Makefile pour la passe 2

all : 
	cd ../../..; javac ProjetCompil/Verif/Src/*.java

html :
	cd ../Doc; javadoc ..*/Src/*.java
clean : 
	/bin/rm -f *.class

veriftest-%: ../Test/%/*.cas
	../Test/veriftest $^

test : all
	java ProjetCompil.Verif.Src.TestVerif ../Test/factorial.cas