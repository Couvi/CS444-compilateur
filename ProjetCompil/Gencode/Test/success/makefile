
allcas=$(addprefix src/,$(shell ls src))
allass=$(subst src/,asm/,$(subst .cas,.ass,$(allcas)))
alltest=$(subst src/,test-,$(subst .cas,,$(allcas)))

all: $(allass)

asm/%.ass: src/%.cas FORCE
	../compile $< asm/

test-%: asm/%.ass src/%.in src/%.out
	../run $< $(subst asm/,src/,$(subst .ass,.in,$<)) $(subst asm/,src/,$(subst .ass,.out,$<))
	ima $<

all-test: $(alltest)

FORCE:

%.in:

%.out:

clean: 
	rm -f asm/*.ass
