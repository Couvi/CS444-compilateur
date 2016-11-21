
allcas=$(addprefix src/,$(shell ls src))
allass=$(subst src/,asm/,$(subst .cas,.ass,$(allcas)))
alltest=$(subst src/,test-,$(subst .cas,,$(allcas)))

all: $(allass)

asm/%.ass: src/%.cas
	../compile $^ asm/

test-%: asm/%.ass
	#ima $^
	cat $^

all-test: $(alltest)

clean: 
	rm -f asm/*.ass
