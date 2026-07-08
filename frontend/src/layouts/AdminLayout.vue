<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>管理后台</h2>
        <span class="badge">Admin</span>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin">
          <el-icon><DataAnalysis /></el-icon>
          <span>概览</span>
        </router-link>
        <router-link to="/admin/certifications">
          <el-icon><Stamp /></el-icon>
          <span>认证审核</span>
        </router-link>
        <router-link to="/admin/audits">
          <el-icon><Checked /></el-icon>
          <span>内容审核</span>
        </router-link>
        <router-link to="/admin/reports">
          <el-icon><WarningFilled /></el-icon>
          <span>举报处理</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <router-link to="/">
          <el-icon><Back /></el-icon>
          <span>返回前台</span>
        </router-link>
      </div>
    </aside>
    <main class="dashboard-main">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { DataAnalysis, Checked, WarningFilled, Back, Stamp } from '@element-plus/icons-vue'
</script>

<style scoped>
.dashboard-layout {
  display: grid;
  min-height: 100vh;
  grid-template-columns: 240px 1fr;
  background: #08080a;
}

.sidebar {
  display: flex;
  flex-direction: column;
  padding: 24px 0;
  background: linear-gradient(180deg, #0f0e0a 0%, #0a0906 100%);
  border-right: 1px solid rgb(214 176 95 / 14%);
  box-shadow: 2px 0 20px rgb(0 0 0 / 40%);
}

.sidebar-header {
  padding: 0 20px 20px;
  margin-bottom: 8px;
  border-bottom: 1px solid rgb(214 176 95 / 12%);
  display: flex;
  align-items: center;
  gap: 10px;
}

.sidebar-header h2 {
  margin: 0;
  color: #e8c16d;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 18px;
  font-weight: 700;
}

.badge {
  padding: 2px 10px;
  border-radius: 10px;
  background: rgb(214 176 95 / 18%);
  color: #d6b05f;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.sidebar-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 8px 12px;
}

.sidebar-nav a {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 8px;
  color: #b9ab90;
  font-size: 14px;
  transition: all 180ms ease;
}

.sidebar-nav a:hover {
  background: rgb(214 176 95 / 8%);
  color: #e8c16d;
}

.sidebar-nav a.router-link-active {
  background: rgb(214 176 95 / 14%);
  color: #f3d58c;
  font-weight: 600;
}

.sidebar-footer {
  padding: 16px 12px 0;
  border-top: 1px solid rgb(214 176 95 / 12%);
}

.sidebar-footer a {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 8px;
  color: #7a6e58;
  font-size: 13px;
  transition: all 180ms ease;
}

.sidebar-footer a:hover {
  background: rgb(214 176 95 / 6%);
  color: #b9ab90;
}

.dashboard-main {
  padding: 32px;
  background:
    radial-gradient(circle at 15% 10%, rgb(214 176 95 / 4%), transparent 30%),
    radial-gradient(circle at 85% 90%, rgb(214 176 95 / 3%), transparent 30%),
    #08080a;
}
</style>