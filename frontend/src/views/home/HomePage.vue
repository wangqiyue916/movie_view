<template>
  <div class="home-page">
    <!-- 轮播展示区 -->
    <section class="showcase" aria-label="热门展示">
      <div class="showcase-frame">
        <article
          v-for="(item, index) in showcaseItems"
          :key="item.title"
          class="showcase-card"
          :class="getSlideClass(index)"
        >
          <img :src="item.image" :alt="item.title" />
          <div class="showcase-mask"></div>
          <div class="showcase-content">
            <span>{{ item.kicker }}</span>
            <h1>{{ item.title }}</h1>
            <p>{{ item.description }}</p>
          </div>
        </article>
      </div>
      <div class="showcase-dots" aria-label="轮播分页">
        <button
          v-for="(_, index) in showcaseItems"
          :key="index"
          type="button"
          :class="{ active: index === activeIndex }"
          :aria-label="`切换到第 ${index + 1} 张展示图`"
          @click="activeIndex = index"
        ></button>
      </div>
    </section>

    <!-- 热门电影 -->
    <section class="content-section movie-section">
      <div class="section-heading">
        <span></span>
        <h2>热门电影</h2>
      </div>
      <div class="movie-category-list">
        <section v-for="group in movieGroups" :key="group.title" class="movie-category">
          <h3>{{ group.title }}</h3>
          <div class="poster-row">
            <article v-for="movie in group.movies" :key="`${group.title}-${movie.title}`" class="poster-card">
              <div class="poster-frame">
                <img :src="movie.poster" :alt="movie.title" />
                <strong>{{ movie.score }}</strong>
              </div>
              <p>{{ movie.title }}</p>
            </article>
          </div>
        </section>
      </div>
    </section>

    <!-- 电影资讯 -->
    <section class="content-section">
      <div class="section-heading">
        <span></span>
        <h2>电影资讯</h2>
        <div class="news-controls" aria-label="电影资讯浏览控制">
          <button type="button" aria-label="向左浏览电影资讯" @click="scrollNews(-1)">‹</button>
          <button type="button" aria-label="向右浏览电影资讯" @click="scrollNews(1)">›</button>
        </div>
      </div>
      <div ref="newsScroller" class="news-list">
        <router-link
          v-for="news in latestNews"
          :key="news.id"
          :to="`/news/${news.id}`"
          class="news-card"
        >
          <img :src="news.coverUrl || 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=85'" :alt="news.title" />
          <div>
            <span>{{ news.category }}</span>
            <h3>{{ news.title }}</h3>
            <p>{{ news.summary }}</p>
          </div>
        </router-link>
      </div>
    </section>

    <!-- 优质长评 -->
    <section class="content-section reviews-section">
      <div class="section-heading">
        <span></span>
        <h2>优质长评</h2>
      </div>
      <div class="review-list">
        <article v-for="review in featuredReviews" :key="review.title" class="review-card">
          <h3>{{ review.title }}</h3>
          <p>{{ review.excerpt }}</p>
          <div class="review-meta">
            <span>{{ review.author }}</span>
            <span>{{ review.date }}</span>
            <strong>{{ review.likes }} 赞</strong>
            <strong>{{ review.comments }} 评论</strong>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { homeApi, type HomeData, type NewsArticle, type ReviewItem, type MovieItem } from '@/api/homeApi'

// 轮播数据
const showcaseItems = ref([
  {
    kicker: '今日热映',
    title: '星际穿越',
    description: '在时间、宇宙与亲情之间，重看一场宏大的银幕冒险。',
    image: 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=1800&q=85',
  },
  {
    kicker: '高分经典',
    title: '盗梦空间',
    description: '层层梦境与意识迷宫，把悬疑感推向极致。',
    image: 'https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=1800&q=85',
  },
  {
    kicker: '华语科幻',
    title: '流浪地球2',
    description: '灾难叙事、工业想象与群像人物交织成新的科幻样貌。',
    image: 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1800&q=85',
  },
  {
    kicker: '口碑长线',
    title: '影院热议榜',
    description: '跟随观众评分与长评热度，发现下一部值得看的电影。',
    image: 'https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1800&q=85',
  },
])

const activeIndex = ref(0)
const newsScroller = ref<HTMLElement | null>(null)
let timer: number | undefined

