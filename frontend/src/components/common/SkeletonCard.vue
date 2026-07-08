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