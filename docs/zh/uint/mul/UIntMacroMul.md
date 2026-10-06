# UIntMacroMul

用 `*` 运算符实现的无符号乘法器。

FPGA 很可能将其实现为内部 DSP。

::: info 来源
`src/main/scala/uint/mul/UIntMacroMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |

**延迟** = 0 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `UInt(width.W)` | 第一个操作数 |
| `src2` | 输入 | `UInt(width.W)` | 第二个操作数 |
| `output` | 输出 | `UInt((2 * width).W)` | 乘积 `src1 * src2` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 7.721 | 8.293 | 7.721 | 47 LUTs + 0 FF |
| `32bit` | 55 nm | 3.2678 | 3.2678 | 3.2678 | 16,313.36 µm² |
