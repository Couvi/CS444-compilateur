
allcas=$(shell ls src/*.cas)
allass=$(subst src/,asm/,$(subst .cas,.ass,$(allcas)))
alltest=$(subst src/,test-,$(subst .cas,,$(allcas)))


all: $(allass)

asm/%.ass: src/%.cas FORCE
	../compile $< asm/

test-%: asm/%.ass FORCE
	../run.sh $< $(subst asm/,src/,$(subst .ass,.in,$<)) $(subst asm/,src/,$(subst .ass,.out,$<)) $(silence)

all-test: $(alltest)

FORCE:


clean: 
	rm -f asm/*.ass
