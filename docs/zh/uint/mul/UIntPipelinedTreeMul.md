# UIntPipelinedTreeMul

使用 AND 部分积的流水化进位保存树乘法器。

归约层级被分布到 `stages` 层寄存器上，归约后的两行由提供的 [`UIntAdd`](/zh/uint/add/) 相加。吞吐率为每周期一个乘积，总延迟等于 `stages` 加上最终加法器的延迟，因此使用流水化加法器可以缩短最终加法。

::: info 来源
`src/main/scala/uint/mul/UIntPipelinedTreeMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `reductionStyle` | `ReductionStyle` | 部分积树的归约风格 |
| `adder` | `=> UIntAdd` | 最终进位传播加法器，必须为 `2 * width` 位宽 |
| `stages` | `Int` | 归约树中的流水寄存器层数 |

**延迟** = `stages + adder.latency` 周期

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
| `32bit-2cyc` | FPGA | 5.976 | 4.530 | 4.947 | 1,597 LUTs + 207 FF |
| `32bit-2cyc` | 55 nm | 1.8291 | 2.6226 | 2.6226 | 11,401.88 µm² |
