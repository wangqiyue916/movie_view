<template>
  <div class="official-layout">
    <aside class="sidebar">
      <h2>电影商后台</h2>
      <router-link to="/official">概览</router-link>
      <router-link to="/official/certification">认证申请</router-link>
      <router-link to="/official/submissions">我的投稿</router-link>
      <router-link to="/official/videos/submit">投稿视频</router-link>
      <router-link to="/official/merchandise/submit">投稿周边</router-link>
      <router-link to="/">返回前台</router-link>
    </aside>
    <main class="official-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.official-layout {
  display: grid;
  min-height: 100vh;
  grid-template-columns: 232px 1fr;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 24px;
  color: #dcfce7;
  background: #14532d;
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
  color: #14532d;
  background: #fff;
}

.official-main {
  padding: 28px;
}
</style>

