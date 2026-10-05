VIVADO ?= vivado
TOP ?= UIntMacroAdd

all:
	@rm -rf vivado report
	@mkdir -p vivado
	@mkdir -p report
	@cd vivado && $(VIVADO) -mode batch -source ../script/synth.tcl -tclargs $(TOP)

clean:
	@rm -rf vivado report