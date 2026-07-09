<template>
  <div class="page merchandise-list-page">
    <div class="page-header">
      <h1>电影周边商城</h1>
    </div>

    <!-- Filters -->
    <div class="filter-bar">
      <div class="filter-row">
        <el-input
          v-model="keyword"
          placeholder="搜索周边商品..."
          class="search-input"
          clearable
          @keyup.enter="search"
          @clear="search"
        />
        <el-select v-model="productType" placeholder="商品类型" clearable @change="search">
          <el-option label="全部" value="" />
          <el-option label="海报" value="海报" />
          <el-option label="模型" value="模型" />
          <el-option label="文创" value="文创" />
          <el-option label="数码" value="数码" />
          <el-option label="服饰" value="服饰" />
          <el-option label="其他" value="其他" />
        </el-select>
        <el-select v-model="priceRange" placeholder="价格区间" clearable @change="search">
          <el-option label="全部" value="" />
          <el-option label="0-50" value="0-50" />
          <el-option label="50-100" value="50-100" />
          <el-option label="100-200" value="100-200" />
          <el-option label="200+" value="200+" />
        </el-select>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else-if="products.length === 0" class="empty-state">
      <el-empty description="暂无周边商品" />
    </div>

    <div v-else class="product-grid">
      <div
        v-for="item in products"
        :key="item.id"
        class="product-card"
        @click="goBuy(item.externalUrl)"
      >
        <div class="product-image">
          <img :src="item.imageUrl" :alt="item.name" />
          <span v-if="item.productType" class="product-type-tag">{{ item.productType }}</span>
        </div>
        <div class="product-body">
          <h3 class="product-name">{{ item.name }}</h3>
          <div class="product-meta">
            <span class="product-price">¥{{ item.price }}</span>
            <span class="product-platform">{{ item.platform }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="total > pageSize" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="goPage"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { merchandiseApi, type MerchandiseItem } from '@/api/merchandiseApi'

const products = ref<MerchandiseItem[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 12
const total = ref(0)
const keyword = ref('')
const productType = ref('')
const priceRange = ref('')

async function fetchProducts() {
  loading.value = true
  try {
    const res = await merchandiseApi.pageProducts({
      page: page.value,
      pageSize,
      keyword: keyword.value || undefined,
      productType: productType.value || undefined,
      priceRange: priceRange.value || undefined,
    })
    products.value = res.list || []
    total.value = res.total || 0
  } catch {
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  fetchProducts()
}

function goPage(p: number) {
  page.value = p
  fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goBuy(url: string) {
  window.open(url, '_blank')
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.merchandise-list-page {
  min-height: calc(100vh - 64px);
  padding: 36px max(22px, calc((100vw - 1280px) / 2)) 72px;
  color: #f7edd5;
  background: #050505;
}

.page-header h1 {
  margin: 0 0 24px;
  color: #e8c16d;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 28px;
  font-weight: 800;
  text-shadow: 0 0 16px rgb(214 176 95 / 32%);
}

.filter-bar {
  margin-bottom: 28px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.search-input {
  width: 240px;
}

/* 金色搜索框 / 下拉框统一样式 */
.filter-row :deep(.el-input__wrapper) {
  background: rgb(214 176 95 / 6%) !important;
  border: 1px solid rgb(214 176 95 / 32%);
  border-radius: 6px;
  box-shadow: none !important;
  transition: border-color 180ms ease, box-shadow 180ms ease;
}

.filter-row :deep(.el-input__wrapper:hover) {
  border-color: rgb(214 176 95 / 52%);
}

.filter-row :deep(.el-input__wrapper.is-focus) {
  border-color: #d6b05f !important;
  box-shadow: 0 0 0 1px #d6b05f inset !important;
}

.filter-row :deep(.el-select__wrapper) {
  background: rgb(214 176 95 / 6%) !important;
  border: 1px solid rgb(214 176 95 / 32%);
  border-radius: 6px;
  box-shadow: none !important;
}

.filter-row :deep(.el-select__wrapper:hover) {
  border-color: rgb(214 176 95 / 52%);
}

.filter-row :deep(.el-select__wrapper.is-focused) {
  border-color: #d6b05f !important;
  box-shadow: 0 0 0 1px #d6b05f inset !important;
}

.filter-row :deep(.el-input__inner),
.filter-row :deep(.el-select__placeholder),
.filter-row :deep(.el-select__placeholder.is-transparent) {
  color: #f7edd5;
}

.filter-row :deep(.el-input__inner::placeholder) {
  color: #f7edd5;
}

.filter-row :deep(.el-select__caret),
.filter-row :deep(.el-select .el-input__suffix .el-icon),
.filter-row :deep(.el-select .el-select__suffix .el-icon) {
  color: #d6b05f;
}

.filter-row :deep(.el-select__selected-item) {
  color: #f7edd5;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 80px 0;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  overflow: hidden;
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  cursor: pointer;
  transition: border-color 180ms ease, transform 180ms ease, box-shadow 180ms ease;
}

.product-card:hover {
  border-color: rgb(214 176 95 / 52%);
  box-shadow: 0 8px 28px rgb(214 176 95 / 14%);
  transform: translateY(-2px);
}

.product-image {
  position: relative;
  height: 200px;
  background: #111;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-type-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 2px 10px;
  border-radius: 999px;
  background: rgb(214 176 95 / 82%);
  color: #050505;
  font-size: 12px;
  font-weight: 600;
}

.product-body {
  padding: 14px 16px 16px;
}

.product-name {
  margin: 0 0 10px;
  color: #fff6df;
  font-size: 15px;
  font-weight: 500;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #e8c16d;
}

.product-platform {
  font-size: 12px;
  color: #8a7b60;
  padding: 2px 8px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 4px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-row {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
