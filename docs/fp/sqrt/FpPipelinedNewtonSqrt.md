# FpPipelinedNewtonSqrt

A pipelined Newton-Raphson square root with a fixed-depth pipeline.

The combinational Newton-Raphson chain is split at every multiplier and every result stage, so a new operand can be accepted every cycle. The reciprocal square root and the final multiply are the same as `FpNewtonSqrt`; the result is faithfully rounded (within one unit in the last place).

::: info Source
`src/main/scala/fp/sqrt/FpPipelinedNewtonSqrt.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The format of the operand |
| `outFmt` | `FpFormat` | The format of the result |
| `mul` | `Int => UIntMul` | The combinational unsigned multiplier factory |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `3 + 3 * iters` cycles

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
| `fp64-12cyc` | FPGA | 8.445 | 0.912 | 25.022 | 3,554 LUTs + 733 FF |
| `fp64-12cyc` | 55 nm | 5.6980 | 0.4508 | 8.0459 | 305,500.72 µm² |
| `fp32-12cyc` | FPGA | 7.048 | 0.912 | 21.793 | 1,568 LUTs + 320 FF |
| `fp32-12cyc` | 55 nm | 3.8242 | 0.4508 | 5.9647 | 94,503.08 µm² |
| `fp16-9cyc` | FPGA | 5.260 | 0.912 | 16.906 | 1,049 LUTs + 250 FF |
| `fp16-9cyc` | 55 nm | 2.9117 | 0.4508 | 4.1158 | 25,272.24 µm² |
| `bf16-9cyc` | FPGA | 6.139 | 0.912 | 16.529 | 601 LUTs + 56 FF |
| `bf16-9cyc` | 55 nm | 2.5437 | 0.4508 | 4.5610 | 18,198.60 µm² |
