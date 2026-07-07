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
  } catch {
    news.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.news-detail-page {
  padding-top: 28px;
  padding-bottom: 48px;
}

.loading,
.empty {
  text-align: center;
  padding: 80px 0;
  color: #9ca3af;
  font-size: 15px;
}

.empty a {
  color: #2563eb;
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
  color: #2563eb;
  font-size: 14px;
}

.cover-wrapper {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
}

.cover-wrapper img {
  width: 100%;
  max-height: 420px;
  object-fit: cover;
  display: block;
}

.article-header {
  margin-bottom: 20px;
}

.category-label {
  display: inline-block;
  padding: 2px 12px;
  margin-bottom: 10px;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  font-size: 12px;
}

.article-header h1 {
  margin: 0 0 12px;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.35;
}

.article-meta {
  display: flex;
  gap: 16px;
  color: #9ca3af;
  font-size: 13px;
}

.article-summary {
  padding: 14px 18px;
  margin-bottom: 24px;
  border-left: 3px solid #2563eb;
  background: #f8fafc;
  border-radius: 0 6px 6px 0;
}

.article-summary p {
  margin: 0;
  color: #475569;
  font-size: 15px;
  line-height: 1.7;
}

.article-content {
  font-size: 16px;
  line-height: 1.85;
  color: #1f2937;
}

.article-content :deep(p) {
  margin: 0 0 16px;
}

.article-relations {
  margin-top: 36px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.article-relations h3 {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
}

.relations-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.relation-tag {
  padding: 4px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 999px;
  font-size: 13px;
  color: #4b5563;
  background: #f9fafb;
}

@media (max-width: 640px) {
  .article-header h1 {
    font-size: 22px;
  }
}
</style>