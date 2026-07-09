import request from './request'

export interface MerchandiseItem {
  id: number
  movieId: number
  name: string
  imageUrl: string
  productType: string
  price: number
  platform: string
  externalUrl: string
  description: string
  status: string
  clickCount: number
  createdAt: string
}

export interface PageResult<T> {
  list: T[]
  page: number
  pageSize: number
  total: number
}

export const merchandiseApi = {
  pageProducts(params: {
    page?: number
    pageSize?: number
    keyword?: string
    productType?: string
    movieId?: number
    priceRange?: string
  }) {
    return request.get<unknown, PageResult<MerchandiseItem>>('/merchandise', { params })
  },

  getDetail(id: number | string) {
    return request.get<unknown, MerchandiseItem>(`/merchandise/${id}`)
  },

  getByMovie(movieId: number | string) {
    return request.get<unknown, MerchandiseItem[]>(`/merchandise/movie/${movieId}`)
  },

  pageAdminProducts(params: {
    page?: number
    pageSize?: number
    status?: string
    keyword?: string
    productType?: string
  }) {
    return request.get<unknown, PageResult<MerchandiseItem>>('/admin/merchandise', { params })
  },

  approve(id: number | string) {
    return request.post<unknown, void>(`/admin/merchandise/${id}/approve`)
  },

  reject(id: number | string, rejectReason: string) {
    return request.post<unknown, void>(`/admin/merchandise/${id}/reject`, { rejectReason })
  },

  publish(id: number | string) {
    return request.post<unknown, void>(`/admin/merchandise/${id}/publish`)
  },

  offline(id: number | string, reason: string) {
    return request.post<unknown, void>(`/admin/merchandise/${id}/offline`, { reason })
  },

  submit(data: {
    movieId: number
    name: string
    imageUrl: string
    productType: string
    price: number
    platform: string
    externalUrl: string
    description?: string
  }) {
    return request.post<unknown, void>('/official/merchandise', data)
  },
}
