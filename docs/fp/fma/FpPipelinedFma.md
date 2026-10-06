# FpPipelinedFma

A pipelined floating-point fused multiply-adder.

The significand product is computed by a pipelined [[harith.uint.UIntPipelinedArrayMul]] whose register layers are `stages`, then the alignment, addition and rounding are combinational on the delayed operands. `stages = 0` makes the FMA combinational. NaN is canonical, per RISC-V.  Note: only the significand product is pipelined internally by the underlying [[harith.uint.UIntPipelinedArrayMul]]; the operands are delayed by a plain register queue matched to the multiplier latency and the whole alignment/add/round tail stays combinational, so the effective depth of that part depends on EDA retiming.

::: info Source
`src/main/scala/fp/fma/FpPipelinedFma.scala`
:::

## Parameters

- **`aFmt`** — The format of the multiplicand
- **`bFmt`** — The format of the multiplier
- **`cFmt`** — The format of the addend
- **`outFmt`** — The format of the result
- **`stages`** — The number of pipeline register layers, which is also the latency
- **`policy`** — The numeric policy

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `fp64-2cyc` | FPGA | 12.581 | 63.282 | 62.711 | 17,188 LUTs + 696 FF |
| `fp64-2cyc` | 55 nm | 4.8029 | 31.7407 | 31.7407 | 51,201.08 µm² |
| `fp32-2cyc` | FPGA | 6.065 | 47.980 | 46.992 | 6,038 LUTs + 327 FF |
| `fp32-2cyc` | 55 nm | 2.0449 | 21.9171 | 21.9171 | 17,519.04 µm² |
| `fp16-2cyc` | FPGA | 3.137 | 40.049 | 39.478 | 2,632 LUTs + 160 FF |
| `fp16-2cyc` | 55 nm | 0.9560 | 16.4629 | 16.4629 | 7,754.32 µm² |
| `bf16-2cyc` | FPGA | 2.712 | 37.846 | 37.275 | 2,294 LUTs + 143 FF |
| `bf16-2cyc` | 55 nm | 0.5774 | 13.2360 | 13.2360 | 6,961.64 µm² |
