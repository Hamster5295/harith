# FpPipelinedMul

A pipelined floating-point multiplier.

The significand product is computed by a pipelined [`harith.uint.UIntPipelinedArrayMul`](/uint/mul/UIntPipelinedArrayMul) whose register layers are `stages`; the floating-point control is delayed by the same number of cycles so that the rounding and special handling line up with the product. `stages = 0` makes the multiplier combinational, equivalently to [`FpArrayMul`](/fp/mul/FpArrayMul). NaN is canonical, per RISC-V.  Note: only the significand multiplier is pipelined internally by the underlying [`harith.uint.UIntPipelinedArrayMul`](/uint/mul/UIntPipelinedArrayMul); the floating-point control is a plain register queue matched to the multiplier latency and the rounding/special logic after it stays combinational, so the effective depth of that part depends on EDA retiming.

::: info Source
`src/main/scala/fp/mul/FpPipelinedMul.scala`
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
| `fp64-2cyc` | FPGA | 12.426 | 31.548 | 30.950 | 8,628 LUTs + 433 FF |
| `fp64-2cyc` | 55 nm | 4.8551 | 12.8657 | 12.8657 | 35,796.32 µm² |
| `fp32-2cyc` | FPGA | 6.057 | 22.781 | 22.210 | 1,889 LUTs + 203 FF |
| `fp32-2cyc` | 55 nm | 2.0848 | 8.0414 | 8.0414 | 9,518.32 µm² |
| `fp16-2cyc` | FPGA | 3.156 | 19.728 | 18.748 | 644 LUTs + 106 FF |
| `fp16-2cyc` | 55 nm | 1.0090 | 5.5721 | 5.5721 | 3,038.00 µm² |
| `bf16-2cyc` | FPGA | 3.406 | 18.215 | 17.246 | 483 LUTs + 91 FF |
| `bf16-2cyc` | 55 nm | 0.9860 | 5.4115 | 5.4115 | 2,340.80 µm² |
