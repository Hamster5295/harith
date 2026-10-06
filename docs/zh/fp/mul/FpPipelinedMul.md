# FpPipelinedMul

流水化浮点乘法器。

有效数乘积由流水化的 [[harith.uint.UIntPipelinedArrayMul]] 计算，其寄存器层数为 `stages`；浮点控制被延迟相同的周期数，使舍入与特殊值处理与乘积对齐。`stages = 0` 使乘法器变为组合逻辑，等价于 [[FpArrayMul]]。按照 RISC-V，NaN 为规范 NaN。

注意：只有有效数乘法器由底层 [[harith.uint.UIntPipelinedArrayMul]] 在内部流水化；浮点控制是一个与乘法器延迟匹配的普通寄存器队列，其后的舍入/特殊值逻辑保持组合逻辑，因此该部分的实际深度取决于 EDA 重定时。

::: info 来源
`src/main/scala/fp/mul/FpPipelinedMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `aFmt` | `FpFormat` | 第一个操作数的格式 |
| `bFmt` | `FpFormat` | 第二个操作数的格式 |
| `outFmt` | `FpFormat` | 结果的格式 |
| `stages` | `Int` | 流水寄存器层数，也即延迟 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `stages` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `UInt(aFmt.width.W)` | 第一个操作数 |
| `src2` | 输入 | `UInt(bFmt.width.W)` | 第二个操作数 |
| `rm` | 输入 | `UInt(3.W)` | RISC-V 舍入模式 |
| `output` | 输出 | `UInt(outFmt.width.W)` | 舍入后的结果 |
| `fflags` | 输出 | `FpFlags` | IEEE-754 异常标志 |
| `fflags.nx` | 输出 | `Bool` | 不精确 |
| `fflags.uf` | 输出 | `Bool` | 下溢 |
| `fflags.of` | 输出 | `Bool` | 上溢 |
| `fflags.dz` | 输出 | `Bool` | 除零 |
| `fflags.nv` | 输出 | `Bool` | 非法操作 |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `fp64-2cyc` | FPGA | 12.426 | 31.548 | 30.950 | 8,628 LUTs + 433 FF |
| `fp64-2cyc` | 55 nm | 4.8551 | 12.8657 | 12.8657 | 35,796.32 µm² |
| `fp32-2cyc` | FPGA | 6.057 | 22.781 | 22.210 | 1,889 LUTs + 203 FF |
| `fp32-2cyc` | 55 nm | 2.0848 | 8.0414 | 8.0414 | 9,518.32 µm² |
| `fp16-2cyc` | FPGA | 3.156 | 19.728 | 18.748 | 644 LUTs + 106 FF |
| `fp16-2cyc` | 55 nm | 1.0090 | 5.5721 | 5.5721 | 3,038.00 µm² |
| `bf16-2cyc` | FPGA | 3.406 | 18.215 | 17.246 | 483 LUTs + 91 FF |
| `bf16-2cyc` | 55 nm | 0.9860 | 5.4115 | 5.4115 | 2,340.80 µm² |
