<template>
  <div class="submissions-page">
    <div class="page-header">
      <h1 class="page-title">我的投稿</h1>
      <router-link to="/official/submit-news" class="submit-link">
        <el-icon><Plus /></el-icon>
        投稿资讯
      </router-link>
    </div>

    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon" :size="28"><Loading /></el-icon>
      <p>加载投稿列表...</p>
    </div>

    <div v-else-if="list.length === 0" class="empty-state">
      <p>暂无投稿记录</p>
      <router-link to="/official/submit-news" class="start-btn">开始投稿 →</router-link>
    </div>

    <div v-else class="submission-list">
      <div v-for="item in list" :key="item.id" class="submission-card">
        <div class="card-header">
          <span class="item-title">{{ item.title }}</span>
          <span class="status-tag" :class="`tag-${(item.status || '').toLowerCase()}`">
            {{ statusLabel(item.status) }}
          </span>
        </div>
        <div class="card-body">
          <span class="item-category" v-if="item.category">{{ item.category }}</span>
          <span class="item-time">{{ formatDate(item.createdAt) }}</span>
        </div>
      </div>
    </div>

    <div v-if="total > pageSize" class="pagination">
      <button :disabled="page <= 1" class="page-btn" @click="page--; fetchList()">上一页</button>
      <span class="page-info">{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" class="page-btn" @click="page++; fetchList()">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, Plus } from '@element-plus/icons-vue'

const apiBase = import.meta.env.VITE_API_BASE_URL || '/api'
const list = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)

function statusLabel(s: string) {
  const m: Record<string, string> = {
    DRAFT: '草稿', PENDING: '待审核', APPROVED: '已通过',
    ONLINE: '已上线', REJECTED: '已驳回', OFFLINE: '已下架',
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
    const p = new URLSearchParams({ page: String(page.value), pageSize: String(pageSize) })
    const r = await fetch(`${apiBase}/official/submissions?${p}`, { headers: { Authorization: `Bearer ${token}` } })
    const j = await r.json()
    if (j.code === 0) { list.value = j.data.list || []; total.value = j.data.total || 0 }
    else ElMessage.error(j.message || '获取失败')
  } catch { ElMessage.error('网络异常') }
  finally { loading.value = false }
}

onMounted(fetchList)
</script>

<style scoped>
.submissions-page { max-width: 800px; }
.page-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 24px;
}
.page-title {
  margin: 0; font-family: "Noto Serif SC","Songti SC",SimSun,serif;
  font-size: 24px; font-weight: 700; color: #e8c16d;
}
.submit-link {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 10px 22px; border-radius: 8px;
  background: linear-gradient(135deg,#d6b05f,#c89a4a); color: #1a1a1a;
  font-size: 14px; font-weight: 600; text-decoration: none; transition: all 200ms;
}
.submit-link:hover { background: linear-gradient(135deg,#e8c16d,#d6b05f); box-shadow: 0 4px 16px rgb(214 176 95 / 20%); }

.loading-state,.empty-state { display: flex; flex-direction: column; align-items: center; padding: 80px 0; color: #6a5e4e; gap: 12px; }
.loading-icon { animation: spin 1s linear infinite; color: #d6b05f; }
@keyframes spin { to { transform: rotate(360deg); } }
.start-btn { color: #d6b05f; font-size: 14px; margin-top: 8px; }

.submission-list { display: flex; flex-direction: column; gap: 10px; }
.submission-card {
  padding: 18px 22px; border-radius: 8px;
  border: 1px solid rgb(214 176 95 / 12%);
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
}
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.item-title { font-size: 15px; font-weight: 600; color: #f7edd5; }
.card-body { display: flex; gap: 14px; }
.item-category { font-size: 12px; color: #6a5e4e; }
.item-time { font-size: 12px; color: #5a5040; }

.status-tag { padding: 2px 10px; border-radius: 10px; font-size: 12px; font-weight: 600; }
.tag-pending { background: rgb(214 176 95 / 16%); color: #d6b05f; }
.tag-approved { background: rgb(52 211 153 / 16%); color: #34d399; }
.tag-online { background: rgb(34 197 94 / 16%); color: #22c55e; }
.tag-rejected { background: rgb(239 68 68 / 16%); color: #f87171; }
.tag-offline { background: rgb(107 114 128 / 16%); color: #9ca3af; }
.tag-draft { background: rgb(107 114 128 / 16%); color: #9ca3af; }

.pagination { display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 28px; }
.page-btn {
  padding: 8px 20px; border: 1px solid rgb(214 176 95 / 18%); border-radius: 8px;
  background: rgb(255 255 255 / 4%); color: #b9ab90; font-size: 13px; cursor: pointer; transition: all 200ms;
}
.page-btn:hover:not(:disabled) { border-color: #d6b05f; color: #d6b05f; }
.page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.page-info { color: #8a7b68; font-size: 14px; }
</style>