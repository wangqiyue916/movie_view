<template>
  <div class="movie-detail-page" v-loading="loading">
    <!-- Hero -->
    <section v-if="movie" class="hero-section">
      <div class="hero-poster">
        <img :src="movie.posterUrl || 'https://via.placeholder.com/400x560/1a1a1a/d6b05f?text=No+Poster'" :alt="movie.title" />
      </div>
      <div class="hero-info">
        <h1 class="hero-title">{{ movie.title }}</h1>
        <p v-if="movie.originalTitle" class="hero-original-title">{{ movie.originalTitle }}</p>
        <div class="hero-meta">
          <span v-if="movie.genres">{{ movie.genres }}</span>
          <span v-if="movie.releaseDate">{{ movie.releaseDate }}</span>
          <span v-if="movie.region">{{ movie.region }}</span>
          <span v-if="movie.durationMinutes">{{ movie.durationMinutes }} 分钟</span>
        </div>
        <p v-if="movie.director" class="hero-attr"><em>导演：</em>{{ movie.director }}</p>
        <p v-if="movie.actors" class="hero-attr"><em>主演：</em>{{ movie.actors }}</p>
        <p v-if="movie.synopsis" class="hero-synopsis">{{ movie.synopsis }}</p>
        <p class="hero-views">{{ movie.viewCount }} 次浏览</p>
      </div>

      <aside class="hero-rating-column" aria-label="电影评分">
        <div class="rating-panel">
          <p class="side-panel-title">评分</p>
          <div class="rating-content">
            <div class="rating-main">
              <span class="big-score">{{ movie.avgTotalScore ?? '—' }}</span>
              <span class="big-score-label">总评分</span>
              <span class="rating-count">{{ movie.ratingCount }} 人评分</span>
            </div>
            <div class="rating-subs">
              <RatingDisplay :rating="movie.avgStoryScore" label="剧情" size="small" />
              <RatingDisplay :rating="movie.avgVisualScore" label="特效" size="small" />
              <RatingDisplay :rating="movie.avgActingScore" label="演技" size="small" />
            </div>
          </div>
        </div>

        <div v-if="userStore.isLogin" class="my-rating-panel">
          <p class="side-panel-title">我的评分</p>
          <div v-if="myCurrentRating" class="my-rating-display">
            <span class="my-score">{{ myCurrentRating.totalScore }}</span>
            <span class="my-score-sub">总评分</span>
            <el-button size="small" class="clear-btn" @click="clearRating">清除重评</el-button>
          </div>
          <div v-else class="rating-form">
            <div class="star-inputs">
              <div class="star-row">
                <label>总评分 <em>(必填)</em></label>
                <StarRating v-model="ratingForm.totalScore" size="small" />
              </div>
              <div class="star-row">
                <label>剧情</label>
                <StarRating v-model="ratingForm.storyScore" size="small" />
              </div>
              <div class="star-row">
                <label>特效</label>
                <StarRating v-model="ratingForm.visualScore" size="small" />
              </div>
              <div class="star-row">
                <label>演技</label>
                <StarRating v-model="ratingForm.actingScore" size="small" />
              </div>
            </div>
            <el-button class="submit-rating-btn" :disabled="!ratingForm.totalScore" @click="submitRating">提交评分</el-button>
          </div>
        </div>
        <div v-else class="login-hint">
          <el-link href="/login">登录</el-link> 后即可评分
        </div>
      </aside>
    </section>

    <!-- Short Comments -->
    <section class="section">
      <ShortCommentList :movie-id="movieId" :has-rated="Boolean(myCurrentRating)" />
    </section>

    <!-- Long Reviews -->
    <section class="section">
      <SectionHeading title="长评" />
      <PlaceholderPanel title="长评区域" description="发布长评前需要先完成电影评分，长评功能将由郭俊岑同学实现" />
    </section>

    <!-- Related News -->
    <section class="section">
      <SectionHeading title="相关资讯" />
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
    </section>

    <!-- Hot Interpretation Videos -->
    <section class="section">
      <SectionHeading title="解读视频" />
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
    </section>

    <!-- Related Merchandise -->
    <section class="section">
      <SectionHeading title="周边商品" />
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
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { movieApi, type MovieDetail, type MyRating, type RatingSubmitRequest } from '@/api/movieApi'
import { homeApi, type NewsArticle } from '@/api/homeApi'
import { videoApi, type VideoItem } from '@/api/videoApi'
import { merchandiseApi, type MerchandiseItem } from '@/api/merchandiseApi'
import { ElMessage } from 'element-plus'
import SectionHeading from '@/components/movie/SectionHeading.vue'
import RatingDisplay from '@/components/movie/RatingDisplay.vue'
import StarRating from '@/components/movie/StarRating.vue'
import ShortCommentList from '@/components/comment/ShortCommentList.vue'
import PlaceholderPanel from '@/components/common/PlaceholderPanel.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const movieId = computed(() => Number(route.params.id))
const movie = ref<MovieDetail | null>(null)
const loading = ref(false)
const myCurrentRating = ref<MyRating | null>(null)

