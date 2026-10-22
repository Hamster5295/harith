# FpNewtonReciprocal

A Newton-Raphson reciprocal with a piecewise-constant seed.

The seed is a table indexed by the top mantissa bits; fixed-point Newton-Raphson iterations converge quadratically to `1 / significand`. The result is faithfully rounded (within one unit in the last place), not correctly rounded.

::: info Source
`src/main/scala/fp/reciprocal/FpNewtonReciprocal.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The format of the operand |
| `outFmt` | `FpFormat` | The format of the result |
| `mul` | `Int => UIntMul` | The combinational unsigned multiplier factory |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = 1 cycle

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
| `fp64` | FPGA | 90.421 | 0.912 | 1.017 | 2,734 LUTs + 68 FF |
| `fp64` | 55 nm | 46.9906 | 0.4560 | 46.9906 | 215,430.88 µm² |
| `fp32` | FPGA | 53.401 | 0.912 | 1.017 | 1,109 LUTs + 36 FF |
| `fp32` | 55 nm | 24.2199 | 0.4560 | 24.2199 | 41,807.08 µm² |
| `fp16` | FPGA | 44.718 | 0.912 | 1.017 | 722 LUTs + 20 FF |
| `fp16` | 55 nm | 17.1130 | 0.4560 | 17.1130 | 16,264.36 µm² |
| `bf16` | FPGA | 37.960 | 0.912 | 1.017 | 454 LUTs + 20 FF |
| `bf16` | 55 nm | 7.5151 | 0.4560 | 7.5151 | 4,809.00 µm² |
