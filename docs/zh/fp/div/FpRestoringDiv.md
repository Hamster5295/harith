# FpRestoringDiv

迭代式恢复余数浮点除法器。

有效数被规格化到 `NW` 位，商每周期生成一位，当减法下溢时恢复余数。商带有 `manWidth + 3` 个额外小数位以及来自非零余数的粘滞位，因此最终舍入是精确的。按照 RISC-V，NaN 为规范 NaN。

::: info 来源
`src/main/scala/fp/div/FpRestoringDiv.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `aFmt` | `FpFormat` | 被除数的格式 |
| `bFmt` | `FpFormat` | 除数的格式 |
| `outFmt` | `FpFormat` | 结果的格式 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `max(aFmt.manWidth, bFmt.manWidth, outFmt.manWidth) + outFmt.manWidth + 4` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `in` | 输入 | `Decoupled(FpDivReq)` | 解耦请求通道 |
| `in.valid` | 输入 | `Bool` | 请求有效 |
| `in.ready` | 输出 | `Bool` | 请求被接受 |
| `in.bits.src1` | 输入 | `UInt(aFmt.width.W)` | 被除数 |
| `in.bits.src2` | 输入 | `UInt(bFmt.width.W)` | 除数 |
| `in.bits.rm` | 输入 | `UInt(3.W)` | RISC-V 舍入模式 |
| `out` | 输出 | `Valid(FpDivResp)` | 有效响应通道 |
| `out.valid` | 输出 | `Bool` | 响应有效 |
| `out.bits.output` | 输出 | `UInt(outFmt.width.W)` | 舍入后的商 |
| `out.bits.fflags` | 输出 | `FpFlags` | IEEE-754 异常标志 |
| `flush` | 输入 | `Bool` | 中止进行中的除法 |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `fp64-108cyc` | FPGA | 5.568 | 1.439 | 22.187 | 2,846 LUTs + 365 FF |
| `fp64-108cyc` | 55 nm | 3.4405 | 0.8743 | 10.1610 | 11,796.96 µm² |
| `fp32-50cyc` | FPGA | 5.535 | 1.414 | 18.934 | 1,232 LUTs + 184 FF |
| `fp32-50cyc` | 55 nm | 2.3681 | 0.8798 | 6.5751 | 5,540.08 µm² |
| `fp16-24cyc` | FPGA | 4.432 | 1.511 | 17.510 | 586 LUTs + 99 FF |
| `fp16-24cyc` | 55 nm | 2.6870 | 0.8858 | 4.7179 | 2,786.56 µm² |
| `bf16-18cyc` | FPGA | 4.203 | 1.489 | 16.685 | 477 LUTs + 87 FF |
| `bf16-18cyc` | 55 nm | 1.9531 | 0.8800 | 4.4104 | 2,530.08 µm² |
