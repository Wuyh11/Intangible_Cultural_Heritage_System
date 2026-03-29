import type { PageResponse } from './common'

export interface LoginResult {
  token: string
  username: string
  nickname: string
  roles: string[]
}

export interface ProjectListItem {
  id: number
  name: string
  categoryId: number
  categoryName: string
  regionId: number
  regionName: string
  level: string
  protectionUnit: string
  status: number
  statusLabel: string
  viewCount: number
  featured: number
  summary: string
  coverImage: string
  longitude?: string
  latitude?: string
}

export interface InheritorItem {
  id: number
  name: string
  gender: string
  birthYear: number
  regionId: number
  regionName: string
  title: string
  avatar?: string
  introduction: string
  representativeWorks?: string
  status: number
  projectIds: number[]
}

export interface ProjectDetailItem extends ProjectListItem {
  batch: string
  content: string
  imageUrls: string[]
  inheritors: InheritorItem[]
  recommendations: ProjectListItem[]
}

export interface CategoryItem {
  id: number
  name: string
  code: string
  sortOrder: number
  status: number
  description?: string
}

export interface RegionItem {
  id: number
  name: string
  code: string
  sortOrder: number
  longitude?: string
  latitude?: string
  status: number
}

export interface AdminUserItem {
  id: number
  username: string
  nickname: string
  email?: string
  phone?: string
  status: number
  roleCodes: string[]
  createdAt: string
}

export interface OperationLogItem {
  id: number
  username: string
  module: string
  action: string
  httpMethod: string
  requestUri: string
  requestIp: string
  requestParam: string
  createdAt: string
}

export interface ChartItem {
  name: string
  value: number
}

export interface StatsOverview {
  projectCount: number
  inheritorCount: number
  regionCount: number
  categoryCount: number
  levelDistribution: ChartItem[]
  categoryDistribution: ChartItem[]
  regionDistribution: ChartItem[]
  hotProjects: ChartItem[]
}

export interface MapPoint {
  projectId: number
  projectName: string
  categoryId: number
  categoryName: string
  regionId: number
  regionName: string
  level: string
  longitude: string
  latitude: string
  viewCount: number
}

export interface FilterPayload {
  categories: CategoryItem[]
  regions: RegionItem[]
  levels: string[]
}

export interface MapPayload {
  points: MapPoint[]
  featuredProjects: ProjectListItem[]
}

export type ProjectPage = PageResponse<ProjectListItem>
export type InheritorPage = PageResponse<InheritorItem>
export type LogPage = PageResponse<OperationLogItem>
