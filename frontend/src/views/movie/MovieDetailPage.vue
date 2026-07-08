<template>
  <div class="page movie-detail-page">
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else-if="!movie" class="empty-state">
      <el-empty description="电影不存在或已下架" />
      <router-link to="/movies" class="back-link">← 返回电影列表</router-link>
    </div>

    <template v-else>
      <!-- Hero Section -->
      <div class="hero-section">
        <div class="hero-backdrop" :style="{ backgroundImage: `url(${movie.posterUrl})` }" />
        <div class="hero-overlay" />
        <div class="hero-content">
          <div class="hero-poster">
            <img :src="movie.posterUrl" :alt="movie.title" />
          </div>
          <div class="hero-info">
            <h1 class="movie-title">{{ movie.title }}</h1>
            <p v-if="movie.originalTitle" class="original-title">{{ movie.originalTitle }}</p>
            <div class="movie-meta">
              <span v-if="movie.genres" class="meta-tag">{{ movie.genres }}</span>
              <span v-if="movie.releaseDate" class="meta-item">{{ movie.releaseDate }}</span>
              <span v-if="movie.region" class="meta-item">{{ movie.region }}</span>
              <span v-if="movie.durationMinutes" class="meta-item">{{ movie.durationMinutes }} 分钟</span>
            </div>
            <div v-if="movie.director" class="movie-crew">
              <span class="label">导演：</span>{{ movie.director }}
            </div>
            <div v-if="movie.actors" class="movie-crew">
              <span class="label">主演：</span>{{ movie.actors }}
            </div>
            <p v-if="movie.synopsis" class="synopsis">{{ movie.synopsis }}</p>
            <div v-if="movie.avgTotalScore" class="rating-badge">
              <span class="rating-score">{{ movie.avgTotalScore }}</span>
              <span class="rating-stars">{{ renderStars(movie.avgTotalScore) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Rating Scores -->
      <div v-if="movie.avgTotalScore" class="section rating-section">
        <h2 class="section-title">评分详情</h2>
        <div class="score-cards">
          <div class="score-card">
            <span class="score-label">综合</span>
            <span class="score-value">{{ movie.avgTotalScore }}</span>
          </div>
          <div v-if="movie.avgStoryScore" class="score-card">
            <span class="score-label">剧情</span>
            <span class="score-value">{{ movie.avgStoryScore }}</span>
          </div>
          <div v-if="movie.avgVisualScore" class="score-card">
            <span class="score-label">视觉</span>
            <span class="score-value">{{ movie.avgVisualScore }}</span>
          </div>
          <div v-if="movie.avgActingScore" class="score-card">
            <span class="score-label">演技</span>
            <span class="score-value">{{ movie.avgActingScore }}</span>
          </div>
        </div>
      </div>

      <!-- Hot Interpretation Videos -->
      <div class="section video-section">
        <h2 class="section-title">热门解读视频</h2>
        <div v-if="videosLoading" class="section-loading">
          <el-skeleton :rows="1" animated />
        </div>
        <div v-else-if="videos.length === 0" class="section-empty">
          <span>暂无解读视频</span>
        </div>
        <div v-else class="video-scroll-row">
          <div
            v-for="video in videos"
            :key="video.id"
            class="video-card"
            @click="handleVideoClick(video)"
          >
            <div class="video-cover">
              <img :src="video.coverUrl || defaultCover" :alt="video.title" />
              <span class="video-platform">{{ video.platform }}</span>
            </div>
            <div class="video-info">
              <h4 class="video-title">{{ video.title }}</h4>
              <span v-if="video.heatScore" class="video-heat">🔥 {{ video.heatScore }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Related Merchandise -->
      <div class="section merchandise-section">
        <h2 class="section-title">相关周边</h2>
        <div v-if="merchandiseLoading" class="section-loading">
          <el-skeleton :rows="1" animated />
        </div>
        <div v-else-if="merchandise.length === 0" class="section-empty">
          <span>暂无相关周边</span>
        </div>
        <div v-else class="merchandise-grid">
          <div
            v-for="item in merchandise"
            :key="item.id"
            class="merchandise-card"
            @click="handleMerchandiseClick(item)"
          >
            <div class="merchandise-image">
              <img :src="item.imageUrl" :alt="item.name" />
            </div>
            <div class="merchandise-info">
              <h4 class="merchandise-name">{{ item.name }}</h4>
              <div class="merchandise-meta">
                <span class="merchandise-price">¥{{ item.price }}</span>
                <span class="merchandise-platform">{{ item.platform }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Related News -->
      <div class="section news-section">
        <h2 class="section-title">相关资讯</h2>
        <div v-if="newsLoading" class="section-loading">
          <el-skeleton :rows="1" animated />
        </div>
        <div v-else-if="newsList.length === 0" class="section-empty">
          <span>暂无相关资讯</span>
        </div>
        <div v-else class="news-mini-grid">
          <article
            v-for="news in newsList"
            :key="news.id"
            class="news-mini-card"
            @click="goNewsDetail(news.id)"
          >
            <div class="news-mini-cover">
              <img :src="news.coverUrl || defaultCover" :alt="news.title" />
            </div>
            <div class="news-mini-body">
              <h4>{{ news.title }}</h4>
              <p>{{ news.summary }}</p>
            </div>
          </article>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { movieApi } from '@/api/movieApi'
import { homeApi, type NewsArticle } from '@/api/homeApi'
import { videoApi, type VideoItem } from '@/api/videoApi'
import { merchandiseApi, type MerchandiseItem } from '@/api/merchandiseApi'

const route = useRoute()
const router = useRouter()

const movie = ref<Record<string, unknown> | null>(null)
const loading = ref(true)
const videos = ref<VideoItem[]>([])
const videosLoading = ref(false)
const merchandise = ref<MerchandiseItem[]>([])
const merchandiseLoading = ref(false)
const newsList = ref<NewsArticle[]>([])
const newsLoading = ref(false)

const defaultCover = 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=85'

const mockMovie: Record<string, unknown> = {
  id: 1,
  title: '星际穿越',
  originalTitle: 'Interstellar',
  director: '克里斯托弗·诺兰',
  actors: '马修·麦康纳, 安妮·海瑟薇',
  genres: '科幻, 冒险',
  releaseDate: '2014-11-12',
  region: '美国',
  durationMinutes: 169,
  synopsis: '在不远的未来，地球面临着严重的环境恶化，人类生存岌岌可危。前NASA宇航员约瑟夫·库珀被选中参与一项穿越虫洞的星际旅行，为人类寻找新的家园。',
  posterUrl: 'https://images.unsplash.com/photo-1534996858221-380b92700493?auto=format&fit=crop&w=1200&q=85',
  avgTotalScore: 9.4,
  avgStoryScore: 9.2,
  avgVisualScore: 9.6,
  avgActingScore: 9.3,
}

function renderStars(score: unknown): string {
  const n = Math.round(Number(score) || 0)
  return '★'.repeat(Math.min(n, 5)) + '☆'.repeat(Math.max(5 - n, 0))
}

function handleVideoClick(video: VideoItem) {
  if (video.externalUrl) {
    window.open(video.externalUrl, '_blank')
    videoApi.recordClick(video.id).catch(() => {})
  }
}

function handleMerchandiseClick(item: MerchandiseItem) {
  if (item.externalUrl) {
    window.open(item.externalUrl, '_blank')
  } else {
    router.push(`/merchandise/${item.id}`)
  }
}

function goNewsDetail(id: number) {
  router.push(`/news/${id}`)
}

async function fetchMovie() {
  const movieId = route.params.id as string
  try {
    const res = await movieApi.getMovieDetail(movieId)
    if (res && Object.keys(res).length > 0) {
      movie.value = res
    } else {
      movie.value = mockMovie
    }
  } catch {
    movie.value = mockMovie
  }
}

async function fetchVideos() {
  videosLoading.value = true
  const movieId = route.params.id as string
  try {
    const res = await videoApi.getByMovie(movieId)
    videos.value = Array.isArray(res) ? res : []
  } catch {
    videos.value = []
  } finally {
    videosLoading.value = false
  }
}

async function fetchMerchandise() {
  merchandiseLoading.value = true
  const movieId = route.params.id as string
  try {
    const res = await merchandiseApi.getByMovie(movieId)
    merchandise.value = Array.isArray(res) ? res : []
  } catch {
    merchandise.value = []
  } finally {
    merchandiseLoading.value = false
  }
}

async function fetchNews() {
  newsLoading.value = true
  const movieId = route.params.id as string
  try {
    const res = await homeApi.getNewsList({ movieId: Number(movieId), pageSize: 4 })
    newsList.value = res.list || []
  } catch {
    newsList.value = []
  } finally {
    newsLoading.value = false
  }
}

onMounted(async () => {
  loading.value = true
  await fetchMovie()
  loading.value = false
  fetchVideos()
  fetchMerchandise()
  fetchNews()
})
</script>

<style scoped>
.movie-detail-page {
  min-height: calc(100vh - 64px);
  color: #f7edd5;
  background: #050505;
  padding-bottom: 72px;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 120px 20px;
}

.empty-state .back-link {
  display: inline-block;
  margin-top: 16px;
  color: #e8c16d;
  font-size: 14px;
}

/* Hero Section */
.hero-section {
  position: relative;
  min-height: 480px;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-backdrop {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  filter: blur(20px) brightness(0.3);
  transform: scale(1.1);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgb(0 0 0 / 60%) 0%, rgb(0 0 0 / 90%) 100%);
}

.hero-content {
  position: relative;
  display: flex;
  gap: 40px;
  padding: 60px max(22px, calc((100vw - 1280px) / 2));
  align-items: flex-start;
}

.hero-poster {
  flex-shrink: 0;
  width: 260px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid rgb(214 176 95 / 30%);
  box-shadow: 0 8px 40px rgb(0 0 0 / 50%);
}

.hero-poster img {
  width: 100%;
  display: block;
}

.hero-info {
  flex: 1;
  padding-top: 8px;
}

.movie-title {
  margin: 0 0 8px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 36px;
  font-weight: 800;
  color: #fff8e6;
  text-shadow: 0 0 20px rgb(214 176 95 / 25%);
}

.original-title {
  margin: 0 0 16px;
  color: #9a8b6e;
  font-size: 16px;
  font-style: italic;
}

.movie-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
}

.meta-tag {
  padding: 3px 12px;
  border-radius: 999px;
  background: rgb(214 176 95 / 20%);
  color: #e8c16d;
  font-size: 13px;
}

.meta-item {
  color: #b9ab90;
  font-size: 14px;
}

.movie-crew {
  color: #c6b78f;
  font-size: 14px;
  margin-bottom: 8px;
}

.movie-crew .label {
  color: #8a7b60;
}

.synopsis {
  margin: 20px 0;
  color: #d8c69b;
  font-size: 15px;
  line-height: 1.8;
  max-width: 700px;
}

.rating-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  border: 1px solid rgb(214 176 95 / 36%);
  border-radius: 8px;
  background: rgb(214 176 95 / 8%);
}

.rating-score {
  font-size: 28px;
  font-weight: 700;
  color: #e8c16d;
}

.rating-stars {
  font-size: 18px;
  color: #d6b05f;
  letter-spacing: 2px;
}

/* Sections */
.section {
  padding: 40px max(22px, calc((100vw - 1280px) / 2)) 0;
}

.section-title {
  margin: 0 0 20px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 22px;
  font-weight: 700;
  color: #e8c16d;
  padding-left: 14px;
  border-left: 3px solid #d6b05f;
}

.section-loading {
  padding: 20px 0;
}

.section-empty {
  padding: 40px 0;
  text-align: center;
  color: #9a8b6e;
  font-size: 14px;
}

/* Rating Section */
.score-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
}

.score-card {
  text-align: center;
  padding: 20px 16px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  background: rgb(255 255 255 / 4%);
}

.score-label {
  display: block;
  color: #8a7b60;
  font-size: 13px;
  margin-bottom: 8px;
}

.score-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #e8c16d;
}

