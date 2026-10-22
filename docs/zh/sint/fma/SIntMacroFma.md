# SIntMacroFma

用 `*` 和 `+` 运算符实现的有符号 FMA。

整个表达式交给综合工具处理，在 FPGA 上很可能映射为带乘加功能的内部 DSP。

::: info 源代码
`src/main/scala/sint/fma/SIntMacroFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 乘数位宽 |

**延迟** = 0 周期

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
| `32bit` | FPGA | 8.869 | 9.441 | 8.869 | 111 LUTs + 0 FF |
| `32bit` | 55 nm | 4.3248 | 4.3248 | 4.3248 | 20,068.16 µm² |
