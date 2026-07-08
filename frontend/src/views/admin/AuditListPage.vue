<template>
  <div class="audit-page">
    <div class="page-header">
      <h1 class="page-title">内容审核</h1>
      <p class="page-desc">管理所有待审核内容</p>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <div class="filter-row">
        <div class="filter-group">
          <label class="filter-label">内容类型</label>
          <div class="type-tabs">
            <button
              v-for="t in typeOptions"
              :key="t.value"
              :class="['type-tab', { active: targetType === t.value }]"
              @click="switchType(t.value)"
            >
              {{ t.label }}
            </button>
          </div>
        </div>
        <div class="filter-group">
          <label class="filter-label">审核状态</label>
          <div class="type-tabs">
            <button
              v-for="s in statusOptions"
              :key="s.value"
              :class="['type-tab', { active: auditStatus === s.value }]"
              @click="auditStatus = s.value; page = 1; fetchList()"
            >
              {{ s.label }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon" :size="28"><Loading /></el-icon>
      <p>加载审核列表...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="list.length === 0" class="empty-state">
      <p>暂无待审核内容</p>
    </div>

    <!-- 审核列表 -->
    <div v-else class="audit-list">
      <div
        v-for="item in list"
        :key="`${item.targetType}-${item.id}`"
        class="audit-card"
      >
        <div class="card-header">
          <div class="card-title">
            <span class="type-badge" :class="`badge-${item.targetType?.toLowerCase()}`">
              {{ typeLabel(item.targetType) }}
            </span>
            <span class="item-title">{{ item.title }}</span>
          </div>
          <span class="card-time">{{ formatDate(item.createdAt) }}</span>
        </div>

        <div class="card-body">
          <span class="status-tag" :class="`tag-${item.status?.toLowerCase()}`">
            {{ statusLabel(item.status) }}
          </span>
          <span class="item-id">ID: {{ item.id }}</span>
        </div>

        <!-- 操作（根据状态显示不同按钮） -->
        <div class="card-actions">
          <!-- PENDING：可审核通过或驳回 -->
          <template v-if="item.status === 'PENDING'">
            <el-button
              type="success"
              size="small"
              :loading="actionLoading === `${item.targetType}-${item.id}-approve`"
              class="action-btn approve-btn"
              @click="handleApprove(item)"
            >
              <el-icon><Check /></el-icon>
              通过
            </el-button>
            <el-button
              type="danger"
              size="small"
              :loading="actionLoading === `${item.targetType}-${item.id}-reject`"
              class="action-btn reject-btn"
              @click="openRejectDialog(item)"
            >
              <el-icon><Close /></el-icon>
              驳回
            </el-button>
          </template>

          <!-- APPROVED：可上线 -->
          <template v-else-if="item.status === 'APPROVED'">
            <el-button
              type="primary"
              size="small"
              :loading="actionLoading === `${item.targetType}-${item.id}-publish`"
              class="action-btn publish-btn"
              @click="handlePublish(item)"
            >
              <el-icon><Upload /></el-icon>
              上线
            </el-button>
          </template>

          <!-- ONLINE：可下架 -->
          <template v-else-if="item.status === 'ONLINE'">
            <el-button
              type="warning"
              size="small"
              :loading="actionLoading === `${item.targetType}-${item.id}-offline`"
              class="action-btn offline-btn"
              @click="handleOffline(item)"
            >
              <el-icon><Download /></el-icon>
              下架
            </el-button>
          </template>
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
    <el-dialog v-model="rejectDialogVisible" title="驳回内容" width="460px" :close-on-click-modal="false">
      <el-form label-position="top">
        <el-form-item label="驳回原因" required>
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请填写驳回原因"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button
          type="danger"
          :loading="!!actionLoading"
          :disabled="!rejectForm.reason.trim()"
          @click="handleReject"
        >确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, Check, Close, Upload, Download } from '@element-plus/icons-vue'

