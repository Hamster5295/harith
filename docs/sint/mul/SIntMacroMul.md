# SIntMacroMul

The signed multiplier implemented with the `*` operator.

Both operands are two's complement and the product is exact in `2 * width` bits. Synthesis maps the expression to a signed multiplier (a Baugh-Wooley array, or an unsigned array with sign extension).

::: info Source
`src/main/scala/sint/mul/SIntMacroMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |

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
| `32bit` | FPGA | 7.721 | 8.293 | 7.721 | 47 LUTs + 0 FF |
| `32bit` | 55 nm | 4.0572 | 4.0572 | 4.0572 | 18,430.16 µm² |
