# SIntPipelinedBoothFma

使用有符号 modified Booth 部分积、加数合并进归约的流水有符号 FMA。

::: info 源代码
`src/main/scala/sint/fma/SIntPipelinedBoothFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `reductionStyle` | `ReductionStyle` | The reduction style of the partial product tree |
| `adder` | `Int => UIntAdd` | The final carry propagate adder factory |
| `stages` | `Int` | The number of pipeline register layers, which is also the latency |

**延迟** = `stages + adder.latency` 周期

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
| `32bit-2cyc` | FPGA | 5.754 | 4.436 | 3.865 | 3,142 LUTs + 459 FF |
| `32bit-2cyc` | 55 nm | 3.4471 | 2.3025 | 3.4471 | 18,142.60 µm² |
