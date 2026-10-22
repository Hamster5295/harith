# FpSrt4Rsqrt

A radix-4 reciprocal square root.

The operand is normalized to `[1, 4)` with an even exponent. The residual `x * y^2` and the product `x * y` are kept in fixed point and two result bits are appended per cycle, choosing the radix-4 digit that keeps `x * y^2 <= 1`. The final residual provides the sticky bit, so the result is correctly rounded.

::: info Source
`src/main/scala/fp/rsqrt/FpSrt4Rsqrt.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The format of the operand |
| `outFmt` | `FpFormat` | The format of the result |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `ceil((outFmt.manWidth + 3) / 2)` cycles

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
| `fp64-28cyc` | FPGA | 6.722 | 1.442 | 30.759 | 4,341 LUTs + 457 FF |
| `fp64-28cyc` | 55 nm | 7.6004 | 0.8862 | 28.8763 | 32,988.76 µm² |
| `fp32-13cyc` | FPGA | 4.848 | 1.456 | 24.933 | 1,987 LUTs + 233 FF |
| `fp32-13cyc` | 55 nm | 2.8003 | 0.8798 | 16.6951 | 15,535.52 µm² |
| `fp16-7cyc` | FPGA | 3.561 | 1.456 | 23.851 | 990 LUTs + 133 FF |
| `fp16-7cyc` | 55 nm | 2.0890 | 0.8864 | 10.7366 | 8,750.84 µm² |
| `bf16-5cyc` | FPGA | 4.125 | 1.453 | 21.229 | 831 LUTs + 118 FF |
| `bf16-5cyc` | 55 nm | 1.9298 | 0.8855 | 8.1891 | 6,666.24 µm² |
