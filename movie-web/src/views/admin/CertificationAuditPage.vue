<template>
  <div class="cert-audit-page">
    <div class="page-header">
      <h1 class="page-title">认证审核</h1>
      <p class="page-desc">审核电影商提交的认证申请</p>
    </div>

    <!-- 筛选 tab -->
    <div class="filter-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        :class="['tab-btn', { active: activeTab === tab.value }]"
        @click="switchTab(tab.value)"
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
      <p>加载审核列表...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="list.length === 0" class="empty-state">
      <p>{{ activeTab === 'PENDING' ? '暂无待审核的认证申请' : '暂无该状态的认证记录' }}</p>
    </div>

    <!-- 审核列表 -->
    <div v-else class="audit-list">
      <div
        v-for="item in list"
        :key="item.id"
        :class="['audit-card', statusClass(item.certificationStatus)]"
      >
        <div class="card-header">
          <div class="card-title">
            <span class="company-name">{{ item.companyName }}</span>
            <span :class="['status-tag', statusTagClass(item.certificationStatus)]">
              {{ statusLabel(item.certificationStatus) }}
            </span>
          </div>
          <span class="card-time">{{ formatDate(item.createdAt) }}</span>
        </div>

        <div class="card-body">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">联系人</span>
              <span class="info-value">{{ item.contactName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">联系电话</span>
              <span class="info-value">{{ item.contactPhone }}</span>
            </div>
            <div class="info-item full-width">
              <span class="info-label">证明材料</span>
              <a v-if="item.licenseUrl" :href="item.licenseUrl" target="_blank" class="license-link">
                查看材料 →
              </a>
              <span v-else class="no-license">未提供</span>
            </div>
          </div>

          <!-- 驳回原因 -->
          <div v-if="item.certificationStatus === 'REJECTED' && item.rejectReason" class="reject-info">
            <span class="reject-label">驳回原因：</span>
            <span class="reject-text">{{ item.rejectReason }}</span>
          </div>
        </div>

        <!-- 操作按钮（仅 PENDING 状态） -->
        <div v-if="item.certificationStatus === 'PENDING'" class="card-actions">
          <el-button
            type="success"
            :loading="approveLoading === item.id"
            class="approve-btn"
            @click="handleApprove(item)"
          >
            <el-icon><Check /></el-icon>
            审核通过
          </el-button>
          <el-button
            type="danger"
            :loading="rejectLoading === item.id"
            class="reject-btn"
            @click="openRejectDialog(item)"
          >
            <el-icon><Close /></el-icon>
            驳回
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

    <!-- 驳回弹窗 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="驳回认证申请"
      width="460px"
      :close-on-click-modal="false"
      class="reject-dialog"
    >
      <el-form :model="rejectForm" label-position="top">
        <el-form-item label="驳回原因" required>
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细填写驳回原因，以便申请人修改后重新提交"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button
          type="danger"
          :loading="rejectLoading !== null"
          :disabled="!rejectForm.reason.trim()"
          @click="handleReject"
        >
          确认驳回
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Check, Close } from '@element-plus/icons-vue'

interface CertificationItem {
  id: number
  userId: number
  companyName: string
  contactName: string
  contactPhone: string
  licenseUrl: string
  certificationStatus: string
  rejectReason: string | null
  certifiedAt: string | null
  createdAt: string
  updatedAt: string
}

interface PageData {
  list: CertificationItem[]
  page: number
  pageSize: number
  total: number
}

const apiBase = import.meta.env.VITE_API_BASE_URL || '/api'

const tabs = [
  { value: 'PENDING', label: '待审核' },
  { value: 'APPROVED', label: '已通过' },
  { value: 'REJECTED', label: '已驳回' },
]

const activeTab = ref('PENDING')
const list = ref<CertificationItem[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const pendingCount = ref(0)
const approveLoading = ref<number | null>(null)
const rejectLoading = ref<number | null>(null)

// 驳回弹窗
const rejectDialogVisible = ref(false)
const rejectTarget = ref<CertificationItem | null>(null)
const rejectForm = ref({ reason: '' })

const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)

function statusClass(status: string) {
  return `card-${status.toLowerCase()}`
}

function statusTagClass(status: string) {
  const map: Record<string, string> = {
    PENDING: 'tag-pending',
    APPROVED: 'tag-approved',
    REJECTED: 'tag-rejected',
  }
  return map[status] || ''
}

function statusLabel(status: string) {
  const map: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已驳回',
  }
  return map[status] || status
}

