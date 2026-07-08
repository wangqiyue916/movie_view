<template>
  <div class="page admin-merchandise-page">
    <div class="page-header">
      <h1>周边管理</h1>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else-if="tableData.length === 0" class="empty-state">
      <el-empty description="暂无周边数据" />
    </div>

    <div v-else class="table-wrapper">
      <el-table :data="tableData" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="名称" min-width="180">
          <template #default="{ row }">
            <span class="cell-title">{{ row.name || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.productType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="90">
          <template #default="{ row }">
            <span class="price-cell">¥{{ row.price ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="平台" width="90">
          <template #default="{ row }">
            {{ row.platform || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="statusTagType(row.status)">
              {{ statusMap[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="点击量" width="90">
          <template #default="{ row }">
            {{ row.clickCount ?? '-' }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <template v-if="row.status === 'PENDING'">
                <el-button size="small" type="success" @click="handleApprove(row)">通过</el-button>
                <el-button size="small" type="danger" @click="openRejectDialog(row)">驳回</el-button>
              </template>
              <el-button
                v-if="row.status === 'APPROVED'"
                size="small"
                type="primary"
                @click="handlePublish(row)"
              >上线</el-button>
              <el-button
                v-if="row.status === 'ONLINE'"
                size="small"
                type="warning"
                @click="openOfflineDialog(row)"
              >下架</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
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

    <!-- Reject Dialog -->
    <el-dialog v-model="rejectDialogVisible" title="驳回原因" width="420px">
      <el-input
        v-model="rejectReason"
        type="textarea"
        :rows="3"
        placeholder="请输入驳回原因（必填）"
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" :disabled="!rejectReason.trim()" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>

    <!-- Offline Dialog -->
    <el-dialog v-model="offlineDialogVisible" title="下架原因" width="420px">
      <el-input
        v-model="offlineReason"
        type="textarea"
        :rows="3"
        placeholder="请输入下架原因（必填）"
      />
      <template #footer>
        <el-button @click="offlineDialogVisible = false">取消</el-button>
        <el-button type="warning" :disabled="!offlineReason.trim()" @click="confirmOffline">确认下架</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { merchandiseApi, type MerchandiseItem } from '@/api/merchandiseApi'

const page = ref(1)
const pageSize = 10
const total = ref(0)
const tableData = ref<MerchandiseItem[]>([])
const loading = ref(false)

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentRejectRow = ref<MerchandiseItem | null>(null)

const offlineDialogVisible = ref(false)
const offlineReason = ref('')
const currentOfflineRow = ref<MerchandiseItem | null>(null)

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

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  return dateStr.substring(0, 10)
}

async function fetchData() {
  loading.value = true
  try {
    const res = await merchandiseApi.pageAdminProducts({ page: page.value, pageSize })
    tableData.value = res.list || []
    total.value = res.total || 0
  } catch {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handlePageChange(p: number) {
  page.value = p
  fetchData()
}

async function handleApprove(row: MerchandiseItem) {
  try {
    await merchandiseApi.approve(row.id)
    ElMessage.success('审核通过')
    fetchData()
  } catch {
    // error handled by interceptor
  }
}

function openRejectDialog(row: MerchandiseItem) {
  currentRejectRow.value = row
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

async function confirmReject() {
  if (!currentRejectRow.value || !rejectReason.value.trim()) return
  try {
    await merchandiseApi.reject(currentRejectRow.value.id, rejectReason.value.trim())
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    fetchData()
  } catch {
    // error handled by interceptor
  }
}

async function handlePublish(row: MerchandiseItem) {
  try {
    await merchandiseApi.publish(row.id)
    ElMessage.success('已上线')
    fetchData()
  } catch {
    // error handled by interceptor
  }
}

function openOfflineDialog(row: MerchandiseItem) {
  currentOfflineRow.value = row
  offlineReason.value = ''
  offlineDialogVisible.value = true
}

async function confirmOffline() {
  if (!currentOfflineRow.value || !offlineReason.value.trim()) return
  try {
    await merchandiseApi.offline(currentOfflineRow.value.id, offlineReason.value.trim())
    ElMessage.success('已下架')
    offlineDialogVisible.value = false
    fetchData()
  } catch {
    // error handled by interceptor
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.admin-merchandise-page {
  min-height: calc(100vh - 64px);
  padding: 24px max(22px, calc((100vw - 1280px) / 2)) 72px;
  color: #f7edd5;
}

.page-header h1 {
  margin: 0 0 20px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 26px;
  font-weight: 800;
  color: #e8c16d;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 80px 0;
}

.table-wrapper {
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 8px;
  overflow: hidden;
}

.cell-title {
  color: #f7edd5;
}

.price-cell {
  color: #e8c16d;
  font-weight: 600;
}

.action-btns {
  display: flex;
  gap: 6px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 28px;
}
</style>
