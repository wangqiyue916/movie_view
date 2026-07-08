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

    <!-- Placeholders for other team members -->
    <section class="section">
      <SectionHeading title="长评" />
      <PlaceholderPanel title="长评区域" description="发布长评前需要先完成电影评分，长评功能将由郭俊岑同学实现" />
    </section>
    <section class="section">
      <SectionHeading title="相关资讯" />
      <PlaceholderPanel title="资讯区域" description="资讯功能将由罗智懿同学实现" />
    </section>
    <section class="section">
      <SectionHeading title="解读视频" />
      <PlaceholderPanel title="视频区域" description="视频功能将由周秋宏同学实现" />
    </section>
    <section class="section">
      <SectionHeading title="周边商品" />
      <PlaceholderPanel title="周边区域" description="周边功能将由周秋宏同学实现" />
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { movieApi, type MovieDetail, type MyRating, type RatingSubmitRequest } from '@/api/movieApi'
import { ElMessage } from 'element-plus'
import SectionHeading from '@/components/movie/SectionHeading.vue'
import RatingDisplay from '@/components/movie/RatingDisplay.vue'
import StarRating from '@/components/movie/StarRating.vue'
import ShortCommentList from '@/components/comment/ShortCommentList.vue'
import PlaceholderPanel from '@/components/common/PlaceholderPanel.vue'

const route = useRoute()
const userStore = useUserStore()

const movieId = computed(() => Number(route.params.id))
const movie = ref<MovieDetail | null>(null)
const loading = ref(false)
const myCurrentRating = ref<MyRating | null>(null)

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

onMounted(() => {
  fetchMovie()
  fetchMyRating()
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
}
</style>
