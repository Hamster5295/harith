# FpNewtonSqrt

A Newton-Raphson square root with a piecewise-constant seed.

The reciprocal square root is computed by fixed-point Newton-Raphson iterations and multiplied by the operand. The result is faithfully rounded (within one unit in the last place), not correctly rounded.

::: info Source
`src/main/scala/fp/sqrt/FpNewtonSqrt.scala`
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
| `fp64` | FPGA | 134.717 | 0.912 | 0.881 | 3,438 LUTs + 68 FF |
| `fp64` | 55 nm | 73.8453 | 0.4557 | 73.8453 | 367,311.00 µm² |
| `fp32` | FPGA | 95.495 | 0.912 | 0.881 | 1,463 LUTs + 36 FF |
| `fp32` | 55 nm | 42.9883 | 0.4557 | 42.9883 | 112,291.20 µm² |
| `fp16` | FPGA | 60.256 | 0.912 | 0.881 | 973 LUTs + 20 FF |
| `fp16` | 55 nm | 25.5042 | 0.4560 | 25.5042 | 29,868.44 µm² |
| `bf16` | FPGA | 48.873 | 0.912 | 0.881 | 519 LUTs + 20 FF |
| `bf16` | 55 nm | 14.7767 | 0.4560 | 14.7767 | 13,866.44 µm² |
