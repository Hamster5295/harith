# SIntArrayMul

A signed carry save array multiplier using Baugh-Wooley partial products.

::: info Source
`src/main/scala/sint/mul/SIntArrayMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |

**Delay** = 0 cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `src1` | Input | `SInt(width.W)` | First operand |
| `src2` | Input | `SInt(width.W)` | Second operand |
| `output` | Output | `SInt((2 * width).W)` | Product `src1 * src2` |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 18.743 | 19.315 | 18.743 | 2,731 LUTs + 0 FF |
| `32bit` | 55 nm | 8.1454 | 8.1454 | 8.1454 | 16,828.56 µm² |
