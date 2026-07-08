<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <h2>管理后台</h2>
      <router-link to="/admin">概览</router-link>
      <router-link to="/admin/audits">内容审核</router-link>
      <router-link to="/admin/reports">举报处理</router-link>
      <router-link to="/admin/videos">视频管理</router-link>
      <router-link to="/admin/merchandise">周边管理</router-link>
      <router-link to="/">返回前台</router-link>
    </aside>
    <main class="admin-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.admin-layout {
  display: grid;
  min-height: 100vh;
  grid-template-columns: 232px 1fr;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 24px;
  color: #dbeafe;
  background: #1f2937;
}

.sidebar h2 {
  margin: 0 0 16px;
  color: #fff;
  font-size: 20px;
}

.sidebar a {
  padding: 10px 12px;
  border-radius: 6px;
}

.sidebar a.router-link-active {
  color: #111827;
  background: #fff;
}

.admin-main {
  padding: 28px;
}
</style>

