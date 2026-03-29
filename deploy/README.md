# 服务器部署说明

本目录用于服务器部署与联调说明。当前仓库已经补充了根目录 `docker-compose.yml`，可直接在服务器上通过 Docker Compose 拉起整套系统。

## 推荐目录

- 应用根目录：`/opt/hubei-ich/Intangible_Cultural_Heritage_System`
- MySQL 数据卷：Docker named volume `mysql_data`
- Redis 数据卷：Docker named volume `redis_data`
- 上传目录：Docker named volume `backend_uploads`

## 运行环境建议

- JDK 17
- MySQL 8.x
- Redis 6.x 或 7.x
- Nginx 1.20+

## 启动顺序

1. 从 GitHub 拉取仓库
2. 复制 `deploy/.env.server.example` 为 `.env`
3. 修改数据库密码、Redis 密码和 JWT 密钥
4. 执行 `docker compose up -d --build`
5. 通过 `docker compose ps` 和 `docker compose logs` 检查服务状态

## 访问约定

- 前端入口：`http://服务器IP/`
- 后端 API：`http://服务器IP/api/`
- Swagger：`http://服务器IP/api/` 通过 Nginx 反代到后端，或容器内访问 `http://backend:8080/swagger-ui.html`
- 上传资源：`http://服务器IP/uploads/...`

## Compose 服务

- `mysql`：自动导入 `sql/schema.sql` 和 `sql/data.sql`
- `redis`：启用密码和持久化
- `backend`：基于 Spring Boot 生产配置启动
- `frontend`：构建 Vue 静态站点并由 Nginx 提供服务

## 备注

- 服务器阶段可再补 `systemd`、`nginx.conf`、自动构建脚本和真正的发布命令。
