# SIntRestoringDiv

A signed divider built from the unsigned restoring divider and sign correction.

The operands are converted to magnitudes and divided by `UIntRestoringDiv`; the quotient and remainder signs are then restored. The quotient is truncated toward zero and the remainder takes the dividend's sign. Divide-by-zero and the `min / -1` overflow follow the RISC-V semantics.

::: info Source
`src/main/scala/sint/div/SIntRestoringDiv.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |

**Delay** = `width` cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `in` | Input | `Decoupled(SIntDivReq)` | Decoupled request channel |
| `in.valid` | Input | `Bool` | Request valid |
| `in.ready` | Output | `Bool` | Request accepted |
| `in.bits.dividend` | Input | `SInt(width.W)` | Dividend |
| `in.bits.divisor` | Input | `SInt(width.W)` | Divisor |
| `out` | Output | `Valid(SIntDivResp)` | Valid response channel |
| `out.valid` | Output | `Bool` | Response valid |
| `out.bits.quotient` | Output | `SInt(width.W)` | Quotient |
| `out.bits.remainder` | Output | `SInt(width.W)` | Remainder |
| `out.bits.divideByZero` | Output | `Bool` | Divide-by-zero flag |
| `flush` | Input | `Bool` | Abort an in-flight division |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit-32cyc` | FPGA | 4.487 | 3.775 | 3.204 | 345 LUTs + 235 FF |
| `32bit-32cyc` | 55 nm | 4.9881 | 3.6132 | 4.9881 | 3,597.16 µm² |
