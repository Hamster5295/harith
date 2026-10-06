# UIntMacroFma

The unsigned FMA implemented with the `*` and `+` operators.

The whole expression is left to the synthesis tool, which on FPGA will likely map it into an inner DSP with a built-in multiply-accumulate.

::: info Source
`src/main/scala/uint/fma/UIntMacroFma.scala`
:::

## Parameters

- **`width`** — The width of the operands

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 8.933 | 9.505 | 8.933 | 111 LUTs + 0 FF |
| `32bit` | 55 nm | 3.2738 | 3.2738 | 3.2484 | 17,201.52 µm² |
