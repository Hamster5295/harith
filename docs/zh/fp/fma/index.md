# FpFma

7 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpArrayFma](./FpArrayFma.md) | `fp32` | 57.147 ns | 6,559 LUTs + 0 FF | 34.1085 ns | 25,570.16 µm² |
| [FpBoothFma](./FpBoothFma.md) | `fp32` | 52.331 ns | 6,531 LUTs + 0 FF | 37.0841 ns | 30,194.92 µm² |
| [FpMacroFma](./FpMacroFma.md) | `fp32` | 49.853 ns | 5,107 LUTs + 0 FF | 36.9288 ns | 28,061.32 µm² |
| [FpPipelinedFma](./FpPipelinedFma.md) | `fp32-2cyc` | 46.992 ns | 6,038 LUTs + 327 FF | 21.9171 ns | 17,519.04 µm² |
| [FpPrefixFma](./FpPrefixFma.md) | `fp32` | 51.516 ns | 6,090 LUTs + 0 FF | 35.9554 ns | 24,540.60 µm² |
| [FpRippleFma](./FpRippleFma.md) | `fp32` | 63.167 ns | 5,256 LUTs + 0 FF | 41.1512 ns | 28,605.08 µm² |
| [FpTreeFma](./FpTreeFma.md) | `fp32` | 51.426 ns | 6,007 LUTs + 0 FF | 41.0896 ns | 26,301.24 µm² |

## 模块

- **[FpArrayFma](./FpArrayFma.md)** —— 采用阵列有效数乘法器的浮点融合乘加器。
- **[FpBoothFma](./FpBoothFma.md)** —— 采用改进 Booth radix-4 树有效数乘法器的浮点融合乘加器。
- **[FpMacroFma](./FpMacroFma.md)** —— 采用推断有效数与对齐数据通路的浮点融合乘加器。
- **[FpPipelinedFma](./FpPipelinedFma.md)** —— 流水化浮点融合乘加器。
- **[FpPrefixFma](./FpPrefixFma.md)** —— 采用并行前缀对齐加法器的浮点融合乘加器。
- **[FpRippleFma](./FpRippleFma.md)** —— 采用行波进位对齐加法器的浮点融合乘加器。
- **[FpTreeFma](./FpTreeFma.md)** —— 采用 AND 部分积树有效数乘法器的浮点融合乘加器。
