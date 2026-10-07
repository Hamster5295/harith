# FpBoothFma

采用改进 Booth radix-4 树有效数乘法器的浮点融合乘加器。

::: info 源代码
`src/main/scala/fp/fma/FpBoothFma.scala`
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
| `fp64` | FPGA | 65.193 | 65.765 | 65.193 | 16,250 LUTs + 0 FF |
| `fp64` | 55 nm | 52.9643 | 52.9643 | 52.9643 | 75,525.24 µm² |
| `fp32` | FPGA | 52.331 | 52.911 | 52.331 | 6,531 LUTs + 0 FF |
| `fp32` | 55 nm | 37.0841 | 37.0841 | 37.0841 | 30,194.92 µm² |
| `fp16` | FPGA | 45.982 | 46.971 | 45.982 | 2,768 LUTs + 0 FF |
| `fp16` | 55 nm | 24.4914 | 24.4914 | 24.4914 | 11,494.00 µm² |
| `bf16` | FPGA | 43.557 | 44.431 | 43.557 | 2,364 LUTs + 0 FF |
| `bf16` | 55 nm | 25.7625 | 25.7625 | 25.7625 | 9,553.32 µm² |
