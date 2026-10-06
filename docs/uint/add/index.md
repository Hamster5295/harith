# UInt · Add

8 modules.

## Comparison

Representative condition per module · `FPGA` = Vivado `xc7a200t`, `55 nm` = icsprout55.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [UIntCarryLookaheadAdd](./UIntCarryLookaheadAdd.md) | `32bit` | 3.879 ns | 70 LUTs + 0 FF | 1.4466 ns | 493.08 µm² |
| [UIntCarrySelectAdd](./UIntCarrySelectAdd.md) | `32bit` | 4.104 ns | 57 LUTs + 0 FF | 2.8304 ns | 416.64 µm² |
| [UIntCarrySkipAdd](./UIntCarrySkipAdd.md) | `32bit` | 6.359 ns | 55 LUTs + 0 FF | 2.6155 ns | 308.00 µm² |
| [UIntMacroAdd](./UIntMacroAdd.md) | `32bit` | 2.726 ns | 32 LUTs + 0 FF | 1.4861 ns | 378.28 µm² |
| [UIntPipelinedPrefixAdd](./UIntPipelinedPrefixAdd.md) | `32bit-2cyc` | 1.479 ns | 168 LUTs + 192 FF | 0.8007 ns | 1,847.72 µm² |
| [UIntPipelinedRippleAdd](./UIntPipelinedRippleAdd.md) | `32bit-8cyc` | 1.791 ns | 88 LUTs + 156 FF | 0.2891 ns | 2,615.76 µm² |
| [UIntPrefixAdd](./UIntPrefixAdd.md) | `32bit` | 3.816 ns | 159 LUTs + 0 FF | 1.3195 ns | 945.84 µm² |
| [UIntRippleAdd](./UIntRippleAdd.md) | `32bit` | 6.366 ns | 56 LUTs + 0 FF | 2.6383 ns | 300.72 µm² |

## Modules

- **[UIntCarryLookaheadAdd](./UIntCarryLookaheadAdd.md)** — A hierarchical carry lookahead adder.
- **[UIntCarrySelectAdd](./UIntCarrySelectAdd.md)** — A block carry select adder.
- **[UIntCarrySkipAdd](./UIntCarrySkipAdd.md)** — A block carry skip (carry bypass) adder.
- **[UIntMacroAdd](./UIntMacroAdd.md)** — The unsigned adder implemented with the `+` operator.
- **[UIntPipelinedPrefixAdd](./UIntPipelinedPrefixAdd.md)** — A pipelined parallel prefix adder.
- **[UIntPipelinedRippleAdd](./UIntPipelinedRippleAdd.md)** — A pipelined ripple carry adder.
- **[UIntPrefixAdd](./UIntPrefixAdd.md)** — A fully parallel prefix adder.
- **[UIntRippleAdd](./UIntRippleAdd.md)** — A structural ripple carry adder.
