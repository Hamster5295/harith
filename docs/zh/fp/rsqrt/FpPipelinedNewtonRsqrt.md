# FpPipelinedNewtonRsqrt

固定深度流水的 Newton-Raphson 倒数开方。

将组合 Newton-Raphson 链在每个乘法器和每个结果级之后切开，因此每周期可接收一个新操作数。初值、迭代与最终舍入与 `FpNewtonRsqrt` 相同；结果为忠实舍入（误差在一个 ulp 以内）。

::: info 源代码
`src/main/scala/fp/rsqrt/FpPipelinedNewtonRsqrt.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `mul` | `Int => UIntMul` | 组合无符号乘法器工厂 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `2 + 3 * iters` 周期

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
| `fp64-11cyc` | FPGA | 8.445 | 0.912 | 25.336 | 3,377 LUTs + 626 FF |
| `fp64-11cyc` | 55 nm | 6.1450 | 0.4508 | 9.0292 | 268,114.00 µm² |
| `fp32-11cyc` | FPGA | 7.050 | 0.912 | 21.524 | 1,531 LUTs + 271 FF |
| `fp32-11cyc` | 55 nm | 4.1663 | 0.4508 | 5.8993 | 85,531.04 µm² |
| `fp16-8cyc` | FPGA | 5.260 | 0.912 | 16.899 | 981 LUTs + 207 FF |
| `fp16-8cyc` | 55 nm | 2.9238 | 0.4508 | 4.4865 | 22,833.44 µm² |
| `bf16-8cyc` | FPGA | 6.139 | 0.912 | 16.252 | 567 LUTs + 57 FF |
| `bf16-8cyc` | 55 nm | 2.5377 | 0.4508 | 4.1432 | 16,423.68 µm² |
