set top [lindex $argv 0]

create_project -force analysis . -part xc7a200t
add_files ../rtl/$top.sv
add_files ../script/constraints.xdc

synth_design -top $top -directive PerformanceOptimized -retiming -flatten_hierarchy full

report_timing -sort_by group -max_paths 256 -path_type summary -file ../report/timing.summary.rpt
report_timing -sort_by group -nworst 20 -file ../report/timing.rpt
report_utilization -file ../report/util.rpt
report_power -file ../report/power.rpt