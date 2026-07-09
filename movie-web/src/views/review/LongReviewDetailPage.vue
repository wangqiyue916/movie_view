<template>
  <div class="page detail-page">
    <div class="container">
      <!-- Loading / Error States -->
      <div v-if="loading" class="loading-state">
        <p>加载中...</p>
      </div>

      <div v-else-if="!review" class="empty-state">
        <p>长评不存在或已被删除</p>
        <router-link to="/long-reviews" class="back-link">← 返回列表</router-link>
      </div>

      <!-- Detail Content -->
      <template v-else>
        <!-- Back Link -->
        <div class="top-bar">
          <router-link to="/long-reviews" class="back-link">← 返回长评列表</router-link>
        </div>

        <!-- Header -->
        <article class="review-article">
          <header class="article-header">
            <h1 class="article-title">{{ review.title }}</h1>
            <div class="article-meta">
              <div class="author-info">
                <img
                  v-if="review.authorAvatar"
                  :src="review.authorAvatar"
                  class="author-avatar"
                  alt=""
                />
                <span class="author-name">{{ review.authorNickname }}</span>
                <span class="meta-divider">·</span>
                <span class="movie-tag">
                  <router-link :to="'/movies/' + review.movieId" class="movie-link">
                    {{ review.movieTitle }}
                  </router-link>
                </span>
              </div>
              <div class="meta-stats">
                <span class="meta-item">{{ formatDate(review.createdAt) }}</span>
                <span class="meta-item">阅读 {{ review.viewCount }}</span>
                <span v-if="review.isFeatured" class="featured-badge">★ 精选</span>
              </div>
            </div>

            <!-- Cover Image (if present) -->
            <div v-if="review.coverUrl" class="article-cover">
              <img :src="review.coverUrl" :alt="review.title" />
            </div>
          </header>

          <!-- Markdown Content -->
          <section class="article-body">
            <div class="markdown-content" v-html="renderedMd"></div>
          </section>

          <!-- Review Images Gallery -->
          <div v-if="review.images && review.images.length > 0" class="image-gallery">
            <div v-for="(img, idx) in review.images" :key="idx" class="gallery-item">
              <img :src="img" :alt="'图片 ' + (idx + 1)" loading="lazy" />
            </div>
          </div>

          <!-- Action Bar (Like / Favorite / Report) -->
          <footer class="action-bar">
            <button
              :class="['action-btn', 'like-btn', { active: review.liked }]"
              @click="toggleLike"
            >
              <span class="action-icon">{{ review.liked ? '❤️' : '🤍' }}</span>
              <span>点赞 {{ review.likeCount }}</span>
            </button>
            <button
              :class="['action-btn', 'fav-btn', { active: review.favorited }]"
              @click="toggleFavorite"
            >
              <span class="action-icon">{{ review.favorited ? '⭐' : '☆' }}</span>
              <span>收藏 {{ review.favoriteCount }}</span>
            </button>
            <button class="action-btn report-btn" @click="showReportDialog = true">
              <span class="action-icon">🚩</span>
              <span>举报</span>
            </button>
          </footer>
        </article>

        <!-- Report Dialog -->
        <div v-if="showReportDialog" class="dialog-overlay" @click.self="showReportDialog = false">
          <div class="report-dialog">
            <h3>举报长评</h3>
            <textarea
              v-model="reportReason"
              placeholder="请填写举报原因..."
              rows="4"
              class="report-input"
              maxlength="255"
            ></textarea>
            <div class="dialog-actions">
              <button class="btn btn-cancel" @click="showReportDialog = false">取消</button>
              <button
                class="btn btn-primary"
                :disabled="!reportReason.trim() || reporting"
                @click="submitReport"
              >
                {{ reporting ? '提交中...' : '提交举报' }}
              </button>
            </div>
          </div>
        </div>

        <!-- Reply Section -->
        <section class="reply-section">
          <h2 class="section-title">回复 ({{ review.replyCount }})</h2>

          <!-- Reply Form -->
          <div class="reply-form">
            <textarea
              v-model="newReplyContent"
              placeholder="写下你的回复..."
              rows="3"
              class="reply-input"
              maxlength="1000"
            ></textarea>
            <div class="reply-form-actions">
              <button
                class="btn btn-primary"
                :disabled="!newReplyContent.trim() || replying"
                @click="submitReply(null)"
              >
                {{ replying ? '发送中...' : '发表回复' }}
              </button>
            </div>
          </div>

          <!-- Reply List -->
          <div v-if="repliesLoading" class="loading-state">
            <p>加载回复中...</p>
          </div>

          <div v-else-if="replies.length === 0" class="empty-state">
            <p>暂无回复，来发表第一条回复吧</p>
          </div>

          <div v-else class="reply-list">
            <div v-for="reply in replies" :key="reply.id" class="reply-item">
              <!-- Top-level Reply -->
              <div class="reply-card">
                <div class="reply-header">
                  <img
                    v-if="reply.userAvatar"
                    :src="reply.userAvatar"
                    class="reply-avatar"
                    alt=""
                  />
                  <div class="reply-meta">
                    <span class="reply-author">{{ reply.userNickname }}</span>
                    <span class="reply-date">{{ formatDate(reply.createdAt) }}</span>
                  </div>
                </div>
                <p class="reply-content">{{ reply.content }}</p>
                <div class="reply-actions">
                  <button class="reply-action-btn" @click="toggleReplyLike(reply)">
                    {{ reply.liked ? '❤️' : '🤍' }} {{ reply.likeCount }}
                  </button>
                  <button
                    class="reply-action-btn"
                    @click="replyingTo = replyingTo === reply.id ? null : reply.id"
                  >
                    💬 回复
                  </button>
                </div>

                <!-- Inline Reply Sub-form -->
                <div v-if="replyingTo === reply.id" class="sub-reply-form">
                  <textarea
                    v-model="subReplyContent"
                    placeholder="回复 {{ reply.userNickname }}..."
                    rows="2"
                    class="reply-input"
                    maxlength="1000"
                  ></textarea>
                  <div class="reply-form-actions">
                    <button
                      class="btn btn-sm btn-cancel"
                      @click="replyingTo = null; subReplyContent = ''"
                    >
                      取消
                    </button>
                    <button
                      class="btn btn-sm btn-primary"
                      :disabled="!subReplyContent.trim() || replying"
                      @click="submitReply(reply.id)"
                    >
                      回复
                    </button>
                  </div>
                </div>
              </div>

              <!-- Child Replies -->
              <div v-if="reply.children && reply.children.length > 0" class="child-replies">
                <div
                  v-for="child in reply.children"
                  :key="child.id"
                  class="reply-card child-reply"
                >
                  <div class="reply-header">
                    <img
                      v-if="child.userAvatar"
                      :src="child.userAvatar"
                      class="reply-avatar"
                      alt=""
                    />
                    <div class="reply-meta">
                      <span class="reply-author">{{ child.userNickname }}</span>
                      <span class="reply-date">{{ formatDate(child.createdAt) }}</span>
                    </div>
                  </div>
                  <p class="reply-content">{{ child.content }}</p>
                  <div class="reply-actions">
                    <button class="reply-action-btn" @click="toggleReplyLike(child)">
                      {{ child.liked ? '❤️' : '🤍' }} {{ child.likeCount }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Reply Pagination -->
          <div v-if="repliesTotal > repliesPageSize" class="pagination">
            <button
              :disabled="repliesPage <= 1"
              class="btn btn-sm"
              @click="repliesPage--; loadReplies()"
            >
              上一页
            </button>
            <span class="page-info">{{ repliesPage }} / {{ Math.ceil(repliesTotal / repliesPageSize) }}</span>
            <button
              :disabled="repliesPage >= Math.ceil(repliesTotal / repliesPageSize)"
              class="btn btn-sm"
              @click="repliesPage++; loadReplies()"
            >
              下一页
            </button>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'
import {
  getReviewDetail,
  likeReview,
  favoriteReview,
  reportReview,
  getReplies,
  createReply,
  likeReply,
  type LongReviewVO,
  type ReviewReplyVO,
} from '@/api/longReviewApi'

const route = useRoute()
const router = useRouter()

// ===== Marked Setup (v12+ compatible) =====
marked.use({
  gfm: true,
  breaks: true,
  renderer: {
    code(code: string, infostring?: string): string {
      const lang = infostring?.match(/\S*/)?.[0]
      const language = lang && hljs.getLanguage(lang) ? lang : null
      let highlighted: string
      if (language) {
        highlighted = hljs.highlight(code, { language }).value
      } else {
        highlighted = hljs.highlightAuto(code).value
      }
      const langAttr = language ? ` class="language-${language}"` : ''
      return `<pre><code${langAttr}>${highlighted}</code></pre>\n`
    }
  }
})

// ===== Review State =====
const review = ref<LongReviewVO | null>(null)
const loading = ref(true)
const renderedMd = computed(() => {
  if (!review.value?.contentMd) return ''
  return marked.parse(review.value.contentMd) as string
})

const reviewId = computed(() => Number(route.params.id))

// ===== Like / Favorite / Report =====
async function toggleLike() {
  if (!review.value) return
  try {
    await likeReview(review.value.id)
    review.value.liked = !review.value.liked
    review.value.likeCount += review.value.liked ? 1 : -1
    if (review.value.likeCount < 0) review.value.likeCount = 0
  } catch {
    ElMessage.error('操作失败，请稍后重试')
  }
}

async function toggleFavorite() {
  if (!review.value) return
  try {
    await favoriteReview(review.value.id)
    review.value.favorited = !review.value.favorited
    review.value.favoriteCount += review.value.favorited ? 1 : -1
    if (review.value.favoriteCount < 0) review.value.favoriteCount = 0
  } catch {
    ElMessage.error('操作失败，请稍后重试')
  }
}

const showReportDialog = ref(false)
const reportReason = ref('')
const reporting = ref(false)

async function submitReport() {
  if (!review.value || !reportReason.value.trim()) return
  reporting.value = true
  try {
    await reportReview(review.value.id, reportReason.value.trim())
    ElMessage.success('举报已提交，我们会尽快处理')
    showReportDialog.value = false
    reportReason.value = ''
  } catch {
    ElMessage.error('举报提交失败，请稍后重试')
  } finally {
    reporting.value = false
  }
}

// ===== Reply State =====
const replies = ref<ReviewReplyVO[]>([])
const repliesLoading = ref(false)
const repliesPage = ref(1)
const repliesPageSize = 20
const repliesTotal = ref(0)
const replying = ref(false)
const newReplyContent = ref('')
const replyingTo = ref<number | null>(null)
const subReplyContent = ref('')

async function loadReplies() {
  if (!review.value) return
  repliesLoading.value = true
  try {
    const res: any = await getReplies(review.value.id, {
      page: repliesPage.value,
      pageSize: repliesPageSize,
    })
    replies.value = res.list || []
    repliesTotal.value = res.total || 0
  } catch {
    console.error('Failed to load replies')
  } finally {
    repliesLoading.value = false
  }
}

async function submitReply(parentId: number | null) {
  if (!review.value) return
  const content = parentId !== null ? subReplyContent.value : newReplyContent.value
  if (!content.trim()) return

  replying.value = true
  try {
    await createReply(review.value.id, {
      parentId,
      content: content.trim(),
    })
    if (parentId !== null) {
      subReplyContent.value = ''
      replyingTo.value = null
    } else {
      newReplyContent.value = ''
    }
    if (review.value) review.value.replyCount += 1
    ElMessage.success('回复成功')
    loadReplies()
  } catch {
    ElMessage.error('回复失败，请稍后重试')
  } finally {
    replying.value = false
  }
}

async function toggleReplyLike(reply: ReviewReplyVO) {
  try {
    await likeReply(reply.id)
    reply.liked = !reply.liked
    reply.likeCount += reply.liked ? 1 : -1
    if (reply.likeCount < 0) reply.likeCount = 0
  } catch {
    ElMessage.error('操作失败')
  }
}

// ===== Utilities =====
function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// ===== Init =====
async function loadReview() {
  loading.value = true
  try {
    const res: any = await getReviewDetail(reviewId.value)
    review.value = res || null
  } catch {
    review.value = null
  } finally {
    loading.value = false
  }
}

watch(reviewId, () => {
  loadReview()
  repliesPage.value = 1
})

onMounted(async () => {
  await loadReview()
  if (review.value) {
    await loadReplies()
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;600;700&display=swap');

/* ===== Page Background ===== */
.detail-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 8% 18%, rgb(214 176 95 / 8%), transparent 24%),
    radial-gradient(circle at 92% 26%, rgb(214 176 95 / 6%), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0e0c08 46%, #050505 100%);
  padding: 32px 0;
}

.container {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 16px;
}

/* ===== Top Bar ===== */
.top-bar {
  margin-bottom: 20px;
}

.back-link {
  color: #d6b05f;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.2s;
}

.back-link:hover {
  color: #e8c16d;
}

/* ===== Loading / Empty ===== */
.loading-state, .empty-state {
  text-align: center;
  padding: 80px 0;
  color: #b9ab90;
  font-size: 15px;
}

/* ===== Article ===== */
.review-article {
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 10px;
  box-shadow: 0 12px 28px rgb(0 0 0 / 34%);
  overflow: hidden;
  margin-bottom: 32px;
}

.article-header {
  padding: 32px 32px 0;
}

.article-title {
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: clamp(22px, 2.8vw, 30px);
  font-weight: 700;
  color: #fff8e6;
  line-height: 1.4;
  margin-bottom: 16px;
}

.article-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgb(214 176 95 / 14%);
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #d8c69b;
}

.author-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgb(214 176 95 / 30%);
}

.author-name {
  font-weight: 500;
}

.meta-divider {
  color: #6a5f4e;
}

.movie-link {
  color: #e8c16d;
  text-decoration: none;
}

.movie-link:hover {
  text-decoration: underline;
}

.meta-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #b9ab90;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.featured-badge {
  background: linear-gradient(135deg, #d6b05f, #c89a4a);
  color: #1a1a1a;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

/* ===== Cover Image ===== */
.article-cover {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
  max-height: 400px;
}

.article-cover img {
  width: 100%;
  height: auto;
  max-height: 400px;
  object-fit: cover;
}

/* ===== Markdown Content ===== */
.article-body {
  padding: 28px 32px;
}

.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3),
.markdown-content :deep(h4) {
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  color: #f0dca0;
  margin: 24px 0 12px;
  line-height: 1.4;
}

.markdown-content :deep(h2) {
  font-size: 22px;
  border-bottom: 1px solid rgb(214 176 95 / 20%);
  padding-bottom: 8px;
}

.markdown-content :deep(h3) {
  font-size: 18px;
}

.markdown-content :deep(p) {
  font-size: 15px;
  line-height: 1.85;
  color: #d4c7a8;
  margin: 14px 0;
}

.markdown-content :deep(strong) {
  color: #e8d098;
  font-weight: 600;
}

.markdown-content :deep(a) {
  color: #e8c16d;
  text-decoration: underline;
}

.markdown-content :deep(blockquote) {
  border-left: 3px solid rgb(214 176 95 / 40%);
  padding: 8px 16px;
  margin: 16px 0;
  background: rgb(214 176 95 / 4%);
  border-radius: 0 4px 4px 0;
  color: #c6b78f;
}

.markdown-content :deep(hr) {
  border: none;
  border-top: 1px solid rgb(214 176 95 / 18%);
  margin: 24px 0;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  padding-left: 24px;
  margin: 12px 0;
}

.markdown-content :deep(li) {
  font-size: 15px;
  line-height: 1.8;
  color: #d4c7a8;
}

.markdown-content :deep(code) {
  background: rgb(214 176 95 / 12%);
  color: #e8c16d;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  font-family: 'Consolas', 'Monaco', monospace;
}

.markdown-content :deep(pre) {
  background: rgb(0 0 0 / 40%);
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 6px;
  padding: 16px;
  overflow-x: auto;
  margin: 16px 0;
}

.markdown-content :deep(pre code) {
  background: none;
  padding: 0;
  color: #d4c7a8;
  font-size: 13px;
}

.markdown-content :deep(img) {
  max-width: 100%;
  border-radius: 6px;
  margin: 12px 0;
}

.markdown-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}

