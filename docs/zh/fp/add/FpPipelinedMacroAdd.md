# FpPipelinedMacroAdd

采用推断对齐加法器的流水化浮点加法器。

对齐加法器本身没有内部寄存器层，因此 `stages` 寄存器队列延迟操作数与控制，而加法器与舍入尾部保持组合逻辑；该尾部的实际深度取决于 EDA 重定时。按照 RISC-V，NaN 为规范 NaN。

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
| `fp64-2cyc` | FPGA | 24.251 | 29.933 | 29.362 | 8,609 LUTs + 710 FF |
| `fp64-2cyc` | 55 nm | 13.3667 | 31.3051 | 31.3051 | 30,299.92 µm² |
| `fp32-2cyc` | FPGA | 18.449 | 25.795 | 25.203 | 4,120 LUTs + 383 FF |
| `fp32-2cyc` | 55 nm | 7.4952 | 31.2895 | 31.2895 | 15,043.56 µm² |
| `fp16-2cyc` | FPGA | 16.316 | 22.367 | 21.796 | 1,984 LUTs + 215 FF |
| `fp16-2cyc` | 55 nm | 4.4135 | 15.5903 | 15.5903 | 8,524.04 µm² |
| `bf16-2cyc` | FPGA | 15.710 | 21.870 | 21.299 | 1,772 LUTs + 221 FF |
| `bf16-2cyc` | 55 nm | 4.8244 | 15.2796 | 15.2796 | 7,106.40 µm² |