/* Video Scroll Row */
.video-scroll-row {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.video-scroll-row::-webkit-scrollbar {
  height: 4px;
}

.video-scroll-row::-webkit-scrollbar-thumb {
  background: rgb(214 176 95 / 30%);
  border-radius: 2px;
}

.video-card {
  flex-shrink: 0;
  width: 240px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  overflow: hidden;
  background: rgb(255 255 255 / 3%);
  cursor: pointer;
  transition: border-color 180ms, transform 180ms, box-shadow 180ms;
}

.video-card:hover {
  border-color: rgb(214 176 95 / 52%);
  box-shadow: 0 4px 20px rgb(214 176 95 / 14%);
  transform: translateY(-2px);
}

.video-cover {
  position: relative;
  height: 135px;
  background: #111;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-platform {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  border-radius: 4px;
  background: rgb(214 176 95 / 82%);
  color: #050505;
  font-size: 11px;
  font-weight: 600;
}

.video-info {
  padding: 10px 12px 12px;
}

.video-title {
  margin: 0 0 6px;
  font-size: 14px;
  font-weight: 500;
  color: #fff6df;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.video-heat {
  font-size: 12px;
  color: #c6b78f;
}

/* Merchandise Grid */
.merchandise-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.merchandise-card {
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  overflow: hidden;
  background: rgb(255 255 255 / 3%);
  cursor: pointer;
  transition: border-color 180ms, transform 180ms, box-shadow 180ms;
}

.merchandise-card:hover {
  border-color: rgb(214 176 95 / 52%);
  box-shadow: 0 4px 20px rgb(214 176 95 / 14%);
  transform: translateY(-2px);
}

.merchandise-image {
  height: 180px;
  background: #111;
}

.merchandise-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.merchandise-info {
  padding: 12px 14px;
}

.merchandise-name {
  margin: 0 0 8px;
  font-size: 14px;
  font-weight: 500;
  color: #fff6df;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
}

.merchandise-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.merchandise-price {
  font-size: 16px;
  font-weight: 700;
  color: #e8c16d;
}

.merchandise-platform {
  font-size: 12px;
  color: #8a7b60;
}

/* News Mini Grid */
.news-mini-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.news-mini-card {
  display: flex;
  gap: 12px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  overflow: hidden;
  background: rgb(255 255 255 / 3%);
  cursor: pointer;
  transition: border-color 180ms, transform 180ms;
}

.news-mini-card:hover {
  border-color: rgb(214 176 95 / 52%);
  transform: translateY(-2px);
}

.news-mini-cover {
  flex-shrink: 0;
  width: 100px;
  height: 80px;
  background: #111;
}

.news-mini-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-mini-body {
  padding: 10px 12px 10px 0;
  flex: 1;
  min-width: 0;
}

.news-mini-body h4 {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 500;
  color: #fff6df;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
}

.news-mini-body p {
  margin: 0;
  font-size: 12px;
  color: #8a7b60;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding-top: 40px;
  }

  .hero-poster {
    width: 200px;
  }

  .movie-meta {
    justify-content: center;
  }

  .movie-title {
    font-size: 26px;
  }

  .section {
    padding-left: 16px;
    padding-right: 16px;
  }
}
</style>
