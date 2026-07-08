import request from './request'

export interface VideoItem {
  id: number
  movieId: number
  title: string
  platform: string
  externalUrl: string
  coverUrl: string
  description: string
  status: string
  heatScore: number
  clickCount: number
  createdAt: string
}

export interface PageResult<T> {
  list: T[]
  page: number
  pageSize: number
  total: number
}

export const videoApi = {
  getByMovie(movieId: number | string) {
    return request.get<unknown, VideoItem[]>(`/videos/movie/${movieId}`)
  },

  getDetail(id: number | string) {
    return request.get<unknown, VideoItem>(`/videos/${id}`)
  },

  recordClick(id: number | string) {
    return request.post<unknown, void>(`/videos/${id}/click`)
  },

  pageVideos(params: {
    page?: number
    pageSize?: number
    status?: string
    keyword?: string
    movieId?: number
  }) {
    return request.get<unknown, PageResult<VideoItem>>('/admin/videos', { params })
  },

  approve(id: number | string) {
    return request.post<unknown, void>(`/admin/videos/${id}/approve`)
  },

  reject(id: number | string, rejectReason: string) {
    return request.post<unknown, void>(`/admin/videos/${id}/reject`, { rejectReason })
  },

  publish(id: number | string) {
    return request.post<unknown, void>(`/admin/videos/${id}/publish`)
  },

  offline(id: number | string, reason: string) {
    return request.post<unknown, void>(`/admin/videos/${id}/offline`, { reason })
  },

  submit(data: {
    movieId: number
    title: string
    platform: string
    externalUrl: string
    coverUrl?: string
    description?: string
  }) {
    return request.post<unknown, void>('/official/videos', data)
  },
}
