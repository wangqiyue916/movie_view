<template>
  <div class="submission-page">
    <div class="page-header">
      <h1 class="page-title">投稿资讯</h1>
      <p class="page-desc">撰写电影资讯并提交审核，通过后将在前台展示</p>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      class="submission-form"
      @submit.prevent="handleSubmit"
    >
      <div class="form-card">
        <h3 class="section-title">基本信息</h3>

        <el-form-item label="资讯标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入资讯标题"
            size="large"
            maxlength="150"
            show-word-limit
            class="custom-input"
          />
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="选择资讯分类" size="large" class="custom-select">
            <el-option label="新片动态" value="新片动态" />
            <el-option label="平台活动" value="平台活动" />
            <el-option label="票房观察" value="票房观察" />
            <el-option label="幕后花絮" value="幕后花絮" />
            <el-option label="演员动态" value="演员动态" />
            <el-option label="获奖信息" value="获奖信息" />
            <el-option label="行业观察" value="行业观察" />
            <el-option label="周边资讯" value="周边资讯" />
          </el-select>
        </el-form-item>

        <el-form-item label="摘要" prop="summary">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
            placeholder="简要概述资讯内容（最长500字）"
            maxlength="500"
            show-word-limit
            class="custom-input"
          />
        </el-form-item>

        <el-form-item label="封面图片链接" prop="coverUrl">
          <el-input
            v-model="form.coverUrl"
            placeholder="https://example.com/cover.jpg"
            size="large"
            class="custom-input"
          />
        </el-form-item>

        <el-form-item label="正文内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="请输入资讯正文内容，支持换行"
            class="custom-input"
          />
        </el-form-item>
      </div>

      <!-- 关联内容 -->
      <div class="form-card">
        <h3 class="section-title">关联内容</h3>
        <p class="section-desc">添加相关的电影、导演或演员，读者可以点击跳转到对应页面</p>

        <div class="relations-list">
          <div v-for="(rel, idx) in form.relations" :key="idx" class="relation-row">
            <el-select
              v-model="rel.targetType"
              placeholder="类型"
              size="large"
              class="relation-type"
            >
              <el-option label="🎬 电影" value="MOVIE" />
              <el-option label="🎥 导演" value="DIRECTOR" />
              <el-option label="👤 演员" value="ACTOR" />
            </el-select>
            <el-input
              v-model="rel.targetName"
              placeholder="名称（如：星际穿越）"
              size="large"
              class="relation-name"
            />
            <el-input-number
              v-model="rel.targetId"
              :min="1"
              placeholder="ID"
              size="large"
              class="relation-id"
            />
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              circle
              class="relation-delete"
              @click="removeRelation(idx)"
            />
          </div>
        </div>

        <el-button class="add-relation-btn" @click="addRelation">
          <el-icon><Plus /></el-icon>
          添加关联
        </el-button>
      </div>

      <!-- 操作按钮 -->
      <div class="form-actions">
        <el-button
          type="primary"
          size="large"
          :loading="submitting"
          class="submit-btn"
          @click="handleSubmit"
        >
          <el-icon v-if="!submitting"><Upload /></el-icon>
          {{ submitting ? '提交中...' : '提交审核' }}
        </el-button>
        <el-button size="large" class="draft-btn" :loading="submitting" @click="handleSaveDraft">
          保存草稿
        </el-button>
      </div>
    </el-form>

    <!-- 提交结果 -->
    <div v-if="submitResult" class="submit-result">
      <el-alert
        :title="submitResult.message"
        :type="submitResult.success ? 'success' : 'error'"
        show-icon
        :closable="false"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, Plus, Delete } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

interface Relation {
  targetType: string
  targetId: number | null
  targetName: string
}

const formRef = ref<FormInstance>()
const submitting = ref(false)
const submitResult = ref<{ success: boolean; message: string } | null>(null)

const form = reactive({
  title: '',
  summary: '',
  content: '',
  coverUrl: '',
  category: '',
  relations: [] as Relation[],
})

const rules: FormRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 150, message: '标题最多150字', trigger: 'blur' },
  ],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  summary: [
    { required: true, message: '请输入摘要', trigger: 'blur' },
    { max: 500, message: '摘要最多500字', trigger: 'blur' },
  ],
  coverUrl: [
    { required: true, message: '请输入封面图片链接', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL', trigger: 'blur' },
  ],
  content: [{ required: true, message: '请输入正文内容', trigger: 'blur' }],
}

function addRelation() {
  form.relations.push({ targetType: 'MOVIE', targetId: null, targetName: '' })
}

