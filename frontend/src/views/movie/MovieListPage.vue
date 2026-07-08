<template>
  <div class="movie-list-page">
    <section class="search-section">
      <div class="search-bar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索电影名 / 导演 / 演员"
          clearable
          class="search-input"
          @keyup.enter="search"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button class="search-btn" @click="search">搜索</el-button>
      </div>

      <div class="filter-panel" aria-label="电影筛选">
        <div v-for="group in filterGroups" :key="group.key" class="filter-row">
          <span class="filter-label">{{ group.label }}</span>
          <button
            v-for="option in group.options"
            :key="option.label"
            type="button"
            class="filter-chip"
            :class="{ active: isFilterActive(group.key, option.value) }"
            @click="chooseFilter(group.key, option.value)"
          >
            {{ option.label }}
          </button>
        </div>
      </div>
    </section>

    <section v-loading="loading" class="poster-sections">
      <div class="result-summary">
        <span>共 {{ total }} 部影片</span>
        <button type="button" @click="resetFilters">重置筛选</button>
      </div>

      <section v-if="hasActiveCondition" class="flat-results">
        <div class="flat-poster-grid">
          <article
            v-for="movie in movies"
            :key="`filtered-${movie.id}`"
            class="poster-card"
            @click="openMovie(movie.id)"
          >
            <div class="poster-cover">
              <img :src="movie.posterUrl || fallbackPoster" :alt="movie.title" />
              <strong v-if="movie.avgTotalScore != null">{{ movie.avgTotalScore }}</strong>
            </div>
            <p>{{ releaseYear(movie) }}/{{ movie.title }}</p>
          </article>
        </div>

        <el-empty v-if="!loading && movies.length === 0" description="暂无匹配影片" />
      </section>

      <section v-for="section in movieSections" v-else :key="section.key" class="poster-section">
        <div class="section-title-row">
          <h2>{{ section.title }}</h2>
          <div class="section-actions">
            <button type="button" :aria-label="`向左浏览${section.title}`" @click="scrollPosters(section.key, -1)">‹</button>
            <button type="button" :aria-label="`向右浏览${section.title}`" @click="scrollPosters(section.key, 1)">›</button>
          </div>
        </div>

        <div
          :ref="(el) => setScroller(section.key, el)"
          class="poster-strip"
          :class="{ 'is-double': section.rows === 2 }"
        >
          <article
            v-for="movie in section.movies"
            :key="`${section.key}-${movie.id}`"
            class="poster-card"
            @click="openMovie(movie.id)"
          >
            <div class="poster-cover">
              <img :src="movie.posterUrl || fallbackPoster" :alt="movie.title" />
              <strong v-if="movie.avgTotalScore != null">{{ movie.avgTotalScore }}</strong>
            </div>
            <p>{{ releaseYear(movie) }}/{{ movie.title }}</p>
          </article>
        </div>

        <el-empty v-if="!loading && section.movies.length === 0" description="暂无影片" />
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { movieApi, type MovieListItem, type MovieQuery } from '@/api/movieApi'

type FilterKey = 'contentType' | 'genre' | 'releasePeriod' | 'region'

const router = useRouter()
const fallbackPoster = 'https://via.placeholder.com/300x450/111111/d6b05f?text=Movie'

const query = reactive<MovieQuery>({
  keyword: '',
  contentType: '',
  genre: '',
  releasePeriod: '',
  region: '',
  page: 1,
  pageSize: 60,
})

const filterGroups: Array<{
  key: FilterKey
  label: string
  options: Array<{ label: string; value: string }>
}> = [
  {
    key: 'contentType',
    label: '影片',
    options: [
      { label: '全部', value: '' },
      { label: '电影', value: '电影' },
      { label: '电视剧', value: '电视剧' },
    ],
  },
  {
    key: 'genre',
    label: '影片类型',
    options: [
      { label: '全部', value: '' },
      { label: '动作', value: '动作' },
      { label: '战争', value: '战争' },
      { label: '科幻', value: '科幻' },
      { label: '悬疑', value: '悬疑' },
      { label: '恐怖', value: '恐怖' },
      { label: '灾难', value: '灾难' },
      { label: '剧情', value: '剧情' },
      { label: '动画', value: '动画' },
      { label: '喜剧', value: '喜剧' },
    ],
  },
  {
    key: 'releasePeriod',
    label: '影片时间',
    options: [
      { label: '全部', value: '' },
      { label: '2026-2020', value: '2026-2020' },
      { label: '2019-2010', value: '2019-2010' },
      { label: '2009-2000', value: '2009-2000' },
      { label: '90年代', value: '90年代' },
      { label: '80年代', value: '80年代' },
      { label: '更早', value: '更早' },
    ],
  },
  {
    key: 'region',
    label: '影片地区',
    options: [
      { label: '全部', value: '' },
      { label: '中国', value: '中国' },
      { label: '美国', value: '美国' },
      { label: '日韩', value: '日韩' },
      { label: '欧洲', value: '欧洲' },
    ],
  },
]

