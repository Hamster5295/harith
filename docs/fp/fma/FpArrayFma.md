# FpArrayFma

A floating-point fused multiply-adder with an array significand multiplier.

::: info Source
`src/main/scala/fp/fma/FpArrayFma.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `aFmt` | `FpFormat` | The format of the multiplicand |
| `bFmt` | `FpFormat` | The format of the multiplier |
| `cFmt` | `FpFormat` | The format of the addend |
| `outFmt` | `FpFormat` | The format of the result |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = 0 cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `src1` | Input | `UInt(aFmt.width.W)` | Multiplicand |
| `src2` | Input | `UInt(bFmt.width.W)` | Multiplier |
| `add` | Input | `UInt(cFmt.width.W)` | Addend |
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
| `fp64` | FPGA | 85.418 | 85.990 | 85.418 | 17,572 LUTs + 0 FF |
| `fp64` | 55 nm | 61.8791 | 61.8791 | 61.8791 | 89,052.88 µm² |
| `fp32` | FPGA | 57.147 | 57.727 | 57.147 | 6,559 LUTs + 0 FF |
| `fp32` | 55 nm | 34.1085 | 34.1085 | 34.1085 | 25,570.16 µm² |
| `fp16` | FPGA | 46.289 | 47.278 | 46.289 | 2,668 LUTs + 0 FF |
| `fp16` | 55 nm | 15.8832 | 15.8832 | 15.8832 | 6,938.68 µm² |
| `bf16` | FPGA | 43.564 | 44.528 | 43.564 | 2,285 LUTs + 0 FF |
| `bf16` | 55 nm | 14.5860 | 14.5860 | 14.5860 | 6,154.96 µm² |
