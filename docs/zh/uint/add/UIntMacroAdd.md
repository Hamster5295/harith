# UIntMacroAdd

用 `+` 运算符实现的无符号加法器。

FPGA 很可能将其实现为内部 DSP 或 CARRY 原语。

::: info 来源
`src/main/scala/uint/add/UIntMacroAdd.scala`
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
| `carry` | 输入 | `Bool` | 进位输入 |
| `output` | 输出 | `UInt((width + 1).W)` | 和 `src1 + src2 + carry` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 2.726 | 3.298 | 2.726 | 32 LUTs + 0 FF |
| `32bit` | 55 nm | 1.4861 | 1.4861 | 1.4861 | 378.28 µm² |
