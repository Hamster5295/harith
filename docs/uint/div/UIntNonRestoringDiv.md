# UIntNonRestoringDiv

An iterative non-restoring divider.

The partial remainder is kept in signed form and the divisor is added or subtracted according to its sign, so no restore step is needed. A final correction handles a negative remainder.

::: info Source
`src/main/scala/uint/div/UIntNonRestoringDiv.scala`
:::

## Parameters

- **`width`** — The width of the operands

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit-32cyc` | FPGA | 2.467 | 1.644 | 4.340 | 141 LUTs + 205 FF |
| `32bit-32cyc` | 55 nm | 5.9947 | 1.7030 | 5.9947 | 3,450.16 µm² |
