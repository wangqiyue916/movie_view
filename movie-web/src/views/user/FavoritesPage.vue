<template>
  <div class="page favorites-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">我的收藏</h1>
        <p class="page-desc">你收藏的资讯和长评</p>
      </div>

      <div v-if="loading" class="loading-skeleton">
        <SkeletonCard v-for="n in 3" :key="n" height="120px" />
      </div>

      <div v-else-if="items.length === 0" class="empty-state">
        <p>暂无收藏内容</p>
        <router-link to="/" class="start-link">去发现精彩内容 →</router-link>
      </div>

      <div v-else class="favorites-list">
        <div
          v-for="item in items"
          :key="`${item.type}-${item.id}`"
          class="favorite-card"
          @click="goToDetail(item)"
        >
          <div class="card-header">
            <span class="type-tag" :class="`tag-${item.type?.toLowerCase()}`">
              {{ item.type === 'news' ? '资讯' : '长评' }}
            </span>
            <span class="card-time">{{ formatDate(item.savedAt) }}</span>
          </div>
          <h3 class="card-title">{{ item.title }}</h3>
          <p class="card-summary">{{ item.summary }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import SkeletonCard from '@/components/common/SkeletonCard.vue'

const router = useRouter()
const loading = ref(true)
const items = ref<any[]>([])

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

function goToDetail(item: any) {
  if (item.type === 'news') {
    router.push(`/news/${item.id}`)
  } else {
    router.push(`/long-reviews/${item.id}`)
  }
}

onMounted(async () => {
  // 模拟收藏数据（后续可对接后端 API）
  setTimeout(() => {
    items.value = [
      {
        id: 1,
        type: 'news',
        title: '暑期档科幻电影热度持续升温',
        summary: '多部科幻题材影片带动观影讨论，视觉效果、叙事表达与人物塑造成为关注焦点。',
        savedAt: new Date().toISOString(),
      },
      {
        id: 1,
        type: 'long_review',
        title: '穿越星际之后，仍然回到人的情感',
        summary: '这部电影最动人的地方，是它把宏大的宇宙尺度和亲情联系在一起。',
        savedAt: new Date(Date.now() - 86400000).toISOString(),
      },
      {
        id: 2,
        type: 'news',
        title: '经典高分电影长评征集活动开启',
        summary: '平台将根据点赞数、收藏数和回复数推荐优质长评，鼓励更深入的电影讨论。',
        savedAt: new Date(Date.now() - 172800000).toISOString(),
      },
    ]
    loading.value = false
  }, 500)
})
</script>

<style scoped>
.favorites-page {
  min-height: calc(100vh - 64px);
  padding: 36px max(22px, calc((100vw - 1280px) / 2)) 72px;
  color: #f7edd5;
  background: #050505;
}

.container {
  max-width: 780px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 28px;
}

.page-title {
  margin: 0 0 6px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 24px;
  font-weight: 700;
  color: #e8c16d;
}

.page-desc {
  margin: 0;
  color: #8a7b68;
  font-size: 14px;
}

/* ===== 骨架屏 ===== */
.loading-skeleton {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #8a7b68;
}

.start-link {
  display: inline-block;
  margin-top: 12px;
  color: #d6b05f;
  font-size: 14px;
}

/* ===== 收藏列表 ===== */
.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.favorite-card {
  padding: 20px 24px;
  border: 1px solid rgb(214 176 95 / 12%);
  border-radius: 10px;
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
  cursor: pointer;
  transition: border-color 200ms, transform 200ms, box-shadow 200ms;
}

.favorite-card:hover {
  border-color: rgb(214 176 95 / 30%);
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgb(0 0 0 / 30%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.type-tag {
  padding: 2px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
}

.tag-news {
  background: rgb(96 165 250 / 16%);
  color: #60a5fa;
}

.tag-long_review {
  background: rgb(214 176 95 / 16%);
  color: #d6b05f;
}

.card-time {
  font-size: 12px;
  color: #5a5040;
}

.card-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: #f7edd5;
}

.card-summary {
  margin: 0;
  font-size: 13px;
  color: #8a7b68;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>