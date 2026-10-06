# UIntAdd

8 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [UIntCarryLookaheadAdd](./UIntCarryLookaheadAdd.md) | `32bit` | 3.879 ns | 70 LUTs + 0 FF | 1.4466 ns | 493.08 µm² |
| [UIntCarrySelectAdd](./UIntCarrySelectAdd.md) | `32bit` | 4.104 ns | 57 LUTs + 0 FF | 2.8304 ns | 416.64 µm² |
| [UIntCarrySkipAdd](./UIntCarrySkipAdd.md) | `32bit` | 6.359 ns | 55 LUTs + 0 FF | 2.6155 ns | 308.00 µm² |
| [UIntMacroAdd](./UIntMacroAdd.md) | `32bit` | 2.726 ns | 32 LUTs + 0 FF | 1.4861 ns | 378.28 µm² |
| [UIntPipelinedPrefixAdd](./UIntPipelinedPrefixAdd.md) | `32bit-2cyc` | 1.479 ns | 168 LUTs + 192 FF | 0.8007 ns | 1,847.72 µm² |
| [UIntPipelinedRippleAdd](./UIntPipelinedRippleAdd.md) | `32bit-8cyc` | 1.791 ns | 88 LUTs + 156 FF | 0.2891 ns | 2,615.76 µm² |
| [UIntPrefixAdd](./UIntPrefixAdd.md) | `32bit` | 3.816 ns | 159 LUTs + 0 FF | 1.3195 ns | 945.84 µm² |
| [UIntRippleAdd](./UIntRippleAdd.md) | `32bit` | 6.366 ns | 56 LUTs + 0 FF | 2.6383 ns | 300.72 µm² |

## 模块

- **[UIntCarryLookaheadAdd](./UIntCarryLookaheadAdd.md)** —— 分层先行进位加法器。
- **[UIntCarrySelectAdd](./UIntCarrySelectAdd.md)** —— 块进位选择加法器。
- **[UIntCarrySkipAdd](./UIntCarrySkipAdd.md)** —— 块进位跳过（进位旁路）加法器。
- **[UIntMacroAdd](./UIntMacroAdd.md)** —— 用 `+` 运算符实现的无符号加法器。
- **[UIntPipelinedPrefixAdd](./UIntPipelinedPrefixAdd.md)** —— 流水化并行前缀加法器。
- **[UIntPipelinedRippleAdd](./UIntPipelinedRippleAdd.md)** —— 流水化行波进位加法器。
- **[UIntPrefixAdd](./UIntPrefixAdd.md)** —— 全并行前缀加法器。
- **[UIntRippleAdd](./UIntRippleAdd.md)** —— 结构化行波进位加法器。
