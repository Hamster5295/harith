# UIntComposedFma

An FMA composed from a [[UIntMul]] and a [[UIntAdd]].

The supplied units are wired in series, so the product and the addend go through two carry propagate adders. This is the flexible and reuse oriented option: pairing a small multiplier with a ripple adder gives the cheapest FMA, while a tree multiplier with a prefix adder gives a fast one. The latency is the sum of the two unit latencies.

::: info Source
`src/main/scala/uint/fma/UIntComposedFma.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `mul` | `=> UIntMul` | The multiplier, whose output must be `2 * width` bits wide |
| `adder` | `=> UIntAdd` | The final adder, which must be `2 * width + 1` bits wide |

**Delay** = `mul.latency + adder.latency` cycles

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
| `32bit` | FPGA | 12.913 | 13.485 | 12.913 | 1,990 LUTs + 0 FF |
| `32bit` | 55 nm | 5.3590 | 5.3590 | 5.3590 | 11,958.24 µm² |
