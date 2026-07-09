<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h1 class="page-title">管理后台</h1>
      <p class="page-desc">欢迎回来，以下是系统概览</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card pending">
        <div class="stat-icon">📋</div>
        <div class="stat-body">
          <span class="stat-value">{{ stats.pendingAudits }}</span>
          <span class="stat-label">待审核内容</span>
        </div>
      </div>
      <div class="stat-card cert">
        <div class="stat-icon">🪪</div>
        <div class="stat-body">
          <span class="stat-value">{{ stats.pendingCerts }}</span>
          <span class="stat-label">待审核认证</span>
        </div>
      </div>
      <div class="stat-card report">
        <div class="stat-icon">🚩</div>
        <div class="stat-body">
          <span class="stat-value">{{ stats.pendingReports }}</span>
          <span class="stat-label">待处理举报</span>
        </div>
      </div>
      <div class="stat-card online">
        <div class="stat-icon">✅</div>
        <div class="stat-body">
          <span class="stat-value">{{ stats.onlineContent }}</span>
          <span class="stat-label">已上线内容</span>
        </div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-links">
      <h3 class="section-title">快捷操作</h3>
      <div class="links-grid">
        <router-link to="/admin/certifications" class="quick-link">
          <span class="link-icon">🪪</span>
          <span>认证审核</span>
          <span class="link-arrow">→</span>
        </router-link>
        <router-link to="/admin/audits" class="quick-link">
          <span class="link-icon">📋</span>
          <span>内容审核</span>
          <span class="link-arrow">→</span>
        </router-link>
        <router-link to="/admin/reports" class="quick-link">
          <span class="link-icon">🚩</span>
          <span>举报处理</span>
          <span class="link-arrow">→</span>
        </router-link>
        <router-link to="/" class="quick-link">
          <span class="link-icon">🏠</span>
          <span>返回前台</span>
          <span class="link-arrow">→</span>
        </router-link>
      </div>
    </div>

    <!-- 最近审核记录 -->
    <div class="recent-section">
      <h3 class="section-title">最近审核动态</h3>
      <div class="recent-list">
        <div v-for="item in recentAudits" :key="item.id" class="recent-item">
          <span class="recent-type">{{ item.type }}</span>
          <span class="recent-title">{{ item.title }}</span>
          <span class="recent-action" :class="`action-${item.action}`">{{ item.actionLabel }}</span>
          <span class="recent-time">{{ item.time }}</span>
        </div>
        <div v-if="recentAudits.length === 0" class="recent-empty">
          <p>暂无审核记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'

interface Stats {
  pendingAudits: number
  pendingCerts: number
  pendingReports: number
  onlineContent: number
}

const stats = ref<Stats>({
  pendingAudits: 0,
  pendingCerts: 0,
  pendingReports: 0,
  onlineContent: 0,
})

const recentAudits = ref<any[]>([])

async function fetchStats() {
  try {
    const res: any = await request.get('/admin/dashboard')
    stats.value.pendingAudits = res.pendingAudits || 0
    stats.value.pendingCerts = res.pendingCerts || 0
    stats.value.pendingReports = res.pendingReports || 0
    stats.value.onlineContent = res.onlineContent || 0
    recentAudits.value = (res.recentAudits || []).map((a: any) => ({
      ...a,
      type: typeLabel(a.type),
      title: a.reason || a.type || '-',
    }))
  } catch {
    stats.value = { pendingAudits: 0, pendingCerts: 0, pendingReports: 0, onlineContent: 0 }
  }
}

function typeLabel(t: string) {
  const m: Record<string, string> = {
    LONG_REVIEW: '长评', SHORT_COMMENT: '短评', NEWS: '资讯', MOVIE: '电影', MERCHANDISE: '周边', OFFICIAL_CERTIFICATION: '认证',
  }
  return m[t] || t
}

onMounted(fetchStats)
</script>

<style scoped>
.dashboard-page { max-width: 900px; }
.page-header { margin-bottom: 28px; }
.page-title { margin: 0 0 6px; font-family: "Noto Serif SC","Songti SC",SimSun,serif; font-size: 24px; font-weight: 700; color: #e8c16d; }
.page-desc { margin: 0; color: #8a7b68; font-size: 14px; }

/* ===== 统计卡片 ===== */
.stats-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(190px, 1fr)); gap: 16px; margin-bottom: 32px; }
.stat-card {
  padding: 22px; border-radius: 12px; border: 1px solid rgb(214 176 95 / 12%);
  background: linear-gradient(180deg, rgb(255 255 255 / 4%), rgb(255 255 255 / 1%));
  display: flex; align-items: center; gap: 16px;
}
.stat-icon { font-size: 36px; }
.stat-body { display: flex; flex-direction: column; }
.stat-value { font-size: 28px; font-weight: 800; color: #f7edd5; line-height: 1; }
.stat-label { font-size: 12px; color: #6a5e4e; margin-top: 4px; text-transform: uppercase; letter-spacing: 1px; }

.stat-card.pending { border-left: 3px solid #d6b05f; }
.stat-card.cert { border-left: 3px solid #60a5fa; }
.stat-card.report { border-left: 3px solid #f87171; }
.stat-card.online { border-left: 3px solid #34d399; }

/* ===== 快捷入口 ===== */
.section-title { margin: 0 0 14px; font-family: "Noto Serif SC","Songti SC",SimSun,serif; font-size: 18px; font-weight: 700; color: #f7edd5; }
.quick-links { margin-bottom: 32px; }
.links-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 10px; }
.quick-link {
  display: flex; align-items: center; gap: 10px; padding: 16px; border-radius: 10px;
  border: 1px solid rgb(214 176 95 / 12%); background: rgb(255 255 255 / 2%);
  color: #b9ab90; text-decoration: none; transition: all 200ms;
}
.quick-link:hover { border-color: #d6b05f; background: rgb(214 176 95 / 8%); color: #e8c16d; }
.link-icon { font-size: 20px; }
.link-arrow { margin-left: auto; color: #6a5e4e; }

/* ===== 最近审核 ===== */
.recent-section { margin-bottom: 32px; }
.recent-list { border: 1px solid rgb(214 176 95 / 10%); border-radius: 10px; overflow: hidden; }
.recent-item {
  display: flex; align-items: center; gap: 14px; padding: 14px 20px;
  border-bottom: 1px solid rgb(214 176 95 / 6%); font-size: 14px;
}
.recent-item:last-child { border-bottom: none; }
.recent-type { width: 50px; color: #6a5e4e; font-size: 12px; }
.recent-title { flex: 1; color: #c6b78f; }
.recent-action { padding: 2px 10px; border-radius: 8px; font-size: 12px; font-weight: 600; }
.action-approve { background: rgb(52 211 153 / 16%); color: #34d399; }
.action-online { background: rgb(34 197 94 / 16%); color: #22c55e; }
.action-reject { background: rgb(239 68 68 / 16%); color: #f87171; }
.recent-time { color: #5a5040; font-size: 12px; }
.recent-empty { text-align: center; padding: 32px; color: #5a5040; }
</style>
