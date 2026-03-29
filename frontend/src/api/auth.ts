import request from '@/utils/request'
import type { LoginResult } from '@/types/models'

export function loginAdmin(payload: { username: string; password: string }) {
  return request.post<never, LoginResult>('/api/admin/auth/login', payload)
}
