# FpCarrySelectAdd

A floating-point adder with a block carry select alignment adder.

::: info Source
`src/main/scala/fp/add/FpCarrySelectAdd.scala`
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
| `fp64` | FPGA | 57.265 | 57.845 | 57.265 | 8,943 LUTs + 0 FF |
| `fp64` | 55 nm | 23.2826 | 23.2826 | 23.2826 | 12,961.20 µm² |
| `fp32` | FPGA | 45.991 | 46.571 | 45.991 | 4,226 LUTs + 0 FF |
| `fp32` | 55 nm | 14.6117 | 14.6117 | 14.6117 | 5,542.88 µm² |
| `fp16` | FPGA | 40.538 | 41.232 | 40.538 | 2,160 LUTs + 0 FF |
| `fp16` | 55 nm | 9.1653 | 9.1653 | 9.1653 | 3,114.44 µm² |
| `bf16` | FPGA | 39.206 | 40.065 | 39.206 | 1,983 LUTs + 0 FF |
| `bf16` | 55 nm | 8.6955 | 8.6955 | 8.6955 | 2,964.64 µm² |
