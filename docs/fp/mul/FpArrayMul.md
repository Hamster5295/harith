# FpArrayMul

A floating-point multiplier with a carry save array significand multiplier, the cheapest option.

::: info Source
`src/main/scala/fp/mul/FpArrayMul.scala`
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
| `fp64` | FPGA | 57.435 | 57.833 | 57.435 | 10,088 LUTs + 0 FF |
| `fp64` | 55 nm | 25.1068 | 25.1068 | 25.1068 | 54,372.08 µm² |
| `fp32` | FPGA | 33.146 | 33.737 | 33.146 | 2,341 LUTs + 0 FF |
| `fp32` | 55 nm | 12.6329 | 12.6329 | 12.6329 | 11,450.88 µm² |
| `fp16` | FPGA | 26.467 | 27.055 | 26.467 | 736 LUTs + 0 FF |
| `fp16` | 55 nm | 6.8266 | 6.8266 | 6.8266 | 2,887.08 µm² |
| `bf16` | FPGA | 22.485 | 23.057 | 22.485 | 465 LUTs + 0 FF |
| `bf16` | 55 nm | 6.5880 | 6.5880 | 6.5880 | 1,956.64 µm² |
