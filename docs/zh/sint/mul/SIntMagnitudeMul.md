# SIntMagnitudeMul

由无符号幅值乘法器和符号修正构成的有符号乘法器。

操作数先转换为幅值，由给定的 `UIntMul` 相乘，当两个操作数符号不同时对乘积取负。这是 `SIntMacroMul` 的符号-幅值替代方案。

::: info 源代码
`src/main/scala/sint/mul/SIntMagnitudeMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `mul` | `Int => UIntMul` | 无符号幅值乘法器工厂 |

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
| `32bit` | FPGA | 11.896 | 12.467 | 11.896 | 300 LUTs + 0 FF |
| `32bit` | 55 nm | 5.0521 | 5.0521 | 5.0521 | 17,607.24 µm² |
