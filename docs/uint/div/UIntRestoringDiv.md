# UIntRestoringDiv

An iterative restoring divider.

The remainder is shifted in one dividend bit per cycle and the divisor is subtracted; when the subtraction underflows the remainder is restored and the quotient bit is zero. It is the smallest divider at the cost of one cycle per operand bit.

::: info Source
`src/main/scala/uint/div/UIntRestoringDiv.scala`
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
| `32bit-32cyc` | FPGA | 2.467 | 1.644 | 2.678 | 122 LUTs + 201 FF |
| `32bit-32cyc` | 55 nm | 6.6857 | 1.6885 | 6.6857 | 2,589.44 µm² |