// API 数据
const latestNews = ref<NewsArticle[]>([])
const hotMovies = ref<MovieItem[]>([])
const topRatedMovies = ref<MovieItem[]>([])
const latestMovies = ref<MovieItem[]>([])
const featuredReviews = ref<ReviewItem[]>([])

const total = computed(() => showcaseItems.value.length)

const getSlideClass = (index: number) => {
  const offset = (index - activeIndex.value + total.value) % total.value
  if (offset === 0) return 'is-active'
  if (offset === 1) return 'is-right'
  if (offset === total.value - 1) return 'is-left'
  return 'is-back'
}

// 组装电影分组
const movieGroups = computed(() => [
  {
    title: '近期热门',
    movies: hotMovies.value.map(m => ({ title: m.title, poster: m.poster, score: m.score })),
  },
  {
    title: '高分推荐',
    movies: topRatedMovies.value.map(m => ({ title: m.title, poster: m.poster, score: m.score })),
  },
])

const scrollNews = (direction: number) => {
  newsScroller.value?.scrollBy({
    left: direction * 420,
    behavior: 'smooth',
  })
}

// 获取首页数据
async function fetchHomeData() {
  try {
    const data: HomeData = await homeApi.getHomeData()
    latestNews.value = data.latestNews || []
    hotMovies.value = data.hotMovies || []
    topRatedMovies.value = data.topRatedMovies || []
    latestMovies.value = data.latestMovies || []
    featuredReviews.value = data.featuredReviews || []
  } catch {
    // 接口失败时使用空数据，页面会优雅降级
  }
}

onMounted(() => {
  fetchHomeData()
  timer = window.setInterval(() => {
    activeIndex.value = (activeIndex.value + 1) % total.value
  }, 4000)
})

onBeforeUnmount(() => {
  if (timer) {
    window.clearInterval(timer)
  }
})
</script>

<style scoped>
:global(body) {
  background: #050505;
}

