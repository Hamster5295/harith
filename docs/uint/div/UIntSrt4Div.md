# UIntSrt4Div

An iterative radix-4 divider.

Two dividend bits are consumed per iteration and each iteration produces one quotient digit from `{0, 1, 2, 3}` by comparing the shifted partial remainder against the divisor and its multiples `2 * divisor` and `3 * divisor`. The digit is selected exactly, so the partial remainder always stays below the divisor and no restore or final correction step is needed. This halves the number of iterations of a radix-2 divider.

::: info Source
`src/main/scala/uint/div/UIntSrt4Div.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |

**Delay** = `(width + 1) / 2` cycles

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
| `32bit-16cyc` | FPGA | 2.467 | 1.528 | 4.338 | 328 LUTs + 202 FF |
| `32bit-16cyc` | 55 nm | 5.8867 | 0.8927 | 5.8867 | 3,832.64 µm² |
