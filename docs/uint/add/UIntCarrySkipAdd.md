# UIntCarrySkipAdd

A block carry skip (carry bypass) adder.

Each block ripples internally, while a block whose bits all propagate lets the incoming carry skip over it, shortening the worst case critical path at little area cost.

::: info Source
`src/main/scala/uint/add/UIntCarrySkipAdd.scala`
:::

## Parameters

- **`width`** — The width of the operands
- **`blockSize`** — The number of bits per skip block

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 6.359 | 6.931 | 6.359 | 55 LUTs + 0 FF |
| `32bit` | 55 nm | 2.6155 | 2.6155 | 2.6155 | 308.00 µm² |
