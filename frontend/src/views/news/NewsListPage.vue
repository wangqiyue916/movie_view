<template>
  <div class="page news-list-page">
    <div class="news-header">
      <h1>电影资讯</h1>
      <div class="news-filters">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索资讯标题..."
          class="search-input"
          @keyup.enter="search"
        />
        <select v-model="category" class="category-select" @change="search">
          <option value="">全部分类</option>
          <option value="新片动态">新片动态</option>
          <option value="平台活动">平台活动</option>
          <option value="票房观察">票房观察</option>
          <option value="幕后花絮">幕后花絮</option>
          <option value="演员动态">演员动态</option>
          <option value="获奖信息">获奖信息</option>
          <option value="行业观察">行业观察</option>
          <option value="周边资讯">周边资讯</option>
        </select>
      </div>
    </div>

    <!-- 骨架屏加载状态（潘玺名，2025-07-08）：数据加载期间显示占位卡片，避免白屏 -->
    <div v-if="loading" class="loading-skeleton">
      <SkeletonCard v-for="n in 6" :key="n" height="200px" />
    </div>

    <div v-else-if="newsList.length === 0" class="empty">
      <p>暂无资讯</p>
    </div>

    <div v-else class="news-grid">
      <article
        v-for="news in newsList"
        :key="news.id"
        class="news-card"
        @click="goDetail(news.id)"
      >
        <div class="news-card-cover">
          <img
            :src="news.coverUrl || 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=85'"
            :alt="news.title"
          />
          <span v-if="news.category" class="news-category-tag">{{ news.category }}</span>
        </div>
        <div class="news-card-body">
          <h2>{{ news.title }}</h2>
          <p class="news-summary">{{ news.summary }}</p>
          <div class="news-meta">
            <span>{{ formatDate(news.publishedAt) }}</span>
            <span>{{ news.viewCount }} 阅读</span>
          </div>
        </div>
      </article>
    </div>

    <div v-if="total > pageSize" class="pagination">
      <button :disabled="page <= 1" @click="goPage(page - 1)">上一页</button>
      <span>第 {{ page }} / {{ totalPages }} 页</span>
      <button :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { homeApi, type NewsArticle } from '@/api/homeApi'
import SkeletonCard from '@/components/common/SkeletonCard.vue'

const router = useRouter()

const newsList = ref<NewsArticle[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const keyword = ref('')
const category = ref('')

const totalPages = ref(1)

async function fetchNews() {
  loading.value = true
  try {
    const res = await homeApi.getNewsList({
      page: page.value,
      pageSize,
      keyword: keyword.value || undefined,
      category: category.value || undefined,
    })
    newsList.value = res.list
    total.value = res.total
    totalPages.value = Math.ceil(res.total / pageSize) || 1
  } catch {
    newsList.value = []
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  fetchNews()
}

function goPage(p: number) {
  page.value = p
  fetchNews()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goDetail(id: number) {
  router.push(`/news/${id}`)
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

onMounted(() => {
  fetchNews()
})
</script>

<style scoped>
.news-list-page {
  min-height: calc(100vh - 64px);
  padding: 36px max(22px, calc((100vw - 1280px) / 2)) 72px;
  color: #f7edd5;
  background: #050505;
}

.news-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 28px;
}

.news-header h1 {
  margin: 0;
  color: #e8c16d;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 28px;
  font-weight: 800;
  text-shadow: 0 0 16px rgb(214 176 95 / 32%);
}

.news-filters {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 220px;
  height: 38px;
  padding: 0 14px;
  border: 1px solid rgb(214 176 95 / 32%);
  border-radius: 6px;
  font-size: 14px;
  color: #f7edd5;
  background: rgb(255 255 255 / 6%);
  outline: none;
  transition: border-color 180ms;
}

.search-input::placeholder {
  color: rgb(214 176 95 / 38%);
}

.search-input:focus {
  border-color: #d6b05f;
}

.category-select {
  height: 38px;
  padding: 0 12px;
  border: 1px solid rgb(214 176 95 / 32%);
  border-radius: 6px;
  font-size: 14px;
  color: #f7edd5;
  background: rgb(255 255 255 / 6%);
  outline: none;
}

.category-select option {
  background: #1a1a1a;
  color: #f7edd5;
}

.loading-skeleton {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.empty {
  text-align: center;
  padding: 80px 0;
  color: #9a8b6e;
  font-size: 15px;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.news-card {
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  overflow: hidden;
  cursor: pointer;
  transition: border-color 180ms ease, transform 180ms ease, box-shadow 180ms ease;
}

.news-card:hover {
  border-color: rgb(214 176 95 / 52%);
  box-shadow: 0 8px 28px rgb(214 176 95 / 14%);
  transform: translateY(-2px);
}

.news-card-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: #111;
}

.news-card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-category-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 2px 10px;
  border-radius: 999px;
  background: rgb(214 176 95 / 82%);
  color: #050505;
  font-size: 12px;
  font-weight: 600;
}

.news-card-body {
  padding: 14px 16px 16px;
}

.news-card-body h2 {
  margin: 0 0 8px;
  color: #fff6df;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.news-summary {
  margin: 0 0 12px;
  color: #b9ab90;
  font-size: 13px;
  line-height: 1.55;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.news-meta {
  display: flex;
  gap: 14px;
  color: #8a7b60;
  font-size: 12px;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 36px;
  color: #c6b78f;
  font-size: 14px;
}

.pagination button {
  height: 36px;
  padding: 0 18px;
  border: 1px solid rgb(214 176 95 / 32%);
  border-radius: 6px;
  background: rgb(255 255 255 / 5%);
  color: #e8c16d;
  font-size: 14px;
  cursor: pointer;
  transition: border-color 180ms, background 180ms;
}

.pagination button:hover:not(:disabled) {
  border-color: #d6b05f;
  background: rgb(214 176 95 / 13%);
}

.pagination button:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

@media (max-width: 640px) {
  .news-grid {
    grid-template-columns: 1fr;
  }

  .news-filters {
    flex-direction: column;
    width: 100%;
  }

  .search-input {
    width: 100%;
  }
}
</style>
