# FpRestoringSqrt

A radix-2 restoring square root.

The operand is normalized to `[1, 4)` with an even exponent, and the root is produced one bit per cycle by the restoring recurrence. The final remainder provides the sticky bit, so the result is correctly rounded.

::: info Source
`src/main/scala/fp/sqrt/FpRestoringSqrt.scala`
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
| `fp64-55cyc` | FPGA | 5.915 | 1.416 | 20.888 | 1,596 LUTs + 264 FF |
| `fp64-55cyc` | 55 nm | 5.2639 | 0.8858 | 8.2514 | 8,034.04 µm² |
| `fp32-26cyc` | FPGA | 5.011 | 1.505 | 18.073 | 810 LUTs + 141 FF |
| `fp32-26cyc` | 55 nm | 1.8587 | 0.8798 | 5.1646 | 4,199.44 µm² |
| `fp16-13cyc` | FPGA | 3.789 | 1.389 | 16.265 | 446 LUTs + 80 FF |
| `fp16-13cyc` | 55 nm | 1.8282 | 0.8856 | 3.9789 | 2,201.64 µm² |
| `bf16-10cyc` | FPGA | 4.195 | 1.493 | 14.461 | 359 LUTs + 74 FF |
| `bf16-10cyc` | 55 nm | 1.7902 | 0.8855 | 4.0429 | 2,177.84 µm² |
