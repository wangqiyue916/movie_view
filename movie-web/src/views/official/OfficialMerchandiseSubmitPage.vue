<template>
  <div class="page official-merchandise-submit-page">
    <div class="form-container">
      <h1>提交周边商品</h1>

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

        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="商品图片URL" prop="imageUrl">
          <el-input v-model="formData.imageUrl" placeholder="请输入商品图片URL" />
        </el-form-item>

        <el-form-item label="商品类型" prop="productType">
          <el-select v-model="formData.productType" placeholder="请选择商品类型">
            <el-option label="海报" value="海报" />
            <el-option label="模型" value="模型" />
            <el-option label="文创" value="文创" />
            <el-option label="服饰" value="服饰" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" placeholder="请输入价格" />
        </el-form-item>

        <el-form-item label="平台" prop="platform">
          <el-select v-model="formData.platform" placeholder="请选择购买平台">
            <el-option label="淘宝" value="淘宝" />
            <el-option label="京东" value="京东" />
            <el-option label="拼多多" value="拼多多" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="外部链接" prop="externalUrl">
          <el-input v-model="formData.externalUrl" placeholder="请输入购买链接URL" />
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述（选填）"
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
import { merchandiseApi } from '@/api/merchandiseApi'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const formData = reactive({
  movieId: undefined as number | undefined,
  name: '',
  imageUrl: '',
  productType: '',
  price: undefined as number | undefined,
  platform: '',
  externalUrl: '',
  description: '',
})

const rules: FormRules = {
  movieId: [{ required: true, message: '请输入关联电影ID', trigger: 'blur' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入商品图片URL', trigger: 'blur' }],
  productType: [{ required: true, message: '请选择商品类型', trigger: 'change' }],
  externalUrl: [{ required: true, message: '请输入购买链接', trigger: 'blur' }],
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await merchandiseApi.submit({
      movieId: formData.movieId!,
      name: formData.name,
      imageUrl: formData.imageUrl,
      productType: formData.productType,
      price: formData.price ?? 0,
      platform: formData.platform,
      externalUrl: formData.externalUrl,
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
.official-merchandise-submit-page {
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
