# UIntRippleAdd

A structural ripple carry adder.

This is the most resource efficient combinational adder, at the cost of an O(width) critical path. On FPGAs the inferred [[UIntMacroAdd]] is usually preferable.

::: info Source
`src/main/scala/uint/add/UIntRippleAdd.scala`
:::

## Parameters

- **`width`** — The width of the operands

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 6.366 | 6.938 | 6.366 | 56 LUTs + 0 FF |
| `32bit` | 55 nm | 2.6383 | 2.6383 | 2.6383 | 300.72 µm² |
