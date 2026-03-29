<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import { getPublicProjects } from '@/api/heritage'
import { getPublicStatsOverview } from '@/api/stats'
import StatCard from '@/components/StatCard.vue'
import type { ProjectListItem, StatsOverview } from '@/types/models'

const featuredProjects = ref<ProjectListItem[]>([])
const overview = ref<StatsOverview | null>(null)

const categoryChartRef = ref<HTMLDivElement | null>(null)
const regionChartRef = ref<HTMLDivElement | null>(null)
let categoryChart: echarts.ECharts | null = null
let regionChart: echarts.ECharts | null = null

async function loadData() {
  const [statsData, featuredData] = await Promise.all([
    getPublicStatsOverview(),
    getPublicProjects({ featured: 1, pageSize: 6, pageNum: 1 })
  ])
  overview.value = statsData
  featuredProjects.value = featuredData.records
}

function renderCharts() {
  if (!overview.value || !categoryChartRef.value || !regionChartRef.value) return
  categoryChart ||= echarts.init(categoryChartRef.value)
  regionChart ||= echarts.init(regionChartRef.value)

  categoryChart.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['42%', '72%'],
        label: { formatter: '{b}\n{d}%' },
        data: overview.value.categoryDistribution
      }
    ]
  })

  regionChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: overview.value.regionDistribution.map((item) => item.name),
      axisLabel: { rotate: 30 }
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        data: overview.value.regionDistribution.map((item) => item.value),
        itemStyle: { color: '#a24b2a', borderRadius: [8, 8, 0, 0] }
      }
    ]
  })
}

watch(overview, () => renderCharts())

onMounted(async () => {
  await loadData()
  renderCharts()
  window.addEventListener('resize', () => {
    categoryChart?.resize()
    regionChart?.resize()
  })
})
</script>

<template>
  <div class="page-shell home-page">
    <section class="hero glass-card">
      <div class="hero-copy">
        <span class="tag-chip">数字化保护 · 省级信息平台</span>
        <h1 class="hero-title">看见湖北非遗的地域分布、项目谱系与传承脉络</h1>
        <p class="hero-subtitle">
          平台聚合湖北省代表性非遗项目、传承人和地市分布信息，支持多维检索、地图可视化、后台维护与统计导出。
        </p>
        <div class="hero-actions">
          <RouterLink to="/projects">
            <el-button type="primary" size="large">浏览非遗项目</el-button>
          </RouterLink>
          <RouterLink to="/map">
            <el-button size="large">打开非遗地图</el-button>
          </RouterLink>
        </div>
      </div>
      <div class="hero-panel">
        <div class="hero-panel-card">
          <span>重点看板</span>
          <strong>{{ overview?.hotProjects?.[0]?.name || '汉绣' }}</strong>
          <p>热门项目会随浏览数据动态变化，用于答辩演示平台的实时统计能力。</p>
        </div>
      </div>
    </section>

    <section class="three-col stats-row">
      <StatCard title="非遗项目" :value="overview?.projectCount || 0" description="覆盖代表性湖北省项目名录" />
      <StatCard title="传承人档案" :value="overview?.inheritorCount || 0" description="聚合项目关联传承人信息" />
      <StatCard title="地市分布" :value="overview?.regionCount || 0" description="按湖北地市可视化检索" />
    </section>

    <section class="two-col chart-row">
      <div class="glass-card chart-card">
        <div class="section-title">
          <div>
            <h2>类别分布</h2>
            <p>按传统技艺、戏曲、民俗、医药等维度展示项目结构。</p>
          </div>
        </div>
        <div ref="categoryChartRef" class="chart-box" />
      </div>
      <div class="glass-card chart-card">
        <div class="section-title">
          <div>
            <h2>地区分布</h2>
            <p>反映武汉、宜昌、恩施、十堰等地区的项目聚合情况。</p>
          </div>
        </div>
        <div ref="regionChartRef" class="chart-box" />
      </div>
    </section>

    <section class="featured-section">
      <div class="section-title">
        <div>
          <h2>精选代表项目</h2>
          <p>围绕湖北省重点非遗资源挑选适合演示的代表性案例。</p>
        </div>
        <RouterLink to="/projects">
          <el-button text>查看更多</el-button>
        </RouterLink>
      </div>
      <div class="featured-grid">
        <RouterLink
          v-for="item in featuredProjects"
          :key="item.id"
          class="glass-card featured-card"
          :to="`/projects/${item.id}`"
        >
          <img :src="item.coverImage || '/placeholder/project-placeholder.svg'" :alt="item.name" />
          <div class="featured-card__body">
            <span class="tag-chip">{{ item.level }}</span>
            <h3>{{ item.name }}</h3>
            <p>{{ item.summary }}</p>
            <div class="meta">
              <span>{{ item.regionName }}</span>
              <span>{{ item.categoryName }}</span>
            </div>
          </div>
        </RouterLink>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  padding: 28px 0 36px;
}

.hero {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 28px;
  padding: 36px;
  margin-bottom: 28px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
}

.hero-panel {
  display: flex;
  align-items: stretch;
}

.hero-panel-card {
  flex: 1;
  padding: 28px;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(122, 48, 23, 0.95), rgba(162, 75, 42, 0.88));
  color: #fff9f2;
}

.hero-panel-card strong {
  display: block;
  margin-top: 16px;
  font-size: 28px;
}

.hero-panel-card p {
  color: rgba(255, 249, 242, 0.8);
  line-height: 1.8;
}

.stats-row,
.chart-row {
  margin-bottom: 28px;
}

.chart-card {
  padding: 24px;
}

.featured-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.featured-card {
  overflow: hidden;
}

.featured-card img {
  display: block;
  width: 100%;
  height: 220px;
  object-fit: cover;
}

.featured-card__body {
  padding: 20px;
}

.featured-card__body h3 {
  margin: 14px 0 12px;
  font-size: 22px;
}

.featured-card__body p {
  margin: 0;
  color: var(--text-sub);
  line-height: 1.8;
}

.meta {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
  color: var(--brand);
}

@media (max-width: 960px) {
  .hero,
  .featured-grid {
    grid-template-columns: 1fr;
  }
}
</style>
