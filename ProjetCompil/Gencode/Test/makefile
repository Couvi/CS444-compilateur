

all: 
	@cd success && make all
	@cd fail && make all

all-test:
	@cd success && make all-test
	@cd fail && make all-test

test-success-%: 
	@cd success && make $(subst success-,,$@)

test-fail-%:
	@cd fail && make $(subst success-,,$@)
	
clean:
	@cd success && make clean
	@cd fail && make clean