const movies = ref<MovieListItem[]>([])
const total = ref(0)
const loading = ref(false)
const scrollers = new Map<string, HTMLElement>()

const sortedByScore = computed(() =>
  [...movies.value].sort((a, b) => (b.avgTotalScore ?? 0) - (a.avgTotalScore ?? 0)),
)

const sortedByHot = computed(() =>
  [...movies.value].sort((a, b) => (b.ratingCount ?? 0) - (a.ratingCount ?? 0)),
)

const sortedByDate = computed(() =>
  [...movies.value].sort((a, b) => (b.releaseDate || '').localeCompare(a.releaseDate || '')),
)

const latestChinese = computed(() =>
  sortedByDate.value.filter((movie) => includesAny(`${movie.region || ''}${movie.language || ''}`, ['中国', '大陆', '香港', '台湾', '汉语'])),
)

const latestWestern = computed(() =>
  sortedByDate.value.filter((movie) => includesAny(movie.region || '', ['美国', '英国', '法国', '德国', '意大利', '西班牙', '欧洲'])),
)

const hasActiveCondition = computed(() =>
  Boolean(
    query.keyword?.trim()
      || query.contentType
      || query.genre
      || query.releaseYear
      || query.releasePeriod
      || query.region,
  ),
)

const movieSections = computed(() => [
  { key: 'top-rated', title: '高分佳作', rows: 2, movies: sortedByScore.value },
  { key: 'hot', title: '近期最热', rows: 2, movies: sortedByHot.value },
  { key: 'chinese', title: '最新华语片', rows: 1, movies: latestChinese.value },
  { key: 'western', title: '最新欧美片', rows: 1, movies: latestWestern.value },
])

