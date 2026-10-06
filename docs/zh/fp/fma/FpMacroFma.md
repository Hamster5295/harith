# FpMacroFma

采用推断有效数与对齐数据通路的浮点融合乘加器。

::: info 来源
`src/main/scala/fp/fma/FpMacroFma.scala`
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
| `fp64` | FPGA | 64.329 | 65.309 | 64.329 | 11,083 LUTs + 0 FF |
| `fp64` | 55 nm | 76.5721 | 76.5721 | 76.4466 | 72,919.28 µm² |
| `fp32` | FPGA | 49.853 | 50.416 | 49.853 | 5,107 LUTs + 0 FF |
| `fp32` | 55 nm | 36.9288 | 36.9288 | 36.9288 | 28,061.32 µm² |
| `fp16` | FPGA | 43.305 | 43.884 | 43.305 | 2,408 LUTs + 0 FF |
| `fp16` | 55 nm | 15.1467 | 15.1467 | 15.1467 | 7,418.32 µm² |
| `bf16` | FPGA | 40.720 | 41.292 | 40.720 | 2,213 LUTs + 0 FF |
| `bf16` | 55 nm | 15.6026 | 15.6026 | 15.6026 | 6,058.92 µm² |
