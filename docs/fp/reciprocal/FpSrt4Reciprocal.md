# FpSrt4Reciprocal

A radix-4 reciprocal.

The operand is normalized to `[1, 2)` and two quotient bits are produced per cycle by an exact radix-4 digit recurrence (digits `0..3`). The final remainder provides the sticky bit, so the result is correctly rounded.

::: info Source
`src/main/scala/fp/reciprocal/FpSrt4Reciprocal.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The format of the operand |
| `outFmt` | `FpFormat` | The format of the result |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `ceil((outFmt.manWidth + 4) / 2)` cycles

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
| `fp64-28cyc` | FPGA | 5.642 | 1.365 | 22.867 | 1,844 LUTs + 259 FF |
| `fp64-28cyc` | 55 nm | 3.0128 | 0.8798 | 9.5054 | 9,808.96 µm² |
| `fp32-14cyc` | FPGA | 4.334 | 1.362 | 19.950 | 912 LUTs + 135 FF |
| `fp32-14cyc` | 55 nm | 2.3968 | 0.8913 | 6.0630 | 4,629.80 µm² |
| `fp16-7cyc` | FPGA | 3.590 | 1.373 | 19.557 | 514 LUTs + 75 FF |
| `fp16-7cyc` | 55 nm | 1.9617 | 0.8856 | 4.0160 | 2,563.96 µm² |
| `bf16-6cyc` | FPGA | 3.643 | 1.485 | 17.876 | 455 LUTs + 70 FF |
| `bf16-6cyc` | 55 nm | 1.8763 | 0.8856 | 4.2271 | 2,255.68 µm² |
