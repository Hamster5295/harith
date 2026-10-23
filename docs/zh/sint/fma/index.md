# SIntFma

6 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [SIntMacroFma](./SIntMacroFma.md) | `32bit` | 8.869 ns | 111 LUTs + 0 FF | 4.3248 ns | 20,068.16 µm² |
| [SIntComposedFma](./SIntComposedFma.md) | `32bit` | 11.330 ns | 239 LUTs + 0 FF | 5.8455 ns | 18,356.80 µm² |
| [SIntBoothFma](./SIntBoothFma.md) | `32bit` | 10.316 ns | 3,311 LUTs + 0 FF | 5.1612 ns | 18,097.52 µm² |
| [SIntTreeFma](./SIntTreeFma.md) | `32bit` | 10.436 ns | 1,744 LUTs + 0 FF | 4.7786 ns | 11,305.84 µm² |
| [SIntPipelinedBoothFma](./SIntPipelinedBoothFma.md) | `32bit-2cyc` | 3.865 ns | 3,142 LUTs + 459 FF | 3.4471 ns | 18,142.60 µm² |
| [SIntPipelinedTreeFma](./SIntPipelinedTreeFma.md) | `32bit-2cyc` | 4.947 ns | 1,712 LUTs + 215 FF | 2.5513 ns | 12,076.68 µm² |

## 模块

- **[SIntMacroFma](./SIntMacroFma.md)** — 用 `*` 和 `+` 运算符实现的有符号 FMA。
- **[SIntComposedFma](./SIntComposedFma.md)** — 由无符号幅值乘法器和有符号加法构成的有符号 FMA。
- **[SIntBoothFma](./SIntBoothFma.md)** — 使用有符号 modified Booth 部分积、加数合并进归约的有符号 FMA。
- **[SIntTreeFma](./SIntTreeFma.md)** — 使用 Baugh-Wooley 部分积、加数合并进归约的有符号 FMA。
- **[SIntPipelinedBoothFma](./SIntPipelinedBoothFma.md)** — 使用有符号 modified Booth 部分积、加数合并进归约的流水有符号 FMA。
- **[SIntPipelinedTreeFma](./SIntPipelinedTreeFma.md)** — 使用 Baugh-Wooley 部分积、加数合并进归约的流水有符号 FMA。
