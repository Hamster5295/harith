# FpBoothFma

A floating-point fused multiply-adder with a modified Booth radix-4 tree significand multiplier.

::: info Source
`src/main/scala/fp/fma/FpBoothFma.scala`
:::

## Parameters

- **`aFmt`** — The format of the multiplicand
- **`bFmt`** — The format of the multiplier
- **`cFmt`** — The format of the addend
- **`outFmt`** — The format of the result
- **`policy`** — The numeric policy

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `fp64` | FPGA | 65.193 | 65.765 | 65.193 | 16,250 LUTs + 0 FF |
| `fp64` | 55 nm | 52.9643 | 52.9643 | 52.9643 | 75,525.24 µm² |
| `fp32` | FPGA | 52.331 | 52.911 | 52.331 | 6,531 LUTs + 0 FF |
| `fp32` | 55 nm | 37.0841 | 37.0841 | 37.0841 | 30,194.92 µm² |
| `fp16` | FPGA | 45.982 | 46.971 | 45.982 | 2,768 LUTs + 0 FF |
| `fp16` | 55 nm | 24.4914 | 24.4914 | 24.4914 | 11,494.00 µm² |
| `bf16` | FPGA | 43.557 | 44.431 | 43.557 | 2,364 LUTs + 0 FF |
| `bf16` | 55 nm | 25.7625 | 25.7625 | 25.7625 | 9,553.32 µm² |
