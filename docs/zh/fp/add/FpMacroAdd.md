# FpMacroAdd

采用推断对齐加法器的浮点加法器。

::: info 源代码
`src/main/scala/fp/add/FpMacroAdd.scala`
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
| `fp64` | FPGA | 48.521 | 49.502 | 48.521 | 8,433 LUTs + 0 FF |
| `fp64` | 55 nm | 30.5496 | 30.5496 | 30.5496 | 15,172.64 µm² |
| `fp32` | FPGA | 40.688 | 41.268 | 40.688 | 3,925 LUTs + 0 FF |
| `fp32` | 55 nm | 14.5881 | 14.5881 | 14.5881 | 5,899.04 µm² |
| `fp16` | FPGA | 38.144 | 38.716 | 38.144 | 1,984 LUTs + 0 FF |
| `fp16` | 55 nm | 9.1341 | 9.1341 | 9.1341 | 3,145.80 µm² |
| `bf16` | FPGA | 37.046 | 37.610 | 37.046 | 1,814 LUTs + 0 FF |
| `bf16` | 55 nm | 10.0211 | 10.0211 | 10.0211 | 3,217.76 µm² |
