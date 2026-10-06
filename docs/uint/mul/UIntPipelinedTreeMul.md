# UIntPipelinedTreeMul

A pipelined carry save tree multiplier using AND partial products.

The reduction levels are distributed over `stages` register layers and the two reduced rows are added by the supplied [[UIntAdd]]. The throughput is one product per cycle and the overall latency is `stages` plus the latency of the final adder, so a pipelined adder can shorten the final add.

::: info Source
`src/main/scala/uint/mul/UIntPipelinedTreeMul.scala`
:::

## Parameters

- **`width`** — The width of the operands
- **`reductionStyle`** — The reduction style of the partial product tree
- **`adder`** — The final carry propagate adder, which must be `2 * width` bits wide
- **`stages`** — The number of pipeline register layers in the reduction tree

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit-2cyc` | FPGA | 5.976 | 4.530 | 4.947 | 1,597 LUTs + 207 FF |
| `32bit-2cyc` | 55 nm | 1.8291 | 2.6226 | 2.6226 | 11,401.88 µm² |
