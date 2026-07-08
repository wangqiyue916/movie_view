<template>
  <div class="page merchandise-detail-page">
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else-if="!product" class="empty-state">
      <el-empty description="商品不存在或已下架" />
      <router-link to="/merchandise" class="back-link">← 返回周边商城</router-link>
    </div>

    <template v-else>
      <router-link to="/merchandise" class="back-link">← 返回周边商城</router-link>

      <div class="detail-layout">
        <div class="detail-image">
          <img :src="product.imageUrl" :alt="product.name" />
        </div>
        <div class="detail-info">
          <span v-if="product.productType" class="type-tag">{{ product.productType }}</span>
          <h1 class="product-name">{{ product.name }}</h1>
          <div class="price-row">
            <span class="product-price">¥{{ product.price }}</span>
            <span v-if="product.platform" class="platform-badge">{{ product.platform }}</span>
          </div>
          <p v-if="product.description" class="product-desc">{{ product.description }}</p>
          <div class="action-row">
            <el-button
              type="warning"
              size="large"
              @click="handleBuy"
              :disabled="!product.externalUrl"
            >
              去购买
            </el-button>
          </div>
        </div>
      </div>

      <!-- Related Merchandise -->
      <div v-if="relatedProducts.length > 0" class="related-section">
        <h2 class="section-title">同系列其他周边</h2>
        <div class="related-grid">
          <div
            v-for="item in relatedProducts"
            :key="item.id"
            class="related-card"
            @click="goDetail(item.id)"
          >
            <div class="related-image">
              <img :src="item.imageUrl" :alt="item.name" />
            </div>
            <div class="related-body">
              <h4>{{ item.name }}</h4>
              <span class="related-price">¥{{ item.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { merchandiseApi, type MerchandiseItem } from '@/api/merchandiseApi'

const route = useRoute()
const router = useRouter()

const product = ref<MerchandiseItem | null>(null)
const relatedProducts = ref<MerchandiseItem[]>([])
const loading = ref(true)

function handleBuy() {
  if (product.value?.externalUrl) {
    window.open(product.value.externalUrl, '_blank')
  }
}

function goDetail(id: number) {
  router.push(`/merchandise/${id}`)
}

async function fetchProduct() {
  const id = route.params.id as string
  try {
    const res = await merchandiseApi.getDetail(id)
    product.value = res

    if (res?.movieId) {
      try {
        const related = await merchandiseApi.getByMovie(res.movieId)
        relatedProducts.value = (related || []).filter((r) => r.id !== res.id)
      } catch {
        relatedProducts.value = []
      }
    }
  } catch {
    product.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchProduct()
})
</script>

<style scoped>
.merchandise-detail-page {
  min-height: calc(100vh - 64px);
  padding: 36px max(22px, calc((100vw - 1280px) / 2)) 72px;
  color: #f7edd5;
  background: #050505;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 120px 20px;
}

.empty-state .back-link {
  display: inline-block;
  margin-top: 16px;
  color: #e8c16d;
  font-size: 14px;
}

.back-link {
  display: inline-block;
  margin-bottom: 28px;
  color: #e8c16d;
  font-size: 14px;
  transition: color 180ms;
}

.back-link:hover {
  color: #f3d58c;
}

.detail-layout {
  display: flex;
  gap: 48px;
  margin-bottom: 60px;
}

.detail-image {
  flex-shrink: 0;
  width: 420px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgb(214 176 95 / 22%);
  background: #111;
}

.detail-image img {
  width: 100%;
  display: block;
}

.detail-info {
  flex: 1;
  padding-top: 8px;
}

.type-tag {
  display: inline-block;
  padding: 3px 14px;
  margin-bottom: 12px;
  border-radius: 999px;
  background: rgb(214 176 95 / 82%);
  color: #050505;
  font-size: 12px;
  font-weight: 600;
}

.product-name {
  margin: 0 0 20px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 28px;
  font-weight: 800;
  color: #fff8e6;
}

.price-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.product-price {
  font-size: 32px;
  font-weight: 700;
  color: #e8c16d;
}

.platform-badge {
  padding: 4px 14px;
  border: 1px solid rgb(214 176 95 / 28%);
  border-radius: 4px;
  color: #c6b78f;
  font-size: 13px;
}

.product-desc {
  margin: 0 0 32px;
  color: #d8c69b;
  font-size: 15px;
  line-height: 1.8;
  max-width: 600px;
}

.related-section {
  padding-top: 36px;
  border-top: 1px solid rgb(214 176 95 / 18%);
}

.section-title {
  margin: 0 0 20px;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 20px;
  font-weight: 700;
  color: #e8c16d;
  padding-left: 14px;
  border-left: 3px solid #d6b05f;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.related-card {
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  overflow: hidden;
  background: rgb(255 255 255 / 3%);
  cursor: pointer;
  transition: border-color 180ms, transform 180ms;
}

.related-card:hover {
  border-color: rgb(214 176 95 / 52%);
  transform: translateY(-2px);
}

.related-image {
  height: 160px;
  background: #111;
}

.related-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.related-body {
  padding: 10px 12px;
}

.related-body h4 {
  margin: 0 0 6px;
  font-size: 14px;
  font-weight: 500;
  color: #fff6df;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
}

.related-price {
  font-size: 14px;
  font-weight: 700;
  color: #e8c16d;
}

@media (max-width: 768px) {
  .detail-layout {
    flex-direction: column;
    gap: 28px;
  }

  .detail-image {
    width: 100%;
  }

  .product-name {
    font-size: 24px;
  }
}
</style>
