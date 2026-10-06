# UIntArrayMul

A structural carry save array multiplier.

Each partial product row is accumulated in carry save form with a row of full adders, and a final ripple carry chain produces the product. The regular structure gives the lowest cost at the price of an O(width) critical path. On FPGAs the inferred [[UIntMacroMul]] is usually preferable.

::: info Source
`src/main/scala/uint/mul/UIntArrayMul.scala`
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
| `output` | Output | `UInt((2 * width).W)` | Product `src1 * src2` |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 20.305 | 20.877 | 20.305 | 2,589 LUTs + 0 FF |
| `32bit` | 55 nm | 7.9127 | 7.9127 | 7.9127 | 16,088.52 µm² |
