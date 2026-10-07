import { defineConfig } from 'vitepress'

/**
 * The module lists of every category, in the order shown in the sidebar.
 * Pipelined implementations always come after the purely combinational ones.
 */
const uintModules: Record<string, string[]> = {
  add: [
    'UIntCarryLookaheadAdd',
    'UIntCarrySelectAdd',
    'UIntCarrySkipAdd',
    'UIntMacroAdd',
    'UIntPrefixAdd',
    'UIntRippleAdd',
    'UIntPipelinedPrefixAdd',
    'UIntPipelinedRippleAdd',
  ],
  mul: [
    'UIntArrayMul',
    'UIntBoothMul',
    'UIntMacroMul',
    'UIntTreeMul',
    'UIntPipelinedArrayMul',
    'UIntPipelinedTreeMul',
  ],
  div: ['UIntNonRestoringDiv', 'UIntRestoringDiv', 'UIntSrt2Div', 'UIntSrt4Div'],
  fma: [
    'UIntBoothFma',
    'UIntComposedFma',
    'UIntMacroFma',
    'UIntTreeFma',
    'UIntPipelinedBoothFma',
    'UIntPipelinedTreeFma',
  ],
  misc: ['PrefixStyle'],
}

const fpModules: Record<string, string[]> = {
  add: [
    'FpCarryLookaheadAdd',
    'FpCarrySelectAdd',
    'FpMacroAdd',
    'FpPrefixAdd',
    'FpRippleAdd',
    'FpPipelinedMacroAdd',
    'FpPipelinedPrefixAdd',
    'FpPipelinedRippleAdd',
  ],
  mul: [
    'FpArrayMul',
    'FpBoothMul',
    'FpMacroMul',
    'FpTreeMul',
    'FpPipelinedMul',
  ],
  div: ['FpRestoringDiv'],
  fma: [
    'FpArrayFma',
    'FpBoothFma',
    'FpMacroFma',
    'FpPrefixFma',
    'FpRippleFma',
    'FpTreeFma',
    'FpPipelinedFma',
  ],
  convert: ['FpGenericConvert', 'FpPipelinedConvert'],
  misc: ['FpFlags', 'FpFormat', 'FpPolicy'],
}

const enCategories: Record<string, string> = {
  add: 'Add',
  mul: 'Mul',
  div: 'Div',
  fma: 'Fma',
  convert: 'Convert',
  misc: 'Misc',
}

const zhCategories: Record<string, string> = {
  add: '加法器',
  mul: '乘法器',
  div: '除法器',
  fma: '乘加器',
  convert: '转换器',
  misc: '杂项',
}

/**
 * Build the sidebar for a locale.
 *
 * @param prefix The locale URL prefix, either `''` for English or `'/zh'` for Chinese
 * @param zh Whether to emit the Chinese labels
 */
const buildSidebar = (prefix: string, zh: boolean) => {
  const overview = zh ? '概览' : 'Overview'
  const categories = zh ? zhCategories : enCategories

  const modules = (base: string, list: Record<string, string[]>) =>
    Object.entries(list).map(([category, mods]) => ({
      text: categories[category],
      collapsed: true,
      items: [
        ...(category === 'misc'
          ? []
          : [{ text: overview, link: `${prefix}/${base}/${category}/` }]),
        ...mods.map((mod) => ({
          text: mod,
          link: `${prefix}/${base}/${category}/${mod}`,
        })),
      ],
    }))

  return [
    {
      text: zh ? '简介' : 'Introduction',
      collapsed: false,
      items: [
        {
          text: zh ? 'Harith 是什么？' : "What's Harith?",
          link: `${prefix}/guide/introduction`,
        },
        { text: zh ? '快速开始' : 'Quick Start', link: `${prefix}/guide/quick-start` },
        { text: zh ? '模块' : 'Modules', link: `${prefix}/guide/modules` },
        { text: zh ? 'PPA 分析' : 'PPA Analysis', link: `${prefix}/guide/analysis-condition` },
      ],
    },
    {
      text: 'UInt',
      collapsed: false,
      items: [{ text: overview, link: `${prefix}/uint/` }, ...modules('uint', uintModules)],
    },
    {
      text: 'Fp',
      collapsed: false,
      items: [{ text: overview, link: `${prefix}/fp/` }, ...modules('fp', fpModules)],
    },
  ]
}