const videos = ref<VideoItem[]>([])
const videosLoading = ref(false)
const merchandise = ref<MerchandiseItem[]>([])
const merchandiseLoading = ref(false)
const newsList = ref<NewsArticle[]>([])
const newsLoading = ref(false)

const defaultCover = 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=85'

const ratingForm = reactive({
  totalScore: 0,
  storyScore: 0,
  visualScore: 0,
  actingScore: 0,
})

async function fetchMovie() {
  loading.value = true
  try {
    movie.value = await movieApi.getMovieDetail(movieId.value)
  } finally {
    loading.value = false
  }
}

async function fetchMyRating() {
  if (!userStore.isLogin) return
  try {
    myCurrentRating.value = await movieApi.getMyRating(movieId.value)
  } catch {
    myCurrentRating.value = null
  }
}

async function submitRating() {
  if (!ratingForm.totalScore) {
    ElMessage.warning('请填写总评分')
    return
  }
  try {
    const data: RatingSubmitRequest = { totalScore: ratingForm.totalScore }
    if (ratingForm.storyScore) data.storyScore = ratingForm.storyScore
    if (ratingForm.visualScore) data.visualScore = ratingForm.visualScore
    if (ratingForm.actingScore) data.actingScore = ratingForm.actingScore
    await movieApi.submitRating(movieId.value, data)
    ElMessage.success('评分成功')
    await Promise.all([fetchMovie(), fetchMyRating()])
  } catch {
    ElMessage.error('评分失败')
  }
}

async function clearRating() {
  myCurrentRating.value = null
  ratingForm.totalScore = 0
  ratingForm.storyScore = 0
  ratingForm.visualScore = 0
  ratingForm.actingScore = 0
}

async function fetchVideos() {
  videosLoading.value = true
  try {
    const res = await videoApi.getByMovie(String(movieId.value))
    videos.value = Array.isArray(res) ? res : []
  } catch {
    videos.value = []
  } finally {
    videosLoading.value = false
  }
}

async function fetchMerchandise() {
  merchandiseLoading.value = true
  try {
    const res = await merchandiseApi.getByMovie(String(movieId.value))
    merchandise.value = Array.isArray(res) ? res : []
  } catch {
    merchandise.value = []
  } finally {
    merchandiseLoading.value = false
  }
}

async function fetchNews() {
  newsLoading.value = true
  try {
    const res = await homeApi.getNewsList({ movieId: movieId.value, pageSize: 4 })
    newsList.value = res.list || []
  } catch {
    newsList.value = []
  } finally {
    newsLoading.value = false
  }
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

onMounted(() => {
  fetchMovie()
  fetchMyRating()
  fetchVideos()
  fetchMerchandise()
  fetchNews()
})
</script>

<style scoped>
.movie-detail-page {
  min-height: 100vh;
  padding-bottom: 80px;
  position: relative;
  color: #f7edd5;
  background: transparent;
}

:global(body) {
  background: #050505;
}

/* Hero */
.hero-section {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr) minmax(320px, 380px);
  gap: 34px;
  align-items: start;
  max-width: 1360px;
  margin: 0 auto;
  padding: 54px 32px 0;
}

.hero-poster {
  flex-shrink: 0;
  width: 260px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgb(214 176 95 / 24%);
  box-shadow: 0 16px 48px rgb(0 0 0 / 50%);
}

.hero-poster img {
  width: 100%;
  height: auto;
  display: block;
}

.hero-info {
  min-width: 0;
  padding-top: 4px;
}

.hero-title {
  font-family: 'Noto Serif SC', 'Songti SC', SimSun, serif;
  font-size: clamp(26px, 3.5vw, 42px);
  color: #fff8e6;
  margin: 0 0 6px;
  line-height: 1.2;
}

