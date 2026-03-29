<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteProject,
  downloadProjects,
  getAdminProjectDetail,
  getAdminProjects,
  getInheritors,
  getProjectFilters,
  saveProject,
  uploadProjectImage
} from '@/api/heritage'
import { downloadBlob } from '@/utils/file'
import type { FilterPayload, InheritorItem, ProjectListItem } from '@/types/models'

const loading = ref(false)
const dialogVisible = ref(false)
const tableData = ref<ProjectListItem[]>([])
const total = ref(0)
const filters = ref<FilterPayload>({ categories: [], regions: [], levels: [] })
const inheritorOptions = ref<InheritorItem[]>([])

const query = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
  regionId: undefined as number | undefined,
  level: '',
  status: undefined as number | undefined,
  pageNum: 1,
  pageSize: 10
})

const form = reactive({
  id: undefined as number | undefined,
  name: '',
  categoryId: undefined as number | undefined,
  regionId: undefined as number | undefined,
  level: '省级',
  batch: '',
  protectionUnit: '',
  coverImage: '',
  summary: '',
  content: '',
  longitude: '',
  latitude: '',
  status: 1,
  featured: 1,
  inheritorIds: [] as number[],
  imageUrls: [] as string[]
})

function resetForm() {
  form.id = undefined
  form.name = ''
  form.categoryId = undefined
  form.regionId = undefined
  form.level = '省级'
  form.batch = ''
  form.protectionUnit = ''
  form.coverImage = ''
  form.summary = ''
  form.content = ''
  form.longitude = ''
  form.latitude = ''
  form.status = 1
  form.featured = 1
  form.inheritorIds = []
  form.imageUrls = []
}

async function loadOptions() {
  const [filterData, inheritorData] = await Promise.all([
    getProjectFilters(),
    getInheritors({ pageNum: 1, pageSize: 200 })
  ])
  filters.value = filterData
  inheritorOptions.value = inheritorData.records
}

