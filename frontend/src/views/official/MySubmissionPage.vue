<template>
  <div class="page my-submission-page">
    <div class="page-header">
      <h1>我的投稿</h1>
      <div class="header-actions">
        <el-button type="warning" @click="router.push('/official/videos/submit')">提交视频</el-button>
        <el-button type="warning" @click="router.push('/official/merchandise/submit')">提交周边</el-button>
      </div>
    </div>

    <!-- Filters -->
    <div class="filter-bar">
      <el-tabs v-model="statusFilter" @tab-change="handleStatusChange">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="待审核" name="PENDING" />
        <el-tab-pane label="已通过" name="APPROVED" />
        <el-tab-pane label="已驳回" name="REJECTED" />
        <el-tab-pane label="已上线" name="ONLINE" />
        <el-tab-pane label="已下架" name="OFFLINE" />
      </el-tabs>
      <div class="type-filter">
        <el-select v-model="typeFilter" placeholder="内容类型" clearable @change="handleTypeChange">
          <el-option label="全部" value="" />
          <el-option label="视频" value="VIDEO" />
          <el-option label="周边" value="MERCHANDISE" />
        </el-select>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="submissions.length === 0" class="empty-state">
      <el-empty description="暂无投稿记录">
        <el-button type="warning" @click="router.push('/official/videos/submit')">去投稿</el-button>
      </el-empty>
    </div>

    <div v-else class="submission-grid">
      <div
        v-for="item in submissions"
        :key="item.id"
        class="submission-card"
      >
        <div class="card-header">
          <el-tag size="small" :type="typeTagColor(item.type)">
            {{ typeLabel(item.type) }}
          </el-tag>
          <el-tag size="small" :type="statusTagType(item.status)">
            {{ statusMap[item.status] || item.status }}
          </el-tag>
        </div>
        <h3 class="card-title">{{ item.title || item.name || '无标题' }}</h3>
        <div class="card-meta">
          <span>{{ formatDate(item.createdAt) }}</span>
          <span v-if="item.clickCount !== undefined">{{ item.clickCount }} 点击</span>
        </div>
        <div v-if="item.rejectReason" class="reject-reason">
          <span class="reject-label">驳回原因：</span>{{ item.rejectReason }}
        </div>
        <div class="card-actions">
          <el-button
            v-if="item.status === 'REJECTED'"
            size="small"
            type="warning"
            @click="handleResubmit(item)"
          >重新提交</el-button>
        </div>
      </div>
    </div>

    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { officialApi } from '@/api/officialApi'

const router = useRouter()

const submissions = ref<Record<string, unknown>[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const statusFilter = ref('')
const typeFilter = ref('')

const statusMap: Record<string, string> = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已驳回',
  ONLINE: '已上线',
  OFFLINE: '已下架',
  DRAFT: '草稿',
}

function statusTagType(status: string) {
  const map: Record<string, string> = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    ONLINE: 'primary',
    OFFLINE: 'info',
    DRAFT: 'info',
  }
  return map[status] || 'info'
}

function typeTagColor(type: string) {
  return type === 'VIDEO' ? 'primary' : 'success'
}

function typeLabel(type: string) {
  const map: Record<string, string> = { VIDEO: '视频', MERCHANDISE: '周边' }
  return map[type] || type || '-'
}

function formatDate(dateStr: unknown): string {
  if (!dateStr) return '-'
  return String(dateStr).substring(0, 10)
}

async function fetchData() {
  loading.value = true
  try {
    const res = await officialApi.pageSubmissions({
      page: page.value,
      pageSize,
      status: statusFilter.value || undefined,
      type: typeFilter.value || undefined,
    })
    const data = res as Record<string, unknown>
    submissions.value = (data.list as Record<string, unknown>[]) || []
    total.value = (data.total as number) || 0
  } catch {
    submissions.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleStatusChange() {
  page.value = 1
  fetchData()
}

function handleTypeChange() {
  page.value = 1
  fetchData()
}

function handlePageChange(p: number) {
  page.value = p
  fetchData()
}

function handleResubmit(item: Record<string, unknown>) {
  if (item.type === 'VIDEO') {
    router.push('/official/videos/submit')
  } else {
    router.push('/official/merchandise/submit')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.my-submission-page {
  min-height: calc(100vh - 64px);
  padding: 24px max(22px, calc((100vw - 1280px) / 2)) 72px;
  color: #f7edd5;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 4px;
}

.page-header h1 {
  margin: 0;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 26px;
  font-weight: 800;
  color: #e8c16d;
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.type-filter {
  width: 140px;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 80px 0;
}

.submission-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 18px;
}

.submission-card {
  padding: 18px 20px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  transition: border-color 180ms, box-shadow 180ms;
}

.submission-card:hover {
  border-color: rgb(214 176 95 / 42%);
  box-shadow: 0 4px 18px rgb(214 176 95 / 10%);
}

.card-header {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.card-title {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 600;
  color: #fff6df;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.card-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #8a7b60;
  margin-bottom: 8px;
}

.reject-reason {
  margin-top: 10px;
  padding: 8px 12px;
  border-radius: 4px;
  background: rgb(245 108 108 / 8%);
  border: 1px solid rgb(245 108 108 / 20%);
  color: #e08888;
  font-size: 13px;
  line-height: 1.5;
}

.reject-label {
  font-weight: 600;
}

.card-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 36px;
}

@media (max-width: 640px) {
  .submission-grid {
    grid-template-columns: 1fr;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .type-filter {
    width: 100%;
  }
}
</style>