.hero-original-title {
  font-size: 15px;
  color: #b9ab90;
  margin: 0 0 14px;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.hero-meta span {
  padding: 3px 10px;
  font-size: 12px;
  color: #d8c69b;
  background: rgb(214 176 95 / 10%);
  border: 1px solid rgb(214 176 95 / 20%);
  border-radius: 4px;
}

.hero-attr {
  font-size: 14px;
  color: #d8c69b;
  margin: 0 0 6px;
}

.hero-attr em {
  font-style: normal;
  color: #b9ab90;
}

.hero-synopsis {
  font-size: 14px;
  color: #b9ab90;
  line-height: 1.8;
  margin: 16px 0 0;
}

.hero-views {
  font-size: 12px;
  color: #6b5e45;
  margin: 12px 0 0;
}

.hero-rating-column {
  display: grid;
  gap: 18px;
  min-width: 0;
}

/* Section */
.section {
  position: relative;
  z-index: 1;
  max-width: 1360px;
  margin: 48px auto 0;
  padding: 0 32px;
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

/* Rating Panel */
.rating-panel,
.my-rating-panel,
.login-hint {
  padding: 24px 28px;
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid rgb(214 176 95 / 20%);
  border-radius: 8px;
  box-shadow: 0 16px 40px rgb(0 0 0 / 26%);
}

.side-panel-title {
  margin: 0 0 18px;
  color: #e8c16d;
  font-family: 'Noto Serif SC', 'Songti SC', SimSun, serif;
  font-size: 26px;
  font-weight: 800;
}

.rating-content {
  display: flex;
  gap: 28px;
  align-items: center;
}

.rating-main {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.big-score {
  font-family: Georgia, 'Noto Serif SC', serif;
  font-size: 52px;
  font-weight: 700;
  color: #e8c16d;
  line-height: 1;
}

.big-score-label {
  font-size: 13px;
  color: #b9ab90;
}

.rating-count {
  font-size: 12px;
  color: #6b5e45;
}

.rating-subs {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

/* My Rating */
.my-rating-panel {
  background: linear-gradient(180deg, rgb(255 255 255 / 4%), rgb(255 255 255 / 1%));
  border: 1px solid rgb(214 176 95 / 16%);
}

.my-rating-title {
  font-size: 15px;
  color: #e8c16d;
  margin: 0 0 14px;
  font-weight: 600;
}

.my-rating-display {
  display: flex;
  align-items: center;
  gap: 16px;
}

.my-score {
  font-family: Georgia, serif;
  font-size: 28px;
  color: #e8c16d;
  font-weight: 700;
}

.my-score-sub {
  font-size: 13px;
  color: #b9ab90;
}

.clear-btn {
  background: transparent;
  border: 1px solid rgb(214 176 95 / 30%);
  color: #cbb98f;
}

.star-inputs {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.star-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.star-row label {
  width: 60px;
  font-size: 13px;
  color: #cbb98f;
  flex-shrink: 0;
}

.star-row label em {
  font-style: normal;
  color: #e8c16d;
  font-size: 11px;
}

.submit-rating-btn {
  margin-top: 14px;
  background: linear-gradient(135deg, #c9a035, #d6b05f);
  color: #050505;
  font-weight: 700;
  border: none;
}

.login-hint {
  font-size: 14px;
  color: #b9ab90;
}

.login-hint a {
  color: #e8c16d;
}

/* Dark placeholder overrides */
:deep(.panel) {
  background: linear-gradient(180deg, rgb(255 255 255 / 4%), rgb(255 255 255 / 1%)) !important;
  border: 1px solid rgb(214 176 95 / 14%) !important;
}

:deep(.page-title) {
  color: #d8c69b !important;
}

:deep(.muted) {
  color: #6b5e45 !important;
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

@media (max-width: 1180px) {
  .hero-section {
    grid-template-columns: 240px minmax(0, 1fr);
  }

  .hero-rating-column {
    grid-column: 1 / -1;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-section {
    grid-template-columns: 1fr;
    align-items: center;
    padding: 36px 20px 0;
  }

  .hero-poster {
    width: 200px;
  }

  .hero-info,
  .hero-rating-column {
    width: 100%;
  }

  .hero-rating-column {
    grid-template-columns: 1fr;
  }

  .section {
    padding: 0 20px;
  }

  .rating-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .rating-main {
    flex-direction: row;
    gap: 12px;
  }

  .video-scroll-row {
    gap: 12px;
  }

  .merchandise-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .news-mini-grid {
    grid-template-columns: 1fr;
  }
}
</style>
