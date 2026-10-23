# UIntTreeMul

A carry save tree multiplier using AND partial products.

The partial product matrix is reduced to two rows by a Wallace or Dadda network and the two rows are added by the supplied [`UIntAdd`](/uint/add/). The reduction is combinational, so the latency is the latency of the final adder.

::: info Source
`src/main/scala/uint/mul/UIntTreeMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `reductionStyle` | `ReductionStyle` | The reduction style of the partial product tree |
| `adder` | `Int => UIntAdd` | The factory of the final carry propagate adder, given the `2 * width` bit width |

**Delay** = `adder.latency` cycles

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
| `32bit-dadda` | FPGA | 10.316 | 10.888 | 10.316 | 1,616 LUTs + 0 FF |
| `32bit-dadda` | 55 nm | 4.8125 | 4.8125 | 4.8125 | 10,564.68 µm² |
| `32bit-wallace` | FPGA | 10.274 | 10.846 | 10.274 | 1,568 LUTs + 0 FF |
| `32bit-wallace` | 55 nm | 4.7953 | 4.7953 | 4.7953 | 10,695.44 µm² |
