# UIntSrt4Div

An iterative radix-4 divider.

Two dividend bits are consumed per iteration and each iteration produces one quotient digit from `{0, 1, 2, 3}` by comparing the shifted partial remainder against the divisor and its multiples `2 * divisor` and `3 * divisor`. The digit is selected exactly, so the partial remainder always stays below the divisor and no restore or final correction step is needed. This halves the number of iterations of a radix-2 divider.

::: info Source
`src/main/scala/uint/div/UIntSrt4Div.scala`
:::

## Parameters

- **`width`** — The width of the operands

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit-16cyc` | FPGA | 2.467 | 1.528 | 4.338 | 328 LUTs + 202 FF |
| `32bit-16cyc` | 55 nm | 5.8867 | 0.8927 | 5.8867 | 3,832.64 µm² |
