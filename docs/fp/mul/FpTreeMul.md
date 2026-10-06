# FpTreeMul

A floating-point multiplier with an AND partial product carry save tree significand multiplier.

::: info Source
`src/main/scala/fp/mul/FpTreeMul.scala`
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
| `fp64` | FPGA | 39.064 | 39.462 | 39.064 | 6,939 LUTs + 0 FF |
| `fp64` | 55 nm | 19.0265 | 19.0265 | 19.0265 | 36,041.60 µm² |
| `fp32` | FPGA | 26.628 | 27.219 | 26.628 | 1,864 LUTs + 0 FF |
| `fp32` | 55 nm | 11.0121 | 11.0121 | 11.0121 | 8,316.00 µm² |
| `fp16` | FPGA | 23.476 | 24.072 | 23.476 | 715 LUTs + 0 FF |
| `fp16` | 55 nm | 7.0437 | 7.0437 | 7.0437 | 2,772.56 µm² |
| `bf16` | FPGA | 22.655 | 23.227 | 22.655 | 523 LUTs + 0 FF |
| `bf16` | 55 nm | 6.1560 | 6.1560 | 6.1560 | 2,072.84 µm² |