function removeRelation(idx: number) {
  form.relations.splice(idx, 1)
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  submitResult.value = null
  try {
    const token = localStorage.getItem('token') || ''
    const apiBase = import.meta.env.VITE_API_BASE_URL || '/api'
    const resp = await fetch(`${apiBase}/official/submissions/news`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        title: form.title,
        summary: form.summary,
        content: form.content,
        coverUrl: form.coverUrl,
        category: form.category,
        relations: form.relations.filter(r => r.targetName.trim()),
      }),
    })
    const json = await resp.json()
    if (json.code === 0) {
      submitResult.value = { success: true, message: '投稿已提交！审核通过后将公开展示。' }
      ElMessage.success('投稿成功，等待审核')
      // 重置表单
      form.title = ''
      form.summary = ''
      form.content = ''
      form.coverUrl = ''
      form.category = ''
      form.relations = []
    } else {
      ElMessage.error(json.message || '投稿失败')
    }
  } catch {
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    submitting.value = false
  }
}

async function handleSaveDraft() {
  const token = localStorage.getItem('token') || ''
  const apiBase = import.meta.env.VITE_API_BASE_URL || '/api'
  submitting.value = true
  try {
    const resp = await fetch(`${apiBase}/official/submissions/news`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        title: form.title || '未命名草稿',
        summary: form.summary,
        content: form.content,
        coverUrl: form.coverUrl,
        category: form.category,
        status: 'DRAFT',
        relations: form.relations.filter(r => r.targetName.trim()),
      }),
    })
    const json = await resp.json()
    if (json.code === 0) {
      ElMessage.success('草稿已保存')
      submitResult.value = { success: true, message: '草稿已保存，可在"我的投稿"中查看和提交审核。' }
    } else {
      ElMessage.error(json.message || '保存失败')
    }
  } catch {
    ElMessage.error('网络异常')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.submission-page {
  max-width: 780px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 6px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 24px;
  font-weight: 700;
  color: #e8c16d;
}

.page-desc {
  margin: 0;
  color: #8a7b68;
  font-size: 14px;
}

/* Form Card */
.form-card {
  padding: 28px;
  margin-bottom: 20px;
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
  border: 1px solid rgb(214 176 95 / 12%);
  border-radius: 12px;
}

.section-title {
  margin: 0 0 6px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 18px;
  font-weight: 700;
  color: #f7edd5;
}

.section-desc {
  margin: 0 0 16px;
  color: #7a6e58;
  font-size: 13px;
}

/* ===== Form Items ===== */
.submission-form :deep(.el-form-item__label) {
  color: #b9ab90;
  font-weight: 500;
  font-size: 14px;
}

.custom-input :deep(.el-input__wrapper),
.custom-input :deep(.el-textarea__inner) {
  background: rgb(255 255 255 / 4%);
  border: 1px solid rgb(214 176 95 / 14%);
  border-radius: 8px;
  box-shadow: none;
  transition: all 250ms ease;
  color: #f7edd5;
}

.custom-input :deep(.el-input__wrapper:hover),
.custom-input :deep(.el-textarea__inner:hover) {
  border-color: rgb(214 176 95 / 30%);
}

.custom-input :deep(.el-input__wrapper.is-focus),
.custom-input :deep(.el-textarea__inner:focus) {
  border-color: #d6b05f;
  box-shadow: 0 0 0 2px rgb(214 176 95 / 10%);
}

.custom-input :deep(.el-input__inner),
.custom-input :deep(.el-textarea__inner) {
  color: #f7edd5;
}

.custom-input :deep(.el-input__inner::placeholder),
.custom-input :deep(.el-textarea__inner::placeholder) {
  color: #5a5040;
}

.custom-select :deep(.el-input__wrapper) {
  background: rgb(255 255 255 / 4%);
  border: 1px solid rgb(214 176 95 / 14%);
  border-radius: 8px;
}

/* ===== Relations ===== */
.relations-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.relation-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.relation-type {
  width: 120px;
}

.relation-name {
  flex: 1;
}

.relation-id {
  width: 100px;
}

.relation-delete {
  flex-shrink: 0;
}

.add-relation-btn {
  border: 1px dashed rgb(214 176 95 / 30%);
  background: transparent;
  color: #d6b05f;
  transition: all 200ms;
}

.add-relation-btn:hover {
  border-color: #d6b05f;
  background: rgb(214 176 95 / 8%);
  color: #e8c16d;
}

/* ===== Actions ===== */
.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.submit-btn {
  background: linear-gradient(135deg, #d6b05f, #c89a4a) !important;
  border: none !important;
  color: #1a1a1a !important;
  font-weight: 600;
  height: 44px;
  padding: 0 28px;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #e8c16d, #d6b05f) !important;
  box-shadow: 0 4px 20px rgb(214 176 95 / 25%);
}

.draft-btn {
  background: rgb(255 255 255 / 5%) !important;
  border: 1px solid rgb(214 176 95 / 14%) !important;
  color: #8a7b68 !important;
  height: 44px;
}

.submit-result {
  margin-top: 20px;
}
</style>