.markdown-content :deep(th) {
  background: rgb(214 176 95 / 12%);
  color: #e8c16d;
  padding: 10px 14px;
  border: 1px solid rgb(214 176 95 / 18%);
  text-align: left;
}

.markdown-content :deep(td) {
  padding: 8px 14px;
  border: 1px solid rgb(214 176 95 / 12%);
  color: #d4c7a8;
}

/* ===== Image Gallery ===== */
.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  padding: 0 32px 24px;
}

.gallery-item {
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid rgb(214 176 95 / 18%);
}

.gallery-item img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  transition: transform 0.3s;
}

.gallery-item:hover img {
  transform: scale(1.03);
}

/* ===== Action Bar ===== */
.action-bar {
  display: flex;
  gap: 16px;
  padding: 16px 32px 24px;
  border-top: 1px solid rgb(214 176 95 / 14%);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 20px;
  background: rgb(255 255 255 / 4%);
  color: #b9ab90;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  border-color: #d6b05f;
  color: #e8c16d;
}

.action-btn.active {
  background: rgb(214 176 95 / 14%);
  border-color: #d6b05f;
  color: #e8c16d;
}

.action-icon {
  font-size: 16px;
}

.report-btn:hover {
  border-color: #e87461;
  color: #e87461;
}

/* ===== Buttons ===== */
.btn {
  padding: 10px 24px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-primary {
  background: linear-gradient(135deg, #d6b05f, #c89a4a);
  color: #1a1a1a;
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #e8c16d, #d6b05f);
  box-shadow: 0 2px 12px rgb(214 176 95 / 30%);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-cancel {
  background: rgb(255 255 255 / 6%);
  color: #b9ab90;
  border: 1px solid rgb(214 176 95 / 22%);
}

.btn-sm {
  padding: 6px 16px;
  font-size: 13px;
  background: rgb(255 255 255 / 6%);
  color: #d8c69b;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 6px;
}

.btn-sm:hover:not(:disabled) {
  border-color: #d6b05f;
  color: #e8c16d;
}

.btn-sm:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

/* ===== Report Dialog ===== */
.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgb(0 0 0 / 60%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.report-dialog {
  background: #1c1a14;
  border: 1px solid rgb(214 176 95 / 28%);
  border-radius: 10px;
  padding: 28px;
  width: 90%;
  max-width: 420px;
  box-shadow: 0 16px 40px rgb(0 0 0 / 50%);
}

.report-dialog h3 {
  color: #e8c16d;
  font-size: 18px;
  margin-bottom: 16px;
}

.report-input {
  width: 100%;
  background: rgb(0 0 0 / 30%);
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 6px;
  color: #f7edd5;
  padding: 10px;
  font-size: 14px;
  resize: vertical;
  box-sizing: border-box;
}

.report-input::placeholder {
  color: #8a7d66;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
}

/* ===== Reply Section ===== */
.reply-section {
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 10px;
  box-shadow: 0 12px 28px rgb(0 0 0 / 34%);
  padding: 28px 32px;
}

.section-title {
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 20px;
  color: #e8c16d;
  margin-bottom: 20px;
  padding-left: 12px;
  position: relative;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px;
  background: #d6b05f;
  border-radius: 2px;
}

/* ===== Reply Form ===== */
.reply-form {
  margin-bottom: 24px;
}

.reply-input {
  width: 100%;
  background: rgb(0 0 0 / 30%);
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 6px;
  color: #f7edd5;
  padding: 10px 14px;
  font-size: 14px;
  resize: vertical;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.reply-input:focus {
  outline: none;
  border-color: #d6b05f;
}

.reply-input::placeholder {
  color: #8a7d66;
}

.reply-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

/* ===== Reply List ===== */
.reply-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.reply-card {
  background: rgb(255 255 255 / 3%);
  border: 1px solid rgb(214 176 95 / 12%);
  border-radius: 8px;
  padding: 16px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.reply-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgb(214 176 95 / 25%);
}

.reply-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.reply-author {
  font-size: 14px;
  font-weight: 500;
  color: #d8c69b;
}

.reply-date {
  font-size: 12px;
  color: #8a7d66;
}

.reply-content {
  font-size: 14px;
  line-height: 1.7;
  color: #d4c7a8;
  margin-bottom: 10px;
}

.reply-actions {
  display: flex;
  gap: 12px;
}

.reply-action-btn {
  background: none;
  border: none;
  color: #b9ab90;
  font-size: 13px;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  transition: color 0.2s;
}

.reply-action-btn:hover {
  color: #e8c16d;
}

/* ===== Child Replies ===== */
.child-replies {
  margin-left: 40px;
  margin-top: 8px;
  padding-left: 16px;
  border-left: 2px solid rgb(214 176 95 / 16%);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ===== Sub Reply Form ===== */
.sub-reply-form {
  margin-top: 12px;
}

.sub-reply-form .reply-input {
  font-size: 13px;
}

/* ===== Pagination ===== */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}

.page-info {
  font-size: 14px;
  color: #c6b78f;
}
</style>
