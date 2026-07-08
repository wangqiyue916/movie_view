<template>
  <div class="page audit-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">内容审核</h1>
      </div>

      <!-- Tabs -->
      <div class="tab-bar">
        <button
          :class="['tab-btn', { active: tab === 'PENDING' }]"
          @click="tab = 'PENDING'; page = 1; loadReviews()"
        >
          待审核
        </button>
        <button
          :class="['tab-btn', { active: tab === 'REJECTED' }]"
          @click="tab = 'REJECTED'; page = 1; loadReviews()"
        >
          已驳回
        </button>
        <button
          :class="['tab-btn', { active: tab === 'ONLINE' }]"
          @click="tab = 'ONLINE'; page = 1; loadReviews()"
        >
          已上线
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <p>加载中...</p>
      </div>

      <!-- Empty -->
      <div v-else-if="reviews.length === 0" class="empty-state">
        <p>暂无{{ tabLabel }}的长评</p>
      </div>

      <!-- Review Cards -->
      <div v-else class="audit-list">
        <article v-for="review in reviews" :key="review.id" class="audit-card">
          <div class="audit-card-body">
            <h3 class="audit-title">{{ review.title }}</h3>
            <div class="audit-meta">
              <span>作者：{{ review.authorNickname }}</span>
              <span>电影：{{ review.movieTitle }}</span>
              <span>{{ formatDate(review.createdAt) }}</span>
              <span class="status-badge" :class="'status-' + (review.status || '').toLowerCase()">
                {{ statusText(review.status) }}
              </span>
            </div>
            <p class="audit-excerpt">{{ getExcerpt(review.contentMd) }}</p>
          </div>
          <div class="audit-actions">
            <template v-if="review.status === 'PENDING'">
              <button class="btn btn-approve" @click="approveReview(review.id)">通过</button>
              <button class="btn btn-reject" @click="openReject(review.id)">驳回</button>
            </template>
            <template v-if="review.status === 'ONLINE'">
              <button class="btn btn-warning" @click="hideReview(review.id)">下架</button>
            </template>
            <template v-if="review.status === 'REJECTED'">
              <button class="btn btn-approve" @click="approveReview(review.id)">通过</button>
            </template>
            <button
              v-if="review.status !== 'DELETED'"
              class="btn btn-delete"
              @click="deleteReview(review.id)"
            >
              删除
            </button>
          </div>
        </article>
      </div>

      <!-- Pagination -->
      <div v-if="total > pageSize" class="pagination">
        <button :disabled="page <= 1" class="btn btn-sm" @click="page--; loadReviews()">
          上一页
        </button>
        <span class="page-info">{{ page }} / {{ Math.ceil(total / pageSize) }}</span>
        <button
          :disabled="page >= Math.ceil(total / pageSize)"
          class="btn btn-sm"
          @click="page++; loadReviews()"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- Reject Dialog -->
    <div v-if="showReject" class="dialog-overlay" @click.self="showReject = false">
      <div class="reject-dialog">
        <h3>驳回长评</h3>
        <textarea
          v-model="rejectReason"
          placeholder="请填写驳回原因..."
          rows="3"
          class="dialog-input"
        ></textarea>
        <div class="dialog-actions">
          <button class="btn btn-cancel" @click="showReject = false">取消</button>
          <button class="btn btn-reject" :disabled="!rejectReason.trim()" @click="doReject">
            确认驳回
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getReviewList,
  setFeatured,
  unsetFeatured,
  hideReview as apiHideReview,
  unhideReview,
  deleteReview as apiDeleteReview,
  type LongReviewVO,
} from '@/api/longReviewApi'

const tab = ref<'PENDING' | 'REJECTED' | 'ONLINE'>('PENDING')
const tabLabel = computed(() => {
  const map = { PENDING: '待审核', REJECTED: '已驳回', ONLINE: '已上线' }
  return map[tab.value]
})

const reviews = ref<LongReviewVO[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)

const showReject = ref(false)
const rejectTargetId = ref<number | null>(null)
const rejectReason = ref('')

async function loadReviews() {
  loading.value = true
  try {
    // We fetch by sortBy=createdAt and filter client-side since the API
    // returns all statuses; in production we'd add a status filter to API
    const res: any = await getReviewList({
      page: page.value,
      pageSize: pageSize,
      sortBy: 'createdAt',
    })
    const all: LongReviewVO[] = res.list || []
    total.value = res.total || 0
    reviews.value = all.filter((r) => r.status === tab.value)
  } catch {
    ElMessage.error('加载审核列表失败')
  } finally {
    loading.value = false
  }
}

async function approveReview(id: number) {
  try {
    await setFeatured(id)
    ElMessage.success('已审核通过')
    loadReviews()
  } catch {
    ElMessage.error('操作失败')
  }
}

function openReject(id: number) {
  rejectTargetId.value = id
  rejectReason.value = ''
  showReject.value = true
}

async function doReject() {
  if (!rejectTargetId.value || !rejectReason.value.trim()) return
  try {
    await apiHideReview(rejectTargetId.value)
    ElMessage.success('已驳回')
    showReject.value = false
    loadReviews()
  } catch {
    ElMessage.error('驳回失败')
  }
}

