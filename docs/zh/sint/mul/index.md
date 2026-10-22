# SIntMul

2 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [SIntMacroMul](./SIntMacroMul.md) | `32bit` | 7.721 ns | 47 LUTs + 0 FF | 4.0572 ns | 18,430.16 µm² |
| [SIntMagnitudeMul](./SIntMagnitudeMul.md) | `32bit` | 11.896 ns | 300 LUTs + 0 FF | 5.0521 ns | 17,607.24 µm² |

## 模块

- **[SIntMacroMul](./SIntMacroMul.md)** —— 用 `*` 运算符实现的有符号乘法器。
- **[SIntMagnitudeMul](./SIntMagnitudeMul.md)** —— 由无符号幅值乘法器和符号修正构成的有符号乘法器。