.home-page {
  position: relative;
  isolation: isolate;
  width: 100%;
  min-height: calc(100vh - 64px);
  padding: 36px max(22px, calc((100vw - 1280px) / 2)) 72px;
  color: #f7edd5;
  background:
    radial-gradient(circle at 8% 18%, rgb(214 176 95 / 14%), transparent 24%),
    radial-gradient(circle at 92% 26%, rgb(214 176 95 / 12%), transparent 24%),
    radial-gradient(circle at 50% 100%, rgb(214 176 95 / 10%), transparent 34%),
    radial-gradient(circle at 18% 34%, rgb(214 176 95 / 18%) 0 1px, transparent 2px),
    radial-gradient(circle at 84% 42%, rgb(214 176 95 / 16%) 0 1px, transparent 2px),
    radial-gradient(circle at 10% 72%, rgb(214 176 95 / 12%) 0 1px, transparent 2px),
    radial-gradient(circle at 91% 78%, rgb(214 176 95 / 14%) 0 1px, transparent 2px),
    linear-gradient(180deg, #050505 0%, #0e0c08 46%, #050505 100%),
    repeating-linear-gradient(90deg, rgb(214 176 95 / 8%) 0 1px, transparent 1px 120px);
}

.home-page::before,
.home-page::after {
  position: fixed;
  top: 64px;
  bottom: 0;
  z-index: -1;
  width: 96px;
  pointer-events: none;
  content: '';
  opacity: 0.48;
}

.home-page::before {
  left: 0;
  background:
    linear-gradient(90deg, rgb(0 0 0 / 82%), transparent),
    repeating-linear-gradient(
      180deg,
      transparent 0 18px,
      rgb(214 176 95 / 22%) 18px 28px,
      transparent 28px 46px
    ),
    linear-gradient(90deg, transparent 0 20px, rgb(214 176 95 / 34%) 20px 21px, transparent 21px);
  mask-image: linear-gradient(90deg, #000 0 76%, transparent);
}

.home-page::after {
  right: 0;
  background:
    linear-gradient(270deg, rgb(0 0 0 / 82%), transparent),
    repeating-linear-gradient(
      180deg,
      transparent 0 18px,
      rgb(214 176 95 / 22%) 18px 28px,
      transparent 28px 46px
    ),
    linear-gradient(270deg, transparent 0 20px, rgb(214 176 95 / 34%) 20px 21px, transparent 21px);
  mask-image: linear-gradient(270deg, #000 0 76%, transparent);
}

.home-page > * {
  position: relative;
  z-index: 1;
}

.home-page::selection {
  color: #050505;
  background: #d6b05f;
}

.showcase {
  position: relative;
  min-height: 470px;
  margin-bottom: 28px;
}

.showcase::before,
.showcase::after {
  position: absolute;
  top: 46px;
  bottom: 74px;
  width: 1px;
  content: '';
  background: linear-gradient(180deg, transparent, rgb(214 176 95 / 72%), transparent);
}

.showcase::before {
  left: 0;
}

.showcase::after {
  right: 0;
}

.showcase-frame {
  position: relative;
  height: 410px;
  perspective: 1600px;
  overflow: hidden;
}

.showcase-card {
  position: absolute;
  top: 32px;
  left: 50%;
  width: min(920px, 76vw);
  height: 340px;
  overflow: hidden;
  border: 1px solid rgb(214 176 95 / 44%);
  border-radius: 8px;
  background: #090909;
  box-shadow: 0 24px 70px rgb(0 0 0 / 58%);
  transition:
    transform 900ms ease,
    opacity 900ms ease,
    filter 900ms ease,
    z-index 900ms ease;
  transform-style: preserve-3d;
}

.showcase-card img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.showcase-mask {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgb(0 0 0 / 78%), rgb(0 0 0 / 18%) 52%, rgb(0 0 0 / 68%)),
    linear-gradient(0deg, rgb(0 0 0 / 42%), transparent 48%);
}

.showcase-content {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 34px;
  text-align: center;
}

.showcase-content span {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 14px;
  border: 1px solid rgb(214 176 95 / 52%);
  color: #e8c16d;
  font-size: 14px;
  background: rgb(0 0 0 / 42%);
}

.showcase-content h1 {
  margin: 18px 0 12px;
  color: #fff8e6;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: clamp(38px, 5vw, 68px);
  font-weight: 800;
  letter-spacing: 0;
  text-shadow: 0 0 24px rgb(214 176 95 / 42%);
}

.showcase-content p {
  max-width: 620px;
  margin: 0;
  color: #ead9b5;
  font-size: 18px;
  line-height: 1.7;
}

.showcase-card.is-active {
  z-index: 4;
  opacity: 1;
  filter: saturate(1.08) brightness(1);
  transform: translateX(-50%) translateZ(120px) scale(1);
}

.showcase-card.is-left {
  z-index: 2;
  opacity: 0.5;
  filter: saturate(0.76) brightness(0.56);
  transform: translateX(-112%) translateZ(-190px) rotateY(18deg) scale(0.7);
}

.showcase-card.is-right {
  z-index: 3;
  opacity: 0.66;
  filter: saturate(0.86) brightness(0.66);
  transform: translateX(12%) translateZ(-150px) rotateY(-18deg) scale(0.74);
}

.showcase-card.is-back {
  z-index: 1;
  opacity: 0;
  pointer-events: none;
  filter: blur(3px) brightness(0.3);
  transform: translateX(-50%) translateZ(-360px) scale(0.52);
}

.showcase-dots {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 0;
}

.showcase-dots button {
  width: 36px;
  height: 4px;
  padding: 0;
  border: 0;
  border-radius: 999px;
  background: rgb(214 176 95 / 28%);
  cursor: pointer;
  transition: background 240ms ease, width 240ms ease;
}

.showcase-dots button.active {
  width: 58px;
  background: #d6b05f;
  box-shadow: 0 0 16px rgb(214 176 95 / 56%);
}

.content-section {
  position: relative;
  padding: 28px 0 34px;
  border-top: 1px solid rgb(214 176 95 / 22%);
}

.section-heading {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 18px;
}

.section-heading span {
  width: 36px;
  height: 1px;
  background: #d6b05f;
  box-shadow: 0 0 12px rgb(214 176 95 / 72%);
}

.home-page .section-heading h2 {
  margin: 0;
  color: #e8c16d;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: clamp(22px, 2.8vw, 34px);
  font-weight: 800;
  letter-spacing: 0;
  text-shadow: 0 0 16px rgb(214 176 95 / 32%);
}

.news-controls {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.news-controls button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  border: 1px solid rgb(214 176 95 / 32%);
  border-radius: 999px;
  color: #e8c16d;
  font-size: 24px;
  line-height: 1;
  background: rgb(255 255 255 / 5%);
  cursor: pointer;
  transition: border-color 180ms ease, background 180ms ease, transform 180ms ease;
}

.news-controls button:hover {
  border-color: rgb(214 176 95 / 68%);
  background: rgb(214 176 95 / 13%);
  transform: translateY(-1px);
}

.movie-category-list {
  display: grid;
  gap: 22px;
}

.movie-category h3 {
  margin: 0 0 10px;
  color: #fff8e6;
  font-size: 18px;
  font-weight: 600;
}

.poster-row {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: 138px;
  gap: 14px;
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-color: rgb(214 176 95 / 45%) transparent;
}

.poster-card {
  min-width: 0;
}

.poster-frame {
  position: relative;
  height: 196px;
  overflow: hidden;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 6px;
  background: #111;
  box-shadow: 0 12px 28px rgb(0 0 0 / 42%);
}

.poster-frame img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 260ms ease, filter 260ms ease;
}

.poster-frame::after {
  position: absolute;
  inset: auto 0 0;
  height: 44%;
  content: '';
  background: linear-gradient(180deg, transparent, rgb(0 0 0 / 78%));
}

.poster-frame strong {
  position: absolute;
  right: 8px;
  bottom: 6px;
  z-index: 1;
  color: #f2b24b;
  font-size: 17px;
  font-family: Georgia, "Times New Roman", serif;
  text-shadow: 0 0 10px rgb(0 0 0 / 86%);
}

.poster-card:hover img {
  filter: brightness(1.08);
  transform: scale(1.04);
}

.poster-card p {
  margin: 8px 0 0;
  overflow: hidden;
  color: #d8c69b;
  font-size: 13px;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.news-list {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: minmax(360px, 410px);
  grid-template-rows: repeat(2, minmax(112px, auto));
  gap: 12px 14px;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 0 2px 10px;
  scroll-behavior: smooth;
  scroll-snap-type: x mandatory;
  scrollbar-color: rgb(214 176 95 / 45%) transparent;
}

.news-card,
.review-card {
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  box-shadow: 0 12px 28px rgb(0 0 0 / 34%);
}

.news-card {
  display: grid;
  grid-template-columns: 118px 1fr;
  min-height: 112px;
  overflow: hidden;
  scroll-snap-align: start;
  color: inherit;
  text-decoration: none;
}

.news-card img {
  width: 100%;
  height: 100%;
  min-height: 112px;
  object-fit: cover;
}

.news-card div {
  padding: 12px 14px;
}

.news-card span {
  color: #d6b05f;
  font-size: 12px;
}

.news-card h3,
.review-card h3 {
  margin: 4px 0 0;
  color: #fff6df;
  font-size: 16px;
  line-height: 1.45;
}

.news-card p,
.review-card p {
  margin: 6px 0 0;
  color: #b9ab90;
  font-size: 12.5px;
  line-height: 1.55;
}

.review-list {
  display: grid;
  gap: 10px;
}

.review-card {
  min-height: 108px;
  padding: 13px 16px;
}

.review-card p {
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.review-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 9px;
  color: #c6b78f;
  font-size: 12px;
}

.review-meta strong {
  color: #e8c16d;
  font-weight: 600;
}

@media (max-width: 1100px) {
  .news-list {
    grid-auto-columns: minmax(340px, 78vw);
  }

  .news-card {
    grid-template-columns: 126px 1fr;
  }
}

@media (max-width: 900px) {
  .home-page {
    padding-inline: 18px;
  }

  .showcase {
    min-height: 410px;
  }

  .showcase-frame {
    height: 350px;
  }

  .showcase-card {
    width: min(760px, 88vw);
    height: 292px;
  }

  .poster-row {
    grid-auto-columns: 126px;
  }

  .poster-frame {
    height: 178px;
  }
}

@media (max-width: 640px) {
  .showcase {
    min-height: 350px;
  }

  .showcase-frame {
    height: 300px;
  }

  .showcase-card {
    top: 24px;
    width: 90vw;
    height: 250px;
  }

  .showcase-card.is-left,
  .showcase-card.is-right {
    opacity: 0;
  }

  .showcase-content {
    padding: 24px;
  }

  .showcase-content p {
    font-size: 15px;
  }

  .news-card {
    grid-template-columns: 1fr;
  }

  .news-card img {
    height: 106px;
  }

  .news-list {
    grid-auto-columns: minmax(260px, 82vw);
    grid-template-rows: repeat(2, minmax(210px, auto));
  }

  .news-controls button {
    width: 30px;
    height: 30px;
    font-size: 22px;
  }
}
</style>