# FpRippleAdd

采用行波进位对齐加法器的浮点加法器，成本最低选项。

::: info 来源
`src/main/scala/fp/add/FpRippleAdd.scala`
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
| `fp64` | FPGA | 72.702 | 73.274 | 72.702 | 8,591 LUTs + 0 FF |
| `fp64` | 55 nm | 31.3539 | 31.3539 | 31.3539 | 13,508.04 µm² |
| `fp32` | FPGA | 51.462 | 52.042 | 51.462 | 4,107 LUTs + 0 FF |
| `fp32` | 55 nm | 14.2377 | 14.2377 | 14.2377 | 5,554.36 µm² |
| `fp16` | FPGA | 42.856 | 43.550 | 42.856 | 2,088 LUTs + 0 FF |
| `fp16` | 55 nm | 9.4318 | 9.4318 | 9.4318 | 3,026.52 µm² |
| `bf16` | FPGA | 40.678 | 41.537 | 40.678 | 1,908 LUTs + 0 FF |
| `bf16` | 55 nm | 9.5703 | 9.5703 | 9.5196 | 3,258.92 µm² |
