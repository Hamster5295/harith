# SIntPipelinedTreeMul

A pipelined signed carry save tree multiplier using Baugh-Wooley partial products.

::: info Source
`src/main/scala/sint/mul/SIntPipelinedTreeMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `reductionStyle` | `ReductionStyle` | The reduction style of the partial product tree |
| `adder` | `Int => UIntAdd` | The final carry propagate adder factory |
| `stages` | `Int` | The number of pipeline register layers, which is also the latency |

**Delay** = `stages + adder.latency` cycles

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
| `32bit-2cyc` | FPGA | 6.310 | 4.255 | 4.534 | 1,611 LUTs + 208 FF |
| `32bit-2cyc` | 55 nm | 1.8000 | 2.4505 | 2.4505 | 11,618.60 µm² |
