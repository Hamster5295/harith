# FpCarrySelectAdd

采用块进位选择对齐加法器的浮点加法器。

::: info 来源
`src/main/scala/fp/add/FpCarrySelectAdd.scala`
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
| `fp64` | FPGA | 57.265 | 57.845 | 57.265 | 8,943 LUTs + 0 FF |
| `fp64` | 55 nm | 23.2826 | 23.2826 | 23.2826 | 12,961.20 µm² |
| `fp32` | FPGA | 45.991 | 46.571 | 45.991 | 4,226 LUTs + 0 FF |
| `fp32` | 55 nm | 14.6117 | 14.6117 | 14.6117 | 5,542.88 µm² |
| `fp16` | FPGA | 40.538 | 41.232 | 40.538 | 2,160 LUTs + 0 FF |
| `fp16` | 55 nm | 9.1653 | 9.1653 | 9.1653 | 3,114.44 µm² |
| `bf16` | FPGA | 39.206 | 40.065 | 39.206 | 1,983 LUTs + 0 FF |
| `bf16` | 55 nm | 8.6955 | 8.6955 | 8.6955 | 2,964.64 µm² |
