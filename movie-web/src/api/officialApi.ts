import request from './request'

export const officialApi = {
  getCertification() {
    return request.get<unknown, Record<string, unknown>>('/official/certification')
  },
  submitCertification(data: Record<string, unknown>) {
    return request.post<unknown, void>('/official/certification', data)
  },
  pageSubmissions(params: Record<string, unknown>) {
    return request.get<unknown, Record<string, unknown>>('/official/submissions', { params })
  },
}

