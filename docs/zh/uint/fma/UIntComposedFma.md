# UIntComposedFma

由 [`UIntMul`](/zh/uint/mul/) 和 [`UIntAdd`](/zh/uint/add/) 组合而成的 FMA。

所提供的单元串联在一起，因此乘积与加数要经过两个进位传播加法器。这是灵活且面向复用的选项：将小乘法器与行波加法器搭配可得到最便宜的 FMA，而将树乘法器与前缀加法器搭配可得到快速的 FMA。延迟为两个单元延迟之和。

::: info 来源
`src/main/scala/uint/fma/UIntComposedFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `mul` | `=> UIntMul` | 乘法器，其输出必须为 `2 * width` 位宽 |
| `adder` | `=> UIntAdd` | 最终加法器，必须为 `2 * width + 1` 位宽 |

**延迟** = `mul.latency + adder.latency` 周期

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
| `32bit` | FPGA | 12.913 | 13.485 | 12.913 | 1,990 LUTs + 0 FF |
| `32bit` | 55 nm | 5.3590 | 5.3590 | 5.3590 | 11,958.24 µm² |
