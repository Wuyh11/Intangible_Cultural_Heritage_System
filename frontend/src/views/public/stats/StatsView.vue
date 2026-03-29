<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import { getPublicStatsOverview } from '@/api/stats'
import StatCard from '@/components/StatCard.vue'
import type { StatsOverview } from '@/types/models'

const overview = ref<StatsOverview | null>(null)
const levelChartRef = ref<HTMLDivElement | null>(null)
const categoryChartRef = ref<HTMLDivElement | null>(null)
const regionChartRef = ref<HTMLDivElement | null>(null)
let levelChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null
let regionChart: echarts.ECharts | null = null

async function loadData() {
  overview.value = await getPublicStatsOverview()
}

function renderCharts() {
  if (!overview.value || !levelChartRef.value || !categoryChartRef.value || !regionChartRef.value) return
  levelChart ||= echarts.init(levelChartRef.value)
  categoryChart ||= echarts.init(categoryChartRef.value)
  regionChart ||= echarts.init(regionChartRef.value)

  levelChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: overview.value.levelDistribution.map((item) => item.name) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: overview.value.levelDistribution.map((item) => item.value), itemStyle: { color: '#7a3017' } }]
  })

  categoryChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: ['38%', '72%'], data: overview.value.categoryDistribution }]
  })

  regionChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: overview.value.regionDistribution.map((item) => item.name),
      axisLabel: { rotate: 35 }
    },
    yAxis: { type: 'value' },
    series: [{ type: 'line', smooth: true, data: overview.value.regionDistribution.map((item) => item.value), itemStyle: { color: '#d58d2a' } }]
  })
}

watch(overview, () => renderCharts())

onMounted(async () => {
  await loadData()
  renderCharts()
  window.addEventListener('resize', () => {
    levelChart?.resize()
    categoryChart?.resize()
    regionChart?.resize()
  })
})
</script>

<template>
  <div class="page-shell stats-page">
    <section class="section-title">
      <div>
        <h2>统计分析中心</h2>
        <p>面向公众展示平台数据概览，也可为后台答辩演示提供实时统计基础。</p>
      </div>
    </section>

    <section class="three-col">
      <StatCard title="项目总量" :value="overview?.projectCount || 0" description="已发布的代表性项目数量" />
      <StatCard title="传承人数量" :value="overview?.inheritorCount || 0" description="已录入的传承人档案数量" />
      <StatCard title="分类维度" :value="overview?.categoryCount || 0" description="非遗类别基础数据覆盖情况" />
    </section>

    <section class="three-col chart-section">
      <div class="glass-card chart-card">
        <div class="section-title"><div><h2>级别分布</h2><p>国家级、省级、市级、县级项目分布。</p></div></div>
        <div ref="levelChartRef" class="chart-box" />
      </div>
      <div class="glass-card chart-card">
        <div class="section-title"><div><h2>类别结构</h2><p>反映不同类别项目的占比。</p></div></div>
        <div ref="categoryChartRef" class="chart-box" />
      </div>
      <div class="glass-card chart-card">
        <div class="section-title"><div><h2>区域趋势</h2><p>各地市项目数量对比。</p></div></div>
        <div ref="regionChartRef" class="chart-box" />
      </div>
    </section>

    <section class="glass-card hot-list">
      <div class="section-title">
        <div>
          <h2>热门项目排行</h2>
          <p>按浏览热度生成，适合作为门户首页和后台仪表盘的联动数据。</p>
        </div>
      </div>
      <div class="hot-grid">
        <div v-for="(item, index) in overview?.hotProjects || []" :key="item.name" class="hot-row">
          <strong>{{ index + 1 }}</strong>
          <span>{{ item.name }}</span>
          <em>{{ item.value }}</em>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.stats-page {
  padding: 28px 0 36px;
}

.chart-section {
  margin: 24px 0;
}

.chart-card,
.hot-list {
  padding: 24px;
}

.hot-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.hot-row {
  display: grid;
  grid-template-columns: 40px 1fr auto;
  gap: 16px;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid var(--line);
}

.hot-row strong {
  color: var(--brand);
  font-size: 20px;
}

.hot-row span,
.hot-row em {
  color: var(--text-sub);
}

@media (max-width: 960px) {
  .hot-grid {
    grid-template-columns: 1fr;
  }
}
</style>
