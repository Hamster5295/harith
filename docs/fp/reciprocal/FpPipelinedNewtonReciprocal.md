# FpPipelinedNewtonReciprocal

A pipelined Newton-Raphson reciprocal with a fixed-depth pipeline.

The combinational Newton-Raphson chain is split at every multiplier and every result stage, so a new operand can be accepted every cycle. The seed, iterations and final rounding are the same as `FpNewtonReciprocal`; the result is faithfully rounded (within one unit in the last place).

::: info Source
`src/main/scala/fp/reciprocal/FpPipelinedNewtonReciprocal.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The format of the operand |
| `outFmt` | `FpFormat` | The format of the result |
| `mul` | `Int => UIntMul` | The combinational unsigned multiplier factory |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `2 + 2 * iters` cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `in` | Input | `Decoupled(FpUnaryReq)` | Decoupled request channel |
| `in.valid` | Input | `Bool` | Request valid |
| `in.ready` | Output | `Bool` | Request accepted |
| `in.bits.src` | Input | `UInt(inFmt.width.W)` | Operand |
| `in.bits.rm` | Input | `UInt(3.W)` | Rounding mode |
| `out` | Output | `Valid(FpUnaryResp)` | Valid response channel |
| `out.valid` | Output | `Bool` | Response valid |
| `out.bits.output` | Output | `UInt(outFmt.width.W)` | Result |
| `out.bits.fflags` | Output | `FpFlags` | IEEE-754 flags |
| `flush` | Input | `Bool` | Abort an in-flight operation |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `fp64-8cyc` | FPGA | 7.502 | 0.912 | 24.383 | 2,744 LUTs + 545 FF |
| `fp64-8cyc` | 55 nm | 4.1115 | 0.4508 | 7.9334 | 189,950.04 µm² |
| `fp32-6cyc` | FPGA | 6.689 | 0.912 | 20.763 | 1,168 LUTs + 148 FF |
| `fp32-6cyc` | 55 nm | 3.3681 | 0.4508 | 5.5111 | 37,731.12 µm² |
| `fp16-6cyc` | FPGA | 5.365 | 0.912 | 17.044 | 750 LUTs + 156 FF |
| `fp16-6cyc` | 55 nm | 3.1719 | 0.4508 | 4.2826 | 15,510.32 µm² |
| `bf16-6cyc` | FPGA | 4.588 | 0.912 | 16.527 | 526 LUTs + 54 FF |
| `bf16-6cyc` | 55 nm | 1.7911 | 0.4508 | 4.1308 | 11,165.56 µm² |
