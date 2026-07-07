import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResponse<unknown>
    if (result.code === 0) {
      return result.data as never
    }
    if (result.code === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    if (result.code === 403) {
      router.push('/403')
    }
    ElMessage.error(result.message || '请求失败')
    return Promise.reject(result)
  },
  (error) => {
    ElMessage.error(error?.message || '网络异常')
    return Promise.reject(error)
  },
)

export default request
