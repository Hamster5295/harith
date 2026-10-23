# FpPipelinedNewtonRsqrt

A pipelined Newton-Raphson reciprocal square root with a fixed-depth pipeline.

The combinational Newton-Raphson chain is split at every multiplier and every result stage, so a new operand can be accepted every cycle. The seed, iterations and final rounding are the same as `FpNewtonRsqrt`; the result is faithfully rounded (within one unit in the last place).

::: info Source
`src/main/scala/fp/rsqrt/FpPipelinedNewtonRsqrt.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The format of the operand |
| `outFmt` | `FpFormat` | The format of the result |
| `mul` | `Int => UIntMul` | The combinational unsigned multiplier factory |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `2 + 3 * iters` cycles

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
| `fp64-11cyc` | FPGA | 8.445 | 0.912 | 25.336 | 3,377 LUTs + 626 FF |
| `fp64-11cyc` | 55 nm | 6.1450 | 0.4508 | 9.0292 | 268,114.00 µm² |
| `fp32-11cyc` | FPGA | 7.050 | 0.912 | 21.524 | 1,531 LUTs + 271 FF |
| `fp32-11cyc` | 55 nm | 4.1663 | 0.4508 | 5.8993 | 85,531.04 µm² |
| `fp16-8cyc` | FPGA | 5.260 | 0.912 | 16.899 | 981 LUTs + 207 FF |
| `fp16-8cyc` | 55 nm | 2.9238 | 0.4508 | 4.4865 | 22,833.44 µm² |
| `bf16-8cyc` | FPGA | 6.139 | 0.912 | 16.252 | 567 LUTs + 57 FF |
| `bf16-8cyc` | 55 nm | 2.5377 | 0.4508 | 4.1432 | 16,423.68 µm² |
