# Modules

Currently, 2 categories of modules are implemented: 
- [`UInt`](/uint/): General Purpose UInt calculations, scale from low to high width.
- [`Fp`](/fp/): General Purpose Float Point calculations, focusing on IEEE standard & scalar calculations.

## UInt

Unsigned integer datapaths compute over a parameterized operand width and report their latency
through the `UIntAdd` / `UIntMul` / … interfaces.

- **Add**: bit-level carry structures around the shared adder interface: ripple, carry-skip,
  carry-lookahead, carry-select, a fully parallel prefix network (`PrefixStyle`), and the inferred
  `+` (a macro adder). The pipelined prefix and ripple variants cut the carry chain into registered
  blocks.
- **Mul**: partial-product generation followed by reduction: a carry-save array, a modified Booth
  radix-4 encoding, AND (or Booth) partial products reduced by a Dadda/Wallace tree, and the
  inferred `*`. Pipelined array and tree variants register the reduction.
- **Fma**: the significand product and the addend are combined with a single carry structure;
  composed, Booth, tree and inferred variants, plus pipelined versions.
- **Div**: iterative digit-recurrence dividers: restoring, non-restoring, and radix-2 / radix-4
  SRT.

## Fp

Floating-point datapaths are IEEE-754 binary and parameterized over the operand and result formats
(`FpFormat(expWidth, manWidth)`: `Fp64`, `Fp32`, `Tf32`, `Fp16`, `Bf16`), so mixed-precision
operations need no extra code. Numeric behavior is controlled by `FpPolicy` (`ftz`, `daz`) and
reported through `FpFlags` (`nv`, `dz`, `of`, `uf`, `nx`); NaN is always canonical, per RISC-V.

The shared wrappers (`FpAddBase`, `FpMulBase`, `FpFmaBase`) implement decode, alignment and
rounding once and delegate the wide integer datapath to a `uint` module through a factory, so every
floating-point implementation reuses the integer building blocks:

- **Add**: decode both operands, align the significands to a common working exponent, add them
  with a signed wide adder, then round and pack. Ripple, carry-select, carry-lookahead, prefix and
  inferred variants.
- **Mul**: significand product, exponent addition, then round and pack. Array, Booth, tree and
  inferred variants.
- **Fma**: the exact product and the aligned addend are summed with a single rounding. Array,
  Booth, tree, prefix, ripple and inferred variants.
- **Div**: an iterative restoring divider with a `Decoupled` request and a `Valid` result; the
  quotient is built one bit per cycle and rounded exactly.
- **Convert**: decode and re-round to the destination format. The converters cover every directed
  pair among `Fp64`, `Fp32`, `Fp16` and `Bf16`.

The pipelined variants pipeline only the wide integer datapath internally; the floating-point
control is a register queue matched to the datapath latency, so `stages` equals the resulting
latency. `stages = 0` makes a variant combinational.
