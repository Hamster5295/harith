# UIntNonRestoringDiv

An iterative non-restoring divider.

The partial remainder is kept in signed form and the divisor is added or subtracted according to its sign, so no restore step is needed. A final correction handles a negative remainder.

::: info Source
`src/main/scala/uint/div/UIntNonRestoringDiv.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |

**Delay** = `width` cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `in` | Input | `Decoupled(UIntDivReq)` | Decoupled request channel |
| `in.valid` | Input | `Bool` | Request valid |
| `in.ready` | Output | `Bool` | Request accepted |
| `in.bits.dividend` | Input | `UInt(width.W)` | Dividend |
| `in.bits.divisor` | Input | `UInt(width.W)` | Divisor |
| `out` | Output | `Valid(UIntDivResp)` | Valid response channel |
| `out.valid` | Output | `Bool` | Response valid |
| `out.bits.quotient` | Output | `UInt(width.W)` | Quotient |
| `out.bits.remainder` | Output | `UInt(width.W)` | Remainder |
| `out.bits.divideByZero` | Output | `Bool` | Divide-by-zero flag |
| `flush` | Input | `Bool` | Abort an in-flight division |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit-32cyc` | FPGA | 2.467 | 1.644 | 4.340 | 141 LUTs + 205 FF |
| `32bit-32cyc` | 55 nm | 5.9947 | 1.7030 | 5.9947 | 3,450.16 µm² |
