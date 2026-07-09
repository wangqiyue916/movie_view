<template>
  <div class="report-page">
    <div class="page-header">
      <h1 class="page-title">举报处理</h1>
      <p class="page-desc">管理用户提交的违规内容举报</p>
    </div>

    <!-- 状态筛选 -->
    <div class="filter-tabs">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        :class="['tab-btn', { active: activeStatus === tab.value }]"
        @click="activeStatus = tab.value; fetchList()"
      >
        {{ tab.label }}
        <span v-if="tab.value === 'PENDING' && pendingCount > 0" class="tab-badge">
          {{ pendingCount }}
        </span>
      </button>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon" :size="28"><Loading /></el-icon>
      <p>加载举报列表...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="reports.length === 0" class="empty-state">
      <p>{{ activeStatus === 'PENDING' ? '暂无待处理举报' : '暂无记录' }}</p>
    </div>

    <!-- 举报列表 -->
    <div v-else class="report-list">
      <div
        v-for="item in reports"
        :key="item.id"
        :class="['report-card', `card-${item.status?.toLowerCase()}`]"
      >
        <div class="card-header">
          <span class="target-type">{{ typeLabel(item.targetType) }}</span>
          <span class="target-content">{{ item.content }}</span>
          <span :class="['status-tag', `tag-${item.status?.toLowerCase()}`]">
            {{ statusLabel(item.status) }}
          </span>
        </div>
        <div class="card-body">
          <div class="info-row">
            <span class="info-label">举报人</span>
            <span class="info-value">{{ item.reporterName || '匿名' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">举报原因</span>
            <span class="info-value">{{ item.reason }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">举报时间</span>
            <span class="info-value">{{ formatDate(item.createdAt) }}</span>
          </div>
        </div>

        <!-- 操作 -->
        <div v-if="item.status === 'PENDING'" class="card-actions">
          <el-button
            type="success"
            size="small"
            class="action-btn resolve-btn"
            @click="handleResolve(item)"
          >
            <el-icon><Check /></el-icon>
            标记已处理
          </el-button>
          <el-button
            type="danger"
            size="small"
            class="action-btn dismiss-btn"
            @click="handleDismiss(item)"
          >
            <el-icon><Close /></el-icon>
            驳回举报
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination">
      <button :disabled="page <= 1" class="page-btn" @click="page--; fetchList()">上一页</button>
      <span class="page-info">{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" class="page-btn" @click="page++; fetchList()">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Check, Close } from '@element-plus/icons-vue'
import request from '@/api/request'

const statusTabs = [
  { value: 'PENDING', label: '待处理' },
  { value: 'RESOLVED', label: '已处理' },
  { value: 'REJECTED', label: '已驳回' },
]

const activeStatus = ref('PENDING')
const reports = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const pendingCount = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)

function typeLabel(t: string) {
  const m: Record<string, string> = {
    LONG_REVIEW: '长评', SHORT_COMMENT: '短评', REVIEW_REPLY: '回复', NEWS: '资讯',
  }
  return m[t] || t
}

function statusLabel(s: string) {
  const m: Record<string, string> = { PENDING: '待处理', RESOLVED: '已处理', REJECTED: '已驳回' }
  return m[s] || s
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN', { year:'numeric', month:'2-digit', day:'2-digit', hour:'2-digit', minute:'2-digit' })
}

async function fetchList() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/reports', {
      params: { status: activeStatus.value, page: page.value, pageSize: pageSize }
    })
    reports.value = res.list || []
    total.value = res.total || 0
    pendingCount.value = res.pendingCount || 0
  } catch {
    reports.value = []
  } finally {
    loading.value = false
  }
}

async function handleResolve(item: any) {
  try {
    await ElMessageBox.confirm('确定标记此举报为已处理？', '确认', { type: 'success' })
  } catch { return }
  try {
    await request.post(`/admin/reports/${item.id}/resolve`)
    ElMessage.success('举报已处理')
    fetchList()
  } catch {
    ElMessage.error('操作失败')
  }
}

