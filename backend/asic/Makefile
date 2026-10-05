TOP ?= UIntMacroAdder

PDK = icsprout55
PDK_DIR = /home/hamster/pdk/icsprout55

BUILD_DIR = build
SYN_DIR = $(BUILD_DIR)/syn
REPORT_DIR = report

STA ?= sta
CLK_FREQ_MHZ ?= 500
CLK_PORT_NAME ?= clock

VSRCS = $(shell find rtl -name "*.sv")
NETLIST = $(SYN_DIR)/$(TOP).netlist.v

DEFAULT_GOAL := all

syn: $(NETLIST)
$(NETLIST):
	@rm -rf $(SYN_DIR)
	@mkdir -p $(SYN_DIR)
	@echo tcl script/syn.tcl $(TOP) $(PDK) $(PDK_DIR) \"$(VSRCS)\" $(NETLIST) | yosys -g -l $(SYN_DIR)/yosys.log -s -

sta: $(NETLIST)
	@echo Running OpenSTA...
	@rm -rf $(REPORT_DIR)
	@mkdir -p $(REPORT_DIR)
	@TOP=$(TOP) PDK=$(PDK) PDK_DIR=$(PDK_DIR) \
		NETLIST=$(abspath $(NETLIST)) REPORT_DIR=$(abspath $(REPORT_DIR)) \
		CLK_FREQ_MHZ=$(CLK_FREQ_MHZ) CLK_PORT_NAME=$(CLK_PORT_NAME) \
		$(STA) -no_init -no_splash -exit script/sta.tcl

all: syn sta

clean:
	@rm -rf build $(REPORT_DIR)

.PHONY: syn sta clean $(NETLIST)
