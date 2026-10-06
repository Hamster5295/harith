# FpRippleFma

A floating-point fused multiply-adder with a ripple carry alignment adder.

::: info Source
`src/main/scala/fp/fma/FpRippleFma.scala`
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
| `fp64` | FPGA | 91.884 | 92.864 | 91.884 | 11,240 LUTs + 0 FF |
| `fp64` | 55 nm | 64.2359 | 64.2359 | 64.2329 | 72,953.16 µm² |
| `fp32` | FPGA | 63.167 | 63.738 | 63.167 | 5,256 LUTs + 0 FF |
| `fp32` | 55 nm | 41.1512 | 41.1512 | 41.1512 | 28,605.08 µm² |
| `fp16` | FPGA | 49.255 | 50.235 | 49.255 | 2,535 LUTs + 0 FF |
| `fp16` | 55 nm | 23.2811 | 23.2811 | 23.2811 | 10,918.32 µm² |
| `bf16` | FPGA | 46.132 | 47.104 | 46.132 | 2,345 LUTs + 0 FF |
| `bf16` | 55 nm | 13.2128 | 13.2128 | 13.2128 | 5,802.16 µm² |
