# UIntMacroAdd

The unsigned adder implemented with the `+` operator.

FPGA will likely implement it as an inner DSP or CARRY primitive.

::: info Source
`src/main/scala/uint/add/UIntMacroAdd.scala`
:::

## Parameters

- **`width`** — The width of the operands

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 2.726 | 3.298 | 2.726 | 32 LUTs + 0 FF |
| `32bit` | 55 nm | 1.4861 | 1.4861 | 1.4861 | 378.28 µm² |