async function fetchMovies() {
  loading.value = true
  try {
    const params = Object.fromEntries(
      Object.entries({ ...query }).filter(([, value]) => value !== '' && value != null),
    ) as MovieQuery
    const res = await movieApi.pageMovies(params)
    movies.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  fetchMovies()
}

function chooseFilter(key: FilterKey, value: string) {
  query[key] = value
  search()
}

function isFilterActive(key: FilterKey, value: string) {
  return (query[key] || '') === value
}

function resetFilters() {
  query.keyword = ''
  query.contentType = ''
  query.genre = ''
  query.releaseYear = ''
  query.releasePeriod = ''
  query.region = ''
  search()
}

function setScroller(key: string, el: unknown) {
  if (el instanceof HTMLElement) {
    scrollers.set(key, el)
  } else {
    scrollers.delete(key)
  }
}

function scrollPosters(key: string, direction: number) {
  scrollers.get(key)?.scrollBy({
    left: direction * 620,
    behavior: 'smooth',
  })
}

function openMovie(id: number) {
  router.push(`/movies/${id}`)
}

function releaseYear(movie: MovieListItem) {
  return movie.releaseDate ? movie.releaseDate.substring(0, 4) : '未知年份'
}

function includesAny(value: string, targets: string[]) {
  return targets.some((target) => value.includes(target))
}

onMounted(() => fetchMovies())
</script>

<style scoped>
.movie-list-page {
  position: relative;
  min-height: 100vh;
  padding: 28px 0 80px;
  color: #f7edd5;
}

:global(body) {
  background: #050505;
}

.search-section,
.poster-sections {
  position: relative;
  z-index: 1;
  width: min(1200px, calc(100% - 48px));
  margin: 0 auto;
}

.search-bar {
  display: flex;
  gap: 12px;
  width: min(860px, 100%);
  margin: 0 auto 18px;
}

.search-input {
  flex: 1;
}

.search-btn {
  min-width: 92px;
  padding: 0 28px;
  border: none;
  border-radius: 6px;
  color: #050505;
  font-weight: 800;
  background: linear-gradient(135deg, #c9a035 0%, #d6b05f 100%);
}

.filter-panel {
  display: grid;
  gap: 9px;
  padding: 14px 16px;
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 8px;
  background:
    linear-gradient(90deg, rgb(150 45 34 / 12%), transparent 34%),
    linear-gradient(180deg, rgb(255 255 255 / 4%), rgb(255 255 255 / 1%));
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 14px;
  min-height: 28px;
}

.filter-label {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 74px;
  min-height: 28px;
  border-radius: 3px;
  color: #ff312b;
  font-size: 13px;
  font-weight: 800;
  background: rgb(255 255 255 / 9%);
}

.filter-chip {
  padding: 4px 0;
  border: 0;
  color: #c7d3df;
  font-size: 14px;
  line-height: 1;
  background: transparent;
  cursor: pointer;
  transition: color 180ms ease, text-shadow 180ms ease;
}

.filter-chip:hover,
.filter-chip.active {
  color: #e8c16d;
  text-shadow: 0 0 12px rgb(214 176 95 / 42%);
}

:deep(.el-input__wrapper) {
  border: 1px solid rgb(214 176 95 / 18%) !important;
  border-radius: 6px !important;
  background: rgb(255 255 255 / 6%) !important;
  box-shadow: none !important;
}

:deep(.el-input__inner) {
  color: #d8c69b !important;
}

:deep(.el-input__inner::placeholder) {
  color: #6b5e45 !important;
}

.poster-sections {
  margin-top: 34px;
}

.result-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 22px;
  color: #8e805e;
  font-size: 13px;
}

.result-summary button,
.section-actions button {
  border: 1px solid rgb(214 176 95 / 24%);
  color: #e8c16d;
  background: rgb(255 255 255 / 5%);
  cursor: pointer;
}

.result-summary button {
  height: 28px;
  padding: 0 12px;
  border-radius: 4px;
}

.poster-section {
  margin-top: 28px;
}

.flat-results {
  margin-top: 8px;
}

.flat-poster-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, 132px);
  gap: 22px 14px;
  align-items: start;
}

.section-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.section-title-row h2 {
  margin: 0;
  color: #fff8e6;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: 0;
}

.section-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.section-actions button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 999px;
  font-size: 24px;
  line-height: 1;
}

.poster-strip {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: 132px;
  grid-template-rows: repeat(1, auto);
  gap: 18px 12px;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 0 2px 10px;
  scroll-behavior: smooth;
  scroll-snap-type: x mandatory;
  scrollbar-color: rgb(214 176 95 / 38%) transparent;
}

.poster-strip.is-double {
  grid-template-rows: repeat(2, auto);
}

.poster-card {
  min-width: 0;
  cursor: pointer;
  scroll-snap-align: start;
}

.poster-cover {
  position: relative;
  height: 188px;
  overflow: hidden;
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 6px;
  background: #111;
  box-shadow: 0 12px 28px rgb(0 0 0 / 36%);
}

.poster-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 260ms ease, filter 260ms ease;
}

.poster-cover::after {
  position: absolute;
  inset: auto 0 0;
  height: 46%;
  content: '';
  background: linear-gradient(180deg, transparent, rgb(0 0 0 / 82%));
}

.poster-cover strong {
  position: absolute;
  right: 8px;
  bottom: 7px;
  z-index: 1;
  color: #ff7a1a;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 18px;
  text-shadow: 0 0 10px rgb(0 0 0 / 90%);
}

.poster-card:hover .poster-cover img {
  filter: brightness(1.08);
  transform: scale(1.04);
}

.poster-card p {
  margin: 8px 0 0;
  overflow: hidden;
  color: #d8c69b;
  font-size: 12px;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.el-empty__description) {
  color: #6b5e45;
}

@media (max-width: 768px) {
  .search-section,
  .poster-sections {
    width: calc(100% - 28px);
  }

  .search-bar {
    flex-direction: column;
  }

  .filter-row {
    align-items: flex-start;
  }

  .filter-label {
    width: 100%;
    justify-content: flex-start;
    padding-left: 9px;
  }

  .poster-strip {
    grid-auto-columns: 118px;
  }

  .flat-poster-grid {
    grid-template-columns: repeat(auto-fill, 118px);
  }

  .poster-cover {
    height: 168px;
  }
}
</style>
