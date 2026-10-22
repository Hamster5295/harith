# FpRestoringRsqrt

A radix-2 restoring reciprocal square root.

The operand is normalized to `[1, 4)` with an even exponent. The residual `x * y^2` and the product `x * y` are kept in fixed point and one result bit is appended per cycle, choosing the digit that keeps `x * y^2 <= 1`. The final residual provides the sticky bit, so the result is correctly rounded.

::: info Source
`src/main/scala/fp/rsqrt/FpRestoringRsqrt.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The format of the operand |
| `outFmt` | `FpFormat` | The format of the result |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `outFmt.manWidth + 3` cycles

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
| `fp64-55cyc` | FPGA | 5.968 | 1.453 | 21.189 | 3,044 LUTs + 443 FF |
| `fp64-55cyc` | 55 nm | 8.1365 | 0.8862 | 15.6430 | 19,876.08 µm² |
| `fp32-26cyc` | FPGA | 5.059 | 1.436 | 19.099 | 1,419 LUTs + 234 FF |
| `fp32-26cyc` | 55 nm | 4.3361 | 0.8862 | 10.8285 | 10,578.40 µm² |
| `fp16-13cyc` | FPGA | 3.558 | 1.354 | 16.442 | 746 LUTs + 128 FF |
| `fp16-13cyc` | 55 nm | 2.1229 | 0.8855 | 6.5856 | 5,530.84 µm² |
| `bf16-10cyc` | FPGA | 4.577 | 1.351 | 15.732 | 589 LUTs + 115 FF |
| `bf16-10cyc` | 55 nm | 2.7075 | 0.8858 | 5.6220 | 4,684.12 µm² |
