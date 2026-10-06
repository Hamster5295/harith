# FpMacroMul

A floating-point multiplier with an inferred significand multiplier.

::: info Source
`src/main/scala/fp/mul/FpMacroMul.scala`
:::

## Parameters

- **`aFmt`** — The format of the first operand
- **`bFmt`** — The format of the second operand
- **`outFmt`** — The format of the result
- **`policy`** — The numeric policy

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `fp64` | FPGA | 32.618 | 33.399 | 32.618 | 2,379 LUTs + 0 FF |
| `fp64` | 55 nm | 17.2452 | 17.2452 | 17.2452 | 39,994.92 µm² |
| `fp32` | FPGA | 24.908 | 25.495 | 24.908 | 984 LUTs + 0 FF |
| `fp32` | 55 nm | 9.4441 | 9.4441 | 9.4441 | 10,452.12 µm² |
| `fp16` | FPGA | 23.001 | 23.596 | 23.001 | 490 LUTs + 0 FF |
| `fp16` | 55 nm | 6.4737 | 6.4737 | 6.4737 | 2,972.48 µm² |
| `bf16` | FPGA | 20.551 | 21.123 | 20.551 | 461 LUTs + 0 FF |
| `bf16` | 55 nm | 6.4107 | 6.4107 | 6.4107 | 1,971.48 µm² |
