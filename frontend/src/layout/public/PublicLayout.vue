<template>
  <div class="public-layout">
    <header class="header">
      <div class="page-shell nav-row">
        <RouterLink class="brand" to="/">湖北省非遗信息管理系统</RouterLink>
        <nav class="nav-links">
          <RouterLink to="/">首页</RouterLink>
          <RouterLink to="/projects">项目名录</RouterLink>
          <RouterLink to="/map">非遗地图</RouterLink>
          <RouterLink to="/stats">统计分析</RouterLink>
          <RouterLink class="admin-entry" to="/admin/login">管理入口</RouterLink>
        </nav>
      </div>
    </header>
    <main>
      <RouterView />
    </main>
    <footer class="footer">
      <div class="page-shell footer-box glass-card">
        <div>
          <strong>荆楚文脉，数字传承</strong>
          <p>以项目、传承人、地域分布和可视化分析构建湖北非遗数字化展示窗口。</p>
        </div>
        <span>Spring Boot + Vue 3 + MySQL + Redis</span>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 10;
  backdrop-filter: blur(18px);
  background: rgba(246, 239, 230, 0.75);
  border-bottom: 1px solid rgba(122, 76, 45, 0.08);
}

.nav-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 76px;
}

.brand {
  font-size: 24px;
  font-weight: 700;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 22px;
}

.nav-links .router-link-active {
  color: var(--brand);
}

.admin-entry {
  padding: 10px 16px;
  border-radius: 999px;
  background: rgba(162, 75, 42, 0.08);
}

.footer {
  padding: 0 0 28px;
}

.footer-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
}

.footer-box p {
  margin: 10px 0 0;
  color: var(--text-sub);
}

@media (max-width: 960px) {
  .nav-row,
  .footer-box {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .nav-links {
    flex-wrap: wrap;
  }
}
</style>
