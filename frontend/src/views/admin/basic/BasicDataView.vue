<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteCategory,
  deleteRegion,
  getCategories,
  getRegions,
  saveCategory,
  saveRegion
} from '@/api/heritage'
import type { CategoryItem, RegionItem } from '@/types/models'

const categories = ref<CategoryItem[]>([])
const regions = ref<RegionItem[]>([])
const categoryDialogVisible = ref(false)
const regionDialogVisible = ref(false)

const categoryForm = reactive({
  id: undefined as number | undefined,
  name: '',
  code: '',
  sortOrder: 1,
  status: 1,
  description: ''
})

const regionForm = reactive({
  id: undefined as number | undefined,
  name: '',
  code: '',
  sortOrder: 1,
  longitude: '',
  latitude: '',
  status: 1
})

async function loadData() {
  const [categoryData, regionData] = await Promise.all([getCategories(true), getRegions(true)])
  categories.value = categoryData
  regions.value = regionData
}

function openCategoryDialog(item?: CategoryItem) {
  categoryForm.id = item?.id
  categoryForm.name = item?.name || ''
  categoryForm.code = item?.code || ''
  categoryForm.sortOrder = item?.sortOrder || 1
  categoryForm.status = item?.status ?? 1
  categoryForm.description = item?.description || ''
  categoryDialogVisible.value = true
}

function openRegionDialog(item?: RegionItem) {
  regionForm.id = item?.id
  regionForm.name = item?.name || ''
  regionForm.code = item?.code || ''
  regionForm.sortOrder = item?.sortOrder || 1
  regionForm.longitude = item?.longitude || ''
  regionForm.latitude = item?.latitude || ''
  regionForm.status = item?.status ?? 1
  regionDialogVisible.value = true
}

async function handleSaveCategory() {
  await saveCategory({ ...categoryForm })
  ElMessage.success('分类保存成功')
  categoryDialogVisible.value = false
  loadData()
}

async function handleSaveRegion() {
  await saveRegion({ ...regionForm })
  ElMessage.success('地区保存成功')
  regionDialogVisible.value = false
  loadData()
}

async function handleDeleteCategory(item: CategoryItem) {
  await ElMessageBox.confirm(`确认删除分类“${item.name}”吗？`, '提示', { type: 'warning' })
  await deleteCategory(item.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleDeleteRegion(item: RegionItem) {
  await ElMessageBox.confirm(`确认删除地区“${item.name}”吗？`, '提示', { type: 'warning' })
  await deleteRegion(item.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="basic-grid">
    <section class="glass-card panel">
      <div class="section-title">
        <div>
          <h2>分类管理</h2>
          <p>为前台筛选和统计图表提供类别维度。</p>
        </div>
        <el-button type="primary" @click="openCategoryDialog()">新增分类</el-button>
      </div>
      <el-table :data="categories">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="code" label="编码" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">{{ row.status === 1 ? '启用' : '停用' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button text @click="openCategoryDialog(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDeleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <section class="glass-card panel">
      <div class="section-title">
        <div>
          <h2>地区管理</h2>
          <p>维护地市名称与经纬度中心点，供地图与筛选使用。</p>
        </div>
        <el-button type="primary" @click="openRegionDialog()">新增地区</el-button>
      </div>
      <el-table :data="regions">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="code" label="编码" />
        <el-table-column prop="longitude" label="经度" />
        <el-table-column prop="latitude" label="纬度" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button text @click="openRegionDialog(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDeleteRegion(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="categoryDialogVisible" title="分类信息" width="560px">
      <el-form label-position="top">
        <el-form-item label="名称"><el-input v-model="categoryForm.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="categoryForm.code" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="categoryForm.sortOrder" :min="1" style="width: 100%" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="categoryForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveCategory">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="regionDialogVisible" title="地区信息" width="560px">
      <el-form label-position="top">
        <el-form-item label="名称"><el-input v-model="regionForm.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="regionForm.code" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="regionForm.sortOrder" :min="1" style="width: 100%" /></el-form-item>
        <div class="two-col">
          <el-form-item label="经度"><el-input v-model="regionForm.longitude" /></el-form-item>
          <el-form-item label="纬度"><el-input v-model="regionForm.latitude" /></el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="regionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRegion">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.basic-grid {
  display: grid;
  gap: 24px;
}

.panel {
  padding: 24px;
}
</style>
