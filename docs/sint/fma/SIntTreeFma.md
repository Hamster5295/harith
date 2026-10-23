# SIntTreeFma

A signed fused multiply-adder using Baugh-Wooley partial products with the addend merged into the reduction.

::: info Source
`src/main/scala/sint/fma/SIntTreeFma.scala`
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
| `mul1` | Input | `SInt(width.W)` | Multiplicand |
| `mul2` | Input | `SInt(width.W)` | Multiplier |
| `add` | Input | `SInt((2 * width).W)` | Addend |
| `output` | Output | `SInt((2 * width + 1).W)` | Result `mul1 * mul2 + add` |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 10.436 | 11.008 | 10.436 | 1,744 LUTs + 0 FF |
| `32bit` | 55 nm | 4.7786 | 4.7786 | 4.7786 | 11,305.84 µm² |
