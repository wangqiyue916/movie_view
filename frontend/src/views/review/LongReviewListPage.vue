<template>
  <div class="page long-review-list-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">长评列表</h1>
        <router-link to="/long-reviews/create" class="btn btn-primary">写长评</router-link>
      </div>

      <div class="toolbar">
        <div class="sort-tabs">
          <button
            :class="['tab-btn', { active: sortBy === 'createdAt' }]"
            @click="sortBy = 'createdAt'; fetchReviews()"
          >
            最新
          </button>
          <button
            :class="['tab-btn', { active: sortBy === 'hot' }]"
            @click="sortBy = 'hot'; fetchReviews()"
          >
            最热
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading-skeleton">
        <SkeletonCard v-for="n in 5" :key="n" height="140px" />
      </div>

      <div v-else-if="reviews.length === 0" class="empty-state">
        <p>暂无长评</p>
      </div>

      <div v-else class="review-list">
        <div
          v-for="review in reviews"
          :key="review.id"
          class="review-card"
          @click="goToDetail(review.id)"
        >
          <div v-if="review.coverUrl" class="review-cover">
            <img :src="review.coverUrl" :alt="review.title" />
          </div>
          <div class="review-body">
            <h3 class="review-title">{{ review.title }}</h3>
            <div class="review-meta">
              <span class="author">
                <img v-if="review.authorAvatar" :src="review.authorAvatar" class="avatar-sm" />
                {{ review.authorNickname }}
              </span>
              <span class="movie-name">
                相关电影：{{ review.movieTitle }}
              </span>
            </div>
            <p class="review-excerpt">{{ getExcerpt(review.contentMd) }}</p>
            <div class="review-stats">
              <span class="stat">
                <i class="icon-thumb-up"></i> {{ review.likeCount }}
              </span>
              <span class="stat">
                <i class="icon-comment"></i> {{ review.replyCount }}
              </span>
              <span class="stat">
                <i class="icon-eye"></i> {{ review.viewCount }}
              </span>
              <span class="stat date">{{ formatDate(review.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="total > pageSize" class="pagination">
        <button
          :disabled="page <= 1"
          @click="page--; fetchReviews()"
          class="btn btn-sm"
        >
          上一页
        </button>
        <span class="page-info">{{ page }} / {{ totalPages }}</span>
        <button
          :disabled="page >= totalPages"
          @click="page++; fetchReviews()"
          class="btn btn-sm"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getReviewList, type LongReviewVO } from '@/api/longReviewApi'
import SkeletonCard from '@/components/common/SkeletonCard.vue'

const route = useRoute()
const router = useRouter()

const reviews = ref<LongReviewVO[]>([])
const loading = ref(true)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const sortBy = ref('createdAt')

const movieId = computed(() => {
  const id = route.query.movieId
  return id ? Number(id) : undefined
})

const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)

async function fetchReviews() {
  loading.value = true
  try {
    const res: any = await getReviewList({
      movieId: movieId.value,
      sortBy: sortBy.value,
      page: page.value,
      pageSize,
    })
    // Axios interceptor unwraps: res is { list, total, ... }
    reviews.value = res.list || []
    total.value = res.total || 0
  } catch (err) {
    console.error('Failed to fetch reviews:', err)
  } finally {
    loading.value = false
  }
}

function getExcerpt(md: string): string {
  const text = md.replace(/[#*>`\[\]()!\-|]/g, '').replace(/\n/g, ' ')
  return text.length > 150 ? text.substring(0, 150) + '...' : text
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

function goToDetail(id: number) {
  router.push(`/long-reviews/${id}`)
}

onMounted(() => {
  fetchReviews()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@600;700&display=swap');

/* ===== 页面背景：黑金风格 ===== */
.long-review-list-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 8% 18%, rgb(214 176 95 / 8%), transparent 24%),
    radial-gradient(circle at 92% 26%, rgb(214 176 95 / 6%), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0e0c08 46%, #050505 100%);
  padding: 32px 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 16px;
}

/* ===== 页面标题区 ===== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgb(214 176 95 / 18%);
}

.page-title {
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: clamp(22px, 2.8vw, 28px);
  color: #e8c16d;
  position: relative;
  padding-left: 16px;
}

.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 22px;
  background: #d6b05f;
  border-radius: 2px;
}

/* ===== 按钮：金色主题 ===== */
.btn {
  padding: 10px 24px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}

.btn-primary {
  background: linear-gradient(135deg, #d6b05f, #c89a4a);
  color: #1a1a1a;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #e8c16d, #d6b05f);
  box-shadow: 0 2px 12px rgb(214 176 95 / 30%);
}

.btn-sm {
  padding: 6px 16px;
  font-size: 13px;
  background: rgb(255 255 255 / 6%);
  color: #d8c69b;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 6px;
}

.btn-sm:hover:not(:disabled) {
  border-color: #d6b05f;
  color: #e8c16d;
}

.btn-sm:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

/* ===== 排序 Tab ===== */
.toolbar {
  margin-bottom: 20px;
}

.sort-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  padding: 8px 20px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 20px;
  background: rgb(255 255 255 / 4%);
  color: #b9ab90;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.tab-btn:hover {
  border-color: rgb(214 176 95 / 40%);
  color: #d8c69b;
}

.tab-btn.active {
  background: linear-gradient(135deg, #d6b05f, #c89a4a);
  color: #1a1a1a;
  border-color: #d6b05f;
}

/* ===== 加载 / 空状态 ===== */
.loading-skeleton {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 20px 0;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #b9ab90;
  font-size: 15px;
}

/* ===== 长评卡片列表 ===== */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.review-card {
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 8px;
  padding: 20px;
  display: flex;
  gap: 16px;
  cursor: pointer;
  transition: border-color 0.2s, transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 12px 28px rgb(0 0 0 / 34%);
}

.review-card:hover {
  border-color: rgb(214 176 95 / 35%);
  transform: translateY(-2px);
  box-shadow: 0 16px 36px rgb(0 0 0 / 50%);
}

.review-cover {
  flex-shrink: 0;
  width: 160px;
  height: 100px;
  border-radius: 6px;
  overflow: hidden;
}

.review-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.review-card:hover .review-cover img {
  transform: scale(1.04);
}

.review-body {
  flex: 1;
  min-width: 0;
}

.review-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff8e6;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.review-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #c6b78f;
  margin-bottom: 8px;
}

.author {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #d8c69b;
}

.avatar-sm {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  object-fit: cover;
}

.movie-name {
  color: #b9ab90;
}

.review-excerpt {
  font-size: 14px;
  color: #b9ab90;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.review-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #c6b78f;
}

.stat {
  display: flex;
  align-items: center;
  gap: 4px;
}

.date {
  margin-left: auto;
  color: #8a7d66;
}

/* ===== 分页 ===== */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
}

.page-info {
  font-size: 14px;
  color: #c6b78f;
}
</style>
