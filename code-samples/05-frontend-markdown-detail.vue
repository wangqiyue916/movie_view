<!--
============================================================
PPT代码展示 5 / 7 — 前端长评详情页 Markdown 渲染
郭俊岑 负责：长评社区与讨论模块
============================================================

【核心问题】如何在前端将 Markdown 文本渲染为美观的文章页面？

我的实现方案：
  ① 使用 marked 库将 Markdown 转为 HTML
  ② 使用 highlight.js 实现代码块语法高亮
  ③ 注册自定义 renderer 覆盖 marked 默认行为，注入高亮 CSS 类名
  ④ 所有样式通过 :deep() 穿透 scoped 限制，覆盖生成的 HTML 样式
  ⑤ 用 computed 属性实现响应式渲染，text 变化时自动更新

【技能点】
  - Vue 3 Composition API（ref / computed / watch / onMounted）
  - 第三方库集成（marked + highlight.js）
  - 安全渲染（v-html 使用前提：内容来自后端、已校验）
  - 黑金主题全局样式 :deep() 穿透 -->
-->

<template>
  <!-- ===== 正文渲染区 ===== -->
  <section class="article-body">
    <!-- v-html 绑定 computed 属性，marked 库将 md 转 html -->
    <div class="markdown-content" v-html="renderedMd"></div>
  </section>

  <!-- ===== 互动操作栏 ===== -->
  <footer class="action-bar">
    <!-- 点赞按钮：liked 为 true 时高亮红色 -->
    <button :class="['action-btn', { active: review.liked }]"
            @click="toggleLike">
      {{ review.liked ? '❤️' : '🤍' }} 点赞 {{ review.likeCount }}
    </button>
    <!-- 收藏按钮：favorited 为 true 时高亮金色 -->
    <button :class="['action-btn', { active: review.favorited }]"
            @click="toggleFavorite">
      {{ review.favorited ? '⭐' : '☆' }} 收藏 {{ review.favoriteCount }}
    </button>
    <!-- 举报按钮：弹出模态框填写举报原因 -->
    <button class="action-btn report-btn" @click="showReportDialog = true">
      🚩 举报
    </button>
  </footer>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'  // 代码高亮主题

// ===== Markdown 渲染配置 =====
marked.use({
  gfm: true,        // GitHub Flavored Markdown：表格、删除线等
  breaks: true,     // 单换行即 <br>
  renderer: {
    // 自定义代码块渲染器：注入语法高亮
    code({ text, lang }: { text: string; lang?: string }): string {
      const language = lang && hljs.getLanguage(lang) ? lang : null
      let highlighted: string
      if (language) {
        highlighted = hljs.highlight(text, { language }).value
      } else {
        highlighted = hljs.highlightAuto(text).value
      }
      // 返回带 CSS 类名的 <pre><code>，样式表中用 :deep() 穿透
      const langAttr = language ? ` class="language-${language}"` : ''
      return `<pre><code${langAttr}>${highlighted}</code></pre>\n`
    }
  }
})

// ===== 响应式数据 =====
const review = ref<LongReviewVO | null>(null)

// computed 属性：当 review.contentMd 变化时自动重新渲染
const renderedMd = computed(() => {
  if (!review.value?.contentMd) return ''
  return marked.parse(review.value.contentMd) as string
})

// 点赞 Toggle：调用后端 POST 接口，前端即时更新 UI
async function toggleLike() {
  if (!review.value) return
  await likeReview(review.value.id)       // 后端 Toggle API
  review.value.liked = !review.value.liked
  review.value.likeCount += review.value.liked ? 1 : -1
}

// 收藏 Toggle：同上
async function toggleFavorite() { /* ... */ }

// 举报提交：打开模态框 → 填写原因 → 提交后端
async function submitReport() { /* ... */ }

onMounted(async () => {
  // 根据路由参数 reviewId 请求后端获取详情
  const res = await getReviewDetail(reviewId.value)
  review.value = res
})
</script>

<style scoped>
/* ===== 使用 :deep() 穿透 scoped，覆盖 marked 生成的 HTML 样式 ===== */

/* 标题：黑金衬线字体 */
.markdown-content :deep(h2) {
  font-family: "Noto Serif SC", serif;
  font-size: 22px;
  color: #f0dca0;
  border-bottom: 1px solid rgb(214 176 95 / 20%);
  padding-bottom: 8px;
}

/* 引用块：金色左边框 + 半透明背景 */
.markdown-content :deep(blockquote) {
  border-left: 3px solid rgb(214 176 95 / 40%);
  padding: 8px 16px;
  background: rgb(214 176 95 / 4%);
  color: #c6b78f;
}

/* 代码块：深色终端风格 */
.markdown-content :deep(pre) {
  background: rgb(0 0 0 / 40%);
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 6px;
  padding: 16px;
  overflow-x: auto;
}

/* 内联代码：金色高亮 */
.markdown-content :deep(code) {
  background: rgb(214 176 95 / 12%);
  color: #e8c16d;
  padding: 2px 6px;
  border-radius: 4px;
}
</style>

<!--
【PPT讲解提示】
"这一页展示了前端长评详情页的核心实现。
 我用 marked + highlight.js 这两个第三方库实现了 Markdown 渲染。
 marked.setOptions 里配置了 GFM 和 breaks 选项来支持表格和换行；
 highlight.js 覆盖了 marked 的 code 渲染器，实现了代码块自动语法高亮。
 关键技巧是 Vue 的 computed 属性——当 review.contentMd 变化时，
 renderedMd 自动重新计算并更新 DOM。
 样式方面，marked 生成的 HTML 没有 scoped 属性，
 所以我用 :deep() 进行样式穿透来给标题、引用块、代码块加上黑金风格。
 这个设计实现了'编辑 Markdown → 即时预览美观文章'的完整体验。"
-->