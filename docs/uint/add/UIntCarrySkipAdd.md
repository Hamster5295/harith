# UIntCarrySkipAdd

A block carry skip (carry bypass) adder.

Each block ripples internally, while a block whose bits all propagate lets the incoming carry skip over it, shortening the worst case critical path at little area cost.

::: info Source
`src/main/scala/uint/add/UIntCarrySkipAdd.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `blockSize` | `Int` | The number of bits per skip block |

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
| `32bit` | FPGA | 6.359 | 6.931 | 6.359 | 55 LUTs + 0 FF |
| `32bit` | 55 nm | 2.6155 | 2.6155 | 2.6155 | 308.00 µm² |
