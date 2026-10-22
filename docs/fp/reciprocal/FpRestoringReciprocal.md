# FpRestoringReciprocal

A radix-2 restoring reciprocal.

The operand is normalized to `[1, 2)` and the quotient bits of `1 / significand` are produced one per cycle by the restoring recurrence, with the final remainder providing the sticky bit. The result is correctly rounded.

::: info Source
`src/main/scala/fp/reciprocal/FpRestoringReciprocal.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `inFmt` | `FpFormat` | The format of the operand |
| `outFmt` | `FpFormat` | The format of the result |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `outFmt.manWidth + 4` cycles

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
| `fp64-56cyc` | FPGA | 5.642 | 1.362 | 20.281 | 1,553 LUTs + 259 FF |
| `fp64-56cyc` | 55 nm | 4.2967 | 0.8856 | 8.7567 | 7,825.16 µm² |
| `fp32-27cyc` | FPGA | 4.334 | 1.477 | 18.282 | 746 LUTs + 136 FF |
| `fp32-27cyc` | 55 nm | 2.3376 | 0.8798 | 5.5987 | 3,828.72 µm² |
| `fp16-14cyc` | FPGA | 3.590 | 1.482 | 16.867 | 425 LUTs + 75 FF |
| `fp16-14cyc` | 55 nm | 1.9488 | 0.8917 | 3.7728 | 2,119.04 µm² |
| `bf16-11cyc` | FPGA | 3.643 | 1.373 | 14.442 | 348 LUTs + 69 FF |
| `bf16-11cyc` | 55 nm | 2.1707 | 0.8858 | 3.4835 | 1,924.72 µm² |
