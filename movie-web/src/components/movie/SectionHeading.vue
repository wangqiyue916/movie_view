<template>
  <div class="section-heading">
    <span class="gold-line"></span>
    <h2 class="heading-title">{{ title }}</h2>
  </div>
</template>

<script setup lang="ts">
defineProps<{ title: string }>()
</script>

<style scoped>
.section-heading {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 24px;
}

.gold-line {
  display: inline-block;
  width: 48px;
  height: 1px;
  background: linear-gradient(90deg, #d6b05f 0%, rgb(214 176 95 / 0%) 100%);
  flex-shrink: 0;
}

.heading-title {
  margin: 0;
  font-family: 'Noto Serif SC', 'Songti SC', SimSun, serif;
  font-size: clamp(20px, 2.4vw, 28px);
  font-weight: 600;
  color: #e8c16d;
  letter-spacing: 1px;
}
</style>
