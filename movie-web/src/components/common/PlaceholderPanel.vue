<template>
  <section class="panel">
    <div class="panel-body">
      <h1 class="page-title">{{ title }}</h1>
      <p class="muted">{{ description }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  description: string
}>()
</script>

