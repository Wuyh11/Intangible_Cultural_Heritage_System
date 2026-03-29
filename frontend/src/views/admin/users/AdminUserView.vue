<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAdminUser, getAdminUsers, saveAdminUser } from '@/api/system'
import type { AdminUserItem } from '@/types/models'

const users = ref<AdminUserItem[]>([])
const dialogVisible = ref(false)

const form = reactive({
  id: undefined as number | undefined,
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1,
  roleCodes: ['EDITOR'] as string[]
})

async function loadUsers() {
  users.value = await getAdminUsers()
}

function openDialog(item?: AdminUserItem) {
  form.id = item?.id
  form.username = item?.username || ''
  form.password = ''
  form.nickname = item?.nickname || ''
  form.email = item?.email || ''
  form.phone = item?.phone || ''
  form.status = item?.status ?? 1
  form.roleCodes = item?.roleCodes?.length ? [...item.roleCodes] : ['EDITOR']
  dialogVisible.value = true
}

async function handleSave() {
  await saveAdminUser({ ...form })
  ElMessage.success('管理员保存成功')
  dialogVisible.value = false
  loadUsers()
}

async function handleDelete(item: AdminUserItem) {
  await ElMessageBox.confirm(`确认删除管理员“${item.username}”吗？`, '提示', { type: 'warning' })
  await deleteAdminUser(item.id)
  ElMessage.success('删除成功')
  loadUsers()
}

onMounted(loadUsers)
</script>

<template>
  <section class="glass-card panel">
    <div class="section-title">
      <div>
        <h2>管理员管理</h2>
        <p>仅超级管理员可维护后台账号与角色。</p>
      </div>
      <el-button type="primary" @click="openDialog()">新增管理员</el-button>
    </div>
    <el-table :data="users">
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="nickname" label="昵称" width="140" />
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column prop="phone" label="手机号" width="150" />
      <el-table-column label="角色" width="180">
        <template #default="{ row }">{{ row.roleCodes.join(', ') }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">{{ row.status === 1 ? '启用' : '停用' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text @click="openDialog(row)">编辑</el-button>
          <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑管理员' : '新增管理员'" width="600px">
      <el-form label-position="top">
        <div class="two-col">
          <el-form-item label="用户名"><el-input v-model="form.username" :disabled="Boolean(form.id)" /></el-form-item>
          <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        </div>
        <div class="two-col">
          <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password placeholder="编辑时可留空" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        </div>
        <div class="two-col">
          <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <el-form-item label="角色">
          <el-checkbox-group v-model="form.roleCodes">
            <el-checkbox label="SUPER_ADMIN">SUPER_ADMIN</el-checkbox>
            <el-checkbox label="EDITOR">EDITOR</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.panel {
  padding: 24px;
}
</style>
