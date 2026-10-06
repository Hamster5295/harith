# FpPipelinedMacroAdd

A pipelined floating-point adder with an inferred alignment adder.

The alignment adder itself has no internal register layers, so the `stages` register queue delays the operands and the control while the adder and the rounding tail stay combinational; the effective depth of that tail depends on EDA retiming. NaN is canonical, per RISC-V.

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
| `fp64-2cyc` | FPGA | 24.251 | 29.933 | 29.362 | 8,609 LUTs + 710 FF |
| `fp64-2cyc` | 55 nm | 13.3667 | 31.3051 | 31.3051 | 30,299.92 µm² |
| `fp32-2cyc` | FPGA | 18.449 | 25.795 | 25.203 | 4,120 LUTs + 383 FF |
| `fp32-2cyc` | 55 nm | 7.4952 | 31.2895 | 31.2895 | 15,043.56 µm² |
| `fp16-2cyc` | FPGA | 16.316 | 22.367 | 21.796 | 1,984 LUTs + 215 FF |
| `fp16-2cyc` | 55 nm | 4.4135 | 15.5903 | 15.5903 | 8,524.04 µm² |
| `bf16-2cyc` | FPGA | 15.710 | 21.870 | 21.299 | 1,772 LUTs + 221 FF |
| `bf16-2cyc` | 55 nm | 4.8244 | 15.2796 | 15.2796 | 7,106.40 µm² |
