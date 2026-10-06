# FpPipelinedConvert

A pipelined floating-point format converter.

The operand is delayed by `stages` and the re-rounding is combinational on the delayed operand, so the throughput is one conversion per cycle. `stages = 0` makes the converter combinational, equivalently to [[FpGenericConvert]]. NaN is canonical, per RISC-V.  Note: the `stages` register layers are a plain delay queue on the operands, not registers placed at internal cut points; the conversion logic between them stays combinational and the intended pipeline depth is only realised once the EDA tool retimes the queue into the logic.

::: info Source
`src/main/scala/fp/convert/FpPipelinedConvert.scala`
:::

## Parameters

- **`inFmt`** — The input format
- **`outFmt`** — The output format
- **`stages`** — The number of pipeline register layers, which is also the latency
- **`policy`** — The numeric policy

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `fp64->fp32-2cyc` | FPGA | 1.364 | 21.083 | 20.512 | 1,073 LUTs + 132 FF |
| `fp64->fp32-2cyc` | 55 nm | 0.0053 | 6.8752 | 6.8752 | 2,601.76 µm² |
| `fp64->fp16-2cyc` | FPGA | 1.364 | 20.703 | 19.435 | 931 LUTs + 132 FF |
| `fp64->fp16-2cyc` | 55 nm | 0.0053 | 5.6125 | 5.6125 | 1,907.36 µm² |
| `fp64->bf16-2cyc` | FPGA | 1.364 | 20.170 | 19.506 | 952 LUTs + 132 FF |
| `fp64->bf16-2cyc` | 55 nm | 0.0053 | 5.0410 | 5.0410 | 1,961.40 µm² |
| `fp32->fp64-2cyc` | FPGA | 1.364 | 19.703 | 19.132 | 639 LUTs + 68 FF |
| `fp32->fp64-2cyc` | 55 nm | 0.0053 | 4.1978 | 4.1978 | 959.00 µm² |
| `fp32->fp16-2cyc` | FPGA | 1.364 | 19.160 | 18.172 | 504 LUTs + 68 FF |
| `fp32->fp16-2cyc` | 55 nm | 0.0053 | 4.8233 | 4.8233 | 1,248.52 µm² |
| `fp32->bf16-2cyc` | FPGA | 1.364 | 18.759 | 17.771 | 487 LUTs + 68 FF |
| `fp32->bf16-2cyc` | 55 nm | 0.0053 | 2.8789 | 2.8789 | 785.96 µm² |
| `fp16->fp64-2cyc` | FPGA | 1.364 | 17.633 | 16.653 | 506 LUTs + 36 FF |
| `fp16->fp64-2cyc` | 55 nm | 0.0053 | 2.5191 | 2.5191 | 413.56 µm² |
| `fp16->fp32-2cyc` | FPGA | 1.364 | 14.354 | 13.783 | 293 LUTs + 35 FF |
| `fp16->fp32-2cyc` | 55 nm | 0.0053 | 2.3150 | 2.3150 | 423.08 µm² |
| `fp16->bf16-2cyc` | FPGA | 1.364 | 13.677 | 13.106 | 242 LUTs + 35 FF |
| `fp16->bf16-2cyc` | 55 nm | 0.0053 | 2.5211 | 2.5211 | 557.48 µm² |
| `bf16->fp64-2cyc` | FPGA | 1.364 | 16.641 | 16.070 | 380 LUTs + 36 FF |
| `bf16->fp64-2cyc` | 55 nm | 0.0053 | 2.8729 | 2.8729 | 445.20 µm² |
| `bf16->fp32-2cyc` | FPGA | 1.364 | 15.865 | 15.294 | 304 LUTs + 36 FF |
| `bf16->fp32-2cyc` | 55 nm | 0.0053 | 1.4419 | 1.4419 | 271.60 µm² |
| `bf16->fp16-2cyc` | FPGA | 1.364 | 16.082 | 15.510 | 280 LUTs + 36 FF |
| `bf16->fp16-2cyc` | 55 nm | 0.0053 | 4.6696 | 4.6696 | 827.12 µm² |
