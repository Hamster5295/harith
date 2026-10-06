import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Harith',
  description: 'DSP library for Chisel',
  cleanUrls: true,
  themeConfig: {
    nav: [
      { text: 'Introduction', link: '/guide/introduction' },
      { text: 'Quick Start', link: '/guide/quick-start' },
    ],
    sidebar: [
  { text: 'Introduction', collapsed: false, items: [
      { text: 'What\'s Harith?', link: '/guide/introduction' },
      { text: 'Quick Start', link: '/guide/quick-start' },
      { text: 'Modules', link: '/guide/modules' },
      { text: 'Analysis Condition', link: '/guide/analysis-condition' },
    ],
  },
  {
    text: 'UInt',
    collapsed: false,
    items: [
      { text: 'Overview', link: '/uint/' },
        {
          text: 'Add',
          collapsed: true,
          items: [
            { text: 'Overview', link: '/uint/add/' },
            { text: 'UIntCarryLookaheadAdd', link: '/uint/add/UIntCarryLookaheadAdd' },
            { text: 'UIntCarrySelectAdd', link: '/uint/add/UIntCarrySelectAdd' },
            { text: 'UIntCarrySkipAdd', link: '/uint/add/UIntCarrySkipAdd' },
            { text: 'UIntMacroAdd', link: '/uint/add/UIntMacroAdd' },
            { text: 'UIntPipelinedPrefixAdd', link: '/uint/add/UIntPipelinedPrefixAdd' },
            { text: 'UIntPipelinedRippleAdd', link: '/uint/add/UIntPipelinedRippleAdd' },
            { text: 'UIntPrefixAdd', link: '/uint/add/UIntPrefixAdd' },
            { text: 'UIntRippleAdd', link: '/uint/add/UIntRippleAdd' }
          ],
        },
        {
          text: 'Mul',
          collapsed: true,
          items: [
            { text: 'Overview', link: '/uint/mul/' },
            { text: 'UIntArrayMul', link: '/uint/mul/UIntArrayMul' },
            { text: 'UIntBoothMul', link: '/uint/mul/UIntBoothMul' },
            { text: 'UIntMacroMul', link: '/uint/mul/UIntMacroMul' },
            { text: 'UIntPipelinedArrayMul', link: '/uint/mul/UIntPipelinedArrayMul' },
            { text: 'UIntPipelinedTreeMul', link: '/uint/mul/UIntPipelinedTreeMul' },
            { text: 'UIntTreeMul', link: '/uint/mul/UIntTreeMul' }
          ],
        },
        {
          text: 'Div',
          collapsed: true,
          items: [
            { text: 'Overview', link: '/uint/div/' },
            { text: 'UIntNonRestoringDiv', link: '/uint/div/UIntNonRestoringDiv' },
            { text: 'UIntRestoringDiv', link: '/uint/div/UIntRestoringDiv' },
            { text: 'UIntSrt2Div', link: '/uint/div/UIntSrt2Div' },
            { text: 'UIntSrt4Div', link: '/uint/div/UIntSrt4Div' }
          ],
        },
        {
          text: 'Fma',
          collapsed: true,
          items: [
            { text: 'Overview', link: '/uint/fma/' },
            { text: 'UIntBoothFma', link: '/uint/fma/UIntBoothFma' },
            { text: 'UIntComposedFma', link: '/uint/fma/UIntComposedFma' },
            { text: 'UIntMacroFma', link: '/uint/fma/UIntMacroFma' },
            { text: 'UIntPipelinedBoothFma', link: '/uint/fma/UIntPipelinedBoothFma' },
            { text: 'UIntPipelinedTreeFma', link: '/uint/fma/UIntPipelinedTreeFma' },
            { text: 'UIntTreeFma', link: '/uint/fma/UIntTreeFma' }
          ],
        },
    ],
  },
  {
    text: 'Fp',
    collapsed: false,
    items: [
      { text: 'Overview', link: '/fp/' },
        {
          text: 'Add',
          collapsed: true,
          items: [
            { text: 'Overview', link: '/fp/add/' },
            { text: 'FpCarryLookaheadAdd', link: '/fp/add/FpCarryLookaheadAdd' },
            { text: 'FpCarrySelectAdd', link: '/fp/add/FpCarrySelectAdd' },
            { text: 'FpMacroAdd', link: '/fp/add/FpMacroAdd' },
            { text: 'FpPipelinedMacroAdd', link: '/fp/add/FpPipelinedMacroAdd' },
            { text: 'FpPipelinedPrefixAdd', link: '/fp/add/FpPipelinedPrefixAdd' },
            { text: 'FpPipelinedRippleAdd', link: '/fp/add/FpPipelinedRippleAdd' },
            { text: 'FpPrefixAdd', link: '/fp/add/FpPrefixAdd' },
            { text: 'FpRippleAdd', link: '/fp/add/FpRippleAdd' }
          ],
        },
        {
          text: 'Mul',
          collapsed: true,
          items: [
            { text: 'Overview', link: '/fp/mul/' },
            { text: 'FpArrayMul', link: '/fp/mul/FpArrayMul' },
            { text: 'FpBoothMul', link: '/fp/mul/FpBoothMul' },
            { text: 'FpMacroMul', link: '/fp/mul/FpMacroMul' },
            { text: 'FpPipelinedMul', link: '/fp/mul/FpPipelinedMul' },
            { text: 'FpTreeMul', link: '/fp/mul/FpTreeMul' }
          ],
        },
        {
          text: 'Div',
          collapsed: true,
          items: [
            { text: 'Overview', link: '/fp/div/' },
            { text: 'FpRestoringDiv', link: '/fp/div/FpRestoringDiv' }
          ],
        },
        {
          text: 'Fma',
          collapsed: true,
          items: [
            { text: 'Overview', link: '/fp/fma/' },
            { text: 'FpArrayFma', link: '/fp/fma/FpArrayFma' },
            { text: 'FpBoothFma', link: '/fp/fma/FpBoothFma' },
            { text: 'FpMacroFma', link: '/fp/fma/FpMacroFma' },
            { text: 'FpPipelinedFma', link: '/fp/fma/FpPipelinedFma' },
            { text: 'FpPrefixFma', link: '/fp/fma/FpPrefixFma' },
            { text: 'FpRippleFma', link: '/fp/fma/FpRippleFma' },
            { text: 'FpTreeFma', link: '/fp/fma/FpTreeFma' }
          ],
        },
        {
          text: 'Convert',
          collapsed: true,
          items: [
            { text: 'Overview', link: '/fp/convert/' },
            { text: 'FpGenericConvert', link: '/fp/convert/FpGenericConvert' },
            { text: 'FpPipelinedConvert', link: '/fp/convert/FpPipelinedConvert' }
          ],
        },
    ],
  },
],
    socialLinks: [
      { icon: 'github', link: 'https://codeberg.org/hamster5295/harith' },
    ],
    outline: { level: [2, 3] },
    search: { provider: 'local' },
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright hamster5295',
    },
  },
})
