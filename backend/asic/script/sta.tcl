# Copyright 2020 Efabless Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#===========================================================
#   set parameter
#
#   Run with:
#     TOP=<module> PDK=<pdk> PDK_DIR=<pdk dir> \
#     NETLIST=<netlist.v> REPORT_DIR=<report dir> \
#     sta -no_init -no_splash -exit script/sta.tcl
#
#   Every parameter falls back to a sensible default, so the script
#   can also be run straight from the asic directory.
#===========================================================
proc env_or {name default} {
  global env
  if {[info exists env($name)] && $env($name) ne ""} {
    return $env($name)
  }
  return $default
}

set DESIGN     [env_or TOP UIntMacroAdd]
set PDK        [env_or PDK icsprout55]
set PDK_DIR    [env_or PDK_DIR /opt/pdk/icsprout55]
set PROJ_HOME  [env_or PROJ_HOME [pwd]]
set NETLIST    [env_or NETLIST $PROJ_HOME/build/syn/$DESIGN.netlist.v]
set RESULT_DIR [env_or REPORT_DIR $PROJ_HOME/report]

if {![file exists $NETLIST]} {
  puts "Error: netlist '$NETLIST' does not exist. Run 'make syn' first."
  exit 1
}
file mkdir $RESULT_DIR

# PDK definitions (LIB_FILES, BUF_CELL, ...)
source $PROJ_HOME/script/pdk/$PDK.tcl

#===========================================================
#   read design
#===========================================================
foreach lib $LIB_FILES { read_liberty $lib }
read_verilog $NETLIST
link_design $DESIGN

#===========================================================
#   constraints
#
#   default.sdc creates the core clock and defines clk_io_pct.
#===========================================================
source $PROJ_HOME/script/default.sdc
if {[info exists env(CLK_IO_PCT)]} {
  set clk_io_pct $::env(CLK_IO_PCT)
}

set CLK_PERIOD_NS [expr {1000.0 / $CLK_FREQ_MHZ}]
set io_delay     [expr {$CLK_PERIOD_NS * $clk_io_pct}]

set clk_port [get_ports $CLK_PORT_NAME]
set data_inputs [list]
foreach port [all_inputs] {
  if {![string equal $port $clk_port]} {
    lappend data_inputs $port
  }
}
set data_outputs [all_outputs]

set driver   [env_or DRIVER_CELL $BUF_CELL]
set cap_load [env_or CAP_LOAD 0.05]

if {[llength $data_inputs] > 0} {
  set_driving_cell -lib_cell $driver $data_inputs
  set_input_delay -clock core_clock $io_delay $data_inputs
}
if {[llength $data_outputs] > 0} {
  set_load $cap_load $data_outputs
  set_output_delay -clock core_clock $io_delay $data_outputs
}

#===========================================================
#   reports
#===========================================================
report_checks -path_delay max -format full_clock_expanded -digits 4 > $RESULT_DIR/timing.rpt
report_checks -path_delay min -format full_clock_expanded -digits 4 > $RESULT_DIR/timing.hold.rpt
report_checks -path_delay max -format summary -group_count 20 -endpoint_count 1 -digits 4 > $RESULT_DIR/timing.summary.rpt

report_power -digits 4 > $RESULT_DIR/power.rpt

set summary [open $RESULT_DIR/sta.rpt w]
puts $summary "OpenSTA summary for $DESIGN"
puts $summary "clock        : $CLK_PORT_NAME = $CLK_FREQ_MHZ MHz (period $CLK_PERIOD_NS ns)"
puts $summary "io delay     : $io_delay ns (${clk_io_pct} of the period)"
puts $summary "driver/load  : $driver / $cap_load pF"
puts $summary ""
close $summary
report_worst_slack -digits 4 >> $RESULT_DIR/sta.rpt
report_tns -digits 4 >> $RESULT_DIR/sta.rpt
report_wns -digits 4 >> $RESULT_DIR/sta.rpt
report_clock_properties >> $RESULT_DIR/sta.rpt

set check [open $RESULT_DIR/check.rpt w]
puts $check "check_setup -verbose for $DESIGN"
puts $check ""
close $check
check_setup -verbose >> $RESULT_DIR/check.rpt

puts "STA reports written to $RESULT_DIR"
