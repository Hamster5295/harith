# FpBoothMul

A floating-point multiplier with a modified Booth radix-4 tree significand multiplier.

::: info Source
`src/main/scala/fp/mul/FpBoothMul.scala`
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
| `fp64` | FPGA | 36.213 | 36.611 | 36.213 | 8,701 LUTs + 0 FF |
| `fp64` | 55 nm | 17.5670 | 17.5670 | 17.5670 | 44,994.32 µm² |
| `fp32` | FPGA | 27.629 | 28.220 | 27.629 | 2,370 LUTs + 0 FF |
| `fp32` | 55 nm | 11.8575 | 11.8575 | 11.8575 | 12,906.60 µm² |
| `fp16` | FPGA | 26.835 | 27.233 | 26.835 | 858 LUTs + 0 FF |
| `fp16` | 55 nm | 7.0969 | 7.0969 | 7.0969 | 3,496.36 µm² |
| `bf16` | FPGA | 22.525 | 23.097 | 22.525 | 549 LUTs + 0 FF |
| `bf16` | 55 nm | 6.2379 | 6.2379 | 6.2379 | 2,506.56 µm² |
