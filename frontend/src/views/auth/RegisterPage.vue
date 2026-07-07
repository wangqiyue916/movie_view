<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="never">
      <h1>注册</h1>
      <el-form :model="form" label-position="top" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="handleRegister">注册</el-button>
        <router-link to="/login">已有账号，去登录</router-link>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/authApi'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: '',
  nickname: '',
  password: '',
})

async function handleRegister() {
  if (!form.username || !form.nickname || !form.password) {
    ElMessage.warning('请完整填写注册信息')
    return
  }
  loading.value = true
  try {
    await authApi.register(form)
    ElMessage.success('注册成功，请登录')
    await router.push('/login')
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