const apiBase = import.meta.env.VITE_API_BASE_URL || '/api'

const typeOptions = [
  { value: 'LONG_REVIEW', label: '长评' },
  { value: 'NEWS', label: '资讯' },
]
const statusOptions = [
  { value: 'PENDING', label: '待审核' },
  { value: 'APPROVED', label: '已通过' },
  { value: 'ONLINE', label: '已上线' },
  { value: 'REJECTED', label: '已驳回' },
  { value: 'OFFLINE', label: '已下架' },
]

const targetType = ref('LONG_REVIEW')
const auditStatus = ref('PENDING')
const list = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const actionLoading = ref<string | null>(null)

const rejectDialogVisible = ref(false)
const rejectTarget = ref<any>(null)
const rejectForm = ref({ reason: '' })

const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)

function typeLabel(type: string) {
  return type === 'LONG_REVIEW' ? '长评' : type === 'NEWS' ? '资讯' : type
}

function statusLabel(s: string) {
  const m: Record<string, string> = {
    DRAFT: '草稿', PENDING: '待审核', APPROVED: '已通过',
    ONLINE: '已上线', REJECTED: '已驳回', OFFLINE: '已下架', DELETED: '已删除',
  }
  return m[s] || s
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN', { year:'numeric', month:'2-digit', day:'2-digit', hour:'2-digit', minute:'2-digit' })
}

async function fetchList() {
  loading.value = true
  try {
    const token = localStorage.getItem('token') || ''
    const p = new URLSearchParams({ targetType: targetType.value, status: auditStatus.value, page: String(page.value), pageSize: String(pageSize) })
    const r = await fetch(`${apiBase}/admin/audits?${p}`, { headers: { Authorization: `Bearer ${token}` } })
    const j = await r.json()
    if (j.code === 0) { list.value = j.data.list || []; total.value = j.data.total || 0 }
    else ElMessage.error(j.message || '获取失败')
  } catch { ElMessage.error('网络异常') }
  finally { loading.value = false }
}

function switchType(type: string) { targetType.value = type; auditStatus.value = 'PENDING'; page.value = 1; fetchList() }

