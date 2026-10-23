# SIntMul

7 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [SIntMacroMul](./SIntMacroMul.md) | `32bit` | 7.721 ns | 47 LUTs + 0 FF | 4.0572 ns | 18,430.16 µm² |
| [SIntMagnitudeMul](./SIntMagnitudeMul.md) | `32bit` | 11.896 ns | 300 LUTs + 0 FF | 5.0521 ns | 17,607.24 µm² |
| [SIntArrayMul](./SIntArrayMul.md) | `32bit` | 18.743 ns | 2,731 LUTs + 0 FF | 8.1454 ns | 16,828.56 µm² |
| [SIntBoothMul](./SIntBoothMul.md) | `32bit` | 10.235 ns | 3,030 LUTs + 0 FF | 5.0887 ns | 15,983.52 µm² |
| [SIntTreeMul](./SIntTreeMul.md) | `32bit` | 10.016 ns | 1,622 LUTs + 0 FF | 4.6843 ns | 10,562.44 µm² |
| [SIntPipelinedArrayMul](./SIntPipelinedArrayMul.md) | `32bit-2cyc` | 7.332 ns | 2,177 LUTs + 220 FF | 3.4569 ns | 19,392.24 µm² |
| [SIntPipelinedTreeMul](./SIntPipelinedTreeMul.md) | `32bit-2cyc` | 4.534 ns | 1,611 LUTs + 208 FF | 2.4505 ns | 11,618.60 µm² |

## 模块

- **[SIntMacroMul](./SIntMacroMul.md)** — 用 `*` 运算符实现的有符号乘法器。
- **[SIntMagnitudeMul](./SIntMagnitudeMul.md)** — 由无符号幅值乘法器和符号修正构成的有符号乘法器。
- **[SIntArrayMul](./SIntArrayMul.md)** — 使用 Baugh-Wooley 部分积的有符号进位保存阵列乘法器。
- **[SIntBoothMul](./SIntBoothMul.md)** — 使用有符号 modified Booth radix-4 的树乘法器。
- **[SIntTreeMul](./SIntTreeMul.md)** — 使用 Baugh-Wooley 部分积的有符号进位保存树乘法器。
- **[SIntPipelinedArrayMul](./SIntPipelinedArrayMul.md)** — 使用 Baugh-Wooley 部分积的流水有符号进位保存阵列乘法器。
- **[SIntPipelinedTreeMul](./SIntPipelinedTreeMul.md)** — 使用 Baugh-Wooley 部分积的流水有符号进位保存树乘法器。
