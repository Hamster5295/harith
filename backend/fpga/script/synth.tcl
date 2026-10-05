set top [lindex $argv 0]

create_project -force analysis . -part xc7a200t
add_files ../rtl/$top.sv
add_files ../script/constraints.xdc

synth_design -top $top -directive PerformanceOptimized -retiming -flatten_hierarchy full

report_timing -sort_by group -max_paths 256 -path_type summary -file ../report/timing.summary.rpt
report_timing -sort_by group -nworst 20 -file ../report/timing.rpt

# Three delay views:
#   in  - worst max path starting at a data input (input-side logic delay)
#   out - worst max path ending at a raw output (output-side logic delay)
#   max - worst max path ending at a register (true register-to-register Fmax)
# The output buffer delay is removed from `out` when the report is post-processed,
# so `out` measures the same fabric logic as `in`/`max`.
set data_ins {}
foreach p [get_ports -filter {DIRECTION == IN}] {
  if {[get_property NAME $p] ne "clock"} {
    lappend data_ins $p
  }
}
set raw_outs {}
foreach p [get_ports -filter {DIRECTION == OUT}] {
  if {![string match "*_reg*" [get_property NAME $p]]} {
    lappend raw_outs $p
  }
}
set regs [get_cells -hierarchical -filter {IS_SEQUENTIAL == 1}]

if {[llength $data_ins] > 0} {
  report_timing -delay_type max -max_paths 1 -from $data_ins -file ../report/timing.in.rpt
}
if {[llength $raw_outs] > 0} {
  report_timing -delay_type max -max_paths 1 -to $raw_outs -file ../report/timing.out.rpt
}
if {[llength $regs] > 0} {
  report_timing -delay_type max -max_paths 1 -to $regs -file ../report/timing.max.rpt
}

report_utilization -file ../report/util.rpt
report_power -file ../report/power.rpt