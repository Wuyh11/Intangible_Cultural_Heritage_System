<script setup lang="ts">
defineProps<{
  title: string
  value: string | number
  description?: string
}>()
</script>

<template>
  <div class="glass-card stat-card">
    <div class="tag-chip">{{ title }}</div>
    <div class="value">{{ value }}</div>
    <p>{{ description }}</p>
  </div>
</template>

<style scoped>
.stat-card {
  padding: 24px;
}

.value {
  margin: 18px 0 10px;
  font-size: 38px;
  font-weight: 700;
  color: var(--brand-deep);
}

p {
  margin: 0;
  color: var(--text-sub);
}
</style>
