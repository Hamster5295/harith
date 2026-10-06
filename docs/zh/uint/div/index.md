# UIntDiv

4 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [UIntNonRestoringDiv](./UIntNonRestoringDiv.md) | `32bit-32cyc` | 4.340 ns | 141 LUTs + 205 FF | 5.9947 ns | 3,450.16 µm² |
| [UIntRestoringDiv](./UIntRestoringDiv.md) | `32bit-32cyc` | 2.678 ns | 122 LUTs + 201 FF | 6.6857 ns | 2,589.44 µm² |
| [UIntSrt2Div](./UIntSrt2Div.md) | `32bit-32cyc` | 13.153 ns | 324 LUTs + 236 FF | 6.6244 ns | 5,332.88 µm² |
| [UIntSrt4Div](./UIntSrt4Div.md) | `32bit-16cyc` | 4.338 ns | 328 LUTs + 202 FF | 5.8867 ns | 3,832.64 µm² |

## 模块

- **[UIntNonRestoringDiv](./UIntNonRestoringDiv.md)** —— 迭代式不恢复余数除法器。
- **[UIntRestoringDiv](./UIntRestoringDiv.md)** —— 迭代式恢复余数除法器。
- **[UIntSrt2Div](./UIntSrt2Div.md)** —— 迭代式 radix-2 SRT 除法器。
- **[UIntSrt4Div](./UIntSrt4Div.md)** —— 迭代式 radix-4 除法器。
