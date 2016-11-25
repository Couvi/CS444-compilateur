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
#
# $HOME/CS444-compilateur

all :
	cd Global/Src; make
	cd Global/Src3; make
	cd Syntaxe/Src; make
	cd Verif/Src; make
	cd Gencode/Src; make

test : 
	cd Syntaxe/Src; make lextest-devlex
	cd Syntaxe/Src; make syntest-devsyn

html :
	cd Html; javadoc -encoding utf8 -docencoding utf8 -charset utf8 ../Global/Src/*.java ../Global/Src3/*.java ../Syntaxe/Src/*.java ../Verif/Src/*.java ../Gencode/Src/*.java
	firefox Html/index.html

clean : 
	cd Global/Src; make clean
	cd Global/Src3; make clean
	cd Syntaxe/Src; make clean
	cd Verif/Src; make clean
	cd Gencode/Src; make clean

coberturaVerif :
	cd Cobertura ; ./testCouvertureVerif ../.. ../Verif/Test/*/*.cas
	firefox Cobertura/CouvertureVerif/index.html
	
coberturaGencode :
	cd Cobertura ; ./testCouvertureGencode ../.. ../Gencode/Test/*/src/*.cas
	firefox Cobertura/CouvertureGencode/index.html
	rm Cobertura/*.ass