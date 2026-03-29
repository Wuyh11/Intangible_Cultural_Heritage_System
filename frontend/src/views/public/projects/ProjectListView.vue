<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getProjectFilters, getPublicProjects } from '@/api/heritage'
import type { FilterPayload, ProjectListItem } from '@/types/models'

const loading = ref(false)
const projects = ref<ProjectListItem[]>([])
const total = ref(0)
const filters = ref<FilterPayload>({ categories: [], regions: [], levels: [] })
const query = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
  regionId: undefined as number | undefined,
  level: '',
  pageNum: 1,
  pageSize: 9
})

async function loadFilters() {
  filters.value = await getProjectFilters()
}

async function loadProjects() {
  loading.value = true
  try {
    const result = await getPublicProjects(query)
    projects.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.keyword = ''
  query.categoryId = undefined
  query.regionId = undefined
  query.level = ''
  query.pageNum = 1
  loadProjects()
}

onMounted(async () => {
  await Promise.all([loadFilters(), loadProjects()])
})
</script>

<template>
  <div class="page-shell page-wrap">
    <section class="glass-card filter-panel">
      <div class="section-title">
        <div>
          <h2>湖北省非遗项目名录</h2>
          <p>按关键字、地区、类别、级别进行多维检索，适合前台浏览与答辩演示。</p>
        </div>
      </div>
      <el-form :inline="true" class="toolbar">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="输入项目名称" clearable />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="query.categoryId" placeholder="全部类别" clearable style="width: 160px">
            <el-option v-for="item in filters.categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="query.regionId" placeholder="全部地区" clearable style="width: 160px">
            <el-option v-for="item in filters.regions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="query.level" placeholder="全部级别" clearable style="width: 140px">
            <el-option v-for="item in filters.levels" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="query.pageNum = 1; loadProjects()">检索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section v-loading="loading" class="project-grid">
      <RouterLink
        v-for="item in projects"
        :key="item.id"
        class="glass-card project-card"
        :to="`/projects/${item.id}`"
      >
        <img :src="item.coverImage || '/placeholder/project-placeholder.svg'" :alt="item.name" />
        <div class="project-card__body">
          <div class="project-card__tags">
            <span class="tag-chip">{{ item.categoryName }}</span>
            <span class="tag-chip">{{ item.level }}</span>
          </div>
          <h3>{{ item.name }}</h3>
          <p>{{ item.summary }}</p>
          <div class="project-meta">
            <span>{{ item.regionName }}</span>
            <span>浏览 {{ item.viewCount || 0 }}</span>
          </div>
        </div>
      </RouterLink>
    </section>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        layout="prev, pager, next, total"
        :total="total"
        @current-change="loadProjects"
      />
    </div>
  </div>
</template>

<style scoped>
.page-wrap {
  padding: 28px 0 36px;
}

.filter-panel {
  padding: 24px;
  margin-bottom: 24px;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.project-card {
  overflow: hidden;
}

.project-card img {
  width: 100%;
  height: 220px;
  object-fit: cover;
  display: block;
}

.project-card__body {
  padding: 20px;
}

.project-card__tags {
  display: flex;
  gap: 8px;
}

.project-card__body h3 {
  margin: 14px 0 12px;
}

.project-card__body p {
  margin: 0;
  color: var(--text-sub);
  line-height: 1.8;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
  color: var(--brand);
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 28px 0 0;
}

@media (max-width: 960px) {
  .project-grid {
    grid-template-columns: 1fr;
  }
}
</style>
