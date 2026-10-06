# FpPipelinedRippleAdd

A pipelined floating-point adder with a pipelined ripple carry alignment adder.

The carry chain is cut into `stages` blocks, so the latency is the block count. The floating-point control is a register queue matched to the adder latency. NaN is canonical, per RISC-V.

::: info Source
`src/main/scala/fp/add/FpPipelinedAdd.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `aFmt` | `FpFormat` | The format of the first operand |
| `bFmt` | `FpFormat` | The format of the second operand |
| `outFmt` | `FpFormat` | The format of the result |
| `stages` | `Int` | The number of pipeline register layers, which is also the latency |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `stages` cycles

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
| `fp64-2cyc` | FPGA | 32.972 | 31.277 | 30.300 | 8,568 LUTs + 800 FF |
| `fp64-2cyc` | 55 nm | 12.5026 | 32.6793 | 32.6793 | 29,576.12 µm² |
| `fp32-2cyc` | FPGA | 22.847 | 27.023 | 26.054 | 4,115 LUTs + 426 FF |
| `fp32-2cyc` | 55 nm | 7.7297 | 22.5578 | 22.5578 | 15,058.68 µm² |
| `fp16-2cyc` | FPGA | 19.377 | 23.726 | 22.760 | 1,983 LUTs + 236 FF |
| `fp16-2cyc` | 55 nm | 4.8011 | 14.7619 | 14.7619 | 7,900.48 µm² |
| `bf16-2cyc` | FPGA | 17.751 | 23.085 | 22.514 | 1,790 LUTs + 240 FF |
| `bf16-2cyc` | 55 nm | 5.0970 | 15.8745 | 15.8745 | 7,479.92 µm² |
