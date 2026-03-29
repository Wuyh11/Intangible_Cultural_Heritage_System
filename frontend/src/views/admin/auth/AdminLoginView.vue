<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginAdmin } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: 'Admin@123456'
})

async function handleLogin() {
  loading.value = true
  try {
    const result = await loginAdmin(form)
    authStore.setLogin(result)
    ElMessage.success('登录成功')
    router.push((route.query.redirect as string) || '/admin')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card glass-card">
      <div class="tag-chip">管理员入口</div>
      <h1>湖北省非遗后台管理台</h1>
      <p>用于维护项目名录、传承人档案、分类地区与统计导出。</p>
      <el-form label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入管理员用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" :loading="loading" size="large" style="width: 100%" @click="handleLogin">
          登录后台
        </el-button>
      </el-form>
      <RouterLink to="/">返回公众门户</RouterLink>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background:
    radial-gradient(circle at top, rgba(162, 75, 42, 0.18), transparent 24%),
    linear-gradient(180deg, #f4ebe0 0%, #f7f2ea 100%);
}

.login-card {
  width: min(460px, 100%);
  padding: 34px;
}

.login-card h1 {
  margin: 18px 0 12px;
  font-size: 34px;
}

.login-card p {
  margin: 0 0 28px;
  color: var(--text-sub);
  line-height: 1.8;
}
</style>
