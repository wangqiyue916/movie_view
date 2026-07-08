<template>
  <div class="comment-section">
    <SectionHeading title="短评" />

    <!-- Comment form -->
    <div v-if="userStore.isLogin && props.hasRated" class="comment-form">
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="2"
        maxlength="500"
        show-word-limit
        placeholder="写下你对这部电影的短评..."
      />
      <el-button class="post-btn" :disabled="!newComment.trim() || submitting" @click="postComment">
        {{ submitting ? '提交中...' : '发布短评' }}
      </el-button>
    </div>
    <div v-else-if="userStore.isLogin" class="rating-required">
      请先完成电影总评分，再发表短评或长评。
    </div>
    <div v-else class="login-hint">
      <el-link href="/login">登录</el-link> 后即可发表短评
    </div>

    <!-- List -->
    <div v-loading="loading" class="comment-list-wrap">
      <div v-if="comments.length === 0 && !loading" class="empty-hint">暂无短评，来写第一条吧</div>
      <ShortCommentItem
        v-for="c in comments"
        :key="c.id"
        :comment="c"
        @like="handleLike(c)"
        @report="handleReport(c)"
      />
    </div>

    <!-- Pagination -->
    <div v-if="total > pageSize" class="pagination-wrap">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        small
        @current-change="fetchComments"
      />
    </div>

    <!-- Report dialog -->
    <el-dialog v-model="reportVisible" title="举报短评" width="380px">
      <el-input v-model="reportReason" type="textarea" :rows="2" maxlength="255" placeholder="请填写举报原因" />
      <template #footer>
        <el-button @click="reportVisible = false">取消</el-button>
        <el-button type="danger" :disabled="!reportReason.trim()" @click="submitReport">确认举报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { movieApi, type ShortCommentItem as ShortCommentItemType } from '@/api/movieApi'
import { ElMessage } from 'element-plus'
import SectionHeading from '@/components/movie/SectionHeading.vue'
import ShortCommentItem from '@/components/comment/ShortCommentItem.vue'

const props = defineProps<{ movieId: number; hasRated?: boolean }>()
const userStore = useUserStore()

const comments = ref<ShortCommentItemType[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10

const newComment = ref('')
const submitting = ref(false)

const reportVisible = ref(false)
const reportTarget = ref<ShortCommentItemType | null>(null)
const reportReason = ref('')

async function fetchComments() {
  loading.value = true
  try {
    const res = await movieApi.listShortComments(props.movieId, currentPage.value, pageSize)
    comments.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

async function postComment() {
  if (!newComment.value.trim()) return
  if (!props.hasRated) {
    ElMessage.warning('请先完成电影评分')
    return
  }
  submitting.value = true
  try {
    await movieApi.postShortComment(props.movieId, { content: newComment.value.trim() })
    newComment.value = ''
    ElMessage.success('短评发布成功')
    currentPage.value = 1
    await fetchComments()
  } catch {
    ElMessage.error('发布失败')
  } finally {
    submitting.value = false
  }
}

async function handleLike(comment: ShortCommentItemType) {
  try {
    if (comment.likedByMe) {
      await movieApi.unlikeComment(comment.id)
      comment.likeCount--
      comment.likedByMe = false
    } else {
      await movieApi.likeComment(comment.id)
      comment.likeCount++
      comment.likedByMe = true
    }
  } catch {
    // ignore
  }
}

function handleReport(comment: ShortCommentItemType) {
  reportTarget.value = comment
  reportReason.value = ''
  reportVisible.value = true
}

async function submitReport() {
  if (!reportTarget.value || !reportReason.value.trim()) return
  try {
    await movieApi.reportComment(reportTarget.value.id, reportReason.value.trim())
    ElMessage.success('举报已提交')
    reportVisible.value = false
  } catch {
    ElMessage.error('举报失败')
  }
}

onMounted(() => fetchComments())
</script>

<style scoped>
.comment-section {
  /* container */
}

.comment-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.post-btn {
  align-self: flex-end;
  background: linear-gradient(135deg, #c9a035, #d6b05f);
  color: #050505;
  font-weight: 700;
  border: none;
}

.login-hint,
.rating-required {
  font-size: 13px;
  color: #b9ab90;
  margin-bottom: 16px;
}

.rating-required {
  padding: 12px 14px;
  border: 1px solid rgb(214 176 95 / 16%);
  border-radius: 6px;
  background: rgb(214 176 95 / 7%);
  color: #d8c69b;
}

.login-hint a {
  color: #e8c16d;
}

.comment-list-wrap {
  min-height: 100px;
}

.empty-hint {
  font-size: 14px;
  color: #6b5e45;
  text-align: center;
  padding: 32px 0;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination .el-pager li) {
  background: rgb(255 255 255 / 5%) !important;
  color: #cbb98f !important;
  border: 1px solid rgb(214 176 95 / 14%) !important;
}

:deep(.el-pagination .el-pager li.is-active) {
  background: #d6b05f !important;
  color: #050505 !important;
}

:deep(.el-textarea__inner) {
  background: rgb(255 255 255 / 5%);
  border: 1px solid rgb(214 176 95 / 18%);
  color: #d8c69b;
}

:deep(.el-textarea__inner::placeholder) {
  color: #6b5e45;
}
</style>
