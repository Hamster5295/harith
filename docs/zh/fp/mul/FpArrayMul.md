# FpArrayMul

采用进位保存阵列有效数乘法器的浮点乘法器，成本最低选项。

::: info 源代码
`src/main/scala/fp/mul/FpArrayMul.scala`
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
| `fp64` | FPGA | 57.435 | 57.833 | 57.435 | 10,088 LUTs + 0 FF |
| `fp64` | 55 nm | 25.1068 | 25.1068 | 25.1068 | 54,372.08 µm² |
| `fp32` | FPGA | 33.146 | 33.737 | 33.146 | 2,341 LUTs + 0 FF |
| `fp32` | 55 nm | 12.6329 | 12.6329 | 12.6329 | 11,450.88 µm² |
| `fp16` | FPGA | 26.467 | 27.055 | 26.467 | 736 LUTs + 0 FF |
| `fp16` | 55 nm | 6.8266 | 6.8266 | 6.8266 | 2,887.08 µm² |
| `bf16` | FPGA | 22.485 | 23.057 | 22.485 | 465 LUTs + 0 FF |
| `bf16` | 55 nm | 6.5880 | 6.5880 | 6.5880 | 1,956.64 µm² |
