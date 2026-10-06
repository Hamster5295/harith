# FpMul

5 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpArrayMul](./FpArrayMul.md) | `fp32` | 33.146 ns | 2,341 LUTs + 0 FF | 12.6329 ns | 11,450.88 µm² |
| [FpBoothMul](./FpBoothMul.md) | `fp32` | 27.629 ns | 2,370 LUTs + 0 FF | 11.8575 ns | 12,906.60 µm² |
| [FpMacroMul](./FpMacroMul.md) | `fp32` | 24.908 ns | 984 LUTs + 0 FF | 9.4441 ns | 10,452.12 µm² |
| [FpPipelinedMul](./FpPipelinedMul.md) | `fp32-2cyc` | 22.210 ns | 1,889 LUTs + 203 FF | 8.0414 ns | 9,518.32 µm² |
| [FpTreeMul](./FpTreeMul.md) | `fp32` | 26.628 ns | 1,864 LUTs + 0 FF | 11.0121 ns | 8,316.00 µm² |

## 模块

- **[FpArrayMul](./FpArrayMul.md)** —— 采用进位保存阵列有效数乘法器的浮点乘法器，成本最低选项。
- **[FpBoothMul](./FpBoothMul.md)** —— 采用改进 Booth radix-4 树有效数乘法器的浮点乘法器。
- **[FpMacroMul](./FpMacroMul.md)** —— 采用推断有效数乘法器的浮点乘法器。
- **[FpPipelinedMul](./FpPipelinedMul.md)** —— 流水化浮点乘法器。
- **[FpTreeMul](./FpTreeMul.md)** —— 采用 AND 部分积进位保存树有效数乘法器的浮点乘法器。