function formatDate(dateStr: string | null): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

async function fetchList() {
  loading.value = true
  try {
    const token = localStorage.getItem('token') || ''
    const params = new URLSearchParams({
      page: String(page.value),
      pageSize: String(pageSize),
      status: activeTab.value,
    })
    const resp = await fetch(`${apiBase}/admin/certifications?${params}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const json = await resp.json()
    if (json.code === 0) {
      const data = json.data as PageData
      list.value = data.list || []
      total.value = data.total || 0
    } else {
      ElMessage.error(json.message || '获取数据失败')
    }
  } catch {
    ElMessage.error('网络异常，请检查后端是否启动')
  } finally {
    loading.value = false
  }
}

async function fetchPendingCount() {
  try {
    const token = localStorage.getItem('token') || ''
    const params = new URLSearchParams({ page: '1', pageSize: '1', status: 'PENDING' })
    const resp = await fetch(`${apiBase}/admin/certifications?${params}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const json = await resp.json()
    if (json.code === 0 && json.data) {
      pendingCount.value = json.data.total || 0
    }
  } catch {
    // ignore
  }
}

function switchTab(tab: string) {
  activeTab.value = tab
  page.value = 1
  fetchList()
}

async function handleApprove(item: CertificationItem) {
  try {
    await ElMessageBox.confirm(
      `确定通过「${item.companyName}」的认证申请吗？`,
      '确认审核',
      { confirmButtonText: '确定通过', cancelButtonText: '取消', type: 'success' },
    )
  } catch {
    return // 用户取消
  }

  approveLoading.value = item.id
  try {
    const token = localStorage.getItem('token') || ''
    const resp = await fetch(`${apiBase}/admin/certifications/${item.id}/approve`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
    })
    const json = await resp.json()
    if (json.code === 0) {
      ElMessage.success('审核已通过')
      fetchList()
      fetchPendingCount()
    } else {
      ElMessage.error(json.message || '操作失败')
    }
  } catch {
    ElMessage.error('网络异常')
  } finally {
    approveLoading.value = null
  }
}

function openRejectDialog(item: CertificationItem) {
  rejectTarget.value = item
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

async function handleReject() {
  if (!rejectTarget.value || !rejectForm.value.reason.trim()) return

  rejectLoading.value = rejectTarget.value.id
  try {
    const token = localStorage.getItem('token') || ''
    const resp = await fetch(`${apiBase}/admin/certifications/${rejectTarget.value.id}/reject`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ rejectReason: rejectForm.value.reason.trim() }),
    })
    const json = await resp.json()
    if (json.code === 0) {
      ElMessage.success('已驳回')
      rejectDialogVisible.value = false
      fetchList()
      fetchPendingCount()
    } else {
      ElMessage.error(json.message || '操作失败')
    }
  } catch {
    ElMessage.error('网络异常')
  } finally {
    rejectLoading.value = null
  }
}

onMounted(() => {
  fetchList()
  fetchPendingCount()
})
</script>

<style scoped>
.cert-audit-page {
  max-width: 860px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 6px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 24px;
  font-weight: 700;
  color: #e8c16d;
}

.page-desc {
  margin: 0;
  color: #8a7b68;
  font-size: 14px;
}

