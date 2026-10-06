# FpPipelinedPrefixAdd

A pipelined floating-point adder with a parallel prefix alignment adder.

The prefix levels of the selected [[harith.uint.PrefixStyle]] are distributed over `stages` register layers, while the floating-point control is a register queue matched to the adder latency. `stages = 0` makes the adder combinational. NaN is canonical, per RISC-V.

::: info Source
`src/main/scala/fp/add/FpPipelinedAdd.scala`
:::

## Parameters

- **`aFmt`** — The format of the first operand
- **`bFmt`** — The format of the second operand
- **`outFmt`** — The format of the result
- **`stages`** — The number of pipeline register layers, which is also the latency
- **`policy`** — The numeric policy

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `fp64-2cyc` | FPGA | 23.093 | 29.830 | 29.259 | 9,805 LUTs + 1,232 FF |
| `fp64-2cyc` | 55 nm | 13.2229 | 33.5948 | 33.5948 | 34,074.88 µm² |
| `fp32-2cyc` | FPGA | 19.973 | 25.614 | 25.022 | 4,952 LUTs + 627 FF |
| `fp32-2cyc` | 55 nm | 8.5569 | 21.8746 | 21.8746 | 17,788.96 µm² |
| `fp16-2cyc` | FPGA | 17.190 | 22.361 | 21.790 | 2,295 LUTs + 334 FF |
| `fp16-2cyc` | 55 nm | 4.5925 | 17.5413 | 17.5413 | 9,081.52 µm² |
| `bf16-2cyc` | FPGA | 16.462 | 21.870 | 21.299 | 2,029 LUTs + 331 FF |
| `bf16-2cyc` | 55 nm | 5.5078 | 15.6008 | 15.6008 | 7,902.72 µm² |
