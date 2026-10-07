# UIntBoothFma

使用改进 Booth radix-4 部分积的融合乘加器。

加数被合并进 Booth 部分积堆，因此需要由单个进位传播加法器归约的部分积比基于 AND 的 [`UIntTreeFma`](/zh/uint/fma/UIntTreeFma) 更少。

::: info 源代码
`src/main/scala/uint/fma/UIntBoothFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `reductionStyle` | `ReductionStyle` | 部分积树的归约风格 |
| `adder` | `=> UIntAdd` | 最终进位传播加法器，必须为 `2 * width + 1` 位宽 |

**延迟** = `adder.latency` 周期

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
| `32bit` | FPGA | 10.731 | 11.303 | 10.731 | 2,642 LUTs + 0 FF |
| `32bit` | 55 nm | 5.5558 | 5.5558 | 5.5558 | 18,806.76 µm² |
