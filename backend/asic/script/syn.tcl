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
#===========================================================
set DESIGN                  [lindex $argv 0]
set PDK                     [lindex $argv 1]
set PDK_DIR                 [lindex $argv 2]
set VERILOG_FILES           [string map {"\"" ""} [lindex $argv 3]]
set NETLIST_SYN_V           [lindex $argv 4]
set VERILOG_INCLUDE_DIRS    ""
set RESULT_DIR              [file dirname $NETLIST_SYN_V]

source "[file dirname [info script]]/common.tcl"

set CLK_FREQ_MHZ            500
if {[info exists env(CLK_FREQ_MHZ)]} {
  set CLK_FREQ_MHZ          $::env(CLK_FREQ_MHZ)
} else {
  puts "Warning: Environment CLK_FREQ_MHZ is not defined. Use $CLK_FREQ_MHZ MHz by default."
}
set CLK_PERIOD_NS           [expr 1000.0 / $CLK_FREQ_MHZ]
set CLK_PERIOD_PS           [expr 1000.0 * $CLK_PERIOD_NS]

set LIBS [concat {*}[lmap lib $LIB_FILES {concat "-liberty" $lib}]]
set EXCLUDE_CELLS [concat {*}[lmap cell $DONT_USE_CELLS {concat "-dont_use" $cell}]]

#===========================================================
#   ABC constraints
#===========================================================

set driver $BUF_CELL
# unit: pF
set cap_load 1.6

# Create the ABC timing constraint file (driving cell and output load)
set sdc_file $RESULT_DIR/abc.sdc
set outfile [open ${sdc_file} w]
puts $outfile "set_driving_cell ${driver}"
puts $outfile "set_load ${cap_load}"
close $outfile

#===========================================================
#   main running
#===========================================================
yosys -import

# read verilog files
foreach file $VERILOG_FILES {
  read_verilog -sv $file
}

# load liberty file before checking
foreach l $LIB_FILES { read_liberty -lib $l }

# generic synthesis (coarse)
synth -top $DESIGN -flatten -run :fine

share -aggressive
onehot
muxpack
opt_demorgan
opt_ffinv

# generic synthesis (fine). The internal generic abc pass is skipped because
# the technology mapping below runs abc with the target liberty file, which
# makes the generic LUT mapping a redundant (and slow) extra layer.
synth -noabc -run fine:

# remove unused cells and wires
opt_clean -purge

# split internal nets
splitnets -format __v
# rename DFFs from the driven signal
yosys rename -wire -suffix _reg_p t:*DFF*_P*
yosys rename -wire -suffix _reg_n t:*DFF*_N*
# rename all other cells
autoname t:*DFF* %n

# technology mapping for clockgate
clockgate {*}$LIBS {*}$EXCLUDE_CELLS

# Cell mapping.
# Retiming is enabled by default: the flip-flops are mapped to generic cells
# with `simplemap`, handed to ABC via `-dff` (so ABC actually sees the latches)
# and moved by ABC's `retime` command towards the target period. Set
# ABC_RETIME=0 to fall back to `dfflibmap` + plain mapping without retiming.
# ABC_RETIME_SCRIPT can override the ABC retiming/mapping script.
set ABC_RETIME 1
if {[info exists env(ABC_RETIME)]} {
  set ABC_RETIME $::env(ABC_RETIME)
}
log "\[INFO\]: ABC retiming = $ABC_RETIME"

if {$ABC_RETIME} {
  set abc_retime_script "+strash; retime -M 4 {D}; strash; dch; map"
  if {[info exists env(ABC_RETIME_SCRIPT)]} {
    set abc_retime_script $::env(ABC_RETIME_SCRIPT)
  }
  simplemap
  opt -undriven -purge
  abc -dff -D "$CLK_PERIOD_PS" \
    -constr "$sdc_file" \
    {*}$LIBS {*}$EXCLUDE_CELLS \
    -script "$abc_retime_script" \
    -showtmp
  dfflibmap {*}$LIBS {*}$EXCLUDE_CELLS
  opt -undriven -purge
} else {
  dfflibmap {*}$LIBS {*}$EXCLUDE_CELLS
  opt -undriven -purge
  abc {*}$LIBS {*}$EXCLUDE_CELLS -script "+strash; dch; map"
}

# technology mapping for constant hi- and/or lo-drivers
hilomap -singleton -hicell {*}$TIEHI_CELL_AND_PORT -locell {*}$TIELO_CELL_AND_PORT

# replace undef values with defined constants
setundef -zero

# remove unused cells and wires
opt_clean -purge

# Generate public names for the various nets, resulting in very long names that include
# the full heirarchy, which is preferable to the internal names that are simply
# sequential numbers such as `_000019_`. Renamed net names can be very long, such as:
#     manual_reset_gf180mcu_fd_sc_mcu7t5v0__dffq_1_Q_D_gf180mcu_ \
#     fd_sc_mcu7t5v0__nor3_1_ZN_A1_gf180mcu_fd_sc_mcu7t5v0__aoi21_ \
#     1_A2_A1_gf180mcu_fd_sc_mcu7t5v0__nand3_1_ZN_A3_gf180mcu_fd_ \
#     sc_mcu7t5v0__and3_1_A3_Z_gf180mcu_fd_sc_mcu7t5v0__buf_1_I_Z
autoname

# write synthesized design for netlist simulation without splitting module ports
write_verilog -noattr -noexpr -nohex -nodec -defparam $NETLIST_SYN_V.sim

# splitting nets resolves unwanted compound assign statements in netlist (assign {..} = {..}
splitnets -format __v -ports

# remove unused cells and wires
opt_clean -purge

# load liberty file before checking
foreach l $LIB_FILES { read_liberty -lib $l }

# reports
tee -o $RESULT_DIR/synth_check.txt check -mapped
tee -o $RESULT_DIR/synth_stat.txt stat {*}$LIBS

# write synthesized design
write_verilog -noattr -noexpr -nohex -nodec -defparam $NETLIST_SYN_V