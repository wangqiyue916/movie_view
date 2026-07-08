<template>
  <div class="page create-review-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">写长评</h1>
        <router-link to="/long-reviews" class="back-link">← 返回列表</router-link>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="create-form"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="选择电影" prop="movieId">
          <el-select v-model="form.movieId" placeholder="请选择要评价的电影" filterable style="width: 100%">
            <el-option
              v-for="movie in movieOptions"
              :key="movie.id"
              :label="movie.name"
              :value="movie.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="100" show-word-limit placeholder="请输入长评标题" />
        </el-form-item>

        <el-form-item label="内容" prop="contentMd">
          <el-input
            v-model="form.contentMd"
            type="textarea"
            :rows="12"
            placeholder="请撰写您的长评（支持 Markdown 格式）"
          />
        </el-form-item>

        <el-form-item label="封面图链接（选填）">
          <el-input v-model="form.coverUrl" placeholder="https://example.com/cover.jpg" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" native-type="submit" class="submit-btn">
            {{ submitting ? '提交中...' : '提交长评' }}
          </el-button>
          <el-button @click="router.push('/long-reviews')" :disabled="submitting">取消</el-button>
        </el-form-item>
      </el-form>

      <div v-if="submitResult" class="submit-result">
        <el-alert :title="submitResult.message" :type="submitResult.type" show-icon :closable="false" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createReview } from '@/api/longReviewApi'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const formRef = ref<FormInstance>()

const form = reactive({
  movieId: null as number | null,
  title: '',
  contentMd: '',
  coverUrl: '',
})

const rules: FormRules = {
  movieId: [{ required: true, message: '请选择电影', trigger: 'change' }],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 100, message: '标题最长100字符', trigger: 'blur' },
  ],
  contentMd: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

const submitting = ref(false)
const submitResult = ref<{ type: 'success' | 'error'; message: string } | null>(null)

const movieOptions = ref<{ id: number; name: string }[]>([])

async function loadMovies() {
  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
    const resp = await fetch(`${baseUrl}/movies?page=1&pageSize=50`)
    const json = await resp.json()
    const list = json.data?.list || []
    movieOptions.value = list.map((m: any) => ({ id: m.id, name: m.title || m.name }))
  } catch {
    // fallback movies
    movieOptions.value = [
      { id: 1, name: '星际穿越' },
      { id: 2, name: '流浪地球2' },
      { id: 3, name: '盗梦空间' },
    ]
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  submitResult.value = null

  try {
    const res: any = await createReview({
      movieId: form.movieId!,
      title: form.title,
      contentMd: form.contentMd,
      coverUrl: form.coverUrl || undefined,
    })

    const reviewId = res.id || (res as any).data?.id
    submitResult.value = {
      type: 'success',
      message: `长评提交成功！将跳转到长评详情...`,
    }
    setTimeout(() => {
      router.push(reviewId ? `/long-reviews/${reviewId}` : '/long-reviews')
    }, 1500)
  } catch (err: any) {
    submitResult.value = {
      type: 'error',
      message: err?.message || '提交失败，请稍后重试',
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadMovies()
})
</script>

<style scoped>
.create-review-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 8% 18%, rgb(214 176 95 / 8%), transparent 24%),
    radial-gradient(circle at 92% 26%, rgb(214 176 95 / 6%), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0e0c08 46%, #050505 100%);
  padding: 32px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgb(214 176 95 / 18%);
}

.page-title {
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: clamp(22px, 2.8vw, 28px);
  color: #e8c16d;
  padding-left: 16px;
  position: relative;
}

.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 22px;
  background: #d6b05f;
  border-radius: 2px;
}

.back-link {
  color: #d6b05f;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.2s;
}

.back-link:hover {
  color: #e8c16d;
}

.create-form {
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 8px;
  padding: 28px;
  box-shadow: 0 12px 28px rgb(0 0 0 / 34%);
}

.create-form :deep(.el-form-item__label) {
  color: #d8c69b;
  font-weight: 500;
}

.create-form :deep(.el-input__wrapper),
.create-form :deep(.el-textarea__inner),
.create-form :deep(.el-select__wrapper) {
  background: rgb(0 0 0 / 30%);
  border-color: rgb(214 176 95 / 22%);
  color: #f7edd5;
}

.create-form :deep(.el-input__wrapper:hover),
.create-form :deep(.el-textarea__inner:hover) {
  border-color: rgb(214 176 95 / 40%);
}

.create-form :deep(.el-input__inner),
.create-form :deep(.el-textarea__inner) {
  color: #f7edd5;
}

.create-form :deep(.el-select__placeholder) {
  color: #8a7d66;
}

.submit-btn {
  background: linear-gradient(135deg, #d6b05f, #c89a4a) !important;
  border: none !important;
  color: #1a1a1a !important;
  font-weight: 600;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #e8c16d, #d6b05f) !important;
  box-shadow: 0 2px 12px rgb(214 176 95 / 30%) !important;
}

.submit-result {
  margin-top: 20px;
}
</style>