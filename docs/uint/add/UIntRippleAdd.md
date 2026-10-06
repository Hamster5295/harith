# UIntRippleAdd

A structural ripple carry adder.

This is the most resource efficient combinational adder, at the cost of an O(width) critical path. On FPGAs the inferred [[UIntMacroAdd]] is usually preferable.

::: info Source
`src/main/scala/uint/add/UIntRippleAdd.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |

**Delay** = 0 cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `src1` | Input | `UInt(width.W)` | First operand |
| `src2` | Input | `UInt(width.W)` | Second operand |
| `carry` | Input | `Bool` | Carry in |
| `output` | Output | `UInt((width + 1).W)` | Sum `src1 + src2 + carry` |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 6.366 | 6.938 | 6.366 | 56 LUTs + 0 FF |
| `32bit` | 55 nm | 2.6383 | 2.6383 | 2.6383 | 300.72 µm² |
