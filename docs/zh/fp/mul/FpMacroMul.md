# FpMacroMul

采用推断有效数乘法器的浮点乘法器。

::: info 来源
`src/main/scala/fp/mul/FpMacroMul.scala`
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
| `fp64` | FPGA | 32.618 | 33.399 | 32.618 | 2,379 LUTs + 0 FF |
| `fp64` | 55 nm | 17.2452 | 17.2452 | 17.2452 | 39,994.92 µm² |
| `fp32` | FPGA | 24.908 | 25.495 | 24.908 | 984 LUTs + 0 FF |
| `fp32` | 55 nm | 9.4441 | 9.4441 | 9.4441 | 10,452.12 µm² |
| `fp16` | FPGA | 23.001 | 23.596 | 23.001 | 490 LUTs + 0 FF |
| `fp16` | 55 nm | 6.4737 | 6.4737 | 6.4737 | 2,972.48 µm² |
| `bf16` | FPGA | 20.551 | 21.123 | 20.551 | 461 LUTs + 0 FF |
| `bf16` | 55 nm | 6.4107 | 6.4107 | 6.4107 | 1,971.48 µm² |
