# FpPrefixFma

A floating-point fused multiply-adder with a parallel prefix alignment adder.

::: info Source
`src/main/scala/fp/fma/FpPrefixFma.scala`
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
| `fp64` | FPGA | 65.041 | 66.021 | 65.041 | 12,901 LUTs + 0 FF |
| `fp64` | 55 nm | 85.5805 | 85.5805 | 85.5805 | 76,243.72 µm² |
| `fp32` | FPGA | 51.516 | 52.095 | 51.516 | 6,090 LUTs + 0 FF |
| `fp32` | 55 nm | 35.9554 | 35.9554 | 35.9554 | 24,540.60 µm² |
| `fp16` | FPGA | 45.782 | 46.762 | 45.782 | 2,939 LUTs + 0 FF |
| `fp16` | 55 nm | 25.5184 | 25.5184 | 25.5184 | 11,367.44 µm² |
| `bf16` | FPGA | 42.660 | 43.632 | 42.660 | 2,673 LUTs + 0 FF |
| `bf16` | 55 nm | 13.7281 | 13.7281 | 13.7281 | 6,730.08 µm² |
