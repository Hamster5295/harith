# FpTreeFma

A floating-point fused multiply-adder with an AND partial product tree significand multiplier.

::: info Source
`src/main/scala/fp/fma/FpTreeFma.scala`
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
| `fp64` | FPGA | 68.247 | 68.819 | 68.247 | 14,942 LUTs + 0 FF |
| `fp64` | 55 nm | 74.7898 | 74.7898 | 74.7898 | 72,748.76 µm² |
| `fp32` | FPGA | 51.426 | 52.006 | 51.426 | 6,007 LUTs + 0 FF |
| `fp32` | 55 nm | 41.0896 | 41.0896 | 41.0896 | 26,301.24 µm² |
| `fp16` | FPGA | 45.734 | 46.723 | 45.734 | 2,622 LUTs + 0 FF |
| `fp16` | 55 nm | 20.8538 | 20.8538 | 20.8538 | 9,047.36 µm² |
| `bf16` | FPGA | 43.161 | 44.035 | 43.161 | 2,332 LUTs + 0 FF |
| `bf16` | 55 nm | 14.0390 | 14.0390 | 14.0390 | 6,468.28 µm² |
