import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/layout/public/PublicLayout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/public/home/HomeView.vue')
        },
        {
          path: 'projects',
          name: 'public-projects',
          component: () => import('@/views/public/projects/ProjectListView.vue')
        },
        {
          path: 'projects/:id',
          name: 'public-project-detail',
          component: () => import('@/views/public/projects/ProjectDetailView.vue')
        },
        {
          path: 'map',
          name: 'public-map',
          component: () => import('@/views/public/map/MapView.vue')
        },
        {
          path: 'stats',
          name: 'public-stats',
          component: () => import('@/views/public/stats/StatsView.vue')
        }
      ]
    },
    {
      path: '/admin/login',
      name: 'admin-login',
      component: () => import('@/views/admin/auth/AdminLoginView.vue')
    },
    {
      path: '/admin',
      component: () => import('@/layout/admin/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'admin-dashboard',
          component: () => import('@/views/admin/dashboard/AdminDashboardView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'projects',
          name: 'admin-projects',
          component: () => import('@/views/admin/projects/ProjectManageView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'inheritors',
          name: 'admin-inheritors',
          component: () => import('@/views/admin/inheritors/InheritorManageView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'basic',
          name: 'admin-basic',
          component: () => import('@/views/admin/basic/BasicDataView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/admin/users/AdminUserView.vue'),
          meta: { requiresAuth: true, roles: ['SUPER_ADMIN'] }
        },
        {
          path: 'logs',
          name: 'admin-logs',
          component: () => import('@/views/admin/logs/OperationLogView.vue'),
          meta: { requiresAuth: true, roles: ['SUPER_ADMIN'] }
        }
      ]
    }
  ],
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.path === '/admin/login' && authStore.isLoggedIn) {
    return { name: 'admin-dashboard' }
  }
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return { name: 'admin-login', query: { redirect: to.fullPath } }
  }
  if (to.meta.roles) {
    const roles = to.meta.roles as string[]
    const allowed = roles.some((role) => authStore.roles.includes(role))
    if (!allowed) {
      return { name: 'admin-dashboard' }
    }
  }
  return true
})

export default router
