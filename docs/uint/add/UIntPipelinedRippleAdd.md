# UIntPipelinedRippleAdd

A pipelined ripple carry adder.

The carry chain is cut at every block boundary and the carry, the operand slices and the accumulated sums advance one block per cycle. This yields a short, block sized critical path and a throughput of one addition per cycle with a fixed latency of the number of blocks.

::: info Source
`src/main/scala/uint/add/UIntPipelinedRippleAdd.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `blockSize` | `Int` | The number of bits per pipeline stage |

**Delay** = `ceil(width / blockSize)` cycles

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
| `32bit-8cyc` | FPGA | 2.115 | 0.912 | 1.791 | 88 LUTs + 156 FF |
| `32bit-8cyc` | 55 nm | 0.2891 | 0.3929 | 0.2891 | 2,615.76 µm² |
