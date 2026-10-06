# Analysis Condition

Every module in Harith is characterized twice: once on an FPGA with Vivado, and once on an ASIC
through Yosys and OpenSTA with the icsprout55 PDK. This page describes the environment, the
constraints and the exact meaning of the reported numbers.

## Metrics

Each analysis produces three pure-logic delays and one area number:

- **in** — *input delay*: from a data input to the first register, or, for a combinational module,
  to the output.
- **out** — *output delay*: from the last register to an output, or, for a combinational module,
  from the input to the output. The output buffer's own delay is excluded.
- **max** — *worst delay*: the worst register-to-register delay, which fixes the achievable frequency.
- **area**: 
  - *FPGA*: LUTs and flip-flops
  - *ASIC*: µm².

The external input/output delay modeled by the timing constraint is removed from all three delays,
so they measure the module's own logic rather than the interface around it.

The standalone wrapper inserted by `ExportForAnalysis` registers every output on a spare register
layer. That register is part of the harness, not of the module, so its bit count (`reg_bits`) is
subtracted from the area — from the FPGA flip-flop count, and as `reg_bits × 6.16 µm²` (one
`DFFQX0P5H7L`) from the ASIC area.

## FPGA

| Setting | Value |
| --- | --- |
| Tool | Vivado v2021.2, batch mode |
| Part | `xc7a200t` |
| Clock | `clock`, period 6 ns (≈166.7 MHz) |
| Synthesis | `synth_design -directive PerformanceOptimized -flatten_hierarchy full` |
| Reports | `report_timing`, `report_utilization`, `report_power` |

The clock is created by `backend/fpga/script/constraints.xdc`; no input/output delay is modeled, so
the three delays are raw logic delays. `backend/fpga/script/synth.tcl` emits the `in` view with
`report_timing -from <data inputs>`, the `out` view with `report_timing -to <non-*_reg* outputs>`,
and the `max` view with `report_timing -to <sequential cells>`.

## ASIC

| Setting | Value |
| --- | --- |
| Frontend | Yosys 0.69 (`read_verilog -sv`, `synth -flatten`, `dfflibmap`, ABC mapping) |
| Backend | OpenSTA 2.0.17 |
| PDK | icsprout55 — ics55 `LLSC_H7CL`, `typ_tt_1p2_25_nldm` |
| Library cells | single `L` VT; `LAT*` latch cells are don't-use |
| Clock | `core_clock`, 500 MHz (2 ns) |
| I/O delay | 0.2 × period = 0.4 ns, input and output |
| Driver / load | `BUFX0P5H7L` / 0.05 pF |

`backend/asic/script/syn.tcl` performs the technology-independent synthesis and maps the design; by
default it uses a lightweight ABC script (`+strash; dch; map`), and an aggressive resynthesis
strategy can be selected with `ABC_SCRIPT=1`. `backend/asic/script/sta.tcl` then reads the netlist
into OpenSTA under `backend/asic/script/default.sdc` and writes the three delay views with
`report_checks -from <data inputs>`, `-to <non-`*_reg*` outputs>` and `-to [all_registers -data_pins]`.
The 0.4 ns input/output delay is modeled only to constrain the interface; it is subtracted from the
reported delays.
