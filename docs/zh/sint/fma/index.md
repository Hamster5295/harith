# SIntFma

2 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [SIntMacroFma](./SIntMacroFma.md) | `32bit` | 8.869 ns | 111 LUTs + 0 FF | 4.3248 ns | 20,068.16 µm² |
| [SIntComposedFma](./SIntComposedFma.md) | `32bit` | 11.330 ns | 239 LUTs + 0 FF | 5.8455 ns | 18,356.80 µm² |

## 模块

- **[SIntMacroFma](./SIntMacroFma.md)** —— 用 `*` 和 `+` 运算符实现的有符号 FMA。
- **[SIntComposedFma](./SIntComposedFma.md)** —— 由无符号幅值乘法器和有符号加法构成的有符号 FMA。
