# SIntMul

2 modules.

## Comparison

Representative condition per module.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [SIntMacroMul](./SIntMacroMul.md) | `32bit` | 7.721 ns | 47 LUTs + 0 FF | 4.0572 ns | 18,430.16 µm² |
| [SIntMagnitudeMul](./SIntMagnitudeMul.md) | `32bit` | 11.896 ns | 300 LUTs + 0 FF | 5.0521 ns | 17,607.24 µm² |

## Modules

- **[SIntMacroMul](./SIntMacroMul.md)** — The signed multiplier implemented with the `*` operator.
- **[SIntMagnitudeMul](./SIntMagnitudeMul.md)** — A signed multiplier built from an unsigned magnitude multiplier and sign correction.
