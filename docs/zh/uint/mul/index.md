# UIntMul

6 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [UIntArrayMul](./UIntArrayMul.md) | `32bit` | 20.305 ns | 2,589 LUTs + 0 FF | 7.9127 ns | 16,088.52 µm² |
| [UIntBoothMul](./UIntBoothMul.md) | `32bit` | 10.234 ns | 2,269 LUTs + 0 FF | 5.3502 ns | 16,288.16 µm² |
| [UIntMacroMul](./UIntMacroMul.md) | `32bit` | 7.721 ns | 47 LUTs + 0 FF | 3.2678 ns | 16,313.36 µm² |
| [UIntPipelinedArrayMul](./UIntPipelinedArrayMul.md) | `32bit-2cyc` | 7.545 ns | 2,124 LUTs + 220 FF | 2.6341 ns | 18,446.40 µm² |
| [UIntPipelinedTreeMul](./UIntPipelinedTreeMul.md) | `32bit-2cyc` | 4.947 ns | 1,597 LUTs + 207 FF | 2.6226 ns | 11,401.88 µm² |
| [UIntTreeMul](./UIntTreeMul.md) | `32bit` | 10.316 ns | 1,616 LUTs + 0 FF | 4.8125 ns | 10,564.68 µm² |

## 模块

- **[UIntArrayMul](./UIntArrayMul.md)** —— 结构化进位保存阵列乘法器。
- **[UIntBoothMul](./UIntBoothMul.md)** —— 改进 Booth radix-4 进位保存树乘法器。
- **[UIntMacroMul](./UIntMacroMul.md)** —— 用 `*` 运算符实现的无符号乘法器。
- **[UIntPipelinedArrayMul](./UIntPipelinedArrayMul.md)** —— 流水化进位保存阵列乘法器。
- **[UIntPipelinedTreeMul](./UIntPipelinedTreeMul.md)** —— 使用 AND 部分积的流水化进位保存树乘法器。
- **[UIntTreeMul](./UIntTreeMul.md)** —— 使用 AND 部分积的进位保存树乘法器。
