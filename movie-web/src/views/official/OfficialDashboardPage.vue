<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h1 class="page-title">电影商后台</h1>
      <p class="page-desc">管理你的投稿和认证</p>
    </div>

    <!-- 认证状态卡片 -->
    <div class="cert-section">
      <div v-if="certLoading" class="cert-loading">
        <el-icon class="loading-icon" :size="24"><Loading /></el-icon>
        <span>加载认证状态...</span>
      </div>

      <div v-else-if="!certStatus" class="cert-card notice">
        <div class="cert-icon">🪪</div>
        <div class="cert-body">
          <h3>未认证</h3>
          <p>你需要完成电影商认证才能使用完整功能</p>
          <router-link to="/official/certification" class="cert-action">立即认证 →</router-link>
        </div>
      </div>

      <div v-else-if="certStatus === 'PENDING'" class="cert-card pending">
        <div class="cert-icon">⏳</div>
        <div class="cert-body">
          <h3>认证审核中</h3>
          <p>你的认证申请正在审核中，请耐心等待</p>
        </div>
      </div>

      <div v-else-if="certStatus === 'APPROVED'" class="cert-card approved">
        <div class="cert-icon">✅</div>
        <div class="cert-body">
          <h3>已认证</h3>
          <p>你已通过电影商认证，可以使用全部投稿功能</p>
        </div>
      </div>

      <div v-else-if="certStatus === 'REJECTED'" class="cert-card rejected">
        <div class="cert-icon">❌</div>
        <div class="cert-body">
          <h3>认证未通过</h3>
          <p>你的认证申请未通过，请查看驳回原因并重新提交</p>
          <router-link to="/official/certification" class="cert-action">查看详情 →</router-link>
        </div>
      </div>
    </div>

    <!-- 投稿统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <span class="stat-value">{{ stats.total }}</span>
        <span class="stat-label">全部投稿</span>
      </div>
      <div class="stat-card pending">
        <span class="stat-value">{{ stats.pending }}</span>
        <span class="stat-label">待审核</span>
      </div>
      <div class="stat-card online">
        <span class="stat-value">{{ stats.online }}</span>
        <span class="stat-label">已上线</span>
      </div>
      <div class="stat-card rejected">
        <span class="stat-value">{{ stats.rejected }}</span>
        <span class="stat-label">已驳回</span>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-links">
      <h3 class="section-title">快捷操作</h3>
      <div class="links-grid">
        <router-link to="/official/submit-news" class="quick-link">
          <span class="link-icon">✏️</span>
          <span>投稿资讯</span>
          <span class="link-arrow">→</span>
        </router-link>
        <router-link to="/official/submissions" class="quick-link">
          <span class="link-icon">📋</span>
          <span>我的投稿</span>
          <span class="link-arrow">→</span>
        </router-link>
        <router-link to="/official/certification" class="quick-link">
          <span class="link-icon">🪪</span>
          <span>认证管理</span>
          <span class="link-arrow">→</span>
        </router-link>
        <router-link to="/" class="quick-link">
          <span class="link-icon">🏠</span>
          <span>返回前台</span>
          <span class="link-arrow">→</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Loading } from '@element-plus/icons-vue'

const certStatus = ref<string | null>(null)
const certLoading = ref(true)

const stats = ref({
  total: 0,
  pending: 0,
  online: 0,
  rejected: 0,
})

async function loadData() {
  const apiBase = import.meta.env.VITE_API_BASE_URL || '/api'
  const token = localStorage.getItem('token') || ''
  const headers = { Authorization: `Bearer ${token}` }

  // 获取认证状态
  try {
    const certRes = await fetch(`${apiBase}/official/certification`, { headers }).then(r => r.json())
    if (certRes.code === 0 && certRes.data) {
      certStatus.value = certRes.data.certificationStatus
    }
  } catch { /* ignore */ }
  certLoading.value = false

  // 获取投稿统计
  try {
    const subRes = await fetch(`${apiBase}/official/submissions`, { headers }).then(r => r.json())
    if (subRes.code === 0 && subRes.data) {
      const list = subRes.data.list || []
      stats.value.total = subRes.data.total || list.length
      stats.value.pending = list.filter((i: any) => i.status === 'PENDING').length
      stats.value.online = list.filter((i: any) => i.status === 'ONLINE').length
      stats.value.rejected = list.filter((i: any) => i.status === 'REJECTED').length
    }
  } catch { /* ignore */ }
}

onMounted(loadData)
</script>

<style scoped>
.dashboard-page { max-width: 800px; }
.page-header { margin-bottom: 28px; }
.page-title { margin: 0 0 6px; font-family: "Noto Serif SC","Songti SC",SimSun,serif; font-size: 24px; font-weight: 700; color: #e8c16d; }
.page-desc { margin: 0; color: #8a7b68; font-size: 14px; }

/* ===== 认证状态卡片 ===== */
.cert-section { margin-bottom: 28px; }
.cert-loading { display: flex; align-items: center; gap: 10px; padding: 24px; color: #6a5e4e; }
.loading-icon { animation: spin 1s linear infinite; color: #d6b05f; }
@keyframes spin { to { transform: rotate(360deg); } }

.cert-card {
  display: flex; align-items: center; gap: 20px; padding: 24px; border-radius: 12px;
  border: 1px solid rgb(214 176 95 / 12%);
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
}
.cert-icon { font-size: 42px; }
.cert-body h3 { margin: 0 0 6px; font-size: 18px; font-weight: 700; color: #f7edd5; }
.cert-body p { margin: 0 0 8px; color: #8a7b68; font-size: 14px; }
.cert-action { color: #d6b05f; font-size: 14px; font-weight: 500; text-decoration: none; }
.cert-action:hover { color: #e8c16d; }

.cert-card.notice { border-left: 3px solid #6a5e4e; }
.cert-card.pending { border-left: 3px solid #d6b05f; }
.cert-card.approved { border-left: 3px solid #34d399; }
.cert-card.rejected { border-left: 3px solid #f87171; }

/* ===== 统计 ===== */
.stats-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(170px, 1fr)); gap: 14px; margin-bottom: 32px; }
.stat-card {
  padding: 18px; border-radius: 10px; border: 1px solid rgb(214 176 95 / 12%);
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
  text-align: center;
}
.stat-value { font-size: 32px; font-weight: 800; color: #f7edd5; display: block; }
.stat-label { font-size: 12px; color: #6a5e4e; margin-top: 4px; text-transform: uppercase; letter-spacing: 1px; }
.stat-card.pending { border-left: 3px solid #d6b05f; }
.stat-card.online { border-left: 3px solid #34d399; }
.stat-card.rejected { border-left: 3px solid #f87171; }

/* ===== 快捷入口 ===== */
.section-title { margin: 0 0 14px; font-family: "Noto Serif SC","Songti SC",SimSun,serif; font-size: 18px; font-weight: 700; color: #f7edd5; }
.links-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 10px; }
.quick-link {
  display: flex; align-items: center; gap: 10px; padding: 14px; border-radius: 10px;
  border: 1px solid rgb(214 176 95 / 12%); background: rgb(255 255 255 / 2%);
  color: #b9ab90; text-decoration: none; transition: all 200ms;
}
.quick-link:hover { border-color: #d6b05f; background: rgb(214 176 95 / 8%); color: #e8c16d; }
.link-icon { font-size: 20px; }
.link-arrow { margin-left: auto; color: #6a5e4e; }
</style>