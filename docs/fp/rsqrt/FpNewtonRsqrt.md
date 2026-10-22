# FpNewtonRsqrt

A Newton-Raphson reciprocal square root with a piecewise-constant seed.

The seed is a table indexed by the top mantissa bits; fixed-point Newton-Raphson iterations converge quadratically to `1 / sqrt(significand)`. The result is faithfully rounded (within one unit in the last place), not correctly rounded.

::: info Source
`src/main/scala/fp/rsqrt/FpNewtonRsqrt.scala`
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
| `fp64` | FPGA | 124.845 | 0.912 | 0.881 | 3,276 LUTs + 70 FF |
| `fp64` | 55 nm | 64.1853 | 0.4560 | 64.1853 | 318,050.60 µm² |
| `fp32` | FPGA | 90.303 | 0.912 | 0.881 | 1,452 LUTs + 38 FF |
| `fp32` | 55 nm | 39.7161 | 0.4560 | 39.7161 | 101,573.36 µm² |
| `fp16` | FPGA | 55.913 | 0.912 | 0.881 | 919 LUTs + 22 FF |
| `fp16` | 55 nm | 23.8437 | 0.4560 | 23.8437 | 26,401.48 µm² |
| `bf16` | FPGA | 45.327 | 0.912 | 0.881 | 510 LUTs + 22 FF |
| `bf16` | 55 nm | 13.1586 | 0.4560 | 13.1586 | 12,145.28 µm² |
