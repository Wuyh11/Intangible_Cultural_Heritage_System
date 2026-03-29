<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getMapPoints, getProjectFilters } from '@/api/heritage'
import type { FilterPayload, MapPoint, ProjectListItem } from '@/types/models'

const router = useRouter()
const chartRef = ref<HTMLDivElement | null>(null)
let chart: echarts.ECharts | null = null

const filters = ref<FilterPayload>({ categories: [], regions: [], levels: [] })
const points = ref<MapPoint[]>([])
const featuredProjects = ref<ProjectListItem[]>([])
const query = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
  regionId: undefined as number | undefined,
  level: ''
})

async function loadData() {
  const [filterData, mapData] = await Promise.all([getProjectFilters(), getMapPoints(query)])
  filters.value = filterData
  points.value = mapData.points
  featuredProjects.value = mapData.featuredProjects
}

async function initChart() {
  if (!chartRef.value) return
  const geoJson = await fetch('/maps/hubei.json').then((res) => res.json())
  echarts.registerMap('hubei-local', geoJson)
  chart ||= echarts.init(chartRef.value)
  chart.off('click')
  chart.on('click', (params) => {
    const data = params.data as { projectId?: number } | undefined
    if (data?.projectId) {
      router.push(`/projects/${data.projectId}`)
    }
  })
  renderChart()
}

function renderChart() {
  if (!chart) return
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params: { data?: MapPoint }) => {
        if (!params.data) return ''
        return `${params.data.projectName}<br/>${params.data.regionName} · ${params.data.level}`
      }
    },
    geo: {
      map: 'hubei-local',
      roam: true,
      itemStyle: {
        areaColor: '#f4d7af',
        borderColor: '#a24b2a'
      },
      emphasis: {
        itemStyle: { areaColor: '#e6ba72' }
      }
    },
    series: [
      {
        type: 'scatter',
        coordinateSystem: 'geo',
        symbolSize: (value: number[]) => Math.max(12, value[2] / 18 + 12),
        itemStyle: { color: '#7a3017' },
        data: points.value.map((item) => ({
          name: item.projectName,
          value: [Number(item.longitude), Number(item.latitude), item.viewCount || 0],
          ...item
        }))
      }
    ]
  })
}

watch(points, () => renderChart())

onMounted(async () => {
  await loadData()
  await initChart()
  window.addEventListener('resize', () => chart?.resize())
})
</script>

<template>
  <div class="page-shell map-page">
    <section class="glass-card filter-panel">
      <div class="section-title">
        <div>
          <h2>湖北省非遗地图</h2>
          <p>基于本地 GeoJSON 与 ECharts 实现项目点位展示，无需第三方地图 Key。</p>
        </div>
      </div>
      <el-form :inline="true" class="toolbar">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="输入项目名称" clearable />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="query.categoryId" clearable placeholder="全部类别" style="width: 160px">
            <el-option v-for="item in filters.categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="query.regionId" clearable placeholder="全部地区" style="width: 160px">
            <el-option v-for="item in filters.regions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="query.level" clearable placeholder="全部级别" style="width: 140px">
            <el-option v-for="item in filters.levels" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">刷新地图</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="two-col map-body">
      <div class="glass-card map-card">
        <div ref="chartRef" class="chart-box" style="height: 640px" />
      </div>
      <div class="side-column">
        <div class="glass-card side-card">
          <div class="section-title">
            <div>
              <h2>地图说明</h2>
              <p>点击点位可以直接跳转到项目详情。</p>
            </div>
          </div>
          <p>当前共加载 {{ points.length }} 个已发布项目点位，涵盖武汉、十堰、宜昌、恩施等主要地市。</p>
        </div>
        <div class="glass-card side-card">
          <div class="section-title">
            <div>
              <h2>推荐项目</h2>
              <p>与地图一起展示的热门代表项目。</p>
            </div>
          </div>
          <RouterLink
            v-for="item in featuredProjects"
            :key="item.id"
            class="project-link"
            :to="`/projects/${item.id}`"
          >
            <strong>{{ item.name }}</strong>
            <span>{{ item.regionName }} · {{ item.categoryName }}</span>
          </RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.map-page {
  padding: 28px 0 36px;
}

.filter-panel,
.map-card,
.side-card {
  padding: 24px;
}

.map-body {
  margin-top: 24px;
}

.side-column {
  display: grid;
  gap: 20px;
}

.side-card p {
  margin: 0;
  color: var(--text-sub);
  line-height: 1.9;
}

.project-link {
  display: block;
  padding: 14px 0;
  border-top: 1px solid var(--line);
}

.project-link:first-of-type {
  border-top: none;
  padding-top: 0;
}

.project-link strong {
  display: block;
  margin-bottom: 6px;
}

.project-link span {
  color: var(--text-sub);
}
</style>
