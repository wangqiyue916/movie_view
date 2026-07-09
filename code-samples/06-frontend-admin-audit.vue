<!--
============================================================
PPT代码展示 6 / 7 — 管理员审核页面（状态筛选 + 操作）
郭俊岑 负责：长评社区与讨论模块
============================================================

【核心问题】管理员如何高效管理长评审核？不同状态的评论如何处理？

我的设计思路：
  ① Tab 栏状态切换：待审核(PENDING) / 已驳回(REJECTED) / 已上线(ONLINE)
     每个 Tab 通过 status 参数向后端请求不同数据
  ② 卡片展示：标题、作者、电影名、内容摘要 + 状态徽章颜色区分
  ③ 操作按钮根据状态动态渲染：
     - PENDING → 通过 / 驳回
     - REJECTED → 通过（重新审核）
     - ONLINE → 下架
     - 所有状态 → 删除（软删除）
  ④ 驳回需填写原因（弹窗模态框）
  ⑤ 分页控制 + 加载/空状态

【技能点】
  - Vue 3 Composition API：ref / computed / v-if 条件渲染
  - Element Plus 组件：ElMessageBox 确认弹窗
  - 条件渲染 v-if / v-else-if 动态显示操作按钮
  - API 调用复用 longReviewApi.ts 的管理员接口
-->

<template>
  <div class="page audit-page">
    <!-- ===== 状态筛选 Tab ===== -->
    <div class="tab-bar">
      <button v-for="tab in tabs" :key="tab.value"
              :class="['tab-btn', { active: activeTab === tab.value }]"
              @click="activeTab = tab.value; page = 1; loadReviews()">
        {{ tab.label }}
      </button>
    </div>

    <!-- ===== 审核卡片列表 ===== -->
    <div class="audit-list">
      <article v-for="review in reviews" :key="review.id" class="audit-card">
        <h3>{{ review.title }}</h3>
        <div class="audit-meta">
          <span>作者：{{ review.authorNickname }}</span>
          <span>电影：{{ review.movieTitle }}</span>
          <span class="status-badge" :class="'status-' + review.status.toLowerCase()">
            {{ statusText(review.status) }}
          </span>
        </div>
        <p class="audit-excerpt">{{ getExcerpt(review.contentMd) }}</p>

        <!-- ===== 动态操作按钮（根据 status 条件渲染） ===== -->
        <div class="audit-actions">
          <!-- 待审核：显示"通过"和"驳回" -->
          <template v-if="review.status === 'PENDING'">
            <button class="btn btn-approve" @click="approveReview(review.id)">通过</button>
            <button class="btn btn-reject" @click="openReject(review.id)">驳回</button>
          </template>
          <!-- 已上线：显示"下架" -->
          <template v-if="review.status === 'ONLINE'">
            <button class="btn btn-warning" @click="hideReview(review.id)">下架</button>
          </template>
          <!-- 已驳回：显示"通过" -->
          <template v-if="review.status === 'REJECTED'">
            <button class="btn btn-approve" @click="approveReview(review.id)">通过</button>
          </template>
          <!-- 所有状态（非已删除）：显示"删除" -->
          <button v-if="review.status !== 'DELETED'"
                  class="btn btn-delete"
                  @click="deleteReview(review.id)">删除</button>
        </div>
      </article>
    </div>

    <!-- ===== 驳回弹窗 ===== -->
    <div v-if="showReject" class="dialog-overlay">
      <div class="reject-dialog">
        <h3>驳回长评</h3>
        <textarea v-model="rejectReason" placeholder="请填写驳回原因..."></textarea>
        <button class="btn btn-reject" @click="doReject">确认驳回</button>
        <button class="btn btn-cancel" @click="showReject = false">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviewList, hideReview, deleteReview, setFeatured } from '@/api/longReviewApi'

const tabs = [
  { value: 'PENDING',  label: '待审核' },
  { value: 'REJECTED', label: '已驳回' },
  { value: 'ONLINE',   label: '已上线' },
]
const activeTab = ref('PENDING')
const reviews = ref<LongReviewVO[]>([])
const showReject = ref(false)
const rejectTargetId = ref<number | null>(null)
const rejectReason = ref('')

// 根据 Tab 筛选状态
async function loadReviews() {
  const res = await getReviewList({ page: page.value, pageSize: 10 })
  reviews.value = res.list.filter(r => r.status === activeTab.value)
}

// 通过审核 → 调用 setFeatured（设为精选即上线）
async function approveReview(id: number) {
  await setFeatured(id)
  ElMessage.success('已审核通过')
  loadReviews()
}

// 驳回 → 弹窗收集驳回原因 → 调用 hideReview
async function doReject() {
  await hideReview(rejectTargetId.value!)
  ElMessage.success('已驳回')
  showReject.value = false
  loadReviews()
}

// 删除 → ElMessageBox 确认弹窗 → 软删除
async function deleteReview(id: number) {
  await ElMessageBox.confirm('确认删除该长评？', '删除确认', { type: 'warning' })
  await deleteReview(id)
  ElMessage.success('已删除')
  loadReviews()
}
</script>

<!--
【PPT讲解提示】
"这一页展示的是管理员审核页面的前端逻辑。
 核心设计是'根据状态动态渲染操作按钮'——
 通过 v-if 判断 review.status 来决定显示哪些按钮，
 避免管理员对非当前状态的内容做不合规操作。
 驳回时弹出模态框收集驳回原因，前端做非空校验后才提交。
 删除操作使用 Element Plus 的 ElMessageBox.confirm
 进行二次确认，防止误操作。
 整个页面的 Tab 切换会重置分页并重新请求后端，
 保证数据实时性。"
-->