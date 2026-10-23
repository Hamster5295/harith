# SIntTreeMul

A signed carry save tree multiplier using Baugh-Wooley partial products.

::: info Source
`src/main/scala/sint/mul/SIntTreeMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `reductionStyle` | `ReductionStyle` | The reduction style of the partial product tree |
| `adder` | `Int => UIntAdd` | The final carry propagate adder factory |

**Delay** = `adder.latency` cycles

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
| `32bit` | FPGA | 10.016 | 10.588 | 10.016 | 1,622 LUTs + 0 FF |
| `32bit` | 55 nm | 4.6843 | 4.6843 | 4.6843 | 10,562.44 µm² |
