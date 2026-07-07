<template>
  <div class="page news-detail-page">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="!news" class="empty">
      <p>资讯不存在或已下架</p>
      <router-link to="/news">← 返回资讯列表</router-link>
    </div>
    <article v-else class="news-article">
      <router-link to="/news" class="back-link">← 返回资讯列表</router-link>

      <div v-if="news.coverUrl" class="cover-wrapper">
        <img :src="news.coverUrl" :alt="news.title" />
      </div>

      <header class="article-header">
        <span v-if="news.category" class="category-label">{{ news.category }}</span>
        <h1>{{ news.title }}</h1>
        <div class="article-meta">
          <span>{{ formatDate(news.publishedAt) }}</span>
          <span v-if="news.source">来源：{{ news.source }}</span>
          <span>{{ news.viewCount }} 阅读</span>
        </div>
      </header>

      <div v-if="news.summary" class="article-summary">
        <p>{{ news.summary }}</p>
      </div>

      <div class="article-content" v-html="renderedContent"></div>

      <!-- 互动操作栏 -->
      <div class="article-actions">
        <button class="action-btn" :class="{ active: isLiked }" @click="toggleLike">
          <span class="action-icon">{{ isLiked ? '❤️' : '🤍' }}</span>
          <span>{{ likeCount }}</span>
        </button>
        <button class="action-btn" :class="{ active: isFavorited }" @click="toggleFavorite">
          <span class="action-icon">{{ isFavorited ? '⭐' : '☆' }}</span>
          <span>{{ isFavorited ? '已收藏' : '收藏' }}</span>
        </button>
        <button class="action-btn" @click="handleShare">
          <span class="action-icon">↗️</span>
          <span>分享</span>
        </button>
      </div>

      <div v-if="relations.length > 0" class="article-relations">
        <h3>关联内容</h3>
        <div class="relations-list">
          <span
            v-for="rel in relations"
            :key="rel.id"
            class="relation-tag"
          >
            {{ rel.targetName }}
          </span>
        </div>
      </div>
    </article>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { homeApi, type NewsArticle, type NewsRelation } from '@/api/homeApi'

const route = useRoute()
const news = ref<NewsArticle | null>(null)
const relations = ref<NewsRelation[]>([])
const loading = ref(true)
const isLiked = ref(false)
const likeCount = ref(0)
const isFavorited = ref(false)

async function toggleLike() {
  if (!news.value) return
  try {
    const res = await homeApi.toggleLike(news.value.id)
    isLiked.value = res.liked
    likeCount.value = res.likeCount
  } catch {
    // 降级：纯前端切换
    isLiked.value = !isLiked.value
    likeCount.value = isLiked.value ? 1 : 0
  }
}

async function toggleFavorite() {
  if (!news.value) return
  try {
    const res = await homeApi.toggleFavorite(news.value.id)
    isFavorited.value = res.favorited
  } catch {
    isFavorited.value = !isFavorited.value
  }
}

function handleShare() {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    alert('链接已复制到剪贴板')
  }).catch(() => {
    prompt('复制以下链接分享：', url)
  })
}

const renderedContent = computed(() => {
  if (!news.value?.content) return ''
  return news.value.content
    .replace(/&/g, '&')
    .replace(/</g, '<')
    .replace(/>/g, '>')
    .replace(/\n\n/g, '</p><p>')
    .replace(/\n/g, '<br>')
    .replace(/^/, '<p>')
    .replace(/$/, '</p>')
})

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

onMounted(async () => {
  const newsId = Number(route.params.id)
  try {
    const [detail, rels] = await Promise.all([
      homeApi.getNewsDetail(newsId),
      homeApi.getNewsRelations(newsId),
    ])
    news.value = detail
    relations.value = rels

    // 获取初始互动状态
    try {
      const [likeRes, favRes] = await Promise.all([
        homeApi.getLikeStatus(newsId),
        homeApi.getFavoriteStatus(newsId),
      ])
      isLiked.value = likeRes.isLiked
      likeCount.value = likeRes.likeCount
      isFavorited.value = favRes.isFavorited
    } catch {
      // 降级：互动状态保持默认值
    }
  } catch {
    news.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.news-detail-page {
  min-height: calc(100vh - 64px);
  padding: 36px max(22px, calc((100vw - 1280px) / 2)) 72px;
  color: #f7edd5;
  background: #050505;
}

.loading,
.empty {
  text-align: center;
  padding: 80px 0;
  color: #9a8b6e;
  font-size: 15px;
}

.empty a {
  color: #e8c16d;
  margin-top: 12px;
  display: inline-block;
}

.news-article {
  max-width: 780px;
  margin: 0 auto;
}

.back-link {
  display: inline-block;
  margin-bottom: 20px;
  color: #e8c16d;
  font-size: 14px;
  transition: color 180ms;
}

.back-link:hover {
  color: #f3d58c;
}

.cover-wrapper {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgb(214 176 95 / 22%);
}

.cover-wrapper img {
  width: 100%;
  max-height: 420px;
  object-fit: cover;
  display: block;
}

.article-header {
  margin-bottom: 24px;
}

.category-label {
  display: inline-block;
  padding: 3px 14px;
  margin-bottom: 12px;
  border-radius: 999px;
  background: rgb(214 176 95 / 82%);
  color: #050505;
  font-size: 12px;
  font-weight: 600;
}

.article-header h1 {
  margin: 0 0 14px;
  color: #fff8e6;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 30px;
  font-weight: 800;
  line-height: 1.4;
  text-shadow: 0 0 16px rgb(214 176 95 / 18%);
}

.article-meta {
  display: flex;
  gap: 18px;
  color: #8a7b60;
  font-size: 13px;
}

.article-summary {
  padding: 16px 20px;
  margin-bottom: 28px;
  border-left: 3px solid #d6b05f;
  background: rgb(214 176 95 / 6%);
  border-radius: 0 6px 6px 0;
}

.article-summary p {
  margin: 0;
  color: #d8c69b;
  font-size: 15px;
  line-height: 1.8;
}

.article-content {
  font-size: 16px;
  line-height: 1.9;
  color: #e0d3b0;
}

.article-content :deep(p) {
  margin: 0 0 18px;
}

.article-relations {
  margin-top: 40px;
  padding-top: 22px;
  border-top: 1px solid rgb(214 176 95 / 22%);
}

.article-relations h3 {
  margin: 0 0 14px;
  color: #e8c16d;
  font-size: 16px;
  font-weight: 600;
}

.relations-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.relation-tag {
  padding: 5px 16px;
  border: 1px solid rgb(214 176 95 / 28%);
  border-radius: 999px;
  font-size: 13px;
  color: #c6b78f;
  background: rgb(255 255 255 / 4%);
}

.article-actions {
  display: flex;
  gap: 12px;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid rgb(214 176 95 / 22%);
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: 1px solid rgb(214 176 95 / 28%);
  border-radius: 8px;
  background: rgb(255 255 255 / 4%);
  color: #c6b78f;
  font-size: 14px;
  cursor: pointer;
  transition: border-color 180ms, background 180ms, color 180ms;
}

.action-btn:hover {
  border-color: #d6b05f;
  background: rgb(214 176 95 / 10%);
  color: #e8c16d;
}

.action-btn.active {
  border-color: #d6b05f;
  background: rgb(214 176 95 / 14%);
  color: #e8c16d;
}

.action-icon {
  font-size: 16px;
}

@media (max-width: 640px) {
  .article-header h1 {
    font-size: 24px;
  }
}
</style>
