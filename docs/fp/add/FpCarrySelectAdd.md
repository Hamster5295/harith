# FpCarrySelectAdd

A floating-point adder with a block carry select alignment adder.

::: info Source
`src/main/scala/fp/add/FpCarrySelectAdd.scala`
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
| `fp64` | FPGA | 57.265 | 57.845 | 57.265 | 8,943 LUTs + 0 FF |
| `fp64` | 55 nm | 23.2826 | 23.2826 | 23.2826 | 12,961.20 µm² |
| `fp32` | FPGA | 45.991 | 46.571 | 45.991 | 4,226 LUTs + 0 FF |
| `fp32` | 55 nm | 14.6117 | 14.6117 | 14.6117 | 5,542.88 µm² |
| `fp16` | FPGA | 40.538 | 41.232 | 40.538 | 2,160 LUTs + 0 FF |
| `fp16` | 55 nm | 9.1653 | 9.1653 | 9.1653 | 3,114.44 µm² |
| `bf16` | FPGA | 39.206 | 40.065 | 39.206 | 1,983 LUTs + 0 FF |
| `bf16` | 55 nm | 8.6955 | 8.6955 | 8.6955 | 2,964.64 µm² |
