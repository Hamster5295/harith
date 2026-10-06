# UIntComposedFma

An FMA composed from a [[UIntMul]] and a [[UIntAdd]].

The supplied units are wired in series, so the product and the addend go through two carry propagate adders. This is the flexible and reuse oriented option: pairing a small multiplier with a ripple adder gives the cheapest FMA, while a tree multiplier with a prefix adder gives a fast one. The latency is the sum of the two unit latencies.

::: info Source
`src/main/scala/uint/fma/UIntComposedFma.scala`
:::

## Parameters

- **`width`** — The width of the operands
- **`mul`** — The multiplier, whose output must be `2 * width` bits wide
- **`adder`** — The final adder, which must be `2 * width + 1` bits wide

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 12.913 | 13.485 | 12.913 | 1,990 LUTs + 0 FF |
| `32bit` | 55 nm | 5.3590 | 5.3590 | 5.3590 | 11,958.24 µm² |
