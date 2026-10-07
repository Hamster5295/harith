# FpBoothMul

采用改进 Booth radix-4 树有效数乘法器的浮点乘法器。

::: info 源代码
`src/main/scala/fp/mul/FpBoothMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `aFmt` | `FpFormat` | 第一个操作数的格式 |
| `bFmt` | `FpFormat` | 第二个操作数的格式 |
| `outFmt` | `FpFormat` | 结果的格式 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = 0 周期

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
| `fp64` | FPGA | 36.213 | 36.611 | 36.213 | 8,701 LUTs + 0 FF |
| `fp64` | 55 nm | 17.5670 | 17.5670 | 17.5670 | 44,994.32 µm² |
| `fp32` | FPGA | 27.629 | 28.220 | 27.629 | 2,370 LUTs + 0 FF |
| `fp32` | 55 nm | 11.8575 | 11.8575 | 11.8575 | 12,906.60 µm² |
| `fp16` | FPGA | 26.835 | 27.233 | 26.835 | 858 LUTs + 0 FF |
| `fp16` | 55 nm | 7.0969 | 7.0969 | 7.0969 | 3,496.36 µm² |
| `bf16` | FPGA | 22.525 | 23.097 | 22.525 | 549 LUTs + 0 FF |
| `bf16` | 55 nm | 6.2379 | 6.2379 | 6.2379 | 2,506.56 µm² |
