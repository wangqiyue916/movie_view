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
      // 不在登录页时才跳转，避免吞掉登录页的错误提示
      if (router.currentRoute.value.name !== 'LoginPage') {
        router.push('/login')
      }
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(result)
    }
    if (result.code === 403) {
      router.push('/403')
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(result)
    }
    // 其他业务错误（400/404/409/500）不在拦截器中弹 toast，
    // 由调用方 .catch() 自行决定是否提示，避免辅助接口失败污染用户体验
    return Promise.reject(result)
  },
  (error) => {
    // Axios 网络/超时错误才有 message，避免弹出两条相同消息
    if (error?.response?.status === 401) {
      localStorage.removeItem('token')
      if (router.currentRoute.value.name !== 'LoginPage') {
        router.push('/login')
      }
    } else if (error?.response?.status !== 403) {
      ElMessage.error(error?.message || '网络异常')
    }
    return Promise.reject(error)
  },
)

export default request
