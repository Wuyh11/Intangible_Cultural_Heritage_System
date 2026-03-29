import request from '@/utils/request'
import type { StatsOverview } from '@/types/models'

export function getPublicStatsOverview() {
  return request.get<never, StatsOverview>('/api/public/stats/overview')
}

export function getAdminStatsOverview() {
  return request.get<never, StatsOverview>('/api/admin/stats/overview')
}

export function downloadStats() {
  return request.get<Blob, Blob>('/api/admin/export/stats', { responseType: 'blob' })
}
