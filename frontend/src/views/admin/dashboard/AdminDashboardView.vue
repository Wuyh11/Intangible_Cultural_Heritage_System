<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import { getAdminStatsOverview, downloadStats } from '@/api/stats'
import { getAdminProjects } from '@/api/heritage'
import { downloadBlob } from '@/utils/file'
import StatCard from '@/components/StatCard.vue'
import type { ProjectListItem, StatsOverview } from '@/types/models'

const overview = ref<StatsOverview | null>(null)
const projects = ref<ProjectListItem[]>([])
const levelChartRef = ref<HTMLDivElement | null>(null)
const hotChartRef = ref<HTMLDivElement | null>(null)
let levelChart: echarts.ECharts | null = null
let hotChart: echarts.ECharts | null = null

async function loadData() {
  const [statsData, projectData] = await Promise.all([
    getAdminStatsOverview(),
    getAdminProjects({ pageSize: 5, pageNum: 1 })
  ])
  overview.value = statsData
  projects.value = projectData.records
}

function renderCharts() {
  if (!overview.value || !levelChartRef.value || !hotChartRef.value) return
  levelChart ||= echarts.init(levelChartRef.value)
  hotChart ||= echarts.init(hotChartRef.value)
  levelChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: overview.value.levelDistribution.map((item) => item.name) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: overview.value.levelDistribution.map((item) => item.value), itemStyle: { color: '#a24b2a' } }]
  })
  hotChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: overview.value.hotProjects.map((item) => item.name) },
    series: [{ type: 'bar', data: overview.value.hotProjects.map((item) => item.value), itemStyle: { color: '#d58d2a' } }]
  })
}

async function handleExportStats() {
  const blob = await downloadStats()
  downloadBlob(blob, 'hubei-ich-stats.xlsx')
}

watch(overview, () => renderCharts())

onMounted(async () => {
  await loadData()
  renderCharts()
  window.addEventListener('resize', () => {
    levelChart?.resize()
    hotChart?.resize()
  })
})
</script>

<template>
  <div class="dashboard">
    <section class="three-col">
      <StatCard title="已发布项目" :value="overview?.projectCount || 0" description="门户端可展示项目数" />
      <StatCard title="传承人档案" :value="overview?.inheritorCount || 0" description="已录入传承人数量" />
      <StatCard title="基础维度" :value="overview?.categoryCount || 0" description="分类数据和地域数据可维护" />
    </section>

    <section class="dashboard-row">
      <div class="glass-card panel">
        <div class="section-title">
          <div>
            <h2>统计图表</h2>
            <p>后台仪表盘复用门户统计逻辑，并支持 Excel 导出。</p>
          </div>
          <el-button type="primary" @click="handleExportStats">导出统计</el-button>
        </div>
        <div class="two-col">
          <div ref="levelChartRef" class="chart-box" />
          <div ref="hotChartRef" class="chart-box" />
        </div>
      </div>
    </section>

    <section class="glass-card panel">
      <div class="section-title">
        <div>
          <h2>最近项目</h2>
          <p>用于快速查看后台已维护的项目状态。</p>
        </div>
      </div>
      <el-table :data="projects">
        <el-table-column prop="name" label="项目名称" min-width="220" />
        <el-table-column prop="categoryName" label="类别" width="130" />
        <el-table-column prop="regionName" label="地区" width="130" />
        <el-table-column prop="level" label="级别" width="100" />
        <el-table-column prop="statusLabel" label="状态" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
      </el-table>
    </section>
  </div>
</template>

<style scoped>
.dashboard {
  display: grid;
  gap: 24px;
}

.panel {
  padding: 24px;
}
</style>
