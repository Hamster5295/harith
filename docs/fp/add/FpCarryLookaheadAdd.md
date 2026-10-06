# FpCarryLookaheadAdd

A floating-point adder with a hierarchical carry lookahead alignment adder.

::: info Source
`src/main/scala/fp/add/FpCarryLookaheadAdd.scala`
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
| `fp64` | FPGA | 50.264 | 50.836 | 50.264 | 8,835 LUTs + 0 FF |
| `fp64` | 55 nm | 26.5524 | 26.5524 | 26.5524 | 14,996.80 µm² |
| `fp32` | FPGA | 42.789 | 43.369 | 42.789 | 4,312 LUTs + 0 FF |
| `fp32` | 55 nm | 13.6956 | 13.6956 | 13.6956 | 6,375.88 µm² |
| `fp16` | FPGA | 39.533 | 40.227 | 39.533 | 2,195 LUTs + 0 FF |
| `fp16` | 55 nm | 9.6391 | 9.6391 | 9.6391 | 3,378.48 µm² |
| `bf16` | FPGA | 38.701 | 39.560 | 38.701 | 2,064 LUTs + 0 FF |
| `bf16` | 55 nm | 9.9088 | 9.9088 | 9.9088 | 3,227.56 µm² |
