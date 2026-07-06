<template>
  <div>
    <header class="main-header">
      <router-link class="brand" to="/">电影点评系统</router-link>
      <nav class="nav">
        <router-link to="/movies">电影</router-link>
        <router-link to="/news">资讯</router-link>
        <router-link to="/long-reviews">长评</router-link>
        <router-link to="/merchandise">周边</router-link>
      </nav>
      <div class="actions">
        <router-link v-if="!userStore.isLogin" to="/login">登录</router-link>
        <router-link v-else-if="userStore.hasRole('ADMIN') || userStore.hasRole('SUPER_ADMIN')" to="/admin">后台</router-link>
        <router-link v-else-if="userStore.hasRole('OFFICIAL')" to="/official">电影商后台</router-link>
        <span v-if="userStore.isLogin">{{ userStore.userInfo?.nickname }}</span>
      </div>
    </header>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
</script>

<style scoped>
.main-header {
  display: flex;
  align-items: center;
  gap: 28px;
  height: 64px;
  padding: 0 32px;
  border-bottom: 1px solid #e5e7eb;
  background: #fff;
}

.brand {
  font-size: 20px;
  font-weight: 800;
}

.nav {
  display: flex;
  gap: 18px;
  color: #4b5563;
}

.actions {
  display: flex;
  gap: 16px;
  margin-left: auto;
  color: #2563eb;
}

.main-content {
  padding: 28px 0 48px;
}
</style>

