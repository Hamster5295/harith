# FpPipelinedNewtonReciprocal

固定深度流水的 Newton-Raphson 倒数。

将组合 Newton-Raphson 链在每个乘法器和每个结果级之后切开，因此每周期可接收一个新操作数。初值、迭代与最终舍入与 `FpNewtonReciprocal` 相同；结果为忠实舍入（误差在一个 ulp 以内）。

::: info 源代码
`src/main/scala/fp/reciprocal/FpPipelinedNewtonReciprocal.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `mul` | `Int => UIntMul` | 组合无符号乘法器工厂 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `2 + 2 * iters` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `in` | 输入 | `Decoupled(FpUnaryReq)` | 解耦请求通道 |
| `in.valid` | 输入 | `Bool` | 请求有效 |
| `in.ready` | 输出 | `Bool` | 请求被接受 |
| `in.bits.src` | 输入 | `UInt(inFmt.width.W)` | 操作数 |
| `in.bits.rm` | 输入 | `UInt(3.W)` | 舍入模式 |
| `out` | 输出 | `Valid(FpUnaryResp)` | 有效响应通道 |
| `out.valid` | 输出 | `Bool` | 响应有效 |
| `out.bits.output` | 输出 | `UInt(outFmt.width.W)` | 结果 |
| `out.bits.fflags` | 输出 | `FpFlags` | IEEE-754 标志 |
| `flush` | 输入 | `Bool` | 中止进行中的运算 |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `fp64-8cyc` | FPGA | 7.502 | 0.912 | 24.383 | 2,744 LUTs + 545 FF |
| `fp64-8cyc` | 55 nm | 4.1115 | 0.4508 | 7.9334 | 189,950.04 µm² |
| `fp32-6cyc` | FPGA | 6.689 | 0.912 | 20.763 | 1,168 LUTs + 148 FF |
| `fp32-6cyc` | 55 nm | 3.3681 | 0.4508 | 5.5111 | 37,731.12 µm² |
| `fp16-6cyc` | FPGA | 5.365 | 0.912 | 17.044 | 750 LUTs + 156 FF |
| `fp16-6cyc` | 55 nm | 3.1719 | 0.4508 | 4.2826 | 15,510.32 µm² |
| `bf16-6cyc` | FPGA | 4.588 | 0.912 | 16.527 | 526 LUTs + 54 FF |
| `bf16-6cyc` | 55 nm | 1.7911 | 0.4508 | 4.1308 | 11,165.56 µm² |
