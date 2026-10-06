# UIntDiv

4 modules.

## Comparison

Representative condition per module.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [UIntNonRestoringDiv](./UIntNonRestoringDiv.md) | `32bit-32cyc` | 4.340 ns | 141 LUTs + 205 FF | 5.9947 ns | 3,450.16 µm² |
| [UIntRestoringDiv](./UIntRestoringDiv.md) | `32bit-32cyc` | 2.678 ns | 122 LUTs + 201 FF | 6.6857 ns | 2,589.44 µm² |
| [UIntSrt2Div](./UIntSrt2Div.md) | `32bit-32cyc` | 13.153 ns | 324 LUTs + 236 FF | 6.6244 ns | 5,332.88 µm² |
| [UIntSrt4Div](./UIntSrt4Div.md) | `32bit-16cyc` | 4.338 ns | 328 LUTs + 202 FF | 5.8867 ns | 3,832.64 µm² |

## Modules

- **[UIntNonRestoringDiv](./UIntNonRestoringDiv.md)** — An iterative non-restoring divider.
- **[UIntRestoringDiv](./UIntRestoringDiv.md)** — An iterative restoring divider.
- **[UIntSrt2Div](./UIntSrt2Div.md)** — An iterative radix-2 SRT divider.
- **[UIntSrt4Div](./UIntSrt4Div.md)** — An iterative radix-4 divider.
