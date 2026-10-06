# UIntFma

6 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [UIntBoothFma](./UIntBoothFma.md) | `32bit` | 10.731 ns | 2,642 LUTs + 0 FF | 5.5558 ns | 18,806.76 µm² |
| [UIntComposedFma](./UIntComposedFma.md) | `32bit` | 12.913 ns | 1,990 LUTs + 0 FF | 5.3590 ns | 11,958.24 µm² |
| [UIntMacroFma](./UIntMacroFma.md) | `32bit` | 8.933 ns | 111 LUTs + 0 FF | 3.2484 ns | 17,201.52 µm² |
| [UIntPipelinedBoothFma](./UIntPipelinedBoothFma.md) | `32bit-2cyc` | 3.718 ns | 2,441 LUTs + 454 FF | 3.4447 ns | 18,331.88 µm² |
| [UIntPipelinedTreeFma](./UIntPipelinedTreeFma.md) | `32bit-2cyc` | 4.947 ns | 1,683 LUTs + 211 FF | 2.5375 ns | 12,053.44 µm² |
| [UIntTreeFma](./UIntTreeFma.md) | `32bit` | 10.448 ns | 1,704 LUTs + 0 FF | 4.8911 ns | 11,172.00 µm² |

## 模块

- **[UIntBoothFma](./UIntBoothFma.md)** —— 使用改进 Booth radix-4 部分积的融合乘加器。
- **[UIntComposedFma](./UIntComposedFma.md)** —— 由 [[UIntMul]] 和 [[UIntAdd]] 组合而成的 FMA。
- **[UIntMacroFma](./UIntMacroFma.md)** —— 用 `*` 和 `+` 运算符实现的无符号 FMA。
- **[UIntPipelinedBoothFma](./UIntPipelinedBoothFma.md)** —— 使用改进 Booth radix-4 部分积的流水化融合乘加器。
- **[UIntPipelinedTreeFma](./UIntPipelinedTreeFma.md)** —— 使用 AND 部分积的流水化融合乘加器。
- **[UIntTreeFma](./UIntTreeFma.md)** —— 使用 AND 部分积的融合乘加器。
