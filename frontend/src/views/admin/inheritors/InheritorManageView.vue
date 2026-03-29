<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteInheritor,
  getAdminProjects,
  getInheritors,
  getProjectFilters,
  saveInheritor,
  uploadProjectImage
} from '@/api/heritage'
import type { FilterPayload, InheritorItem, ProjectListItem } from '@/types/models'

const loading = ref(false)
const dialogVisible = ref(false)
const tableData = ref<InheritorItem[]>([])
const total = ref(0)
const filters = ref<FilterPayload>({ categories: [], regions: [], levels: [] })
const projectOptions = ref<ProjectListItem[]>([])

const query = reactive({
  keyword: '',
  regionId: undefined as number | undefined,
  status: undefined as number | undefined,
  pageNum: 1,
  pageSize: 10
})

const form = reactive({
  id: undefined as number | undefined,
  name: '',
  gender: '女',
  birthYear: 1980,
  regionId: undefined as number | undefined,
  title: '',
  avatar: '',
  introduction: '',
  representativeWorks: '',
  status: 1,
  projectIds: [] as number[]
})

function resetForm() {
  form.id = undefined
  form.name = ''
  form.gender = '女'
  form.birthYear = 1980
  form.regionId = undefined
  form.title = ''
  form.avatar = ''
  form.introduction = ''
  form.representativeWorks = ''
  form.status = 1
  form.projectIds = []
}

async function loadOptions() {
  const [filterData, projectData] = await Promise.all([
    getProjectFilters(),
    getAdminProjects({ pageNum: 1, pageSize: 200 })
  ])
  filters.value = filterData
  projectOptions.value = projectData.records
}

async function loadInheritors() {
  loading.value = true
  try {
    const result = await getInheritors(query)
    tableData.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: InheritorItem) {
  form.id = row.id
  form.name = row.name
  form.gender = row.gender
  form.birthYear = row.birthYear
  form.regionId = row.regionId
  form.title = row.title
  form.avatar = row.avatar || ''
  form.introduction = row.introduction
  form.representativeWorks = row.representativeWorks || ''
  form.status = row.status
  form.projectIds = row.projectIds || []
  dialogVisible.value = true
}

async function handleSave() {
  await saveInheritor({ ...form })
  ElMessage.success('传承人保存成功')
  dialogVisible.value = false
  loadInheritors()
}

async function handleDelete(row: InheritorItem) {
  await ElMessageBox.confirm(`确认删除传承人“${row.name}”吗？`, '提示', { type: 'warning' })
  await deleteInheritor(row.id)
  ElMessage.success('删除成功')
  loadInheritors()
}

async function uploadAvatar(option: { file: File }) {
  const result = await uploadProjectImage(option.file)
  form.avatar = result.url
  ElMessage.success('头像上传成功')
}

onMounted(async () => {
  await Promise.all([loadOptions(), loadInheritors()])
})
</script>

<template>
  <section class="glass-card panel">
    <div class="section-title">
      <div>
        <h2>传承人管理</h2>
        <p>支持建立传承人档案，并与多个非遗项目建立关联。</p>
      </div>
      <el-button type="primary" @click="openCreate">新增传承人</el-button>
    </div>
    <el-form :inline="true" class="toolbar">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="姓名或称号" clearable />
      </el-form-item>
      <el-form-item label="地区">
        <el-select v-model="query.regionId" clearable style="width: 160px">
          <el-option v-for="item in filters.regions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable style="width: 140px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="query.pageNum = 1; loadInheritors()">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="tableData">
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="gender" label="性别" width="90" />
      <el-table-column prop="birthYear" label="出生年份" width="110" />
      <el-table-column prop="regionName" label="地区" width="120" />
      <el-table-column prop="title" label="称号/身份" min-width="200" />
      <el-table-column prop="introduction" label="简介" min-width="280" show-overflow-tooltip />
      <el-table-column label="操作" width="160">
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
        @current-change="loadInheritors"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑传承人' : '新增传承人'" width="860px">
      <el-form label-position="top">
        <div class="three-col">
          <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="出生年份"><el-input-number v-model="form.birthYear" :min="1900" :max="2026" style="width: 100%" /></el-form-item>
        </div>
        <div class="two-col">
          <el-form-item label="所属地区">
            <el-select v-model="form.regionId" style="width: 100%">
              <el-option v-for="item in filters.regions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="称号/身份"><el-input v-model="form.title" /></el-form-item>
        </div>
        <el-form-item label="头像">
          <div class="upload-area">
            <el-upload :show-file-list="false" :http-request="uploadAvatar">
              <el-button>上传头像</el-button>
            </el-upload>
            <el-input v-model="form.avatar" placeholder="或填写头像 URL" />
          </div>
        </el-form-item>
        <el-form-item label="关联项目">
          <el-select v-model="form.projectIds" multiple style="width: 100%">
            <el-option v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.introduction" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="代表作品/代表项目说明">
          <el-input v-model="form.representativeWorks" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.panel {
  padding: 24px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
}

.upload-area {
  display: grid;
  gap: 12px;
  width: 100%;
}
</style>
