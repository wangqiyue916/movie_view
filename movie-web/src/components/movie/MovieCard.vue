<template>
  <div class="movie-card" @click="$router.push(`/movies/${movie.id}`)">
    <div class="poster-frame">
      <img
        :src="movie.posterUrl || 'https://via.placeholder.com/300x420/1a1a1a/d6b05f?text=No+Poster'"
        :alt="movie.title"
        class="poster-img"
      />
      <div class="poster-overlay"></div>
      <span v-if="movie.avgTotalScore != null" class="score-badge">{{ movie.avgTotalScore }}</span>
    </div>
    <div class="card-info">
      <p class="movie-year">{{ releaseYear }}</p>
      <h3 class="movie-title">{{ movie.title }}</h3>
      <p v-if="movie.genres" class="movie-genres">{{ movie.genres }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MovieListItem } from '@/api/movieApi'

const props = defineProps<{ movie: MovieListItem }>()

const releaseYear = computed(() => {
  if (!props.movie.releaseDate) return ''
  return props.movie.releaseDate.substring(0, 4)
})
</script>

<style scoped>
.movie-card {
  cursor: pointer;
  border-radius: 6px;
  overflow: hidden;
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid rgb(214 176 95 / 18%);
  transition: transform 0.28s ease, border-color 0.28s ease, box-shadow 0.28s ease;
}

.movie-card:hover {
  transform: translateY(-3px);
  border-color: rgb(214 176 95 / 40%);
  box-shadow: 0 12px 32px rgb(0 0 0 / 40%);
}

.poster-frame {
  position: relative;
  width: 100%;
  aspect-ratio: 2 / 3;
  overflow: hidden;
}

.poster-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.movie-card:hover .poster-img {
  transform: scale(1.05);
}

.poster-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgb(5 5 5 / 60%) 0%, transparent 40%);
}

.score-badge {
  position: absolute;
  right: 8px;
  bottom: 8px;
  padding: 2px 8px;
  font-size: 18px;
  font-weight: 700;
  font-family: Georgia, 'Noto Serif SC', serif;
  color: #e8c16d;
  background: rgb(5 5 5 / 72%);
  border: 1px solid rgb(214 176 95 / 32%);
  border-radius: 4px;
  line-height: 1.4;
}

.card-info {
  padding: 10px 12px 14px;
}

.movie-year {
  font-size: 12px;
  color: #c6b78f;
  margin: 0 0 2px;
}

.movie-title {
  font-size: 15px;
  font-weight: 600;
  color: #fff8e6;
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.movie-genres {
  font-size: 12px;
  color: #b9ab90;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
