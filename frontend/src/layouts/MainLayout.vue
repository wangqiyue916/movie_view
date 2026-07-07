<template>
  <div class="main-layout" :class="{ 'home-layout': isHomePage }">
    <header class="main-header">
      <router-link class="brand" to="/">电影点评系统</router-link>
      <nav class="nav">
        <router-link to="/movies">电影</router-link>
        <router-link to="/news">资讯</router-link>
        <a href="/#quality-reviews" @click.prevent="scrollToReviews">长评</a>
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isHomePage = computed(() => route.name === 'HomePage')

function scrollToReviews() {
  if (route.name === 'HomePage') {
    const el = document.getElementById('quality-reviews')
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' })
    }
  } else {
    router.push({ path: '/', hash: '#quality-reviews' })
  }
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
}

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

.home-layout {
  background:
    radial-gradient(circle at 6% 18%, rgb(214 176 95 / 10%), transparent 22%),
    radial-gradient(circle at 94% 24%, rgb(214 176 95 / 9%), transparent 22%),
    linear-gradient(180deg, #050505 0%, #0e0c08 46%, #050505 100%);
}

.home-layout .main-header {
  position: sticky;
  top: 0;
  z-index: 20;
  border-bottom: 1px solid rgb(214 176 95 / 24%);
  color: #f6ead0;
  background: rgb(5 5 5 / 94%);
  backdrop-filter: blur(18px);
  box-shadow: 0 8px 28px rgb(0 0 0 / 34%);
}

.home-layout .brand {
  color: #e8c16d;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  letter-spacing: 0;
  text-shadow: 0 0 18px rgb(214 176 95 / 28%);
}

.home-layout .nav {
  color: #cbb98f;
}

.home-layout .nav a,
.home-layout .actions a {
  transition: color 180ms ease;
}

.home-layout .nav a:hover,
.home-layout .actions a:hover {
  color: #f3d58c;
}

.home-layout .actions {
  color: #e8c16d;
}

.home-layout .main-content {
  padding: 0;
}

@media (max-width: 640px) {
  .main-header {
    gap: 16px;
    padding: 0 18px;
  }

  .brand {
    font-size: 18px;
  }

  .nav {
    gap: 12px;
  }
}
</style>