async function loadProjects() {
  loading.value = true
  try {
    const result = await getAdminProjects(query)
    tableData.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function openCreate() {
  resetForm()
  dialogVisible.value = true
}

async function openEdit(row: ProjectListItem) {
  const detail = await getAdminProjectDetail(row.id)
  form.id = detail.id
  form.name = detail.name
  form.categoryId = detail.categoryId
  form.regionId = detail.regionId
  form.level = detail.level
  form.batch = detail.batch
  form.protectionUnit = detail.protectionUnit
  form.coverImage = detail.coverImage
  form.summary = detail.summary
  form.content = detail.content
  form.longitude = detail.longitude || ''
  form.latitude = detail.latitude || ''
  form.status = detail.status
  form.featured = detail.featured
  form.inheritorIds = detail.inheritors.map((item) => item.id)
  form.imageUrls = [...detail.imageUrls]
  dialogVisible.value = true
}

async function handleSave() {
  await saveProject({ ...form })
  ElMessage.success('项目保存成功')
  dialogVisible.value = false
  loadProjects()
}

async function handleDelete(row: ProjectListItem) {
  await ElMessageBox.confirm(`确认删除项目“${row.name}”吗？`, '提示', { type: 'warning' })
  await deleteProject(row.id)
  ElMessage.success('删除成功')
  loadProjects()
}

async function uploadCover(option: { file: File }) {
  const result = await uploadProjectImage(option.file)
  form.coverImage = result.url
  ElMessage.success('封面上传成功')
}

async function uploadGallery(option: { file: File }) {
  const result = await uploadProjectImage(option.file)
  form.imageUrls.push(result.url)
  ElMessage.success('图片上传成功')
}

function removeGalleryImage(url: string) {
  form.imageUrls = form.imageUrls.filter((item) => item !== url)
}

async function handleExport() {
  const blob = await downloadProjects(query)
  downloadBlob(blob, 'hubei-ich-projects.xlsx')
}

onMounted(async () => {
  await Promise.all([loadOptions(), loadProjects()])
})
</script>

<template>
  <div class="page-grid">
    <section class="glass-card panel">
      <div class="section-title">
        <div>
          <h2>非遗项目管理</h2>
          <p>支持项目新增、编辑、发布隐藏、图片维护和导出。</p>
        </div>
        <div class="toolbar">
          <el-button @click="handleExport">导出 Excel</el-button>
          <el-button type="primary" @click="openCreate">新增项目</el-button>
        </div>
      </div>
      <el-form :inline="true" class="toolbar">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" clearable placeholder="项目名称" />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="query.categoryId" clearable style="width: 160px">
            <el-option v-for="item in filters.categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="query.regionId" clearable style="width: 160px">
            <el-option v-for="item in filters.regions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width: 140px">
            <el-option label="已发布" :value="1" />
            <el-option label="已隐藏" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="query.pageNum = 1; loadProjects()">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData">
        <el-table-column prop="name" label="项目名称" min-width="220" />
        <el-table-column prop="categoryName" label="类别" width="120" />
        <el-table-column prop="regionName" label="地区" width="120" />
        <el-table-column prop="level" label="级别" width="100" />
        <el-table-column prop="statusLabel" label="状态" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text @click="openEdit(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          layout="prev, pager, next, total"
          :total="total"
          @current-change="loadProjects"
        />
      </div>
    </section>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑项目' : '新增项目'" width="900px">
      <el-form label-position="top" class="dialog-form">
        <div class="two-col">
          <el-form-item label="项目名称"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="保护单位"><el-input v-model="form.protectionUnit" /></el-form-item>
        </div>
        <div class="three-col">
          <el-form-item label="类别">
            <el-select v-model="form.categoryId" style="width: 100%">
              <el-option v-for="item in filters.categories" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="地区">
            <el-select v-model="form.regionId" style="width: 100%">
              <el-option v-for="item in filters.regions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="级别">
            <el-select v-model="form.level" style="width: 100%">
              <el-option v-for="item in filters.levels" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
        </div>
        <div class="three-col">
          <el-form-item label="申报批次"><el-input v-model="form.batch" /></el-form-item>
          <el-form-item label="经度"><el-input v-model="form.longitude" /></el-form-item>
          <el-form-item label="纬度"><el-input v-model="form.latitude" /></el-form-item>
        </div>
        <div class="three-col">
          <el-form-item label="发布状态">
            <el-radio-group v-model="form.status">
              <el-radio :label="1">已发布</el-radio>
              <el-radio :label="0">已隐藏</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="首页推荐">
            <el-radio-group v-model="form.featured">
              <el-radio :label="1">推荐</el-radio>
              <el-radio :label="0">不推荐</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="关联传承人">
            <el-select v-model="form.inheritorIds" multiple style="width: 100%">
              <el-option v-for="item in inheritorOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="项目简介">
          <el-input v-model="form.summary" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="项目详情">
          <el-input v-model="form.content" type="textarea" :rows="6" />
        </el-form-item>
        <div class="two-col">
          <el-form-item label="封面图片">
            <div class="upload-area">
              <el-upload :show-file-list="false" :http-request="uploadCover">
                <el-button>上传封面</el-button>
              </el-upload>
              <el-input v-model="form.coverImage" placeholder="或粘贴图片 URL" />
            </div>
          </el-form-item>
          <el-form-item label="图文资料">
            <div class="upload-area">
              <el-upload :show-file-list="false" :http-request="uploadGallery">
                <el-button>上传图片</el-button>
              </el-upload>
              <div class="tag-wrap">
                <el-tag
                  v-for="url in form.imageUrls"
                  :key="url"
                  closable
                  class="tag-item"
                  @close="removeGalleryImage(url)"
                >
                  {{ url.split('/').pop() }}
                </el-tag>
              </div>
            </div>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-grid {
  display: grid;
}

.panel {
  padding: 24px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
}

.dialog-form {
  max-height: 70vh;
  overflow: auto;
  padding-right: 12px;
}

.upload-area {
  display: grid;
  gap: 12px;
  width: 100%;
}

.tag-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  max-width: 100%;
}
</style>
