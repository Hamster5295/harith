# FpSrt4Sqrt

A radix-4 square root.

The operand is normalized to `[1, 4)` with an even exponent, and two root bits are produced per cycle by an exact radix-4 digit recurrence. The final remainder provides the sticky bit, so the result is correctly rounded.

::: info Source
`src/main/scala/fp/sqrt/FpSrt4Sqrt.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The format of the operand |
| `outFmt` | `FpFormat` | The format of the result |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `rootBits / 2` cycles (`rootBits` is the even root width)

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
| `fp64-28cyc` | FPGA | 6.175 | 1.415 | 22.011 | 1,773 LUTs + 271 FF |
| `fp64-28cyc` | 55 nm | 5.2306 | 0.8862 | 8.8452 | 10,170.72 µm² |
| `fp32-13cyc` | FPGA | 5.140 | 1.400 | 19.971 | 883 LUTs + 144 FF |
| `fp32-13cyc` | 55 nm | 1.8460 | 0.8856 | 5.9918 | 5,274.36 µm² |
| `fp16-7cyc` | FPGA | 3.605 | 1.376 | 19.074 | 518 LUTs + 87 FF |
| `fp16-7cyc` | 55 nm | 1.8003 | 0.8856 | 4.9940 | 2,850.12 µm² |
| `bf16-5cyc` | FPGA | 4.126 | 1.471 | 16.515 | 409 LUTs + 79 FF |
| `bf16-5cyc` | 55 nm | 1.8209 | 0.8856 | 4.8894 | 2,471.00 µm² |
