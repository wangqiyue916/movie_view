<template>
  <div class="page user-mgmt-page">
    <div class="page-header">
      <h1>用户管理</h1>
      <span class="badge">SUPER_ADMIN</span>
    </div>

    <!-- Search -->
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索用户名..."
        class="search-input"
        clearable
        @keyup.enter="fetchData"
        @clear="fetchData"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" class="search-btn" @click="fetchData">搜索</el-button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- Table -->
    <div v-else class="table-wrapper">
      <el-table :data="tableData" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="用户名" min-width="120">
          <template #default="{ row }">
            <span class="cell-title">{{ row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column label="昵称" min-width="100">
          <template #default="{ row }">{{ row.nickname || '-' }}</template>
        </el-table-column>
        <el-table-column label="邮箱" min-width="140">
          <template #default="{ row }">{{ row.email || '-' }}</template>
        </el-table-column>
        <el-table-column label="角色" min-width="160">
          <template #default="{ row }">
            <el-tag
              v-for="role in row.roles"
              :key="role"
              size="small"
              :type="roleTagType(role)"
              class="role-tag"
            >
              {{ roleLabel(role) }}
            </el-tag>
            <span v-if="!row.roles?.length" class="muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 'NORMAL' ? 'success' : 'danger'">
              {{ row.status === 'NORMAL' ? '正常' : '已封禁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后登录" width="160">
          <template #default="{ row }">{{ fmt(row.lastLoginAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button size="small" class="edit-btn" @click="openRoleDialog(row)">
                <el-icon><Setting /></el-icon>
                角色
              </el-button>
              <el-button
                v-if="row.status === 'NORMAL'"
                size="small"
                type="danger"
                @click="handleBan(row)"
              >
                封禁
              </el-button>
              <el-button
                v-if="row.status === 'BANNED'"
                size="small"
                type="success"
                @click="handleUnban(row)"
              >
                解封
              </el-button>
              <el-button size="small" type="warning" @click="openResetPwdDialog(row)">
                重置密码
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Pagination -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <!-- Role Edit Dialog -->
    <el-dialog v-model="roleDialogVisible" title="设置角色" width="460px">
      <div class="dialog-body">
        <p class="dialog-hint">用户：<strong>{{ currentUser?.username }}</strong></p>
        <el-checkbox-group v-model="selectedRoles" class="role-checkboxes">
          <el-checkbox
            v-for="r in ALL_ROLES"
            :key="r.code"
            :label="r.code"
            border
            size="large"
            class="role-checkbox-item"
          >
            {{ r.label }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleSaving" @click="confirmRoles">保存</el-button>
      </template>
    </el-dialog>

    <!-- Reset Password Dialog -->
    <el-dialog v-model="pwdDialogVisible" title="重置密码" width="400px">
      <div class="dialog-body">
        <p class="dialog-hint">将为用户 <strong>{{ currentUser?.username }}</strong> 重置密码</p>
        <el-input
          v-model="newPassword"
          placeholder="请输入新密码（默认 123456）"
          show-password
        />
      </div>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="warning" :loading="pwdSaving" @click="confirmResetPwd">
          确认重置
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Setting } from '@element-plus/icons-vue'
import request from '@/api/request'

interface UserItem {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  status: string
  roles: string[]
  lastLoginAt: string
  createdAt: string
}

const ALL_ROLES = [
  { code: 'USER', label: '普通用户' },
  { code: 'OFFICIAL', label: '观影团' },
  { code: 'ADMIN', label: '管理员' },
  { code: 'SUPER_ADMIN', label: '超级管理员' },
]

const tableData = ref<UserItem[]>([])
const loading = ref(false)
const keyword = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)

const roleDialogVisible = ref(false)
const roleSaving = ref(false)
const currentUser = ref<UserItem | null>(null)
const selectedRoles = ref<string[]>([])

const pwdDialogVisible = ref(false)
const pwdSaving = ref(false)
const newPassword = ref('')

function roleTagType(code: string) {
  const m: Record<string, string> = {
    SUPER_ADMIN: 'danger',
    ADMIN: 'warning',
    OFFICIAL: 'success',
    USER: '',
  }
  return m[code] || 'info'
}

function roleLabel(code: string) {
  const m: Record<string, string> = {
    USER: '用户',
    OFFICIAL: '观影团',
    ADMIN: '管理',
    SUPER_ADMIN: '超管',
  }
  return m[code] || code
}

function fmt(v: string | null) {
  if (!v) return '-'
  return v.substring(0, 16).replace('T', ' ')
}

async function fetchData() {
  loading.value = true
  try {
    const params: any = { page: page.value, pageSize }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    const res: any = await request.get('/admin/users', { params })
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

function openRoleDialog(row: UserItem) {
  currentUser.value = row
  selectedRoles.value = [...row.roles]
  roleDialogVisible.value = true
}

async function confirmRoles() {
  if (!currentUser.value) return
  roleSaving.value = true
  try {
    await request.put(`/admin/users/${currentUser.value.id}/roles`, { roles: selectedRoles.value })
    ElMessage.success('角色已更新')
    roleDialogVisible.value = false
    fetchData()
  } catch {
    // error handled by interceptor
  } finally {
    roleSaving.value = false
  }
}

async function handleBan(row: UserItem) {
  try {
    await ElMessageBox.confirm(`确定封禁用户「${row.username}」吗？`, '确认封禁', {
      type: 'warning',
      confirmButtonText: '确定封禁',
    })
  } catch { return }
  try {
    await request.put(`/admin/users/${row.id}/ban`)
    ElMessage.success('用户已封禁')
    fetchData()
  } catch {
    // error handled by interceptor
  }
}

async function handleUnban(row: UserItem) {
  try {
    await request.put(`/admin/users/${row.id}/unban`)
    ElMessage.success('用户已解封')
    fetchData()
  } catch {
    // error handled by interceptor
  }
}

function openResetPwdDialog(row: UserItem) {
  currentUser.value = row
  newPassword.value = '123456'
  pwdDialogVisible.value = true
}

async function confirmResetPwd() {
  if (!currentUser.value) return
  pwdSaving.value = true
  try {
    await request.post(`/admin/users/${currentUser.value.id}/reset-password`, {
      password: newPassword.value || '123456',
    })
    ElMessage.success('密码已重置')
    pwdDialogVisible.value = false
  } catch {
    // error handled by interceptor
  } finally {
    pwdSaving.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.user-mgmt-page {
  min-height: calc(100vh - 64px);
  padding: 24px max(22px, calc((100vw - 1280px) / 2)) 72px;
  color: #f7edd5;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 26px;
  font-weight: 800;
  color: #e8c16d;
}

.badge {
  padding: 2px 10px;
  border-radius: 10px;
  background: rgb(239 68 68 / 16%);
  color: #f87171;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  max-width: 420px;
}

.search-input {
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  background: rgb(255 255 255 / 4%);
  border: 1px solid rgb(214 176 95 / 14%);
}

.search-btn {
  background: linear-gradient(135deg, #d6b05f, #c89a4a) !important;
  border: none !important;
  color: #1a1a1a !important;
  font-weight: 600;
}

.loading-state {
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
  font-weight: 500;
}

.muted {
  color: #5a5040;
}

.role-tag {
  margin: 1px 3px;
}

.action-btns {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.edit-btn {
  background: rgb(214 176 95 / 10%) !important;
  border: 1px solid rgb(214 176 95 / 20%) !important;
  color: #d6b05f !important;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 28px;
}

.dialog-body {
  padding: 8px 0;
}

.dialog-hint {
  margin: 0 0 16px;
  color: #b9ab90;
  font-size: 14px;
}

.dialog-hint strong {
  color: #e8c16d;
}

.role-checkboxes {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.role-checkbox-item {
  padding: 8px 14px;
  border-radius: 8px;
}

.role-checkbox-item :deep(.el-checkbox__label) {
  color: #b9ab90;
}

/* ===== Dark Table Theme ===== */
:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(214, 176, 95, 0.08);
  --el-table-border-color: rgba(214, 176, 95, 0.12);
  --el-table-text-color: #b9ab90;
  --el-table-header-text-color: #e8c16d;
  --el-table-row-hover-bg-color: rgba(214, 176, 95, 0.06);
  background-color: transparent;
  color: #b9ab90;
}

:deep(.el-table__header-wrapper th.el-table__cell) {
  background-color: rgba(214, 176, 95, 0.08);
  color: #e8c16d;
  font-weight: 600;
  border-color: rgba(214, 176, 95, 0.15);
}

:deep(.el-table__body-wrapper td.el-table__cell) {
  background-color: transparent;
  border-color: rgba(214, 176, 95, 0.08);
  color: #b9ab90;
}

:deep(.el-table tr:hover > td.el-table__cell) {
  background-color: rgba(214, 176, 95, 0.06);
}

:deep(.el-table .el-table__inner-wrapper::before) {
  display: none;
}

:deep(.el-table--border::after),
:deep(.el-table--border::before),
:deep(.el-table__inner-wrapper::before) {
  background-color: rgba(214, 176, 95, 0.15);
}

/* Pagination dark */
:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination .el-pager li) {
  background: rgba(214, 176, 95, 0.06) !important;
  color: #b9ab90 !important;
  border: 1px solid rgba(214, 176, 95, 0.15);
}

:deep(.el-pagination .el-pager li.is-active) {
  background: rgba(214, 176, 95, 0.2) !important;
  color: #e8c16d !important;
  font-weight: 600;
}

:deep(.el-pagination .el-pager li:hover:not(.is-active)) {
  background: rgba(214, 176, 95, 0.12) !important;
  color: #d8c69b !important;
}

:deep(.el-pagination button:disabled) {
  background: transparent !important;
  color: #5a5040 !important;
  opacity: 0.4;
}
</style>