# FpMacroAdd

A floating-point adder with an inferred alignment adder.

::: info Source
`src/main/scala/fp/add/FpMacroAdd.scala`
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
| `fp64` | FPGA | 48.521 | 49.502 | 48.521 | 8,433 LUTs + 0 FF |
| `fp64` | 55 nm | 30.5496 | 30.5496 | 30.5496 | 15,172.64 µm² |
| `fp32` | FPGA | 40.688 | 41.268 | 40.688 | 3,925 LUTs + 0 FF |
| `fp32` | 55 nm | 14.5881 | 14.5881 | 14.5881 | 5,899.04 µm² |
| `fp16` | FPGA | 38.144 | 38.716 | 38.144 | 1,984 LUTs + 0 FF |
| `fp16` | 55 nm | 9.1341 | 9.1341 | 9.1341 | 3,145.80 µm² |
| `bf16` | FPGA | 37.046 | 37.610 | 37.046 | 1,814 LUTs + 0 FF |
| `bf16` | 55 nm | 10.0211 | 10.0211 | 10.0211 | 3,217.76 µm² |
