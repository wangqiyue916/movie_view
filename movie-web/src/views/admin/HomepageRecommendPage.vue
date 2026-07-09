<template>
  <div class="recommend-page">
    <div class="page-header">
      <h1 class="page-title">首页推荐管理</h1>
      <p class="page-desc">管理首页各区块的推荐内容展示</p>
    </div>

    <!-- 区块选择 -->
    <div class="section-tabs">
      <button
        v-for="s in sections"
        :key="s.value"
        :class="['section-tab', { active: currentSection === s.value }]"
        @click="switchSection(s.value)"
      >
        {{ s.label }}
      </button>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon" :size="28"><Loading /></el-icon>
      <p>加载推荐列表...</p>
    </div>

    <!-- 操作栏 -->
    <div v-else class="toolbar">
      <el-button type="primary" class="add-btn" @click="openAddDialog">
        <el-icon><Plus /></el-icon>
        新增推荐
      </el-button>
      <span class="hint">共 {{ list.length }} 条记录</span>
    </div>

    <!-- 推荐列表 -->
    <div class="recommend-list">
      <div
        v-for="(item, idx) in list"
        :key="item.id"
        :class="['recommend-card', { disabled: !item.enabled }]"
      >
        <div class="card-left">
          <div class="sort-arrows">
            <button :disabled="idx === 0" class="arrow-btn" title="上移" @click="moveItem(idx, -1)">▲</button>
            <span class="sort-num">{{ item.sortOrder }}</span>
            <button :disabled="idx === list.length - 1" class="arrow-btn" title="下移" @click="moveItem(idx, 1)">▼</button>
          </div>
          <div class="card-preview" v-if="item.imageUrl">
            <img :src="item.imageUrl" :alt="item.title" />
          </div>
          <div class="card-info">
            <div class="info-row">
              <span class="info-label">标题</span>
              <span class="info-value">{{ item.title || '(无标题)' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">类型</span>
              <span class="type-tag">{{ typeLabel(item.targetType) }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">目标 ID</span>
              <span class="info-value">{{ item.targetId }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">状态</span>
              <span :class="['status-dot', item.enabled ? 'online' : 'offline']"></span>
              <span class="info-value">{{ item.enabled ? '已启用' : '已禁用' }}</span>
            </div>
          </div>
        </div>
        <div class="card-actions">
          <el-button size="small" class="edit-btn" @click="openEditDialog(item)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button size="small" :class="item.enabled ? 'disable-btn' : 'enable-btn'" @click="toggleEnabled(item)">
            {{ item.enabled ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" class="delete-btn" @click="handleDelete(item)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>

      <div v-if="list.length === 0" class="empty-state">
        <p>暂无推荐内容，点击"新增推荐"添加</p>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingItem ? '编辑推荐' : '新增推荐'"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-position="top" class="dialog-form">
        <el-form-item label="所属区块" required>
          <el-select v-model="form.sectionCode" :disabled="!!editingItem" class="full-width">
            <el-option v-for="s in sections" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容类型" required>
          <el-select v-model="form.targetType" class="full-width">
            <el-option v-for="t in targetTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标 ID" required>
          <el-input-number v-model="form.targetId" :min="1" class="full-width" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="推荐标题（选填）" maxlength="150" />
        </el-form-item>
        <el-form-item label="图片链接">
          <el-input v-model="form.imageUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          {{ editingItem ? '保存修改' : '确认新增' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Plus, Edit, Delete } from '@element-plus/icons-vue'
import request from '@/api/request'

interface RecommendItem {
  id: number
  sectionCode: string
  targetType: string
  targetId: number
  title: string
  imageUrl: string
  sortOrder: number
  enabled: number
}

const list = ref<RecommendItem[]>([])
const loading = ref(false)
const currentSection = ref('BANNER_NEWS')
const dialogVisible = ref(false)
const editingItem = ref<RecommendItem | null>(null)
const saving = ref(false)

const sections = [
  { value: 'BANNER_NEWS', label: '顶部轮播' },
  { value: 'HOT_MOVIE', label: '热门电影' },
  { value: 'FEATURED_REVIEW', label: '精选长评' },
  { value: 'RECOMMEND_MERCH', label: '推荐周边' },
]

const targetTypes = [
  { value: 'NEWS', label: '资讯' },
  { value: 'MOVIE', label: '电影' },
  { value: 'LONG_REVIEW', label: '长评' },
  { value: 'MERCHANDISE', label: '周边' },
]

const form = ref({
  sectionCode: 'BANNER_NEWS',
  targetType: 'MOVIE',
  targetId: 1,
  title: '',
  imageUrl: '',
  sortOrder: 0,
  enabled: 1,
})

function typeLabel(t: string) {
  const m: Record<string, string> = { NEWS: '资讯', MOVIE: '电影', LONG_REVIEW: '长评', MERCHANDISE: '周边' }
  return m[t] || t
}

async function fetchList() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/homepage-recommendations', {
      params: { sectionCode: currentSection.value },
    })
    list.value = (res || []) as RecommendItem[]
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

function switchSection(code: string) {
  currentSection.value = code
  fetchList()
}

function openAddDialog() {
  editingItem.value = null
  form.value = { sectionCode: currentSection.value, targetType: 'MOVIE', targetId: 1, title: '', imageUrl: '', sortOrder: list.value.length, enabled: 1 }
  dialogVisible.value = true
}

function openEditDialog(item: RecommendItem) {
  editingItem.value = item
  form.value = { ...item }
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editingItem.value) {
      await request.put(`/admin/homepage-recommendations/${editingItem.value.id}`, form.value)
    } else {
      await request.post('/admin/homepage-recommendations', form.value)
    }
    ElMessage.success(editingItem.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    fetchList()
  } catch {
    // error handled by interceptor
  } finally {
    saving.value = false
  }
}

async function handleDelete(item: RecommendItem) {
  try {
    await ElMessageBox.confirm(`确定删除推荐「${item.title || '无标题'}」？`, '确认删除', { type: 'warning' })
  } catch { return }
  try {
    await request.delete(`/admin/homepage-recommendations/${item.id}`)
    ElMessage.success('已删除')
    fetchList()
  } catch {
    // error handled by interceptor
  }
}

async function toggleEnabled(item: RecommendItem) {
  const newEnabled = item.enabled ? 0 : 1
  try {
    await request.put(`/admin/homepage-recommendations/${item.id}`, { enabled: newEnabled })
    ElMessage.success(newEnabled ? '已启用' : '已禁用')
    fetchList()
  } catch {
    // error handled by interceptor
  }
}

async function moveItem(idx: number, direction: number) {
  const a = list.value[idx]
  const b = list.value[idx + direction]
  if (!a || !b) return
  try {
    await Promise.all([
      request.put(`/admin/homepage-recommendations/${a.id}`, { sortOrder: b.sortOrder }),
      request.put(`/admin/homepage-recommendations/${b.id}`, { sortOrder: a.sortOrder }),
    ])
    ElMessage.success('排序已更新')
    fetchList()
  } catch {
    // error handled by interceptor
  }
}

onMounted(fetchList)
</script>

<style scoped>
.recommend-page { max-width: 900px; }
.page-header { margin-bottom: 24px; }
.page-title { margin: 0 0 6px; font-family: "Noto Serif SC","Songti SC",SimSun,serif; font-size: 24px; font-weight: 700; color: #e8c16d; }
.page-desc { margin: 0; color: #8a7b68; font-size: 14px; }

/* Section Tabs */
.section-tabs { display: flex; gap: 8px; margin-bottom: 22px; flex-wrap: wrap; }
.section-tab {
  padding: 8px 18px; border: 1px solid rgb(214 176 95 / 18%); border-radius: 20px;
  background: transparent; color: #b9ab90; font-size: 14px; cursor: pointer; transition: all 200ms;
}
.section-tab:hover { border-color: rgb(214 176 95 / 40%); color: #d8c69b; }
.section-tab.active { background: linear-gradient(135deg,#d6b05f,#c89a4a); color: #1a1a1a; border-color:#d6b05f; font-weight: 600; }

/* Toolbar */
.toolbar { display: flex; align-items: center; gap: 16px; margin-bottom: 16px; }
.add-btn { background: linear-gradient(135deg,#d6b05f,#c89a4a)!important; border: none!important; color: #1a1a1a!important; font-weight: 600; }
.hint { color: #6a5e4e; font-size: 13px; }

/* List */
.recommend-list { display: flex; flex-direction: column; gap: 10px; }
.recommend-card {
  display: flex; align-items: center; justify-content: space-between; padding: 14px 18px;
  border: 1px solid rgb(214 176 95 / 12%); border-radius: 10px;
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
}
.recommend-card.disabled { opacity: 0.45; }
.card-left { display: flex; align-items: center; gap: 16px; flex: 1; min-width: 0; }
.sort-arrows { display: flex; flex-direction: column; align-items: center; gap: 0; }
.arrow-btn {
  background: transparent; border: none; color: #6a5e4e; cursor: pointer; font-size: 12px; padding: 2px 4px;
  line-height: 1; transition: color 150ms;
}
.arrow-btn:hover:not(:disabled) { color: #d6b05f; }
.arrow-btn:disabled { opacity: 0.2; cursor: not-allowed; }
.sort-num { font-size: 12px; color: #6a5e4e; }
.card-preview { width: 60px; height: 40px; border-radius: 6px; overflow: hidden; flex-shrink: 0; background: #111; }
.card-preview img { width: 100%; height: 100%; object-fit: cover; }
.card-info { display: flex; flex-direction: column; gap: 4px; min-width: 0; }
.info-row { display: flex; align-items: center; gap: 8px; font-size: 13px; }
.info-label { color: #5a5040; min-width: 50px; }
.info-value { color: #b9ab90; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.type-tag { padding: 1px 8px; border-radius: 8px; background: rgb(214 176 95 / 14%); color: #d6b05f; font-size: 12px; font-weight: 600; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; }
.status-dot.online { background: #34d399; box-shadow: 0 0 6px rgb(52 211 153 / 40%); }
.status-dot.offline { background: #6a5e4e; }

.card-actions { display: flex; gap: 6px; flex-shrink: 0; }
.edit-btn { background: rgb(214 176 95 / 10%)!important; border: 1px solid rgb(214 176 95 / 20%)!important; color: #d6b05f!important; }
.enable-btn { background: rgb(52 211 153 / 10%)!important; border: 1px solid rgb(52 211 153 / 20%)!important; color: #34d399!important; }
.disable-btn { background: rgb(107 114 128 / 10%)!important; border: 1px solid rgb(107 114 128 / 20%)!important; color: #9ca3af!important; }
.delete-btn { background: rgb(239 68 68 / 10%)!important; border: 1px solid rgb(239 68 68 / 20%)!important; color: #f87171!important; }

/* Dialog */
.dialog-form :deep(.el-form-item__label) { color: #b9ab90; }
.dialog-form :deep(.el-input__wrapper), .dialog-form :deep(.el-input-number__wrapper) {
  background: rgb(255 255 255 / 6%); border: 1px solid rgb(214 176 95 / 14%); border-radius: 8px; color: #f7edd5;
}
.full-width { width: 100%; }

/* States */
.loading-state, .empty-state { display: flex; flex-direction: column; align-items: center; padding: 80px 0; color: #6a5e4e; gap: 12px; }
.loading-icon { animation: spin 1s linear infinite; color: #d6b05f; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>