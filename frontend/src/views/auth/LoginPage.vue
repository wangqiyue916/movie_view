<template>
  <div class="auth-page">
    <!-- 背景装饰 -->
    <div class="bg-ornaments">
      <div class="ornament ring-1"></div>
      <div class="ornament ring-2"></div>
      <div class="ornament glow-1"></div>
      <div class="ornament glow-2"></div>
    </div>

    <!-- 左侧品牌区 -->
    <div class="auth-brand">
      <div class="brand-content">
        <div class="brand-icon">
          <span class="icon-film">🎬</span>
        </div>
        <h1 class="brand-title">MovieReview</h1>
        <p class="brand-subtitle">发现光影之美，分享观影之思</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-icon">★</span>
            <span>专业电影评分</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">✎</span>
            <span>深度长评社区</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">☊</span>
            <span>AI 智能推荐</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="auth-form-section">
      <div class="form-card">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>登录你的账号，继续探索电影世界</p>
        </div>

        <el-form :model="form" label-position="top" class="auth-form" @submit.prevent>
          <el-form-item>
            <el-input
              v-model="form.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              show-password
              :prefix-icon="Lock"
              class="custom-input"
            />
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            @click="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>验证中...</span>
          </el-button>
        </el-form>

        <div class="form-footer">
          <span class="hint-text">还没有账号？</span>
          <router-link to="/register" class="link-text">立即注册 →</router-link>
        </div>

        <div class="test-accounts">
          <p class="test-title">测试账号</p>
          <div class="test-tags">
            <span class="test-tag" @click="quickFill('user', '123456')">普通用户</span>
            <span class="test-tag" @click="quickFill('admin', '123456')">管理员</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
})

function quickFill(username: string, password: string) {
  form.username = username
  form.password = password
}

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功！欢迎回来 🎉')
    await router.push('/')
  } catch (err: any) {
    // 错误提示已在 request 拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: grid;
  min-height: 100vh;
  grid-template-columns: 1fr 1fr;
  position: relative;
  overflow: hidden;
  background: #060606;
}

/* ========== 背景装饰 ========== */
.bg-ornaments {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.ornament {
  position: absolute;
  border-radius: 50%;
}

.ring-1 {
  width: 600px;
  height: 600px;
  border: 1px solid rgb(214 176 95 / 10%);
  top: -200px;
  right: -100px;
}

.ring-2 {
  width: 400px;
  height: 400px;
  border: 1px solid rgb(214 176 95 / 8%);
  bottom: -120px;
  left: -80px;
}

.glow-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgb(214 176 95 / 6%), transparent 70%);
  top: 10%;
  left: 55%;
}

.glow-2 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgb(214 176 95 / 5%), transparent 70%);
  bottom: 20%;
  left: 30%;
}

/* ========== 左侧品牌区 ========== */
.auth-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  background: linear-gradient(
    135deg,
    rgb(214 176 95 / 3%) 0%,
    transparent 50%,
    rgb(214 176 95 / 2%) 100%
  );
  border-right: 1px solid rgb(214 176 95 / 8%);
}

.brand-content {
  text-align: center;
  max-width: 380px;
  padding: 40px;
}

.brand-icon {
  margin-bottom: 24px;
}

.icon-film {
  font-size: 64px;
  display: block;
  filter: drop-shadow(0 0 20px rgb(214 176 95 / 30%));
}

.brand-title {
  margin: 0 0 12px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 36px;
  font-weight: 800;
  color: #e8c16d;
  letter-spacing: 2px;
  text-shadow: 0 0 30px rgb(214 176 95 / 20%);
}

.brand-subtitle {
  margin: 0 0 40px;
  color: #8a7a5e;
  font-size: 15px;
  line-height: 1.6;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  text-align: left;
  padding: 0 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #a09070;
  font-size: 14px;
}

.feature-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: rgb(214 176 95 / 8%);
  color: #d6b05f;
  font-size: 16px;
  flex-shrink: 0;
}

/* ========== 右侧表单区 ========== */
.auth-form-section {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
}

.form-card {
  width: 420px;
  padding: 48px 44px;
  background: linear-gradient(
    180deg,
    rgb(255 255 255 / 3%) 0%,
    rgb(255 255 255 / 1%) 100%
  );
  border: 1px solid rgb(214 176 95 / 10%);
  border-radius: 16px;
  backdrop-filter: blur(20px);
  box-shadow:
    0 0 60px rgb(0 0 0 / 50%),
    inset 0 1px 0 rgb(214 176 95 / 6%);
}

.form-header {
  text-align: center;
  margin-bottom: 36px;
}

.form-header h2 {
  margin: 0 0 8px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 26px;
  font-weight: 700;
  color: #f7edd5;
  letter-spacing: 1px;
}

.form-header p {
  margin: 0;
  color: #7a6e58;
  font-size: 14px;
}

/* ========== 表单样式 ========== */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.custom-input :deep(.el-input__wrapper) {
  background: rgb(255 255 255 / 3%);
  border: 1px solid rgb(214 176 95 / 14%);
  border-radius: 10px;
  box-shadow: none;
  transition: all 250ms ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: rgb(214 176 95 / 30%);
  background: rgb(255 255 255 / 5%);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #d6b05f;
  box-shadow: 0 0 0 3px rgb(214 176 95 / 8%);
  background: rgb(255 255 255 / 5%);
}

.custom-input :deep(.el-input__inner) {
  color: #f7edd5;
  font-size: 15px;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: #5a5040;
}

.custom-input :deep(.el-input__prefix) {
  color: #6a5e4e;
}

.submit-btn {
  width: 100%;
  height: 48px;
  margin-top: 12px;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #d6b05f 0%, #c89a4a 50%, #b8883a 100%);
  color: #1a1a1a;
  transition: all 300ms ease;
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #e8c16d 0%, #d6b05f 50%, #c89a4a 100%);
  opacity: 0;
  transition: opacity 300ms ease;
}

.submit-btn:hover::before {
  opacity: 1;
}

.submit-btn:hover {
  box-shadow: 0 8px 32px rgb(214 176 95 / 25%);
  transform: translateY(-1px);
}

.submit-btn span {
  position: relative;
  z-index: 1;
}

/* ========== 底部链接 ========== */
.form-footer {
  text-align: center;
  margin-top: 24px;
}

.hint-text {
  color: #5a5040;
  font-size: 14px;
}

.link-text {
  color: #d6b05f;
  font-size: 14px;
  font-weight: 500;
  margin-left: 4px;
  transition: color 200ms;
}

.link-text:hover {
  color: #e8c16d;
}

/* ========== 测试账号 ========== */
.test-accounts {
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid rgb(214 176 95 / 10%);
  text-align: center;
}

.test-title {
  margin: 0 0 12px;
  color: #4a4030;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.test-tags {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.test-tag {
  padding: 6px 16px;
  border: 1px solid rgb(214 176 95 / 16%);
  border-radius: 20px;
  color: #a09070;
  font-size: 12px;
  cursor: pointer;
  transition: all 200ms ease;
  background: transparent;
}

.test-tag:hover {
  border-color: #d6b05f;
  color: #d6b05f;
  background: rgb(214 176 95 / 8%);
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .auth-brand {
    display: none;
  }

  .form-card {
    width: calc(100% - 32px);
    padding: 36px 24px;
  }
}
</style>