# FpPrefixFma

采用并行前缀对齐加法器的浮点融合乘加器。

::: info 来源
`src/main/scala/fp/fma/FpPrefixFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `aFmt` | `FpFormat` | 被乘数的格式 |
| `bFmt` | `FpFormat` | 乘数的格式 |
| `cFmt` | `FpFormat` | 加数的格式 |
| `outFmt` | `FpFormat` | 结果的格式 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = 0 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `UInt(aFmt.width.W)` | 被乘数 |
| `src2` | 输入 | `UInt(bFmt.width.W)` | 乘数 |
| `add` | 输入 | `UInt(cFmt.width.W)` | 加数 |
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
| `fp64` | FPGA | 65.041 | 66.021 | 65.041 | 12,901 LUTs + 0 FF |
| `fp64` | 55 nm | 85.5805 | 85.5805 | 85.5805 | 76,243.72 µm² |
| `fp32` | FPGA | 51.516 | 52.095 | 51.516 | 6,090 LUTs + 0 FF |
| `fp32` | 55 nm | 35.9554 | 35.9554 | 35.9554 | 24,540.60 µm² |
| `fp16` | FPGA | 45.782 | 46.762 | 45.782 | 2,939 LUTs + 0 FF |
| `fp16` | 55 nm | 25.5184 | 25.5184 | 25.5184 | 11,367.44 µm² |
| `bf16` | FPGA | 42.660 | 43.632 | 42.660 | 2,673 LUTs + 0 FF |
| `bf16` | 55 nm | 13.7281 | 13.7281 | 13.7281 | 6,730.08 µm² |
