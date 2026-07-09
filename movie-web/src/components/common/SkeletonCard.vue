<!--
  骨架屏卡片组件（潘玺名，2025-07-08）
  用于页面数据加载期间的占位展示，避免白屏等待
  金色 shimmer 动画与黑金主题一致
  用法：<SkeletonCard width="100%" height="200px" />
-->
<template>
  <div class="skeleton-card" :style="{ width: width, height: height }">
    <div class="skeleton-pulse" />
  </div>
</template>

<script setup lang="ts">
defineProps<{ width?: string; height?: string }>()
</script>

<style scoped>
.skeleton-card {
  border-radius: 8px;
  overflow: hidden;
  background: rgb(255 255 255 / 4%);
  border: 1px solid rgb(214 176 95 / 8%);
}

.skeleton-pulse {
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgb(214 176 95 / 6%) 50%,
    transparent 100%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>