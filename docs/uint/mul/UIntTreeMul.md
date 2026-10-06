# UIntTreeMul

A carry save tree multiplier using AND partial products.

The partial product matrix is reduced to two rows by a Wallace or Dadda network and the two rows are added by the supplied [[UIntAdd]]. The reduction is combinational, so the latency is the latency of the final adder.

::: info Source
`src/main/scala/uint/mul/UIntTreeMul.scala`
:::

## Parameters

- **`width`** — The width of the operands
- **`reductionStyle`** — The reduction style of the partial product tree
- **`adder`** — The final carry propagate adder, which must be `2 * width` bits wide

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 10.316 | 10.888 | 10.316 | 1,616 LUTs + 0 FF |
| `32bit` | 55 nm | 4.8125 | 4.8125 | 4.8125 | 10,564.68 µm² |
