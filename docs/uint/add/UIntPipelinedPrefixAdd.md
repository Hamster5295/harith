# UIntPipelinedPrefixAdd

A pipelined parallel prefix adder.

The prefix levels of the selected [[PrefixStyle]] are distributed over `stages` register layers. The throughput is one addition per cycle and the latency equals `stages`, regardless of whether a layer is used for prefix logic or only for retiming. A value of 0 makes the adder combinational, equivalently to [[UIntPrefixAdd]].

::: info Source
`src/main/scala/uint/add/UIntPipelinedPrefixAdd.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `style` | `PrefixStyle` | The parallel prefix network style |
| `stages` | `Int` | The number of pipeline register layers, which is also the latency |

**Delay** = `stages` cycles

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
| `32bit-2cyc` | FPGA | 2.833 | 1.716 | 1.479 | 168 LUTs + 192 FF |
| `32bit-2cyc` | 55 nm | 0.3475 | 0.8007 | 0.8007 | 1,847.72 µm² |
