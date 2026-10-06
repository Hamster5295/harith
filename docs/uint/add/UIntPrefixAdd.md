# UIntPrefixAdd

A fully parallel prefix adder.

All carries are computed by a single prefix network, giving a logarithmic critical path. The [[PrefixStyle]] selects the network shape and therefore the area/performance point.

::: info Source
`src/main/scala/uint/add/UIntPrefixAdd.scala`
:::

## Parameters

- **`width`** — The width of the operands
- **`style`** — The parallel prefix network style

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 3.816 | 4.388 | 3.816 | 159 LUTs + 0 FF |
| `32bit` | 55 nm | 1.3195 | 1.3195 | 1.3195 | 945.84 µm² |
