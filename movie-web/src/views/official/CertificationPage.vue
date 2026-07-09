<template>
  <div class="certification-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">认证申请</h1>
      <p class="page-desc">提交电影商认证材料，通过审核后即可发布电影相关投稿</p>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon" :size="32"><Loading /></el-icon>
      <p>加载认证信息...</p>
    </div>

    <!-- 已认证 -->
    <div v-else-if="profile && profile.certificationStatus === 'APPROVED'" class="status-card approved">
      <div class="status-icon">✅</div>
      <h2>认证已通过</h2>
      <p>恭喜！你的电影商认证已通过审核，现在可以使用电影商后台的完整功能。</p>
      <div class="profile-info">
        <div class="info-row">
          <span class="info-label">公司名称</span>
          <span class="info-value">{{ profile.companyName }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">联系人</span>
          <span class="info-value">{{ profile.contactName }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">联系电话</span>
          <span class="info-value">{{ profile.contactPhone }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">认证时间</span>
          <span class="info-value">{{ formatDate(profile.certifiedAt) }}</span>
        </div>
      </div>
    </div>

    <!-- 审核中 -->
    <div v-else-if="profile && profile.certificationStatus === 'PENDING'" class="status-card pending">
      <div class="status-icon">⏳</div>
      <h2>审核中</h2>
      <p>你的认证申请已提交，管理员正在审核中，请耐心等待...</p>
      <div class="profile-info">
        <div class="info-row">
          <span class="info-label">公司名称</span>
          <span class="info-value">{{ profile.companyName }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">联系人</span>
          <span class="info-value">{{ profile.contactName }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">联系电话</span>
          <span class="info-value">{{ profile.contactPhone }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">提交时间</span>
          <span class="info-value">{{ formatDate(profile.createdAt) }}</span>
        </div>
      </div>
      <div class="pending-hint">
        <el-icon><Clock /></el-icon>
        <span>审核通常需要 1-3 个工作日，请留意审核结果</span>
      </div>
    </div>

    <!-- 已驳回 -->
    <div v-else-if="profile && profile.certificationStatus === 'REJECTED'" class="status-card rejected">
      <div class="status-icon">❌</div>
      <h2>认证未通过</h2>
      <p>你的认证申请未通过审核，请根据驳回原因修改后重新提交</p>
      <div class="reject-reason-box">
        <p class="reject-label">驳回原因：</p>
        <p class="reject-text">{{ profile.rejectReason || '未提供具体原因' }}</p>
      </div>
      <el-button type="primary" class="resubmit-btn" @click="showForm = true">
        重新申请认证
      </el-button>
    </div>

    <!-- 提交表单 / 重新提交 -->
    <div v-if="!profile || profile.certificationStatus === 'REJECTED' || showForm" class="form-card-wrapper">
      <div class="form-card" v-if="showForm || !profile || profile.certificationStatus === 'REJECTED'">
        <div class="form-header">
          <h2>{{ profile?.certificationStatus === 'REJECTED' ? '重新申请认证' : '提交认证申请' }}</h2>
          <p>请填写以下信息，上传相关证明材料</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="cert-form"
          @submit.prevent="handleSubmit"
        >
          <el-form-item label="公司名称" prop="companyName">
            <el-input
              v-model="form.companyName"
              placeholder="请输入公司或机构全称"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item label="联系人姓名" prop="contactName">
            <el-input
              v-model="form.contactName"
              placeholder="请输入联系人姓名"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item label="联系电话" prop="contactPhone">
            <el-input
              v-model="form.contactPhone"
              placeholder="请输入联系电话"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item label="营业执照 / 证明材料链接" prop="licenseUrl">
            <el-input
              v-model="form.licenseUrl"
              placeholder="请输入证明材料图片URL（支持 jpg/png/webp）"
              size="large"
              class="custom-input"
            />
            <div class="form-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>请先上传图片至图床，然后粘贴图片链接。支持营业执照、授权书等证明材料。</span>
            </div>
          </el-form-item>

          <div class="form-actions">
            <el-button
              type="primary"
              size="large"
              :loading="submitting"
              class="submit-btn"
              @click="handleSubmit"
            >
              {{ submitting ? '提交中...' : '提交认证申请' }}
            </el-button>
            <el-button
              v-if="profile?.certificationStatus === 'REJECTED'"
              size="large"
              class="cancel-btn"
              @click="showForm = false"
            >
              取消
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, Clock, InfoFilled } from '@element-plus/icons-vue'
import { officialApi } from '@/api/officialApi'
import type { FormInstance, FormRules } from 'element-plus'

interface ProfileData {
  id: number
  userId: number
  companyName: string
  contactName: string
  contactPhone: string
  licenseUrl: string
  certificationStatus: string
  rejectReason: string | null
  certifiedAt: string | null
  createdAt: string
  updatedAt: string
}

const formRef = ref<FormInstance>()
const profile = ref<ProfileData | null>(null)
const loading = ref(true)
const submitting = ref(false)
const showForm = ref(false)

const form = reactive({
  companyName: '',
  contactName: '',
  contactPhone: '',
  licenseUrl: '',
})

const rules: FormRules = {
  companyName: [
    { required: true, message: '请输入公司名称', trigger: 'blur' },
    { max: 100, message: '公司名称最长100字符', trigger: 'blur' },
  ],
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { max: 50, message: '联系人姓名最长50字符', trigger: 'blur' },
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^[\d\-+() ]{7,20}$/, message: '请输入有效的电话号码', trigger: 'blur' },
  ],
  licenseUrl: [
    { required: true, message: '请提供证明材料链接', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL', trigger: 'blur' },
  ],
}

async function loadProfile() {
  loading.value = true
  try {
    const data = await officialApi.getCertification()
    profile.value = (data as unknown as ProfileData) || null
    if (profile.value?.certificationStatus === 'REJECTED') {
      // 预填表单
      form.companyName = profile.value.companyName
      form.contactName = profile.value.contactName
      form.contactPhone = profile.value.contactPhone
      form.licenseUrl = profile.value.licenseUrl
    }
  } catch {
    profile.value = null
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await officialApi.submitCertification(form)
    ElMessage.success('认证申请已提交，请等待审核')
    showForm.value = false
    await loadProfile()
  } catch (err: any) {
    // 错误提示已在 request 拦截器中处理
  } finally {
    submitting.value = false
  }
}

function formatDate(dateStr: string | null): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.certification-page {
  max-width: 680px;
}

.page-header {
  margin-bottom: 28px;
}

.page-title {
  margin: 0 0 8px;
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

/* ========== 加载状态 ========== */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 0;
  color: #6a5e4e;
  gap: 12px;
}

.loading-icon {
  animation: spin 1s linear infinite;
  color: #d6b05f;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ========== 状态卡片 ========== */
.status-card {
  padding: 36px;
  border-radius: 12px;
  border: 1px solid;
  text-align: center;
  margin-bottom: 28px;
}

.status-card.approved {
  background: linear-gradient(180deg, rgb(52 211 153 / 8%), rgb(52 211 153 / 2%));
  border-color: rgb(52 211 153 / 25%);
}

.status-card.pending {
  background: linear-gradient(180deg, rgb(214 176 95 / 8%), rgb(214 176 95 / 2%));
  border-color: rgb(214 176 95 / 25%);
}

.status-card.rejected {
  background: linear-gradient(180deg, rgb(239 68 68 / 8%), rgb(239 68 68 / 2%));
  border-color: rgb(239 68 68 / 25%);
}

.status-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.status-card h2 {
  margin: 0 0 8px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 22px;
  font-weight: 700;
}

.status-card.approved h2 { color: #34d399; }
.status-card.pending h2 { color: #e8c16d; }
.status-card.rejected h2 { color: #f87171; }

.status-card > p {
  margin: 0 0 20px;
  color: #8a7b68;
  font-size: 14px;
  line-height: 1.6;
}

/* ========== 资料展示 ========== */
.profile-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
  background: rgb(0 0 0 / 20%);
  border-radius: 8px;
  text-align: left;
}

.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.info-label {
  color: #6a5e4e;
}

.info-value {
  color: #d8c69b;
  font-weight: 500;
}

/* ========== 驳回提示 ========== */
.reject-reason-box {
  margin: 16px 0;
  padding: 16px;
  background: rgb(239 68 68 / 8%);
  border: 1px solid rgb(239 68 68 / 20%);
  border-radius: 8px;
  text-align: left;
}

.reject-label {
  margin: 0 0 4px;
  color: #f87171;
  font-size: 13px;
  font-weight: 600;
}

.reject-text {
  margin: 0;
  color: #b9ab90;
  font-size: 14px;
  line-height: 1.6;
}

.resubmit-btn {
  margin-top: 16px;
  background: linear-gradient(135deg, #d6b05f, #c89a4a) !important;
  border: none !important;
  color: #1a1a1a !important;
  font-weight: 600;
}

/* ========== 审核中提示 ========== */
.pending-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
  padding: 12px;
  border-radius: 8px;
  background: rgb(214 176 95 / 6%);
  color: #8a7b68;
  font-size: 13px;
}

/* ========== 表单 ========== */
.form-card-wrapper {
  margin-top: 8px;
}

.form-card {
  padding: 36px;
  background: linear-gradient(180deg, rgb(255 255 255 / 3%), rgb(255 255 255 / 1%));
  border: 1px solid rgb(214 176 95 / 12%);
  border-radius: 12px;
}

.form-header {
  margin-bottom: 28px;
}

.form-header h2 {
  margin: 0 0 6px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 20px;
  font-weight: 700;
  color: #f7edd5;
}

.form-header p {
  margin: 0;
  color: #7a6e58;
  font-size: 14px;
}

.cert-form {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cert-form :deep(.el-form-item__label) {
  color: #b9ab90;
  font-weight: 500;
  font-size: 14px;
}

.custom-input :deep(.el-input__wrapper) {
  background: rgb(255 255 255 / 4%);
  border: 1px solid rgb(214 176 95 / 14%);
  border-radius: 8px;
  box-shadow: none;
  transition: all 250ms ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: rgb(214 176 95 / 30%);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #d6b05f;
  box-shadow: 0 0 0 2px rgb(214 176 95 / 10%);
}

.custom-input :deep(.el-input__inner) {
  color: #f7edd5;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: #5a5040;
}

.form-tip {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-top: 8px;
  color: #6a5e4e;
  font-size: 12px;
  line-height: 1.5;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.submit-btn {
  background: linear-gradient(135deg, #d6b05f, #c89a4a) !important;
  border: none !important;
  color: #1a1a1a !important;
  font-weight: 600;
  height: 44px;
  padding: 0 32px;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #e8c16d, #d6b05f) !important;
  box-shadow: 0 4px 20px rgb(214 176 95 / 25%);
}

.cancel-btn {
  background: rgb(255 255 255 / 5%) !important;
  border: 1px solid rgb(214 176 95 / 14%) !important;
  color: #8a7b68 !important;
  height: 44px;
  padding: 0 24px;
}
</style>