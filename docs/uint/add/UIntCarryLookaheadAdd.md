# UIntCarryLookaheadAdd

A hierarchical carry lookahead adder.

The bits are split into groups. Each group resolves its internal carries with a lookahead network, exposes a group generate/propagate pair, and the group carries are resolved by a second lookahead level. This keeps the critical path logarithmic while using far less logic than a fully parallel prefix adder.

::: info Source
`src/main/scala/uint/add/UIntCarryLookaheadAdd.scala`
:::

## Parameters

- **`width`** — The width of the operands
- **`groupSize`** — The number of bits per lookahead group

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 3.879 | 4.451 | 3.879 | 70 LUTs + 0 FF |
| `32bit` | 55 nm | 1.4466 | 1.4466 | 1.4466 | 493.08 µm² |
