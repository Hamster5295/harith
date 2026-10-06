# FpPrefixAdd

A floating-point adder with a parallel prefix alignment adder, the fast option.

::: info Source
`src/main/scala/fp/add/FpPrefixAdd.scala`
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
| `fp64` | FPGA | 50.114 | 50.686 | 50.114 | 10,087 LUTs + 0 FF |
| `fp64` | 55 nm | 21.8837 | 21.8837 | 21.8837 | 13,830.04 µm² |
| `fp32` | FPGA | 42.413 | 42.993 | 42.413 | 4,837 LUTs + 0 FF |
| `fp32` | 55 nm | 13.1978 | 13.1978 | 13.1978 | 6,136.48 µm² |
| `fp16` | FPGA | 39.409 | 40.156 | 39.409 | 2,378 LUTs + 0 FF |
| `fp16` | 55 nm | 8.9057 | 8.9057 | 8.9057 | 3,279.36 µm² |
| `bf16` | FPGA | 37.657 | 38.410 | 37.657 | 2,138 LUTs + 0 FF |
| `bf16` | 55 nm | 9.4356 | 9.4356 | 9.4356 | 3,161.76 µm² |
