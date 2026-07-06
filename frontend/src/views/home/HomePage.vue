<template>
  <div class="page">
    <section class="hero">
      <div>
        <h1>电影点评系统</h1>
        <p>搜索电影、浏览资讯、发布影评、发现周边。</p>
      </div>
    </section>
    <div class="grid">
      <PlaceholderPanel title="热门电影" description="后续接入 /api/home 的热门电影数据。" />
      <PlaceholderPanel title="电影资讯" description="后续展示热门资讯轮播和最新资讯。" />
      <PlaceholderPanel title="优质长评" description="后续展示审核上线后的优质长评。" />
    </div>
  </div>
</template>

<script setup lang="ts">
import PlaceholderPanel from '@/components/common/PlaceholderPanel.vue'
</script>

<style scoped>
.hero {
  display: flex;
  align-items: end;
  min-height: 260px;
  margin-bottom: 20px;
  padding: 32px;
  border-radius: 8px;
  color: #fff;
  background:
    linear-gradient(90deg, rgb(17 24 39 / 86%), rgb(17 24 39 / 28%)),
    url('https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1600&q=80') center/cover;
}

.hero h1 {
  margin: 0 0 10px;
  font-size: 42px;
}

.hero p {
  margin: 0;
  font-size: 18px;
}

.grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(3, 1fr);
}
</style>

