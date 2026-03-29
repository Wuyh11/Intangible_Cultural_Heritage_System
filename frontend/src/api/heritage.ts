import request from '@/utils/request'
import type {
  CategoryItem,
  FilterPayload,
  InheritorItem,
  InheritorPage,
  MapPayload,
  ProjectDetailItem,
  ProjectPage,
  ProjectListItem,
  RegionItem
} from '@/types/models'

export function getPublicProjects(params: Record<string, unknown>) {
  return request.get<never, ProjectPage>('/api/public/projects', { params })
}

export function getProjectDetail(id: number | string) {
  return request.get<never, ProjectDetailItem>(`/api/public/projects/${id}`)
}

export function getProjectFilters() {
  return request.get<never, FilterPayload>('/api/public/projects/filters')
}

export function getMapPoints(params: Record<string, unknown>) {
  return request.get<never, MapPayload>('/api/public/map/points', { params })
}

export function getAdminProjects(params: Record<string, unknown>) {
  return request.get<never, ProjectPage>('/api/admin/projects', { params })
}

export function getAdminProjectDetail(id: number) {
  return request.get<never, ProjectDetailItem>(`/api/admin/projects/${id}`)
}

export function saveProject(payload: Record<string, unknown>) {
  return payload.id
    ? request.put<never, ProjectDetailItem>(`/api/admin/projects/${payload.id}`, payload)
    : request.post<never, ProjectDetailItem>('/api/admin/projects', payload)
}

export function deleteProject(id: number) {
  return request.delete<never, void>(`/api/admin/projects/${id}`)
}

export function uploadProjectImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<never, { url: string }>('/api/admin/projects/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getInheritors(params: Record<string, unknown>) {
  return request.get<never, InheritorPage>('/api/admin/inheritors', { params })
}

export function saveInheritor(payload: Record<string, unknown>) {
  return payload.id
    ? request.put<never, InheritorItem>(`/api/admin/inheritors/${payload.id}`, payload)
    : request.post<never, InheritorItem>('/api/admin/inheritors', payload)
}

export function deleteInheritor(id: number) {
  return request.delete<never, void>(`/api/admin/inheritors/${id}`)
}

export function getCategories(admin = false) {
  if (admin) {
    return request.get<never, CategoryItem[]>('/api/admin/categories')
  }
  return getProjectFilters().then((result) => result.categories)
}

export function saveCategory(payload: Record<string, unknown>) {
  return payload.id
    ? request.put<never, CategoryItem>(`/api/admin/categories/${payload.id}`, payload)
    : request.post<never, CategoryItem>('/api/admin/categories', payload)
}

export function deleteCategory(id: number) {
  return request.delete<never, void>(`/api/admin/categories/${id}`)
}

export function getRegions(admin = false) {
  if (admin) {
    return request.get<never, RegionItem[]>('/api/admin/regions')
  }
  return getProjectFilters().then((result) => result.regions)
}

export function saveRegion(payload: Record<string, unknown>) {
  return payload.id
    ? request.put<never, RegionItem>(`/api/admin/regions/${payload.id}`, payload)
    : request.post<never, RegionItem>('/api/admin/regions', payload)
}

export function deleteRegion(id: number) {
  return request.delete<never, void>(`/api/admin/regions/${id}`)
}

export function downloadProjects(params: Record<string, unknown>) {
  return request.get<Blob, Blob>('/api/admin/export/projects', { params, responseType: 'blob' })
}
