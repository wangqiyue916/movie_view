<template>
  <div class="comment-item">
    <div class="comment-avatar">
      <img :src="comment.userAvatarUrl || defaultAvatar" :alt="comment.userNickname" />
    </div>
    <div class="comment-body">
      <div class="comment-meta">
        <span class="comment-author">{{ comment.userNickname }}</span>
        <span v-if="comment.userTotalScore != null" class="comment-rating">{{ comment.userTotalScore }} 分</span>
        <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
      </div>
      <p class="comment-content">{{ comment.content }}</p>
      <div class="comment-actions">
        <button class="action-btn" :class="{ active: comment.likedByMe }" @click="$emit('like')">
          {{ comment.likedByMe ? '★' : '☆' }} {{ comment.likeCount || '' }}
        </button>
        <button class="action-btn report" @click="$emit('report')">举报</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ShortCommentItem } from '@/api/movieApi'

defineProps<{ comment: ShortCommentItem }>()
defineEmits<{ like: []; report: [] }>()

const defaultAvatar = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 40 40%22%3E%3Ccircle cx=%2220%22 cy=%2220%22 r=%2220%22 fill=%22%23333%22/%3E%3Ctext x=%2220%22 y=%2226%22 text-anchor=%22middle%22 fill=%22%23999%22 font-size=%2216%22%3E?%3C/text%3E%3C/svg%3E'

function formatTime(dateStr: string): string {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return dateStr.substring(0, 10)
}
</script>

<style scoped>
.comment-item {
  display: flex;
  gap: 14px;
  padding: 16px 0;
  border-bottom: 1px solid rgb(214 176 95 / 8%);
}

.comment-avatar {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  background: #1a1a1a;
  border: 1px solid rgb(214 176 95 / 16%);
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-meta {
  display: flex;
  gap: 10px;
  align-items: baseline;
  margin-bottom: 6px;
}

.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: #d8c69b;
}

.comment-time {
  font-size: 12px;
  color: #6b5e45;
}

.comment-rating {
  padding: 2px 7px;
  border: 1px solid rgb(214 176 95 / 26%);
  border-radius: 999px;
  color: #e8c16d;
  font-size: 12px;
  line-height: 1.2;
  background: rgb(214 176 95 / 8%);
}

.comment-content {
  font-size: 14px;
  color: #b9ab90;
  line-height: 1.7;
  margin: 0;
}

.comment-actions {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}

.action-btn {
  background: none;
  border: none;
  font-size: 12px;
  color: #6b5e45;
  cursor: pointer;
  padding: 2px 4px;
  transition: color 0.2s;
}

.action-btn:hover {
  color: #cbb98f;
}

.action-btn.active {
  color: #e8c16d;
}

.action-btn.report:hover {
  color: #e0704a;
}
</style>
