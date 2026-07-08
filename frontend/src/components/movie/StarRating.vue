<template>
  <div class="star-rating" :class="`size-${size}`">
    <span
      v-for="i in 5"
      :key="i"
      class="star"
      :class="{ filled: i <= modelValue, interactive: !disabled }"
      @click="!disabled && setRating(i)"
      @mouseenter="!disabled && (hoverIdx = i)"
      @mouseleave="hoverIdx = 0"
    >
      &#9733;
    </span>
    <span class="score-text">{{ modelValue > 0 ? (modelValue * 2).toFixed(1) : '未评' }}</span>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  modelValue: number
  disabled?: boolean
  size?: 'small' | 'large'
}>()

const emit = defineEmits<{ 'update:modelValue': [value: number] }>()

const hoverIdx = ref(0)

function setRating(value: number) {
  emit('update:modelValue', value)
}

function isFilled(idx: number) {
  const effective = hoverIdx.value || props.modelValue
  return idx <= effective
}

// For simplicity, each star = 2 points (so 5 stars = 10 max)
// The modelValue is 0-5 (stars), backend receives 0-10
</script>

<style scoped>
.star-rating {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.star {
  font-size: 24px;
  color: rgb(255 255 255 / 10%);
  transition: color 0.15s, transform 0.15s;
}

.star.interactive {
  cursor: pointer;
}

.star.interactive:hover {
  transform: scale(1.15);
  color: #f0c850;
}

.star.filled {
  color: #d6b05f;
}

.size-small .star {
  font-size: 18px;
}

.size-large .star {
  font-size: 30px;
}

.score-text {
  margin-left: 10px;
  font-family: Georgia, 'Noto Serif SC', serif;
  font-size: 16px;
  font-weight: 700;
  color: #e8c16d;
  min-width: 36px;
}

.size-small .score-text {
  font-size: 14px;
}
</style>
