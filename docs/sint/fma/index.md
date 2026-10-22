# SIntFma

2 modules.

## Comparison

Representative condition per module.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [SIntMacroFma](./SIntMacroFma.md) | `32bit` | 8.869 ns | 111 LUTs + 0 FF | 4.3248 ns | 20,068.16 µm² |
| [SIntComposedFma](./SIntComposedFma.md) | `32bit` | 11.330 ns | 239 LUTs + 0 FF | 5.8455 ns | 18,356.80 µm² |

## Modules

- **[SIntMacroFma](./SIntMacroFma.md)** — The signed FMA implemented with the `*` and `+` operators.
- **[SIntComposedFma](./SIntComposedFma.md)** — A signed FMA composed from an unsigned magnitude multiplier and a signed add.
