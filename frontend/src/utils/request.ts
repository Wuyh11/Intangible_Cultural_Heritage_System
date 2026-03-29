import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types/common'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('ICH_ADMIN_TOKEN')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const payload = response.data as ApiResponse<unknown>
    if (payload && typeof payload.code === 'number') {
      if (payload.code !== 200) {
        ElMessage.error(payload.message || '请求失败')
        return Promise.reject(new Error(payload.message || '请求失败'))
      }
      return payload.data
    }
    return response.data
  },
  (error) => {
    const message = error?.response?.data?.message || error.message || '网络异常'
    ElMessage.error(message)
    if (error?.response?.status === 401) {
      localStorage.removeItem('ICH_ADMIN_TOKEN')
      localStorage.removeItem('ICH_ADMIN_PROFILE')
      if (window.location.pathname.startsWith('/admin')) {
        window.location.href = '/admin/login'
      }
    }
    return Promise.reject(error)
  }
)

export default request
