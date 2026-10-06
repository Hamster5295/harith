# UIntPipelinedPrefixAdd

流水化并行前缀加法器。

所选 [[PrefixStyle]] 的前缀层级被分布到 `stages` 层寄存器上。吞吐率为每周期一次加法，延迟等于 `stages`，无论某层用于前缀逻辑还是仅用于重定时。取值为 0 时加法器为组合逻辑，等价于 [[UIntPrefixAdd]]。

::: info 来源
`src/main/scala/uint/add/UIntPipelinedPrefixAdd.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `style` | `PrefixStyle` | 并行前缀网络风格 |
| `stages` | `Int` | 流水寄存器层数，也即延迟 |

**延迟** = `stages` 周期

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
| `32bit-2cyc` | FPGA | 2.833 | 1.716 | 1.479 | 168 LUTs + 192 FF |
| `32bit-2cyc` | 55 nm | 0.3475 | 0.8007 | 0.8007 | 1,847.72 µm² |
