# SIntMacroFma

The signed FMA implemented with the `*` and `+` operators.

The whole expression is left to the synthesis tool, which on FPGA will likely map it into an inner DSP with a built-in multiply-accumulate.

::: info Source
`src/main/scala/sint/fma/SIntMacroFma.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the multiplicands |

**Delay** = 0 cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `mul1` | Input | `SInt(width.W)` | Multiplicand |
| `mul2` | Input | `SInt(width.W)` | Multiplier |
| `add` | Input | `SInt((2 * width).W)` | Addend |
| `output` | Output | `SInt((2 * width + 1).W)` | Result `mul1 * mul2 + add` |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 8.869 | 9.441 | 8.869 | 111 LUTs + 0 FF |
| `32bit` | 55 nm | 4.3248 | 4.3248 | 4.3248 | 20,068.16 µm² |
