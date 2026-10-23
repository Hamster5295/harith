# UIntPrefixAdd

全并行前缀加法器。

所有进位都由单一前缀网络计算，得到对数级关键路径。[`PrefixStyle`](/zh/uint/misc/PrefixStyle) 决定网络形状，从而决定面积/性能取舍点。

::: info 源代码
`src/main/scala/uint/add/UIntPrefixAdd.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `style` | `PrefixStyle` | 并行前缀网络风格 |

**延迟** = 0 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `UInt(width.W)` | 第一个操作数 |
| `src2` | 输入 | `UInt(width.W)` | 第二个操作数 |
| `carry` | 输入 | `Bool` | 进位输入 |
| `output` | 输出 | `UInt((width + 1).W)` | 和 `src1 + src2 + carry` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit-kogge-stone` | FPGA | 3.816 | 4.388 | 3.816 | 159 LUTs + 0 FF |
| `32bit-kogge-stone` | 55 nm | 1.3195 | 1.3195 | 1.3195 | 945.84 µm² |
| `32bit-brent-kung` | FPGA | 4.169 | 4.741 | 4.169 | 68 LUTs + 0 FF |
| `32bit-brent-kung` | 55 nm | 1.3866 | 1.3866 | 1.3866 | 509.32 µm² |
| `32bit-sklansky` | FPGA | 3.867 | 4.439 | 3.867 | 81 LUTs + 0 FF |
| `32bit-sklansky` | 55 nm | 1.5115 | 1.5115 | 1.5115 | 656.32 µm² |
| `32bit-han-carlson` | FPGA | 4.142 | 4.714 | 4.142 | 97 LUTs + 0 FF |
| `32bit-han-carlson` | 55 nm | 1.7993 | 1.7993 | 1.7993 | 715.12 µm² |
