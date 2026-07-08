<template>
  <div class="page official-video-submit-page">
    <div class="form-container">
      <h1>提交视频</h1>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        class="submit-form"
      >
        <el-form-item label="关联电影ID" prop="movieId">
          <el-input-number v-model="formData.movieId" :min="1" placeholder="请输入电影ID" />
        </el-form-item>

        <el-form-item label="视频标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入视频标题" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="平台" prop="platform">
          <el-select v-model="formData.platform" placeholder="请选择平台">
            <el-option label="Bilibili" value="Bilibili" />
            <el-option label="YouTube" value="YouTube" />
            <el-option label="抖音" value="抖音" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="外部链接" prop="externalUrl">
          <el-input v-model="formData.externalUrl" placeholder="请输入视频链接URL" />
        </el-form-item>

        <el-form-item label="封面图URL">
          <el-input v-model="formData.coverUrl" placeholder="请输入封面图URL（选填）" />
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入视频描述（选填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button type="warning" :loading="submitting" @click="handleSubmit">提交审核</el-button>
            <el-button @click="router.push('/official/submissions')">取消</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { videoApi } from '@/api/videoApi'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const formData = reactive({
  movieId: undefined as number | undefined,
  title: '',
  platform: '',
  externalUrl: '',
  coverUrl: '',
  description: '',
})

const rules: FormRules = {
  movieId: [{ required: true, message: '请输入关联电影ID', trigger: 'blur' }],
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
  externalUrl: [{ required: true, message: '请输入外部链接', trigger: 'blur' }],
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await videoApi.submit({
      movieId: formData.movieId!,
      title: formData.title,
      platform: formData.platform,
      externalUrl: formData.externalUrl,
      coverUrl: formData.coverUrl || undefined,
      description: formData.description || undefined,
    })
    ElMessage.success('提交成功')
    router.push('/official/submissions')
  } catch {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.official-video-submit-page {
  min-height: calc(100vh - 64px);
  padding: 36px max(22px, calc((100vw - 1280px) / 2)) 72px;
  display: flex;
  justify-content: center;
  color: #f7edd5;
}

.form-container {
  width: 100%;
  max-width: 640px;
}

.form-container h1 {
  margin: 0 0 32px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 26px;
  font-weight: 800;
  color: #e8c16d;
}

.submit-form {
  padding: 32px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 10px;
  background: rgb(255 255 255 / 3%);
}

.form-actions {
  display: flex;
  gap: 12px;
}
</style>
