# SIntMacroMul

用 `*` 运算符实现的有符号乘法器。

两个操作数均为补码，乘积在 `2 * width` 位内精确。综合工具会将其映射为有符号乘法器（Baugh-Wooley 阵列，或带符号扩展的无符号阵列）。

::: info 源代码
`src/main/scala/sint/mul/SIntMacroMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |

**延迟** = 0 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `SInt(width.W)` | 第一个操作数 |
| `src2` | 输入 | `SInt(width.W)` | 第二个操作数 |
| `output` | 输出 | `SInt((2 * width).W)` | 乘积 `src1 * src2` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 7.721 | 8.293 | 7.721 | 47 LUTs + 0 FF |
| `32bit` | 55 nm | 4.0572 | 4.0572 | 4.0572 | 18,430.16 µm² |
