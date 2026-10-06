# FpCarryLookaheadAdd

采用分层先行进位对齐加法器的浮点加法器。

::: info 来源
`src/main/scala/fp/add/FpCarryLookaheadAdd.scala`
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
| `fp64` | FPGA | 50.264 | 50.836 | 50.264 | 8,835 LUTs + 0 FF |
| `fp64` | 55 nm | 26.5524 | 26.5524 | 26.5524 | 14,996.80 µm² |
| `fp32` | FPGA | 42.789 | 43.369 | 42.789 | 4,312 LUTs + 0 FF |
| `fp32` | 55 nm | 13.6956 | 13.6956 | 13.6956 | 6,375.88 µm² |
| `fp16` | FPGA | 39.533 | 40.227 | 39.533 | 2,195 LUTs + 0 FF |
| `fp16` | 55 nm | 9.6391 | 9.6391 | 9.6391 | 3,378.48 µm² |
| `bf16` | FPGA | 38.701 | 39.560 | 38.701 | 2,064 LUTs + 0 FF |
| `bf16` | 55 nm | 9.9088 | 9.9088 | 9.9088 | 3,227.56 µm² |
