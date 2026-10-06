# FpRippleFma

采用行波进位对齐加法器的浮点融合乘加器。

::: info 来源
`src/main/scala/fp/fma/FpRippleFma.scala`
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
| `fp64` | FPGA | 91.884 | 92.864 | 91.884 | 11,240 LUTs + 0 FF |
| `fp64` | 55 nm | 64.2359 | 64.2359 | 64.2329 | 72,953.16 µm² |
| `fp32` | FPGA | 63.167 | 63.738 | 63.167 | 5,256 LUTs + 0 FF |
| `fp32` | 55 nm | 41.1512 | 41.1512 | 41.1512 | 28,605.08 µm² |
| `fp16` | FPGA | 49.255 | 50.235 | 49.255 | 2,535 LUTs + 0 FF |
| `fp16` | 55 nm | 23.2811 | 23.2811 | 23.2811 | 10,918.32 µm² |
| `bf16` | FPGA | 46.132 | 47.104 | 46.132 | 2,345 LUTs + 0 FF |
| `bf16` | 55 nm | 13.2128 | 13.2128 | 13.2128 | 5,802.16 µm² |
