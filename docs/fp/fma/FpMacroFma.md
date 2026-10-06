# FpMacroFma

A floating-point fused multiply-adder with inferred significand and alignment datapaths.

::: info Source
`src/main/scala/fp/fma/FpMacroFma.scala`
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
| `fp64` | FPGA | 64.329 | 65.309 | 64.329 | 11,083 LUTs + 0 FF |
| `fp64` | 55 nm | 76.5721 | 76.5721 | 76.4466 | 72,919.28 µm² |
| `fp32` | FPGA | 49.853 | 50.416 | 49.853 | 5,107 LUTs + 0 FF |
| `fp32` | 55 nm | 36.9288 | 36.9288 | 36.9288 | 28,061.32 µm² |
| `fp16` | FPGA | 43.305 | 43.884 | 43.305 | 2,408 LUTs + 0 FF |
| `fp16` | 55 nm | 15.1467 | 15.1467 | 15.1467 | 7,418.32 µm² |
| `bf16` | FPGA | 40.720 | 41.292 | 40.720 | 2,213 LUTs + 0 FF |
| `bf16` | 55 nm | 15.6026 | 15.6026 | 15.6026 | 6,058.92 µm² |
