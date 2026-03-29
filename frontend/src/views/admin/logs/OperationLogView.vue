<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getOperationLogs } from '@/api/system'
import type { OperationLogItem } from '@/types/models'

const loading = ref(false)
const tableData = ref<OperationLogItem[]>([])
const total = ref(0)
const query = reactive({
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

async function loadLogs() {
  loading.value = true
  try {
    const result = await getOperationLogs(query)
    tableData.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

onMounted(loadLogs)
</script>

<template>
  <section class="glass-card panel">
    <div class="section-title">
      <div>
        <h2>操作日志</h2>
        <p>记录后台写操作，便于答辩演示审计与可追溯能力。</p>
      </div>
    </div>
    <el-form :inline="true" class="toolbar">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="用户名、模块或动作" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="query.pageNum = 1; loadLogs()">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="tableData">
      <el-table-column prop="username" label="操作人" width="120" />
      <el-table-column prop="module" label="模块" width="120" />
      <el-table-column prop="action" label="动作" width="140" />
      <el-table-column prop="httpMethod" label="方法" width="100" />
      <el-table-column prop="requestUri" label="接口地址" min-width="220" />
      <el-table-column prop="requestIp" label="IP" width="140" />
      <el-table-column prop="createdAt" label="时间" width="180" />
    </el-table>
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        layout="prev, pager, next, total"
        :total="total"
        @current-change="loadLogs"
      />
    </div>
  </section>
</template>

<style scoped>
.panel {
  padding: 24px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
}
</style>
