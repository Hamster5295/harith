# UIntPipelinedArrayMul

A pipelined carry save array multiplier.

The partial product rows are distributed over `stages` register layers. The throughput is one product per cycle and the latency equals `stages`. A value of 0 makes the multiplier combinational, equivalently to [[UIntArrayMul]].

::: info Source
`src/main/scala/uint/mul/UIntPipelinedArrayMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `stages` | `Int` | The number of pipeline register layers |

**Delay** = `stages` cycles

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
| `32bit-2cyc` | FPGA | 7.198 | 6.753 | 7.545 | 2,124 LUTs + 220 FF |
| `32bit-2cyc` | 55 nm | 2.6341 | 2.7981 | 2.6341 | 18,446.40 µm² |
