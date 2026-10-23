# SIntPipelinedArrayMul

A pipelined signed carry save array multiplier using Baugh-Wooley partial products.

::: info Source
`src/main/scala/sint/mul/SIntPipelinedArrayMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `stages` | `Int` | The number of pipeline register layers, which is also the latency |

**Delay** = `stages` cycles

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
| `32bit-2cyc` | FPGA | 7.452 | 6.329 | 7.332 | 2,177 LUTs + 220 FF |
| `32bit-2cyc` | 55 nm | 3.4569 | 2.4716 | 3.4569 | 19,392.24 µm² |
