<template>
  <div class="featured-review-card" @click="goToDetail(review.id)">
    <div v-if="review.coverUrl" class="card-cover">
      <img :src="review.coverUrl" :alt="review.title" />
    </div>
    <div class="card-body">
      <div class="card-movie" v-if="review.movieTitle">
        <span class="movie-tag">{{ review.movieTitle }}</span>
      </div>
      <h4 class="card-title">{{ review.title }}</h4>
      <p class="card-summary">{{ review.summary }}</p>
      <div class="card-footer">
        <div class="author">
          <img v-if="review.authorAvatar" :src="review.authorAvatar" class="author-avatar" />
          <span class="author-name">{{ review.authorNickname }}</span>
        </div>
        <div class="stats">
          <span class="stat">👍 {{ review.likeCount }}</span>
          <span class="stat">💬 {{ review.replyCount }}</span>
          <span class="stat">⭐ {{ review.favoriteCount }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { FeaturedReviewVO } from '@/api/longReviewApi'

const props = defineProps<{
  review: FeaturedReviewVO
}>()

const router = useRouter()

function goToDetail(id: number) {
  router.push(`/reviews/${id}`)
}
</script>

<style scoped>
.featured-review-card {
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.2s, transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 12px 28px rgb(0 0 0 / 34%);
  display: flex;
  flex-direction: column;
}

.featured-review-card:hover {
  border-color: rgb(214 176 95 / 35%);
  transform: translateY(-3px);
  box-shadow: 0 16px 36px rgb(0 0 0 / 50%);
}

.card-cover {
  height: 160px;
  overflow: hidden;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.featured-review-card:hover .card-cover img {
  transform: scale(1.04);
}

.card-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-movie {
  margin-bottom: 8px;
}

.movie-tag {
  display: inline-block;
  padding: 2px 10px;
  background: rgb(214 176 95 / 15%);
  color: #e8c16d;
  border: 1px solid rgb(214 176 95 / 25%);
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff8e6;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.card-summary {
  font-size: 13px;
  color: #b9ab90;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
  flex: 1;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid rgb(214 176 95 / 18%);
}

.author {
  display: flex;
  align-items: center;
  gap: 6px;
}

.author-avatar {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-size: 13px;
  color: #d8c69b;
}

.stats {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: #c6b78f;
}

.stat {
  white-space: nowrap;
}
</style>
