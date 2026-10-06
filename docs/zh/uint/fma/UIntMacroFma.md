# UIntMacroFma

用 `*` 和 `+` 运算符实现的无符号 FMA。

整个表达式交给综合工具处理，在 FPGA 上它很可能将其映射到带有内建乘加的内部 DSP 中。

::: info 来源
`src/main/scala/uint/fma/UIntMacroFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |

**延迟** = 0 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `mul1` | 输入 | `UInt(width.W)` | 被乘数 |
| `mul2` | 输入 | `UInt(width.W)` | 乘数 |
| `add` | 输入 | `UInt((2 * width).W)` | 加数 |
| `output` | 输出 | `UInt((2 * width + 1).W)` | 结果 `mul1 * mul2 + add` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 8.933 | 9.505 | 8.933 | 111 LUTs + 0 FF |
| `32bit` | 55 nm | 3.2738 | 3.2738 | 3.2484 | 17,201.52 µm² |
