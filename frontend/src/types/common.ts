export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PageResponse<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface OptionItem {
  id: number
  name: string
  code?: string
  sortOrder?: number
  status?: number
}
