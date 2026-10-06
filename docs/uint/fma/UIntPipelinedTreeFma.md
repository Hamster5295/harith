# UIntPipelinedTreeFma

A pipelined fused multiply-adder using AND partial products.

The addend is merged into the partial product heap, the reduction levels are distributed over `stages` register layers and the two reduced rows are added by the supplied [[UIntAdd]]. The throughput is one FMA per cycle and the latency is `stages` plus the adder latency.

::: info Source
`src/main/scala/uint/fma/UIntPipelinedTreeFma.scala`
:::

## Parameters

- **`width`** — The width of the operands
- **`reductionStyle`** — The reduction style of the partial product tree
- **`adder`** — The final carry propagate adder, which must be `2 * width + 1` bits wide
- **`stages`** — The number of pipeline register layers in the reduction tree

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit-2cyc` | FPGA | 6.132 | 4.783 | 4.947 | 1,683 LUTs + 211 FF |
| `32bit-2cyc` | 55 nm | 1.9244 | 2.5375 | 2.5375 | 12,053.44 µm² |
