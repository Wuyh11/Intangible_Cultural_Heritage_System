import request from '@/utils/request'
import type { AdminUserItem, LogPage } from '@/types/models'

export function getAdminUsers() {
  return request.get<never, AdminUserItem[]>('/api/admin/users')
}

export function saveAdminUser(payload: Record<string, unknown>) {
  return payload.id
    ? request.put<never, AdminUserItem>(`/api/admin/users/${payload.id}`, payload)
    : request.post<never, AdminUserItem>('/api/admin/users', payload)
}

export function deleteAdminUser(id: number) {
  return request.delete<never, void>(`/api/admin/users/${id}`)
}

export function getOperationLogs(params: Record<string, unknown>) {
  return request.get<never, LogPage>('/api/admin/logs', { params })
}
