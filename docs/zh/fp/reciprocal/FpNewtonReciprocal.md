# FpNewtonReciprocal

带分段常数初值的 Newton-Raphson 倒数。

初值来自以高位尾数为索引的查找表；定点 Newton-Raphson 迭代平方收敛到 `1 / 有效数`。结果为忠实舍入（误差在一个 ulp 以内），不是正确舍入。

::: info 源代码
`src/main/scala/fp/reciprocal/FpNewtonReciprocal.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `mul` | `Int => UIntMul` | 组合无符号乘法器工厂 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = 1 周期

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
| `fp64` | FPGA | 90.421 | 0.912 | 1.017 | 2,734 LUTs + 68 FF |
| `fp64` | 55 nm | 46.9906 | 0.4560 | 46.9906 | 215,430.88 µm² |
| `fp32` | FPGA | 53.401 | 0.912 | 1.017 | 1,109 LUTs + 36 FF |
| `fp32` | 55 nm | 24.2199 | 0.4560 | 24.2199 | 41,807.08 µm² |
| `fp16` | FPGA | 44.718 | 0.912 | 1.017 | 722 LUTs + 20 FF |
| `fp16` | 55 nm | 17.1130 | 0.4560 | 17.1130 | 16,264.36 µm² |
| `bf16` | FPGA | 37.960 | 0.912 | 1.017 | 454 LUTs + 20 FF |
| `bf16` | 55 nm | 7.5151 | 0.4560 | 7.5151 | 4,809.00 µm² |