async function hideReview(id: number) {
  try {
    await apiHideReview(id)
    ElMessage.success('已下架')
    loadReviews()
  } catch {
    ElMessage.error('操作失败')
  }
}

async function deleteReview(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该长评？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await apiDeleteReview(id)
    ElMessage.success('已删除')
    loadReviews()
  } catch {
    // cancelled
  }
}

function getExcerpt(md: string): string {
  const text = md.replace(/[#*>`\[\]()!\-|]/g, '').replace(/\n/g, ' ')
  return text.length > 200 ? text.substring(0, 200) + '...' : text
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

function statusText(status: string): string {
  const map: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    ONLINE: '已上线',
    REJECTED: '已驳回',
    OFFLINE: '已下架',
    DELETED: '已删除',
  }
  return map[status] || status
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.audit-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 8% 18%, rgb(214 176 95 / 8%), transparent 24%),
    radial-gradient(circle at 92% 26%, rgb(214 176 95 / 6%), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0e0c08 46%, #050505 100%);
  padding: 32px 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 16px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: clamp(22px, 2.8vw, 28px);
  color: #e8c16d;
  padding-left: 16px;
  position: relative;
}

.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 22px;
  background: #d6b05f;
  border-radius: 2px;
}

/* ===== Tabs ===== */
.tab-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.tab-btn {
  padding: 8px 22px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 20px;
  background: rgb(255 255 255 / 4%);
  color: #b9ab90;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn:hover {
  border-color: rgb(214 176 95 / 40%);
  color: #d8c69b;
}

.tab-btn.active {
  background: linear-gradient(135deg, #d6b05f, #c89a4a);
  color: #1a1a1a;
  border-color: #d6b05f;
}

/* ===== States ===== */
.loading-state, .empty-state {
  text-align: center;
  padding: 60px 0;
  color: #b9ab90;
  font-size: 15px;
}

/* ===== Audit Cards ===== */
.audit-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.audit-card {
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 12px 28px rgb(0 0 0 / 34%);
}

.audit-card-body {
  margin-bottom: 14px;
}

.audit-title {
  font-size: 17px;
  font-weight: 600;
  color: #fff8e6;
  margin-bottom: 8px;
}

.audit-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #c6b78f;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.status-badge {
  padding: 1px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.status-pending {
  background: rgb(255 193 7 / 16%);
  color: #ffc107;
  border: 1px solid rgb(255 193 7 / 30%);
}

.status-online {
  background: rgb(76 175 80 / 16%);
  color: #4caf50;
  border: 1px solid rgb(76 175 80 / 30%);
}

.status-rejected {
  background: rgb(244 67 54 / 16%);
  color: #f44336;
  border: 1px solid rgb(244 67 54 / 30%);
}

.audit-excerpt {
  font-size: 13px;
  color: #b9ab90;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.audit-actions {
  display: flex;
  gap: 10px;
}

/* ===== Buttons ===== */
.btn {
  padding: 8px 22px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-approve {
  background: linear-gradient(135deg, #4caf50, #388e3c);
  color: #fff;
}

.btn-approve:hover {
  box-shadow: 0 2px 10px rgb(76 175 80 / 30%);
}

.btn-reject {
  background: linear-gradient(135deg, #f44336, #c62828);
  color: #fff;
}

.btn-reject:hover {
  box-shadow: 0 2px 10px rgb(244 67 54 / 30%);
}

.btn-warning {
  background: linear-gradient(135deg, #ff9800, #e65100);
  color: #fff;
}

.btn-delete {
  background: rgb(255 255 255 / 6%);
  color: #b9ab90;
  border: 1px solid rgb(244 67 54 / 30%);
}

.btn-delete:hover {
  border-color: #f44336;
  color: #f44336;
}

.btn-sm {
  padding: 6px 16px;
  font-size: 13px;
  background: rgb(255 255 255 / 6%);
  color: #d8c69b;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 6px;
}

.btn-sm:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.btn-cancel {
  padding: 8px 22px;
  border-radius: 6px;
  border: 1px solid rgb(214 176 95 / 22%);
  background: rgb(255 255 255 / 6%);
  color: #b9ab90;
  cursor: pointer;
  font-size: 13px;
}

/* ===== Pagination ===== */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 28px;
}

.page-info {
  font-size: 14px;
  color: #c6b78f;
}

/* ===== Reject Dialog ===== */
.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgb(0 0 0 / 60%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.reject-dialog {
  background: #1c1a14;
  border: 1px solid rgb(214 176 95 / 28%);
  border-radius: 10px;
  padding: 28px;
  width: 90%;
  max-width: 420px;
  box-shadow: 0 16px 40px rgb(0 0 0 / 50%);
}

.reject-dialog h3 {
  color: #e8c16d;
  font-size: 18px;
  margin-bottom: 16px;
}

.dialog-input {
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

.dialog-input::placeholder {
  color: #8a7d66;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
}
</style>