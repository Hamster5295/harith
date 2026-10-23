# SIntPipelinedBoothFma

A pipelined signed fused multiply-adder using signed modified Booth partial products with the addend merged into the reduction.

::: info Source
`src/main/scala/sint/fma/SIntPipelinedBoothFma.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `reductionStyle` | `ReductionStyle` | The reduction style of the partial product tree |
| `adder` | `Int => UIntAdd` | The final carry propagate adder factory |
| `stages` | `Int` | The number of pipeline register layers, which is also the latency |

**Delay** = `stages + adder.latency` cycles

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
| `32bit-2cyc` | FPGA | 5.754 | 4.436 | 3.865 | 3,142 LUTs + 459 FF |
| `32bit-2cyc` | 55 nm | 3.4471 | 2.3025 | 3.4471 | 18,142.60 µm² |
