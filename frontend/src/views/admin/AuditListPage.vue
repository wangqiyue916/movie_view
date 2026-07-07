<template>
  <div class="audit-page">
    <h2>内容审核</h2>
    <p class="hint">所有待审核的长评都会显示在这里，管理员可以审核通过、驳回或操作上下线。</p>

    <div class="tabs">
      <button :class="['tab', { active: tab === 'PENDING' }]" @click="tab = 'PENDING'; page = 1; loadReviews()">待审核</button>
      <button :class="['tab', { active: tab === 'ALL' }]" @click="tab = 'ALL'; page = 1; loadReviews()">全部长评</button>
    </div>

    <div v-if="loading" class="status-text">加载中...</div>
    <div v-else-if="reviews.length === 0" class="empty-text">
      <template v-if="tab === 'PENDING'">暂无待审核长评</template>
      <template v-else>暂无长评</template>
    </div>
    <div v-else class="list">
      <div v-for="review in reviews" :key="review.id" class="card">
        <div class="card-header">
          <strong>{{ review.title }}</strong>
          <span class="badge" :class="statusClass(review.status)">{{ review.status }}</span>
        </div>
        <div class="card-meta">
          <span>作者: {{ review.authorNickname }}</span>
          <span>电影: {{ review.movieTitle }}</span>
          <span>{{ formatDate(review.createdAt) }}</span>
        </div>
        <p class="card-excerpt">{{ getExcerpt(review.contentMd) }}</p>
        <div class="card-actions">
          <template v-if="review.status === 'PENDING'">
            <button class="btn-approve" @click="handleApprove(review.id)">✓ 通过</button>
            <button class="btn-reject" @click="promptReject(review.id)">✗ 驳回</button>
          </template>
          <template v-if="review.status === 'ONLINE'">
            <button class="btn-warn" @click="handleHide(review.id)">下架</button>
          </template>
          <template v-if="review.status === 'OFFLINE'">
            <button class="btn-approve" @click="handleUnhide(review.id)">恢复上线</button>
          </template>
          <button class="btn-featured" @click="handleFeature(review.id)">精选</button>
          <button class="btn-danger" @click="handleDelete(review.id)">删除</button>
        </div>
      </div>
    </div>

    <div v-if="total > pageSize" class="pagination">
      <button :disabled="page <= 1" @click="page--; loadReviews()">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" @click="page++; loadReviews()">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviewList, setFeatured, hideReview, unhideReview, deleteReview } from '@/api/longReviewApi'

const reviews = ref<any[]>([])
const loading = ref(true)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const tab = ref('PENDING')

const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)

function statusClass(status: string) {
  const map: Record<string, string> = {
    PENDING: 'pending',
    ONLINE: 'online',
    OFFLINE: 'offline',
    REJECTED: 'rejected',
  }
  return map[status] || ''
}

function getExcerpt(md: string): string {
  if (!md) return ''
  const text = md.replace(/[#*>`\[\]()!\-|]/g, '').replace(/\n/g, ' ')
  return text.length > 120 ? text.substring(0, 120) + '...' : text
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

async function loadReviews() {
  loading.value = true
  try {
    const res: any = await getReviewList({
      sortBy: 'createdAt',
      page: page.value,
      pageSize,
    })
    let list = res.list || []
    if (tab.value === 'PENDING') {
      list = list.filter((r: any) => r.status === 'PENDING')
    }
    reviews.value = list
    total.value = tab.value === 'PENDING' ? list.length : res.total || 0
  } catch (e: any) {
    ElMessage.error('加载长评列表失败')
  } finally {
    loading.value = false
  }
}

async function handleApprove(id: number) {
  try {
    // The API uses POST /admin/long-reviews/{id}/feature (sets status to ONLINE indirectly)
    await setFeatured(id)
    ElMessage.success('长评已通过审核并设为精选')
    loadReviews()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

async function handleHide(id: number) {
  try {
    await hideReview(id)
    ElMessage.success('已下架')
    loadReviews()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

async function handleUnhide(id: number) {
  try {
    await unhideReview(id)
    ElMessage.success('已恢复上线')
    loadReviews()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

async function handleFeature(id: number) {
  try {
    await setFeatured(id)
    ElMessage.success('已设为精选')
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该长评吗？', '确认', { type: 'warning' })
    await deleteReview(id)
    ElMessage.success('已删除')
    loadReviews()
  } catch {
    // cancelled
  }
}

async function promptReject(id: number) {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入驳回原因', '驳回长评', {
      inputPlaceholder: '请说明驳回原因',
      confirmButtonText: '驳回',
      cancelButtonText: '取消',
    })
    if (reason) {
      // Use the hide API as a rejection mechanism (sets to OFFLINE)
      await hideReview(id)
      ElMessage.success('已驳回')
      loadReviews()
    }
  } catch {
    // cancelled
  }
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.audit-page {
  max-width: 900px;
  margin: 0 auto;
}

h2 {
  font-size: 22px;
  color: #e8c16d;
  margin-bottom: 6px;
}

.hint {
  color: #8a7d66;
  font-size: 13px;
  margin-bottom: 20px;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.tab {
  padding: 8px 18px;
  border: 1px solid rgb(214 176 95 / 24%);
  border-radius: 20px;
  background: transparent;
  color: #b9ab90;
  cursor: pointer;
  font-size: 14px;
}

.tab.active {
  background: linear-gradient(135deg, #d6b05f, #c89a4a);
  color: #111;
  border-color: #d6b05f;
}

.status-text, .empty-text {
  text-align: center;
  padding: 48px;
  color: #8a7d66;
  font-size: 14px;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card {
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 8px;
  padding: 16px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.card-header strong {
  color: #fff6df;
  font-size: 16px;
}

.badge {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 12px;
}

.badge.pending {
  background: #2d3748;
  color: #a0aec0;
}

.badge.online {
  background: #1c4d2e;
  color: #68d391;
}

.badge.offline {
  background: #4a2c1c;
  color: #f6ad55;
}

.badge.rejected {
  background: #5c1a1a;
  color: #fc8181;
}

.card-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #8a7d66;
  margin-bottom: 8px;
}

.card-excerpt {
  color: #b9ab90;
  font-size: 13px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.card-actions button {
  padding: 6px 14px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 6px;
  background: transparent;
  color: #c6b78f;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.15s;
}

.btn-approve:hover { border-color: #68d391; color: #68d391; }
.btn-reject:hover { border-color: #fc8181; color: #fc8181; }
.btn-warn:hover { border-color: #f6ad55; color: #f6ad55; }
.btn-featured:hover { border-color: #d6b05f; color: #e8c16d; }
.btn-danger:hover { border-color: #fc8181; color: #fc8181; background: rgb(252 129 129 / 8%); }

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 28px;
  color: #c6b78f;
  font-size: 13px;
}

.pagination button {
  padding: 6px 16px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 6px;
  background: transparent;
  color: #d8c69b;
  cursor: pointer;
}

.pagination button:disabled { opacity: 0.35; cursor: not-allowed; }
</style>