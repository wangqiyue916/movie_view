<template>
  <div class="rating-display" :class="`size-${size}`">
    <span class="rating-label">{{ label }}</span>
    <div class="stars-row">
      <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= filledStars, half: i === halfStarIndex }">
        &#9733;
      </span>
    </div>
    <span class="rating-num">{{ displayValue }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  rating: number | null | undefined
  label?: string
  size?: 'small' | 'large'
}>()

const displayValue = computed(() => {
  if (props.rating == null) return '暂无'
  return (props.rating / 2).toFixed(1)
})

const filledStars = computed(() => {
  if (props.rating == null) return 0
  return Math.floor(props.rating / 2)
})

const halfStarIndex = computed(() => {
  if (props.rating == null) return -1
  const half = props.rating % 2 >= 0.5
  return half ? filledStars.value + 1 : -1
})
</script>

<style scoped>
.rating-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-label {
  font-size: 12px;
  color: #b9ab90;
  min-width: 32px;
}

.stars-row {
  display: flex;
  gap: 2px;
}

.star {
  color: rgb(255 255 255 / 12%);
  font-size: 16px;
}

.star.filled {
  color: #d6b05f;
}

.star.half {
  color: rgb(214 176 95 / 50%);
}

.rating-num {
  font-family: Georgia, 'Noto Serif SC', serif;
  color: #e8c16d;
  font-weight: 700;
}

.size-small .star { font-size: 14px; }
.size-small .rating-num { font-size: 14px; }
.size-small .rating-label { font-size: 11px; }

.size-large .star { font-size: 22px; }
.size-large .rating-num { font-size: 26px; }
</style>
