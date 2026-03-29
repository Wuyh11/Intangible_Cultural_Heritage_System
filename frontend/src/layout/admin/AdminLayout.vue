<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const menuItems = computed(() => {
  const base = [
    { index: '/admin', label: '仪表盘' },
    { index: '/admin/projects', label: '项目管理' },
    { index: '/admin/inheritors', label: '传承人管理' },
    { index: '/admin/basic', label: '分类地区管理' }
  ]
  if (authStore.isSuperAdmin) {
    base.push({ index: '/admin/users', label: '管理员管理' })
    base.push({ index: '/admin/logs', label: '操作日志' })
  }
  return base
})

function handleLogout() {
  authStore.logout()
  router.push('/admin/login')
}
</script>

<template>
  <div class="admin-layout">
    <aside class="aside">
      <RouterLink class="brand" to="/admin">湖北省非遗后台</RouterLink>
      <el-menu :default-active="route.path" router>
        <el-menu-item v-for="item in menuItems" :key="item.index" :index="item.index">
          {{ item.label }}
        </el-menu-item>
      </el-menu>
    </aside>
    <div class="content">
      <header class="topbar">
        <div>
          <h2>{{ authStore.profile?.nickname || '管理员' }}</h2>
          <p>{{ authStore.roles.join(' / ') }}</p>
        </div>
        <div class="toolbar">
          <RouterLink to="/">返回门户</RouterLink>
          <el-button type="primary" plain @click="handleLogout">退出登录</el-button>
        </div>
      </header>
      <main class="main-panel">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  min-height: 100vh;
}

.aside {
  padding: 28px 18px;
  background: linear-gradient(180deg, #3f2217 0%, #5f3321 100%);
  color: #fff7ef;
}

.brand {
  display: block;
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 700;
}

.content {
  min-width: 0;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 28px;
}

.topbar h2 {
  margin: 0;
}

.topbar p {
  margin: 6px 0 0;
  color: var(--text-sub);
}

.main-panel {
  padding: 0 28px 28px;
}

@media (max-width: 960px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }
}
</style>
