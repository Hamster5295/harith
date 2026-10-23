# SIntBoothMul

A signed modified Booth radix-4 tree multiplier.

::: info Source
`src/main/scala/sint/mul/SIntBoothMul.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `reductionStyle` | `ReductionStyle` | The reduction style of the partial product tree |
| `adder` | `Int => UIntAdd` | The final carry propagate adder factory |

**Delay** = `adder.latency` cycles

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
| `32bit` | FPGA | 10.235 | 10.807 | 10.235 | 3,030 LUTs + 0 FF |
| `32bit` | 55 nm | 5.0887 | 5.0887 | 5.0887 | 15,983.52 µm² |
