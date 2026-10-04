PRJ = _
TARGET ?= harith.uint.UIntMacroAdder

MILL = ./mill
JAVA = java

BUILD_DIR = build

TEST_DIR = $(BUILD_DIR)/test

TEST_TARGET ?= $(TARGET)
TEST_NAME = $(lastword $(subst ., ,$(TEST_TARGET)))
TEST_TARGET_DIR = $(TEST_DIR)/$(TEST_NAME)


# Generate / Tests

verilog:
	@echo Exporting SystemVerilog...
	@$(MILL) $(PRJ).runMain $(TARGET) --target-dir $(BUILD_DIR)

test-all:
	@echo Conducting all Tests..
	@$(MILL) $(PRJ).test

test:
	@echo Conducting Test for $(TEST_TARGET)
	@$(MILL) $(PRJ).test.testOnly $(TEST_TARGET) -v

test-wave:
	@mkdir -p $(TEST_DIR)
	@rm -rf $(TEST_TARGET_DIR)
	@echo Conducting Test for $(TEST_TARGET) with Waveform
	@$(MILL) $(PRJ).test.testOnly $(TEST_TARGET) --verbose -- -DemitVcd=1

format:
	@$(MILL) _.reformat


# Publish

MVN_DIR ?= .deps

lib:
	@echo Publishing M2 library to $(MVN_DIR)...
	@$(MILL) $(PRJ).publishM2Local --m2RepoPath $(abspath $(MVN_DIR))


sonatype:
	@echo Publishing to Sonatype...
	@$(MILL) _.publishSonatypeCentral


# Clean up

clean:
	@rm -rf $(BUILD_DIR)