# FpBoothMul

A floating-point multiplier with a modified Booth radix-4 tree significand multiplier.

::: info Source
`src/main/scala/fp/mul/FpBoothMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `aFmt` | `FpFormat` | The format of the first operand |
| `bFmt` | `FpFormat` | The format of the second operand |
| `outFmt` | `FpFormat` | The format of the result |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = 0 cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `src1` | Input | `UInt(aFmt.width.W)` | First operand |
| `src2` | Input | `UInt(bFmt.width.W)` | Second operand |
| `rm` | Input | `UInt(3.W)` | RISC-V rounding mode |
| `output` | Output | `UInt(outFmt.width.W)` | Rounded result |
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
| `fp64` | FPGA | 36.213 | 36.611 | 36.213 | 8,701 LUTs + 0 FF |
| `fp64` | 55 nm | 17.5670 | 17.5670 | 17.5670 | 44,994.32 µm² |
| `fp32` | FPGA | 27.629 | 28.220 | 27.629 | 2,370 LUTs + 0 FF |
| `fp32` | 55 nm | 11.8575 | 11.8575 | 11.8575 | 12,906.60 µm² |
| `fp16` | FPGA | 26.835 | 27.233 | 26.835 | 858 LUTs + 0 FF |
| `fp16` | 55 nm | 7.0969 | 7.0969 | 7.0969 | 3,496.36 µm² |
| `bf16` | FPGA | 22.525 | 23.097 | 22.525 | 549 LUTs + 0 FF |
| `bf16` | 55 nm | 6.2379 | 6.2379 | 6.2379 | 2,506.56 µm² |
