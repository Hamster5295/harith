# UIntMacroMul

The unsigned multiplier implemented with the `*` operator.

FPGA will likely implement it as an inner DSP.

::: info Source
`src/main/scala/uint/mul/UIntMacroMul.scala`
:::

## Parameters

- **`width`** — The width of the operands

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 7.721 | 8.293 | 7.721 | 47 LUTs + 0 FF |
| `32bit` | 55 nm | 3.2678 | 3.2678 | 3.2678 | 16,313.36 µm² |