const buildNav = (prefix: string, zh: boolean) => [
  { text: zh ? '简介' : 'Introduction', link: `${prefix}/guide/introduction` },
  { text: zh ? '快速开始' : 'Quick Start', link: `${prefix}/guide/quick-start` },
]

/**
 * The base path the site is served from. GitHub Pages serves the project site under `/harith/`,
 * while local development stays at the root. The value always ends with a slash.
 */
const base = (process.env.DOCS_BASE ?? '/').replace(/\/*$/, '/')

/**
 * Redirect a Chinese browser to the Chinese home on its first visit.
 *
 * The stored preference is written by the theme layout on every navigation, so
 * a visitor who explicitly switches language is never redirected again.
 */
const languageRedirect = `
(function () {
  try {
    if (localStorage.getItem('harith-lang')) return
    var lang = (navigator.language || navigator.userLanguage || 'en').toLowerCase()
    if (lang.indexOf('zh') !== 0) return
    var base = ${JSON.stringify(base)}
    if (location.pathname !== base) return
    location.replace(base + 'zh/')
  } catch (e) {}
})()
`

export default defineConfig({
  title: 'Harith',
  description: 'DSP library for Chisel',
  base,
  cleanUrls: true,
  head: [['script', {}, languageRedirect]],
  locales: {
    root: {
      label: 'English',
      lang: 'en',
      link: '/',
      themeConfig: {
        nav: buildNav('', false),
        sidebar: buildSidebar('', false),
        outline: { level: [2, 3] },
        footer: {
          message: 'Released under the MIT License.',
          copyright: 'Copyright hamster5295',
        },
      },
    },
    zh: {
      label: '简体中文',
      lang: 'zh-Hans',
      link: '/zh/',
      description: '面向 Chisel 的 DSP 库',
      markdown: {
        container: {
          tipLabel: '提示',
          warningLabel: '警告',
          dangerLabel: '危险',
          infoLabel: '信息',
          detailsLabel: '详细信息',
        },
        codeCopyButton: {
          tooltipText: '复制代码',
          copiedText: '已复制',
        },
      },
      themeConfig: {
        nav: buildNav('/zh', true),
        sidebar: buildSidebar('/zh', true),
        outline: { label: '本页目录', level: [2, 3] },
        docFooter: { prev: '上一页', next: '下一页' },
        returnToTopLabel: '返回顶部',
        sidebarMenuLabel: '目录',
        darkModeSwitchLabel: '外观',
        lightModeSwitchTitle: '切换到浅色模式',
        darkModeSwitchTitle: '切换到深色模式',
        langMenuLabel: '切换语言',
        skipToContentLabel: '跳转到内容',
        notFound: {
          title: '页面未找到',
          quote: '但如果你不改变方向，并且继续寻找，你或许终会到达你正前往的地方。',
          linkText: '回到首页',
          linkLabel: '回到首页',
        },
        footer: {
          message: '基于 MIT 许可证发布。',
          copyright: '版权所有 hamster5295',
        },
      },
    },
  },
  themeConfig: {
    socialLinks: [
      { icon: 'github', link: 'https://codeberg.org/hamster5295/harith' },
    ],
    search: {
      provider: 'local',
      options: {
        locales: {
          zh: {
            translations: {
              button: { buttonText: '搜索', buttonAriaLabel: '搜索' },
              modal: {
                displayDetails: '显示详情',
                resetButtonTitle: '清除查询条件',
                backButtonTitle: '关闭搜索',
                noResultsText: '未找到结果',
                footer: {
                  selectText: '选择',
                  selectKeyAriaLabel: '回车',
                  navigateText: '切换',
                  navigateUpKeyAriaLabel: '上箭头',
                  navigateDownKeyAriaLabel: '下箭头',
                  closeText: '关闭',
                  closeKeyAriaLabel: 'Esc',
                },
              },
            },
          },
        },
      },
    },
  },
})
