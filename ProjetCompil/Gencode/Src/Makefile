# Makefile pour la passe 3

all : 
	cd ../../..; javac ProjetCompil/Gencode/Src/*.java

test-success-%: 
	@cd ../Test/success && make $(subst success-,,$@)

test-fail-%:
	@cd ../Test/fail && make $(subst success-,,$@)

test-clean:
	@cd ../Test/ && make clean

clean : 
	/bin/rm -f *.class

