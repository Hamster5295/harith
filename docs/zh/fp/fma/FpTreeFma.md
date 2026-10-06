# FpTreeFma

采用 AND 部分积树有效数乘法器的浮点融合乘加器。

::: info 来源
`src/main/scala/fp/fma/FpTreeFma.scala`
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
| `fp64` | FPGA | 68.247 | 68.819 | 68.247 | 14,942 LUTs + 0 FF |
| `fp64` | 55 nm | 74.7898 | 74.7898 | 74.7898 | 72,748.76 µm² |
| `fp32` | FPGA | 51.426 | 52.006 | 51.426 | 6,007 LUTs + 0 FF |
| `fp32` | 55 nm | 41.0896 | 41.0896 | 41.0896 | 26,301.24 µm² |
| `fp16` | FPGA | 45.734 | 46.723 | 45.734 | 2,622 LUTs + 0 FF |
| `fp16` | 55 nm | 20.8538 | 20.8538 | 20.8538 | 9,047.36 µm² |
| `bf16` | FPGA | 43.161 | 44.035 | 43.161 | 2,332 LUTs + 0 FF |
| `bf16` | 55 nm | 14.0390 | 14.0390 | 14.0390 | 6,468.28 µm² |
