# FpAdd

8 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpCarryLookaheadAdd](./FpCarryLookaheadAdd.md) | `fp32` | 42.789 ns | 4,312 LUTs + 0 FF | 13.6956 ns | 6,375.88 µm² |
| [FpCarrySelectAdd](./FpCarrySelectAdd.md) | `fp32` | 45.991 ns | 4,226 LUTs + 0 FF | 14.6117 ns | 5,542.88 µm² |
| [FpMacroAdd](./FpMacroAdd.md) | `fp32` | 40.688 ns | 3,925 LUTs + 0 FF | 14.5881 ns | 5,899.04 µm² |
| [FpPipelinedMacroAdd](./FpPipelinedMacroAdd.md) | `fp32-2cyc` | 25.203 ns | 4,120 LUTs + 383 FF | 31.2895 ns | 15,043.56 µm² |
| [FpPipelinedPrefixAdd](./FpPipelinedPrefixAdd.md) | `fp32-2cyc` | 25.022 ns | 4,952 LUTs + 627 FF | 21.8746 ns | 17,788.96 µm² |
| [FpPipelinedRippleAdd](./FpPipelinedRippleAdd.md) | `fp32-2cyc` | 26.054 ns | 4,115 LUTs + 426 FF | 22.5578 ns | 15,058.68 µm² |
| [FpPrefixAdd](./FpPrefixAdd.md) | `fp32` | 42.413 ns | 4,837 LUTs + 0 FF | 13.1978 ns | 6,136.48 µm² |
| [FpRippleAdd](./FpRippleAdd.md) | `fp32` | 51.462 ns | 4,107 LUTs + 0 FF | 14.2377 ns | 5,554.36 µm² |

## 模块

- **[FpCarryLookaheadAdd](./FpCarryLookaheadAdd.md)** —— 采用分层先行进位对齐加法器的浮点加法器。
- **[FpCarrySelectAdd](./FpCarrySelectAdd.md)** —— 采用块进位选择对齐加法器的浮点加法器。
- **[FpMacroAdd](./FpMacroAdd.md)** —— 采用推断对齐加法器的浮点加法器。
- **[FpPipelinedMacroAdd](./FpPipelinedMacroAdd.md)** —— 采用推断对齐加法器的流水化浮点加法器。
- **[FpPipelinedPrefixAdd](./FpPipelinedPrefixAdd.md)** —— 采用并行前缀对齐加法器的流水化浮点加法器。
- **[FpPipelinedRippleAdd](./FpPipelinedRippleAdd.md)** —— 采用流水化行波进位对齐加法器的流水化浮点加法器。
- **[FpPrefixAdd](./FpPrefixAdd.md)** —— 采用并行前缀对齐加法器的浮点加法器，速度优先选项。
- **[FpRippleAdd](./FpRippleAdd.md)** —— 采用行波进位对齐加法器的浮点加法器，成本最低选项。
