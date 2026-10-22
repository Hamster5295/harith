# SIntMagnitudeMul

A signed multiplier built from an unsigned magnitude multiplier and sign correction.

The operands are converted to magnitudes, multiplied by the supplied `UIntMul`, and the product is negated when the operand signs differ. This is the sign-magnitude alternative to `SIntMacroMul`.

::: info Source
`src/main/scala/sint/mul/SIntMagnitudeMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `mul` | `Int => UIntMul` | The unsigned magnitude multiplier factory |

**Delay** = 0 cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `src1` | Input | `SInt(width.W)` | First operand |
| `src2` | Input | `SInt(width.W)` | Second operand |
| `output` | Output | `SInt((2 * width).W)` | Product `src1 * src2` |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 11.896 | 12.467 | 11.896 | 300 LUTs + 0 FF |
| `32bit` | 55 nm | 5.0521 | 5.0521 | 5.0521 | 17,607.24 µm² |