/* ===== 筛选 Tab ===== */
.filter-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.tab-btn {
  padding: 8px 22px;
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 20px;
  background: transparent;
  color: #b9ab90;
  font-size: 14px;
  cursor: pointer;
  transition: all 200ms ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-btn:hover {
  border-color: rgb(214 176 95 / 40%);
  color: #d8c69b;
}

.tab-btn.active {
  background: linear-gradient(135deg, #d6b05f, #c89a4a);
  color: #1a1a1a;
  border-color: #d6b05f;
  font-weight: 600;
}

.tab-badge {
  padding: 1px 8px;
  border-radius: 10px;
  background: rgb(239 68 68 / 90%);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
}

.tab-btn.active .tab-badge {
  background: rgb(0 0 0 / 30%);
}

/* ===== 加载/空状态 ===== */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 0;
  color: #6a5e4e;
  gap: 12px;
}

.loading-icon {
  animation: spin 1s linear infinite;
  color: #d6b05f;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== 审核卡片 ===== */
.audit-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.audit-card {
  padding: 24px;
  border-radius: 10px;
  border: 1px solid rgb(214 176 95 / 12%);
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
}

.audit-card.card-pending {
  border-left: 3px solid rgb(214 176 95 / 50%);
}

.audit-card.card-approved {
  border-left: 3px solid rgb(52 211 153 / 50%);
}

.audit-card.card-rejected {
  border-left: 3px solid rgb(239 68 68 / 50%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.company-name {
  font-size: 16px;
  font-weight: 600;
  color: #f7edd5;
}

.card-time {
  font-size: 12px;
  color: #6a5e4e;
}

/* 状态标签 */
.status-tag {
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.tag-pending {
  background: rgb(214 176 95 / 16%);
  color: #d6b05f;
}

.tag-approved {
  background: rgb(52 211 153 / 16%);
  color: #34d399;
}

.tag-rejected {
  background: rgb(239 68 68 / 16%);
  color: #f87171;
}

/* ===== 资料展示 ===== */
.card-body {
  margin-bottom: 4px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 12px;
  color: #5a5040;
}

.info-value {
  font-size: 14px;
  color: #b9ab90;
}

.license-link {
  color: #d6b05f;
  text-decoration: underline;
  font-size: 14px;
}

.license-link:hover {
  color: #e8c16d;
}

.no-license {
  color: #5a5040;
  font-style: italic;
}

.reject-info {
  margin-top: 12px;
  padding: 10px 14px;
  background: rgb(239 68 68 / 6%);
  border: 1px solid rgb(239 68 68 / 14%);
  border-radius: 6px;
}

.reject-label {
  color: #f87171;
  font-size: 12px;
  font-weight: 600;
}

.reject-text {
  color: #b9ab90;
  font-size: 13px;
  margin-left: 4px;
}

/* ===== 操作按钮 ===== */
.card-actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid rgb(214 176 95 / 10%);
}

.approve-btn {
  background: rgb(52 211 153 / 14%) !important;
  border: 1px solid rgb(52 211 153 / 30%) !important;
  color: #34d399 !important;
}

.approve-btn:hover {
  background: rgb(52 211 153 / 22%) !important;
}

.reject-btn {
  background: rgb(239 68 68 / 10%) !important;
  border: 1px solid rgb(239 68 68 / 24%) !important;
  color: #f87171 !important;
}

.reject-btn:hover {
  background: rgb(239 68 68 / 18%) !important;
}

/* ===== 分页 ===== */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 28px;
}

.page-btn {
  padding: 8px 20px;
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 8px;
  background: rgb(255 255 255 / 4%);
  color: #b9ab90;
  font-size: 13px;
  cursor: pointer;
  transition: all 200ms;
}

.page-btn:hover:not(:disabled) {
  border-color: #d6b05f;
  color: #d6b05f;
}

.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-info {
  color: #8a7b68;
  font-size: 14px;
}
</style>