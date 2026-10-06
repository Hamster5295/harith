# UIntSrt2Div

An iterative radix-2 SRT divider.

Each iteration produces one redundant signed quotient digit from `{-1, 0, 1}` by comparing twice the partial remainder against `+-divisor`, and the digits are accumulated into a signed quotient that is corrected once at the end. It needs no restore step and finishes in one cycle per operand bit.

::: info Source
`src/main/scala/uint/div/UIntSrt2Div.scala`
:::

## Parameters

- **`width`** — The width of the operands

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit-32cyc` | FPGA | 2.467 | 1.498 | 13.153 | 324 LUTs + 236 FF |
| `32bit-32cyc` | 55 nm | 6.6244 | 0.8858 | 6.6244 | 5,332.88 µm² |
