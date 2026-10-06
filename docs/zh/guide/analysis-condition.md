# 分析条件

Harith 中每个模块都会被刻画两次：一次在 FPGA 上用 Vivado，一次在 ASIC 上通过 Yosys 和 OpenSTA 配合 icsprout55 PDK。本页描述分析环境、约束以及所报告数值的确切含义。

## 指标

每次分析产生三个纯逻辑延迟和一个面积数值：

- **in** —— *输入延迟*：从数据输入到第一个寄存器；对于组合模块，则到输出。
- **out** —— *输出延迟*：从最后一个寄存器到输出；对于组合模块，则从输入到输出。输出缓冲自身的延迟不计入。
- **max** —— *最差延迟*：最差的寄存器到寄存器延迟，它决定了可达到的频率。
- **area**：
  - *FPGA*：LUT 与触发器
  - *ASIC*：µm²。

时序约束所建模的外部输入/输出延迟会从这三个延迟中扣除，因此它们度量的是模块自身的逻辑，而非其外围接口。

`ExportForAnalysis` 插入的独立包装器会将每个输出寄存到一层备用寄存器上。该寄存器属于测试框架而非模块本身，因此其位宽（`reg_bits`）会从面积中扣除——FPGA 触发器数量扣除相应位，ASIC 面积扣除 `reg_bits × 6.16 µm²`（一个 `DFFQX0P5H7L`）。

## FPGA

| 设置 | 取值 |
| --- | --- |
| 工具 | Vivado v2021.2，批处理模式 |
| 器件 | `xc7a200t` |
| 时钟 | `clock`，周期 6 ns（≈166.7 MHz） |
| 综合 | `synth_design -directive PerformanceOptimized -flatten_hierarchy full` |
| 报告 | `report_timing`、`report_utilization`、`report_power` |

时钟由 `backend/fpga/script/constraints.xdc` 创建；未建模输入/输出延迟，因此这三个延迟是原始逻辑延迟。`backend/fpga/script/synth.tcl` 分别以 `report_timing -from <data inputs>` 输出 `in` 视图，以 `report_timing -to <non-*_reg* outputs>` 输出 `out` 视图，并以 `report_timing -to <sequential cells>` 输出 `max` 视图。

## ASIC

| 设置 | 取值 |
| --- | --- |
| 前端 | Yosys 0.69（`read_verilog -sv`、`synth -flatten`、`dfflibmap`、ABC 映射） |
| 后端 | OpenSTA 2.0.17 |
| PDK | icsprout55 —— ics55 `LLSC_H7CL`、`typ_tt_1p2_25_nldm` |
| 库单元 | 单一 `L` VT；`LAT*` 锁存器单元为 don't-use |
| 时钟 | `core_clock`，500 MHz（2 ns） |
| I/O 延迟 | 0.2 × 周期 = 0.4 ns，输入与输出 |
| 驱动 / 负载 | `BUFX0P5H7L` / 0.05 pF |

`backend/asic/script/syn.tcl` 执行与工艺无关的综合并映射设计；默认使用轻量级 ABC 脚本（`+strash; dch; map`），可通过 `ABC_SCRIPT=1` 选择更激进的重综合策略。随后 `backend/asic/script/sta.tcl` 在 `backend/asic/script/default.sdc` 下将网表读入 OpenSTA，并用 `report_checks -from <data inputs>`、`-to <non-`*_reg*` outputs>` 和 `-to [all_registers -data_pins]` 写出三个延迟视图。0.4 ns 的输入/输出延迟仅用于约束接口，会从所报告的延迟中扣除。
