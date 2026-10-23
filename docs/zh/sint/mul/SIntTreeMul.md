# SIntTreeMul

使用 Baugh-Wooley 部分积的有符号进位保存树乘法器。

::: info 源代码
`src/main/scala/sint/mul/SIntTreeMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `reductionStyle` | `ReductionStyle` | The reduction style of the partial product tree |
| `adder` | `Int => UIntAdd` | The final carry propagate adder factory |

**延迟** = `adder.latency` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `SInt(width.W)` | 第一个操作数 |
| `src2` | 输入 | `SInt(width.W)` | 第二个操作数 |
| `output` | 输出 | `SInt((2 * width).W)` | 乘积 `src1 * src2` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 10.016 | 10.588 | 10.016 | 1,622 LUTs + 0 FF |
| `32bit` | 55 nm | 4.6843 | 4.6843 | 4.6843 | 10,562.44 µm² |
