# SIntComposedFma

A signed FMA composed from an unsigned magnitude multiplier and a signed add.

The significands are converted to magnitudes, multiplied by the supplied `UIntMul` and negated according to the operand signs, then the signed addend is added. This is the sign-magnitude alternative to `SIntMacroFma`.

::: info Source
`src/main/scala/sint/fma/SIntComposedFma.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the multiplicands |
| `mul` | `Int => UIntMul` | The unsigned magnitude multiplier factory |

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
| `32bit` | FPGA | 11.330 | 11.901 | 11.330 | 239 LUTs + 0 FF |
| `32bit` | 55 nm | 5.8455 | 5.8455 | 5.8455 | 18,356.80 µm² |
