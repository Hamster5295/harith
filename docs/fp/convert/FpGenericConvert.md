# FpGenericConvert

A floating-point format converter.

The value is decoded to its exact significand and exponent and re-rounded to the destination format. Special values map to the destination encoding; NaN is canonical, per RISC-V.

::: info Source
`src/main/scala/fp/convert/FpGenericConvert.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The input format |
| `outFmt` | `FpFormat` | The output format |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = 0 cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `src` | Input | `UInt(inFmt.width.W)` | Input value |
| `rm` | Input | `UInt(3.W)` | RISC-V rounding mode |
| `output` | Output | `UInt(outFmt.width.W)` | Converted result |
| `fflags` | Output | `FpFlags` | IEEE-754 exception flags |
| `fflags.nx` | Output | `Bool` | Inexact |
| `fflags.uf` | Output | `Bool` | Underflow |
| `fflags.of` | Output | `Bool` | Overflow |
| `fflags.dz` | Output | `Bool` | Divide by zero |
| `fflags.nv` | Output | `Bool` | Invalid operation |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `fp64->fp32` | FPGA | 21.106 | 21.557 | 21.106 | 1,040 LUTs + 0 FF |
| `fp64->fp32` | 55 nm | 6.4557 | 6.4557 | 6.4557 | 1,778.00 µm² |
| `fp64->fp16` | FPGA | 20.787 | 21.529 | 20.787 | 936 LUTs + 0 FF |
| `fp64->fp16` | 55 nm | 5.2308 | 5.2308 | 5.2308 | 1,114.96 µm² |
| `fp64->bf16` | FPGA | 21.005 | 21.691 | 21.005 | 931 LUTs + 0 FF |
| `fp64->bf16` | 55 nm | 4.8884 | 4.8884 | 4.8342 | 1,086.12 µm² |
| `fp32->fp64` | FPGA | 19.632 | 20.420 | 19.632 | 707 LUTs + 0 FF |
| `fp32->fp64` | 55 nm | 3.5764 | 3.5764 | 3.5764 | 565.32 µm² |
| `fp32->fp16` | FPGA | 18.533 | 19.522 | 18.533 | 484 LUTs + 0 FF |
| `fp32->fp16` | 55 nm | 4.4324 | 4.4324 | 4.4324 | 853.72 µm² |
| `fp32->bf16` | FPGA | 18.194 | 19.183 | 18.194 | 478 LUTs + 0 FF |
| `fp32->bf16` | 55 nm | 2.8542 | 2.8542 | 2.8542 | 351.12 µm² |
| `fp16->fp64` | FPGA | 17.826 | 18.406 | 17.826 | 486 LUTs + 0 FF |
| `fp16->fp64` | 55 nm | 2.4871 | 2.4871 | 2.4871 | 232.96 µm² |
| `fp16->fp32` | FPGA | 14.596 | 15.187 | 14.596 | 315 LUTs + 0 FF |
| `fp16->fp32` | 55 nm | 2.1520 | 2.1520 | 2.1520 | 210.84 µm² |
| `fp16->bf16` | FPGA | 14.189 | 14.761 | 14.189 | 243 LUTs + 0 FF |
| `fp16->bf16` | 55 nm | 2.3162 | 2.3162 | 2.3162 | 331.80 µm² |
| `bf16->fp64` | FPGA | 17.372 | 17.952 | 17.372 | 458 LUTs + 0 FF |
| `bf16->fp64` | 55 nm | 2.4125 | 2.4125 | 2.4125 | 229.04 µm² |
| `bf16->fp32` | FPGA | 15.718 | 16.290 | 15.718 | 329 LUTs + 0 FF |
| `bf16->fp32` | 55 nm | 1.2570 | 1.2570 | 1.2570 | 74.20 µm² |
| `bf16->fp16` | FPGA | 15.857 | 16.437 | 15.857 | 258 LUTs + 0 FF |
| `bf16->fp16` | 55 nm | 4.1644 | 4.1644 | 4.1644 | 594.16 µm² |
