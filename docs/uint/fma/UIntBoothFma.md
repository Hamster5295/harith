# UIntBoothFma

A fused multiply-adder using modified Booth radix-4 partial products.

The addend is merged into the Booth partial product heap, so fewer partial products than the AND based [[UIntTreeFma]] are reduced by a single carry propagate adder.

::: info Source
`src/main/scala/uint/fma/UIntBoothFma.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `reductionStyle` | `ReductionStyle` | The reduction style of the partial product tree |
| `adder` | `=> UIntAdd` | The final carry propagate adder, which must be `2 * width + 1` bits wide |

**Delay** = `adder.latency` cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `mul1` | Input | `UInt(width.W)` | Multiplicand |
| `mul2` | Input | `UInt(width.W)` | Multiplier |
| `add` | Input | `UInt((2 * width).W)` | Addend |
| `output` | Output | `UInt((2 * width + 1).W)` | Result `mul1 * mul2 + add` |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 10.731 | 11.303 | 10.731 | 2,642 LUTs + 0 FF |
| `32bit` | 55 nm | 5.5558 | 5.5558 | 5.5558 | 18,806.76 µm² |
