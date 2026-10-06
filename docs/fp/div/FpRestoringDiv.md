# FpRestoringDiv

An iterative restoring floating-point divider.

The significands are normalized to `NW` bits and the quotient is built one bit per cycle, restoring the remainder when the subtraction underflows. The quotient carries `manWidth + 3` extra fractional bits and a sticky from the nonzero remainder, so the final rounding is exact. NaN is canonical, per RISC-V.

::: info Source
`src/main/scala/fp/div/FpRestoringDiv.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `aFmt` | `FpFormat` | The format of the dividend |
| `bFmt` | `FpFormat` | The format of the divisor |
| `outFmt` | `FpFormat` | The format of the result |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = `max(aFmt.manWidth, bFmt.manWidth, outFmt.manWidth) + outFmt.manWidth + 4` cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `in` | Input | `Decoupled(FpDivReq)` | Decoupled request channel |
| `in.valid` | Input | `Bool` | Request valid |
| `in.ready` | Output | `Bool` | Request accepted |
| `in.bits.src1` | Input | `UInt(aFmt.width.W)` | Dividend |
| `in.bits.src2` | Input | `UInt(bFmt.width.W)` | Divisor |
| `in.bits.rm` | Input | `UInt(3.W)` | RISC-V rounding mode |
| `out` | Output | `Valid(FpDivResp)` | Valid response channel |
| `out.valid` | Output | `Bool` | Response valid |
| `out.bits.output` | Output | `UInt(outFmt.width.W)` | Rounded quotient |
| `out.bits.fflags` | Output | `FpFlags` | IEEE-754 exception flags |
| `flush` | Input | `Bool` | Abort an in-flight division |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `fp64-108cyc` | FPGA | 5.568 | 1.439 | 22.187 | 2,846 LUTs + 365 FF |
| `fp64-108cyc` | 55 nm | 3.4405 | 0.8743 | 10.1610 | 11,796.96 µm² |
| `fp32-50cyc` | FPGA | 5.535 | 1.414 | 18.934 | 1,232 LUTs + 184 FF |
| `fp32-50cyc` | 55 nm | 2.3681 | 0.8798 | 6.5751 | 5,540.08 µm² |
| `fp16-24cyc` | FPGA | 4.432 | 1.511 | 17.510 | 586 LUTs + 99 FF |
| `fp16-24cyc` | 55 nm | 2.6870 | 0.8858 | 4.7179 | 2,786.56 µm² |
| `bf16-18cyc` | FPGA | 4.203 | 1.489 | 16.685 | 477 LUTs + 87 FF |
| `bf16-18cyc` | 55 nm | 1.9531 | 0.8800 | 4.4104 | 2,530.08 µm² |