async function doAction(item: any, action: string, body?: Record<string, any>) {
  const key = `${item.targetType}-${item.id}-${action}`
  actionLoading.value = key
  try {
    const token = localStorage.getItem('token') || ''
    const r = await fetch(`${apiBase}/admin/audits/${item.targetType}/${item.id}/${action}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
      body: body ? JSON.stringify(body) : undefined,
    })
    const j = await r.json()
    if (j.code === 0) ElMessage.success(j.message || '操作成功')
    else ElMessage.error(j.message || '操作失败')
    fetchList()
  } catch { ElMessage.error('网络异常') }
  finally { actionLoading.value = null }
}

function handleApprove(item: any) { doAction(item, 'approve') }
function handlePublish(item: any) { doAction(item, 'publish') }
function handleOffline(item: any) { doAction(item, 'offline') }
function openRejectDialog(item: any) { rejectTarget.value = item; rejectForm.value.reason = ''; rejectDialogVisible.value = true }
async function handleReject() {
  if (!rejectTarget.value || !rejectForm.value.reason.trim()) return
  await doAction(rejectTarget.value, 'reject', { rejectReason: rejectForm.value.reason.trim() })
  rejectDialogVisible.value = false
}

onMounted(fetchList)
</script>

<style scoped>
.audit-page { max-width: 900px; }
.page-header { margin-bottom: 24px; }
.page-title { margin: 0 0 6px; font-family: "Noto Serif SC","Songti SC",SimSun,serif; font-size: 24px; font-weight: 700; color: #e8c16d; }
.page-desc { margin: 0; color: #8a7b68; font-size: 14px; }

/* Type/Status tabs */
.filter-bar { margin-bottom: 20px; }
.filter-row { display: flex; flex-wrap: wrap; gap: 20px; }
.filter-group { display: flex; flex-direction: column; gap: 8px; }
.filter-label { font-size: 12px; color: #6a5e4e; font-weight: 600; text-transform: uppercase; letter-spacing: 1px; }
.type-tabs { display: flex; gap: 6px; flex-wrap: wrap; }
.type-tab {
  padding: 6px 16px; border: 1px solid rgb(214 176 95 / 18%); border-radius: 16px;
  background: transparent; color: #b9ab90; font-size: 13px; cursor: pointer; transition: all 200ms;
}
.type-tab:hover { border-color: rgb(214 176 95 / 40%); color: #d8c69b; }
.type-tab.active { background: linear-gradient(135deg,#d6b05f,#c89a4a); color: #1a1a1a; border-color:#d6b05f; font-weight: 600; }

/* List */
.audit-list { display: flex; flex-direction: column; gap: 12px; }
.audit-card {
  padding: 20px; border-radius: 10px;
  border: 1px solid rgb(214 176 95 / 12%);
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
}
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.card-title { display: flex; align-items: center; gap: 10px; }
.type-badge { padding: 2px 10px; border-radius: 8px; font-size: 11px; font-weight: 700; }
.badge-long_review { background: rgb(214 176 95 / 16%); color: #d6b05f; }
.badge-news { background: rgb(96 165 250 / 16%); color: #60a5fa; }
.item-title { font-size: 15px; font-weight: 600; color: #f7edd5; }
.card-time { font-size: 12px; color: #6a5e4e; }
.card-body { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.status-tag { padding: 2px 10px; border-radius: 10px; font-size: 12px; font-weight: 600; }
.tag-pending { background: rgb(214 176 95 / 16%); color: #d6b05f; }
.tag-approved { background: rgb(52 211 153 / 16%); color: #34d399; }
.tag-online { background: rgb(34 197 94 / 16%); color: #22c55e; }
.tag-rejected { background: rgb(239 68 68 / 16%); color: #f87171; }
.tag-offline { background: rgb(107 114 128 / 16%); color: #9ca3af; }
.item-id { font-size: 12px; color: #5a5040; }

/* Actions */
.card-actions { display: flex; gap: 8px; padding-top: 12px; border-top: 1px solid rgb(214 176 95 / 8%); }
.action-btn { font-weight: 500; font-size: 13px; }
.approve-btn { background: rgb(52 211 153 / 12%)!important; border: 1px solid rgb(52 211 153 / 25%)!important; color: #34d399!important; }
.approve-btn:hover { background: rgb(52 211 153 / 20%)!important; }
.reject-btn { background: rgb(239 68 68 / 10%)!important; border: 1px solid rgb(239 68 68 / 20%)!important; color: #f87171!important; }
.reject-btn:hover { background: rgb(239 68 68 / 18%)!important; }
.publish-btn { background: rgb(214 176 95 / 12%)!important; border: 1px solid rgb(214 176 95 / 25%)!important; color: #d6b05f!important; }
.publish-btn:hover { background: rgb(214 176 95 / 20%)!important; }
.offline-btn { background: rgb(107 114 128 / 12%)!important; border: 1px solid rgb(107 114 128 / 25%)!important; color: #9ca3af!important; }
.offline-btn:hover { background: rgb(107 114 128 / 20%)!important; }

/* Pagination */
.pagination { display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 28px; }
.page-btn {
  padding: 8px 20px; border: 1px solid rgb(214 176 95 / 18%); border-radius: 8px;
  background: rgb(255 255 255 / 4%); color: #b9ab90; font-size: 13px; cursor: pointer; transition: all 200ms;
}
.page-btn:hover:not(:disabled) { border-color: #d6b05f; color: #d6b05f; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: #8a7b68; font-size: 14px; }

/* Loading / Empty */
.loading-state, .empty-state { display: flex; flex-direction: column; align-items: center; padding: 80px 0; color: #6a5e4e; gap: 12px; }
.loading-icon { animation: spin 1s linear infinite; color: #d6b05f; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>