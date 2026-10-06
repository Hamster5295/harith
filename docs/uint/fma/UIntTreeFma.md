# UIntTreeFma

A fused multiply-adder using AND partial products.

The addend is merged into the partial product heap, so the reduction tree produces two rows that a single carry propagate adder resolves. It is one adder cheaper than [[UIntComposedFma]].

::: info Source
`src/main/scala/uint/fma/UIntTreeFma.scala`
:::

## Parameters

- **`width`** — The width of the operands
- **`reductionStyle`** — The reduction style of the partial product tree
- **`adder`** — The final carry propagate adder, which must be `2 * width + 1` bits wide

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 10.448 | 11.020 | 10.448 | 1,704 LUTs + 0 FF |
| `32bit` | 55 nm | 4.8911 | 4.8911 | 4.8911 | 11,172.00 µm² |
