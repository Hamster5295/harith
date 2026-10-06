<script setup lang="ts">
import DefaultTheme from 'vitepress/theme'
import { inBrowser, useData } from 'vitepress'
import { watchEffect } from 'vue'

const { localeIndex } = useData()

if (inBrowser) {
  watchEffect(() => {
    localStorage.setItem('harith-lang', localeIndex.value === 'zh' ? 'zh' : 'en')
  })
}
</script>

<template>
  <DefaultTheme.Layout />
</template>
