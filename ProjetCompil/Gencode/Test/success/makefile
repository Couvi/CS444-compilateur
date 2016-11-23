
allcas=$(addprefix src/,$(shell ls src))
allass=$(subst src/,asm/,$(subst .cas,.ass,$(allcas)))
alltest=$(subst src/,test-,$(subst .cas,,$(allcas)))

all: $(allass)

asm/%.ass: src/%.cas FORCE
	../compile $< asm/

test-%: asm/%.ass
	ima $<

all-test: $(alltest)

FORCE:

clean: 
	rm -f asm/*.ass
