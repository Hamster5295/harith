# UIntPipelinedArrayMul

流水化进位保存阵列乘法器。

部分积行被分布到 `stages` 层寄存器上。吞吐率为每周期一个乘积，延迟等于 `stages`。取值为 0 时乘法器为组合逻辑，等价于 [[UIntArrayMul]]。

::: info 来源
`src/main/scala/uint/mul/UIntPipelinedArrayMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `stages` | `Int` | 流水寄存器层数 |

**延迟** = `stages` 周期

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
| `32bit-2cyc` | FPGA | 7.198 | 6.753 | 7.545 | 2,124 LUTs + 220 FF |
| `32bit-2cyc` | 55 nm | 2.6341 | 2.7981 | 2.6341 | 18,446.40 µm² |
