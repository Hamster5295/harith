# FpPipelinedPrefixAdd

采用并行前缀对齐加法器的流水化浮点加法器。

所选 [`harith.uint.PrefixStyle`](/zh/uint/misc/PrefixStyle) 的前缀层级被分布到 `stages` 层寄存器上，而浮点控制是一个与加法器延迟匹配的寄存器队列。`stages = 0` 使加法器变为组合逻辑。按照 RISC-V，NaN 为规范 NaN。

::: info 源代码
`src/main/scala/fp/add/FpPipelinedAdd.scala`
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
| `fp64-2cyc` | FPGA | 23.093 | 29.830 | 29.259 | 9,805 LUTs + 1,232 FF |
| `fp64-2cyc` | 55 nm | 13.2229 | 33.5948 | 33.5948 | 34,074.88 µm² |
| `fp32-2cyc` | FPGA | 19.973 | 25.614 | 25.022 | 4,952 LUTs + 627 FF |
| `fp32-2cyc` | 55 nm | 8.5569 | 21.8746 | 21.8746 | 17,788.96 µm² |
| `fp16-2cyc` | FPGA | 17.190 | 22.361 | 21.790 | 2,295 LUTs + 334 FF |
| `fp16-2cyc` | 55 nm | 4.5925 | 17.5413 | 17.5413 | 9,081.52 µm² |
| `bf16-2cyc` | FPGA | 16.462 | 21.870 | 21.299 | 2,029 LUTs + 331 FF |
| `bf16-2cyc` | 55 nm | 5.5078 | 15.6008 | 15.6008 | 7,902.72 µm² |
