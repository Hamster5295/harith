# UIntCarrySelectAdd

A block carry select adder.

Each block speculatively computes its result for both possible incoming carries and selects the correct one once the real carry arrives. The duplicated logic reduces the critical path to one carry select per block.

::: info Source
`src/main/scala/uint/add/UIntCarrySelectAdd.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `blockSize` | `Int` | The number of bits per select block |

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
| `32bit` | FPGA | 4.104 | 4.676 | 4.104 | 57 LUTs + 0 FF |
| `32bit` | 55 nm | 2.8304 | 2.8304 | 2.8304 | 416.64 µm² |