async function handleDismiss(item: any) {
  try {
    await ElMessageBox.confirm('确定驳回此举报？', '确认', { type: 'warning' })
  } catch { return }
  try {
    await request.post(`/admin/reports/${item.id}/reject`)
    ElMessage.success('举报已驳回')
    fetchList()
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(fetchList)
</script>

<style scoped>
.report-page { max-width: 860px; }
.page-header { margin-bottom: 24px; }
.page-title { margin: 0 0 6px; font-family: "Noto Serif SC","Songti SC",SimSun,serif; font-size: 24px; font-weight: 700; color: #e8c16d; }
.page-desc { margin: 0; color: #8a7b68; font-size: 14px; }

/* ===== 筛选 ===== */
.filter-tabs { display: flex; gap: 8px; margin-bottom: 22px; }
.tab-btn {
  padding: 8px 22px; border: 1px solid rgb(214 176 95 / 18%); border-radius: 20px;
  background: transparent; color: #b9ab90; font-size: 14px; cursor: pointer;
  transition: all 200ms; display: flex; align-items: center; gap: 6px;
}
.tab-btn:hover { border-color: rgb(214 176 95 / 40%); color: #d8c69b; }
.tab-btn.active { background: linear-gradient(135deg,#d6b05f,#c89a4a); color: #1a1a1a; border-color:#d6b05f; font-weight: 600; }
.tab-badge { padding: 1px 8px; border-radius: 10px; background: rgb(239 68 68 / 90%); color: #fff; font-size: 11px; font-weight: 700; }
.tab-btn.active .tab-badge { background: rgb(0 0 0 / 30%); }

/* ===== List ===== */
.report-list { display: flex; flex-direction: column; gap: 12px; }
.report-card {
  padding: 20px; border-radius: 10px;
  border: 1px solid rgb(214 176 95 / 12%);
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
}
.report-card.card-pending { border-left: 3px solid rgb(214 176 95 / 50%); }
.report-card.card-resolved { border-left: 3px solid rgb(52 211 153 / 50%); }
.report-card.card-rejected { border-left: 3px solid rgb(239 68 68 / 50%); }

.card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.target-type {
  padding: 2px 10px; border-radius: 8px; background: rgb(214 176 95 / 14%); color: #d6b05f;
  font-size: 12px; font-weight: 700;
}
.target-content { flex: 1; font-size: 14px; color: #c6b78f; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.status-tag { padding: 2px 10px; border-radius: 10px; font-size: 12px; font-weight: 600; }
.tag-pending { background: rgb(214 176 95 / 16%); color: #d6b05f; }
.tag-resolved { background: rgb(52 211 153 / 16%); color: #34d399; }
.tag-rejected { background: rgb(107 114 128 / 16%); color: #9ca3af; }

.card-body { display: flex; flex-direction: column; gap: 6px; margin-bottom: 4px; }
.info-row { display: flex; gap: 12px; font-size: 13px; }
.info-label { color: #5a5040; min-width: 60px; }
.info-value { color: #8a7b68; }

.card-actions { display: flex; gap: 8px; margin-top: 12px; padding-top: 12px; border-top: 1px solid rgb(214 176 95 / 8%); }
.action-btn { font-weight: 500; font-size: 13px; }
.resolve-btn { background: rgb(52 211 153 / 12%)!important; border: 1px solid rgb(52 211 153 / 25%)!important; color: #34d399!important; }
.resolve-btn:hover { background: rgb(52 211 153 / 20%)!important; }
.dismiss-btn { background: rgb(239 68 68 / 10%)!important; border: 1px solid rgb(239 68 68 / 20%)!important; color: #f87171!important; }
.dismiss-btn:hover { background: rgb(239 68 68 / 18%)!important; }

/* Pagination / Loading / Empty */
.pagination { display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 28px; }
.page-btn {
  padding: 8px 20px; border: 1px solid rgb(214 176 95 / 18%); border-radius: 8px;
  background: rgb(255 255 255 / 4%); color: #b9ab90; font-size: 13px; cursor: pointer; transition: all 200ms;
}
.page-btn:hover:not(:disabled) { border-color: #d6b05f; color: #d6b05f; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: #8a7b68; font-size: 14px; }
.loading-state, .empty-state { display: flex; flex-direction: column; align-items: center; padding: 80px 0; color: #6a5e4e; gap: 12px; }
.loading-icon { animation: spin 1s linear infinite; color: #d6b05f; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>