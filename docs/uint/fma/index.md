# UIntFma

6 modules.

## Comparison

Representative condition per module.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [UIntBoothFma](./UIntBoothFma.md) | `32bit` | 10.731 ns | 2,642 LUTs + 0 FF | 5.5558 ns | 18,806.76 µm² |
| [UIntComposedFma](./UIntComposedFma.md) | `32bit` | 12.913 ns | 1,990 LUTs + 0 FF | 5.3590 ns | 11,958.24 µm² |
| [UIntMacroFma](./UIntMacroFma.md) | `32bit` | 8.933 ns | 111 LUTs + 0 FF | 3.2484 ns | 17,201.52 µm² |
| [UIntPipelinedBoothFma](./UIntPipelinedBoothFma.md) | `32bit-2cyc` | 3.718 ns | 2,441 LUTs + 454 FF | 3.4447 ns | 18,331.88 µm² |
| [UIntPipelinedTreeFma](./UIntPipelinedTreeFma.md) | `32bit-2cyc` | 4.947 ns | 1,683 LUTs + 211 FF | 2.5375 ns | 12,053.44 µm² |
| [UIntTreeFma](./UIntTreeFma.md) | `32bit` | 10.448 ns | 1,704 LUTs + 0 FF | 4.8911 ns | 11,172.00 µm² |

## Modules

- **[UIntBoothFma](./UIntBoothFma.md)** — A fused multiply-adder using modified Booth radix-4 partial products.
- **[UIntComposedFma](./UIntComposedFma.md)** — An FMA composed from a [`UIntMul`](/uint/mul/) and a [`UIntAdd`](/uint/add/).
- **[UIntMacroFma](./UIntMacroFma.md)** — The unsigned FMA implemented with the `*` and `+` operators.
- **[UIntPipelinedBoothFma](./UIntPipelinedBoothFma.md)** — A pipelined fused multiply-adder using modified Booth radix-4 partial products.
- **[UIntPipelinedTreeFma](./UIntPipelinedTreeFma.md)** — A pipelined fused multiply-adder using AND partial products.
- **[UIntTreeFma](./UIntTreeFma.md)** — A fused multiply-adder using AND partial products.
