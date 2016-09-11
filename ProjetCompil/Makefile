# Makefile général pour le projet
# 
# - Recompilation de tous les répertoires : 
#      make
#
# - Génération de la documentation html : 
#      make html
#
# - Faire du ménage dans les répertoires Src :
#      make clean

all :
	cd Global/Src; make
	cd Global/Src3; make
	cd Syntaxe/Src; make
	cd Verif/Src; make
	cd Gencode/Src; make

html :
	cd Html; javadoc ../Global/Src/*.java ../Global/Src3/*.java ../Syntaxe/Src/*.java ../Verif/Src/*.java ../Gencode/Src/*.java

clean : 
	cd Global/Src; make clean
	cd Global/Src3; make clean
	cd Syntaxe/Src; make clean
	cd Verif/Src; make clean
	cd Gencode/Src; make clean


