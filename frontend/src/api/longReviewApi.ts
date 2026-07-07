import request from './request'

// ============================================================
// 长评模块 — 接口类型定义
// ============================================================

export interface LongReviewVO {
  id: number
  movieId: number
  movieTitle: string
  userId: number
  authorNickname: string
  authorAvatar: string | null
  title: string
  contentMd: string
  coverUrl: string | null
  status: string
  viewCount: number
  likeCount: number
  favoriteCount: number
  replyCount: number
  isFeatured: boolean
  featuredAt: string | null
  liked: boolean
  favorited: boolean
  images: string[]
  createdAt: string
  updatedAt: string
}

export interface FeaturedReviewVO {
  id: number
  movieId: number
  movieTitle: string
  moviePoster: string | null
  userId: number
  authorNickname: string
  authorAvatar: string | null
  title: string
  summary: string
  coverUrl: string | null
  likeCount: number
  favoriteCount: number
  replyCount: number
  viewCount: number
  hotScore: number
  createdAt: string
}

export interface ReviewReplyVO {
  id: number
  reviewId: number
  userId: number
  userNickname: string
  userAvatar: string | null
  parentId: number | null
  content: string
  status: string
  likeCount: number
  reportCount: number
  liked: boolean
  children: ReviewReplyVO[]
  createdAt: string
}

export interface CreateReviewRequest {
  movieId: number
  title: string
  contentMd: string
  coverUrl?: string
  images?: string[]
}

export interface UpdateReviewRequest {
  title: string
  contentMd: string
  coverUrl?: string
  images?: string[]
}

export interface CreateReplyRequest {
  parentId?: number | null
  content: string
}

export interface PageData<T> {
  list: T[]
  current: number
  size: number
  total: number
}

// ============================================================
// 用户端 API
// ============================================================

/** 获取长评列表（支持按电影ID筛选、排序、分页） */
export function getReviewList(params: {
  movieId?: number
  sortBy?: string
  page?: number
  pageSize?: number
}): Promise<{ data: { data: PageData<LongReviewVO> } }> {
  return request.get('/long-reviews', { params })
}

/** 获取精选长评列表（首页"优质长评"模块使用） */
export function getFeaturedReviews(params: {
  page?: number
  pageSize?: number
}): Promise<{ data: { data: PageData<FeaturedReviewVO> } }> {
  return request.get('/long-reviews/featured', { params })
}

/** 获取长评详情 */
export function getReviewDetail(reviewId: number): Promise<{ data: { data: LongReviewVO } }> {
  return request.get(`/long-reviews/${reviewId}`)
}

/** 发布长评 */
export function createReview(data: CreateReviewRequest): Promise<{ data: { data: { id: number } } }> {
  return request.post('/long-reviews', data)
}

/** 修改长评（仅 DRAFT / REJECTED 状态可修改） */
export function updateReview(reviewId: number, data: UpdateReviewRequest): Promise<{ data: unknown }> {
  return request.put(`/long-reviews/${reviewId}`, data)
}

/** 点赞 / 取消点赞（Toggle 模式） */
export function likeReview(reviewId: number): Promise<{ data: unknown }> {
  return request.post(`/long-reviews/${reviewId}/like`)
}

/** 收藏 / 取消收藏（Toggle 模式） */
export function favoriteReview(reviewId: number): Promise<{ data: unknown }> {
  return request.post(`/long-reviews/${reviewId}/favorite`)
}

/** 举报长评 */
export function reportReview(reviewId: number, reason: string): Promise<{ data: unknown }> {
  return request.post(`/long-reviews/${reviewId}/report`, { reason })
}

/** 获取长评的回复列表（一级回复带子回复） */
export function getReplies(reviewId: number, params: {
  page?: number
  pageSize?: number
}): Promise<{ data: { data: PageData<ReviewReplyVO> } }> {
  return request.get(`/long-reviews/${reviewId}/replies`, { params })
}

/** 发表回复 */
export function createReply(reviewId: number, data: CreateReplyRequest): Promise<{ data: { data: { id: number } } }> {
  return request.post(`/long-reviews/${reviewId}/replies`, data)
}

/** 点赞回复 */
export function likeReply(replyId: number): Promise<{ data: unknown }> {
  return request.post(`/long-reviews/replies/${replyId}/like`)
}

// ============================================================
// 管理员 API
// ============================================================

/** 设为精选 */
export function setFeatured(reviewId: number): Promise<{ data: unknown }> {
  return request.post(`/admin/long-reviews/${reviewId}/feature`)
}

/** 取消精选 */
export function unsetFeatured(reviewId: number): Promise<{ data: unknown }> {
  return request.delete(`/admin/long-reviews/${reviewId}/feature`)
}

/** 下架长评（status → OFFLINE） */
export function hideReview(reviewId: number): Promise<{ data: unknown }> {
  return request.put(`/admin/long-reviews/${reviewId}/hide`)
}

/** 上架长评（status → ONLINE） */
export function unhideReview(reviewId: number): Promise<{ data: unknown }> {
  return request.put(`/admin/long-reviews/${reviewId}/unhide`)
}

/** 软删除长评（status → DELETED） */
export function deleteReview(reviewId: number): Promise<{ data: unknown }> {
  return request.delete(`/admin/long-reviews/${reviewId}`)
}