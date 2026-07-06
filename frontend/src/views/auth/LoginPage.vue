<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="never">
      <h1>登录</h1>
      <el-form :model="form" label-position="top" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="user / official / admin / super_admin" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="handleLogin">登录</el-button>
        <router-link to="/register">注册账号</router-link>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({
  username: 'user',
  password: '123456',
})

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    await router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: grid;
  min-height: 100vh;
  place-items: center;
  background: #eef2f7;
}

.auth-card {
  width: 380px;
}

.auth-card h1 {
  margin: 0 0 20px;
}
</style>

