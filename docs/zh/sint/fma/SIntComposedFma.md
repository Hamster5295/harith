# SIntComposedFma

由无符号幅值乘法器和有符号加法构成的有符号 FMA。

乘数先转换为幅值，由给定的 `UIntMul` 相乘，再根据操作数符号取负，最后加上有符号加数。这是 `SIntMacroFma` 的符号-幅值替代方案。

::: info 源代码
`src/main/scala/sint/fma/SIntComposedFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 乘数位宽 |
| `mul` | `Int => UIntMul` | 无符号幅值乘法器工厂 |

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
| `32bit` | FPGA | 11.330 | 11.901 | 11.330 | 239 LUTs + 0 FF |
| `32bit` | 55 nm | 5.8455 | 5.8455 | 5.8455 | 18,356.80 µm² |
