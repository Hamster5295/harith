# FpMacroAdd

A floating-point adder with an inferred alignment adder.

::: info Source
`src/main/scala/fp/add/FpMacroAdd.scala`
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
| `fp64` | FPGA | 48.521 | 49.502 | 48.521 | 8,433 LUTs + 0 FF |
| `fp64` | 55 nm | 30.5496 | 30.5496 | 30.5496 | 15,172.64 µm² |
| `fp32` | FPGA | 40.688 | 41.268 | 40.688 | 3,925 LUTs + 0 FF |
| `fp32` | 55 nm | 14.5881 | 14.5881 | 14.5881 | 5,899.04 µm² |
| `fp16` | FPGA | 38.144 | 38.716 | 38.144 | 1,984 LUTs + 0 FF |
| `fp16` | 55 nm | 9.1341 | 9.1341 | 9.1341 | 3,145.80 µm² |
| `bf16` | FPGA | 37.046 | 37.610 | 37.046 | 1,814 LUTs + 0 FF |
| `bf16` | 55 nm | 10.0211 | 10.0211 | 10.0211 | 3,217.76 µm² |
