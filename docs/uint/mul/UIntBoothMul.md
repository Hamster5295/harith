# UIntBoothMul

A modified Booth radix-4 carry save tree multiplier.

The multiplier is recoded so that only about half as many partial products as the AND based [`UIntTreeMul`](/uint/mul/UIntTreeMul) are generated. The partial products are reduced to two rows by a Wallace or Dadda network and the two rows are added by the supplied [`UIntAdd`](/uint/add/).

::: info Source
`src/main/scala/uint/mul/UIntBoothMul.scala`
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
| `32bit-dadda` | FPGA | 10.234 | 10.806 | 10.234 | 2,269 LUTs + 0 FF |
| `32bit-dadda` | 55 nm | 5.3502 | 5.3502 | 5.3502 | 16,288.16 µm² |
| `32bit-wallace` | FPGA | 10.137 | 10.709 | 10.137 | 2,392 LUTs + 0 FF |
| `32bit-wallace` | 55 nm | 5.4746 | 5.4746 | 5.4746 | 17,640.84 µm² |
