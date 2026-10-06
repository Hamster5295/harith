# 快速开始

本页将引导你使用与开发 Harith。


## 使用

Harith 以 `io.github.hamster5295` 发布在 Maven Central。将其与 Chisel 一同加入你的 `build.mill`：

```scala
override def mvnDeps = Seq(
  mvn"org.chipsalliance::chisel:7.15.0",
  mvn"io.github.hamster5295::harith:<version>",
)
```

`mill` 解析依赖后，你就可以在自己的设计中使用任意 Harith 模块。

若使用 `mill 1.x` 以下版本或 `sbt`，请相应修改依赖行。


## 开发

如需开发与贡献，请遵循以下步骤：

### 前置条件

- **JDK 21** 以及自带的 `mill` 启动器（无需单独安装）。
- 可选：**Vivado**（FPGA）以及 **OpenSTA** 加 `icsprout55` PDK（ASIC）。

::: info 说明
`icsprout55` 体积过大，因此未作为 git 子模块捆绑。

你可以从 https://github.com/openecos-projects/icsprout55-pdk 获取。
:::


### 测试

克隆仓库并运行测试套件：

```shell
git clone https://codeberg.org/hamster5295/harith
cd harith
make test-all
```

这会为每个计算模块运行测试套件。


### 单独分析

每个模块都附带一个独立入口（`object ... extends App`），它会生成一个将所有输出寄存以用于分析的 SystemVerilog 包装。可用以下命令对单个模块运行 FPGA 或 ASIC 流程：

```shell
make verilog TARGET=harith.fp.FpRippleAddFp32
make fpga    TARGET=harith.fp.FpRippleAddFp32
make asic    TARGET=harith.fp.FpRippleAddFp32
```

浮点模块为每种格式暴露一个入口点——例如 `FpRippleAddFp64`、`FpRippleAddFp32`、`FpRippleAddFp16` 和 `FpRippleAddBf16`——而整数模块各只暴露一个入口点。

### 仓库命令

| 命令 | 说明 |
| --- | --- |
| `make test-all` | 运行完整测试套件。 |
| `make test TARGET=harith.uint.UIntRippleAddSpec` | 运行单个测试用例。 |
| `make format` | 使用 scalafmt 格式化所有源码。 |
| `make verilog TARGET=<object>` | 将某个入口点导出为 SystemVerilog。 |
| `make fpga TARGET=<object>` | 运行 Vivado FPGA PPA 流程。 |
| `make asic TARGET=<object>` | 运行 OpenSTA 55 nm PPA 流程。 |
| `make lib` | 将库发布到本地 Maven 仓库。 |
