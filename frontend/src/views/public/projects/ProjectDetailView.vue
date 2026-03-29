<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getProjectDetail } from '@/api/heritage'
import type { ProjectDetailItem } from '@/types/models'

const route = useRoute()
const loading = ref(false)
const detail = ref<ProjectDetailItem | null>(null)

const galleryImages = computed(() => {
  if (!detail.value) return []
  const urls = detail.value.imageUrls?.length ? detail.value.imageUrls : [detail.value.coverImage]
  return urls.filter(Boolean)
})

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getProjectDetail(Number(route.params.id))
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)
</script>

<template>
  <div class="page-shell detail-page" v-loading="loading">
    <section v-if="detail" class="glass-card hero-panel">
      <img :src="detail.coverImage || '/placeholder/project-placeholder.svg'" :alt="detail.name" />
      <div class="hero-content">
        <span class="tag-chip">{{ detail.categoryName }}</span>
        <h1>{{ detail.name }}</h1>
        <p>{{ detail.summary }}</p>
        <div class="facts">
          <div><strong>地区</strong><span>{{ detail.regionName }}</span></div>
          <div><strong>级别</strong><span>{{ detail.level }}</span></div>
          <div><strong>保护单位</strong><span>{{ detail.protectionUnit }}</span></div>
          <div><strong>浏览量</strong><span>{{ detail.viewCount || 0 }}</span></div>
        </div>
      </div>
    </section>

    <section v-if="detail" class="two-col info-row">
      <div class="glass-card article-card">
        <div class="section-title">
          <div>
            <h2>项目介绍</h2>
            <p>包含项目概述、代表性价值和传承背景。</p>
          </div>
        </div>
        <div class="article-text">{{ detail.content }}</div>
      </div>
      <div class="side-column">
        <div class="glass-card side-card">
          <div class="section-title">
            <div>
              <h2>关联传承人</h2>
              <p>展示项目对应的代表性传承人档案。</p>
            </div>
          </div>
          <div v-for="item in detail.inheritors" :key="item.id" class="inheritor-item">
            <h3>{{ item.name }}</h3>
            <p>{{ item.title }} · {{ item.regionName }}</p>
            <span>{{ item.introduction }}</span>
          </div>
        </div>
        <div class="glass-card side-card">
          <div class="section-title">
            <div>
              <h2>项目信息</h2>
              <p>可用于答辩时展示基础元数据结构。</p>
            </div>
          </div>
          <ul class="meta-list">
            <li><strong>申报批次：</strong>{{ detail.batch || '待补充' }}</li>
            <li><strong>经度：</strong>{{ detail.longitude || '待补充' }}</li>
            <li><strong>纬度：</strong>{{ detail.latitude || '待补充' }}</li>
          </ul>
        </div>
      </div>
    </section>

    <section v-if="galleryImages.length" class="gallery-row">
      <div class="section-title">
        <div>
          <h2>图文资料</h2>
          <p>项目图片素材支持后台上传与维护。</p>
        </div>
      </div>
      <div class="gallery-grid">
        <img v-for="url in galleryImages" :key="url" :src="url" alt="项目图片" class="glass-card" />
      </div>
    </section>

    <section v-if="detail?.recommendations?.length" class="recommend-row">
      <div class="section-title">
        <div>
          <h2>同类推荐</h2>
          <p>根据类别和热度推荐的其他非遗项目。</p>
        </div>
      </div>
      <div class="recommend-grid">
        <RouterLink
          v-for="item in detail.recommendations"
          :key="item.id"
          class="glass-card recommend-card"
          :to="`/projects/${item.id}`"
        >
          <h3>{{ item.name }}</h3>
          <p>{{ item.summary }}</p>
        </RouterLink>
      </div>
    </section>
  </div>
</template>

<style scoped>
.detail-page {
  padding: 28px 0 36px;
}

.hero-panel {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: 24px;
  overflow: hidden;
  margin-bottom: 24px;
}

.hero-panel img {
  width: 100%;
  height: 100%;
  min-height: 320px;
  object-fit: cover;
}

.hero-content {
  padding: 28px;
}

.hero-content h1 {
  margin: 16px 0;
  font-size: 42px;
}

.hero-content p {
  color: var(--text-sub);
  line-height: 1.9;
}

.facts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-top: 24px;
}

.facts div {
  padding: 16px;
  border-radius: 18px;
  background: rgba(162, 75, 42, 0.06);
}

.facts strong {
  display: block;
  margin-bottom: 8px;
}

.info-row {
  margin-bottom: 24px;
}

.article-card,
.side-card {
  padding: 24px;
}

.article-text {
  line-height: 2;
  white-space: pre-line;
}

.side-column {
  display: grid;
  gap: 20px;
}

.inheritor-item + .inheritor-item {
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
}

.inheritor-item h3 {
  margin: 0 0 6px;
}

.inheritor-item p,
.inheritor-item span {
  margin: 0;
  color: var(--text-sub);
  line-height: 1.8;
}

.meta-list {
  margin: 0;
  padding-left: 18px;
  color: var(--text-sub);
  line-height: 2;
}

.gallery-row,
.recommend-row {
  margin-top: 28px;
}

.gallery-grid,
.recommend-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.gallery-grid img,
.recommend-card {
  width: 100%;
  min-height: 220px;
  object-fit: cover;
}

.recommend-card {
  padding: 24px;
}

.recommend-card h3 {
  margin: 0 0 12px;
}

.recommend-card p {
  margin: 0;
  color: var(--text-sub);
  line-height: 1.8;
}

@media (max-width: 960px) {
  .hero-panel,
  .gallery-grid,
  .recommend-grid {
    grid-template-columns: 1fr;
  }
}
</style>
