# FpRippleAdd

A floating-point adder with a ripple carry alignment adder, the cheapest option.

::: info Source
`src/main/scala/fp/add/FpRippleAdd.scala`
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
| `fp64` | FPGA | 72.702 | 73.274 | 72.702 | 8,591 LUTs + 0 FF |
| `fp64` | 55 nm | 31.3539 | 31.3539 | 31.3539 | 13,508.04 µm² |
| `fp32` | FPGA | 51.462 | 52.042 | 51.462 | 4,107 LUTs + 0 FF |
| `fp32` | 55 nm | 14.2377 | 14.2377 | 14.2377 | 5,554.36 µm² |
| `fp16` | FPGA | 42.856 | 43.550 | 42.856 | 2,088 LUTs + 0 FF |
| `fp16` | 55 nm | 9.4318 | 9.4318 | 9.4318 | 3,026.52 µm² |
| `bf16` | FPGA | 40.678 | 41.537 | 40.678 | 1,908 LUTs + 0 FF |
| `bf16` | 55 nm | 9.5703 | 9.5703 | 9.5196 | 3,258.92 µm² |
