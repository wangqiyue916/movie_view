import request from './request'

export const auditApi = {
  pageAudits(params: Record<string, unknown>) {
    return request.get<unknown, Record<string, unknown>>('/admin/audits', { params })
  },
  approve(targetType: string, targetId: number | string) {
    return request.post<unknown, void>(`/admin/audits/${targetType}/${targetId}/approve`)
  },
  reject(targetType: string, targetId: number | string, rejectReason: string) {
    return request.post<unknown, void>(`/admin/audits/${targetType}/${targetId}/reject`, { rejectReason })
  },
}

