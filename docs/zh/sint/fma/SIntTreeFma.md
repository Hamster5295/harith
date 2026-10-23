# SIntTreeFma

使用 Baugh-Wooley 部分积、加数合并进归约的有符号 FMA。

::: info 源代码
`src/main/scala/sint/fma/SIntTreeFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `reductionStyle` | `ReductionStyle` | The reduction style of the partial product tree |
| `adder` | `Int => UIntAdd` | The final carry propagate adder factory |

**延迟** = `adder.latency` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `mul1` | 输入 | `SInt(width.W)` | 被乘数 |
| `mul2` | 输入 | `SInt(width.W)` | 乘数 |
| `add` | 输入 | `SInt((2 * width).W)` | 加数 |
| `output` | 输出 | `SInt((2 * width + 1).W)` | 结果 `mul1 * mul2 + add` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 10.436 | 11.008 | 10.436 | 1,744 LUTs + 0 FF |
| `32bit` | 55 nm | 4.7786 | 4.7786 | 4.7786 | 11,305.84 µm² |
