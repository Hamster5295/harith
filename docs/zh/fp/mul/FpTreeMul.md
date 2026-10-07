# FpTreeMul

采用 AND 部分积进位保存树有效数乘法器的浮点乘法器。

::: info 源代码
`src/main/scala/fp/mul/FpTreeMul.scala`
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
| `fp64` | FPGA | 39.064 | 39.462 | 39.064 | 6,939 LUTs + 0 FF |
| `fp64` | 55 nm | 19.0265 | 19.0265 | 19.0265 | 36,041.60 µm² |
| `fp32` | FPGA | 26.628 | 27.219 | 26.628 | 1,864 LUTs + 0 FF |
| `fp32` | 55 nm | 11.0121 | 11.0121 | 11.0121 | 8,316.00 µm² |
| `fp16` | FPGA | 23.476 | 24.072 | 23.476 | 715 LUTs + 0 FF |
| `fp16` | 55 nm | 7.0437 | 7.0437 | 7.0437 | 2,772.56 µm² |
| `bf16` | FPGA | 22.655 | 23.227 | 22.655 | 523 LUTs + 0 FF |
| `bf16` | 55 nm | 6.1560 | 6.1560 | 6.1560 | 2,072.84 µm² |
