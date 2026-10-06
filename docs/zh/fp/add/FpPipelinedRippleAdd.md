# FpPipelinedRippleAdd

采用流水化行波进位对齐加法器的流水化浮点加法器。

进位链被切成 `stages` 个块，因此延迟就是块的数量。浮点控制是一个与加法器延迟匹配的寄存器队列。按照 RISC-V，NaN 为规范 NaN。

::: info 来源
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
| `fp64-2cyc` | FPGA | 32.972 | 31.277 | 30.300 | 8,568 LUTs + 800 FF |
| `fp64-2cyc` | 55 nm | 12.5026 | 32.6793 | 32.6793 | 29,576.12 µm² |
| `fp32-2cyc` | FPGA | 22.847 | 27.023 | 26.054 | 4,115 LUTs + 426 FF |
| `fp32-2cyc` | 55 nm | 7.7297 | 22.5578 | 22.5578 | 15,058.68 µm² |
| `fp16-2cyc` | FPGA | 19.377 | 23.726 | 22.760 | 1,983 LUTs + 236 FF |
| `fp16-2cyc` | 55 nm | 4.8011 | 14.7619 | 14.7619 | 7,900.48 µm² |
| `bf16-2cyc` | FPGA | 17.751 | 23.085 | 22.514 | 1,790 LUTs + 240 FF |
| `bf16-2cyc` | 55 nm | 5.0970 | 15.8745 | 15.8745 | 7,479.92 µm² |
