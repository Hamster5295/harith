# UIntPipelinedTreeFma

使用 AND 部分积的流水化融合乘加器。

加数被合并进部分积堆，归约层级被分布到 `stages` 层寄存器上，归约后的两行由提供的 [`UIntAdd`](/zh/uint/add/) 相加。吞吐率为每周期一次 FMA，延迟为 `stages` 加上加法器延迟。

::: info 来源
`src/main/scala/uint/fma/UIntPipelinedTreeFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `reductionStyle` | `ReductionStyle` | 部分积树的归约风格 |
| `adder` | `=> UIntAdd` | 最终进位传播加法器，必须为 `2 * width + 1` 位宽 |
| `stages` | `Int` | 归约树中的流水寄存器层数 |

**延迟** = `stages + adder.latency` 周期

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
| `32bit-2cyc` | FPGA | 6.132 | 4.783 | 4.947 | 1,683 LUTs + 211 FF |
| `32bit-2cyc` | 55 nm | 1.9244 | 2.5375 | 2.5375 | 12,053.44 µm² |
