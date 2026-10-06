# FpArrayMul

A floating-point multiplier with a carry save array significand multiplier, the cheapest option.

::: info Source
`src/main/scala/fp/mul/FpArrayMul.scala`
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
| `fp64` | FPGA | 57.435 | 57.833 | 57.435 | 10,088 LUTs + 0 FF |
| `fp64` | 55 nm | 25.1068 | 25.1068 | 25.1068 | 54,372.08 µm² |
| `fp32` | FPGA | 33.146 | 33.737 | 33.146 | 2,341 LUTs + 0 FF |
| `fp32` | 55 nm | 12.6329 | 12.6329 | 12.6329 | 11,450.88 µm² |
| `fp16` | FPGA | 26.467 | 27.055 | 26.467 | 736 LUTs + 0 FF |
| `fp16` | 55 nm | 6.8266 | 6.8266 | 6.8266 | 2,887.08 µm² |
| `bf16` | FPGA | 22.485 | 23.057 | 22.485 | 465 LUTs + 0 FF |
| `bf16` | 55 nm | 6.5880 | 6.5880 | 6.5880 | 1,956.64 µm² |
