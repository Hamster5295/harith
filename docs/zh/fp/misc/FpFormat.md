# FpFormat

IEEE-754 二进制浮点格式。它是每个 `fp` 模块的操作数与结果类型，因此混合精度运算无需额外代码。

一个值具有 `1 + expWidth + manWidth` 位。指数偏置采用 IEEE 二进制交换格式的默认值 `2^(expWidth - 1) - 1`，它是派生得到而非存储的。

::: info 源代码
`src/main/scala/fp/FpFormat.scala`
:::

## 构造

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `expWidth` | `Int` | 指数位数（至少 2） |
| `manWidth` | `Int` | 显式存储的尾数位数（至少 1） |

## 预定义格式

| 名称 | `expWidth` | `manWidth` | 位宽 |
| --- | --- | --- | --- |
| `Fp64` | 11 | 52 | 64 |
| `Fp32` | 8 | 23 | 32 |
| `Tf32` | 8 | 10 | 19 |
| `Fp16` | 5 | 10 | 16 |
| `Bf16` | 8 | 7 | 16 |

## 派生成员

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 总位数，`1 + expWidth + manWidth` |
| `expMask` / `manMask` | `BigInt` | 全 1 的指数 / 尾数字段 |
| `signBit` | `Int` | 符号位的索引 |
| `bias` | `BigInt` | 指数偏置 `2^(expWidth - 1) - 1` |
| `maxExp` / `minExp` | `BigInt` | 最大 / 最小无偏规格化指数 |
| `minSubExp` | `BigInt` | 最小无偏非规格化指数 |
| `zero(sign)` | `UInt` | 带符号的零位模式 |
| `infinity` / `infinityMag` | `UInt` | 无穷位模式 / 无符号无穷幅值 |
| `canonicalNaN` | `UInt` | RISC-V 规定的规范静默 NaN 位模式 |
| `maxFinite` / `maxFiniteMag` | `UInt` | 最大有限幅值（带正号 / 仅幅值） |
