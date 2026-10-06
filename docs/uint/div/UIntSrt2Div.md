# UIntSrt2Div

An iterative radix-2 SRT divider.

Each iteration produces one redundant signed quotient digit from `{-1, 0, 1}` by comparing twice the partial remainder against `+-divisor`, and the digits are accumulated into a signed quotient that is corrected once at the end. It needs no restore step and finishes in one cycle per operand bit.

::: info Source
`src/main/scala/uint/div/UIntSrt2Div.scala`
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
| `32bit-32cyc` | FPGA | 2.467 | 1.498 | 13.153 | 324 LUTs + 236 FF |
| `32bit-32cyc` | 55 nm | 6.6244 | 0.8858 | 6.6244 | 5,332.88 µm² |
