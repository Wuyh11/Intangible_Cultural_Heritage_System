# 服务器部署说明

本目录用于后续拿到 SSH 后完成服务器部署与联调。当前阶段先提供部署约定和文件组织建议。

## 推荐目录

- 应用根目录：`/data/hubei-ich`
- 后端 jar：`/data/hubei-ich/backend/app.jar`
- 前端静态文件：`/data/hubei-ich/frontend/dist`
- 上传目录：`/data/hubei-ich/uploads`
- 日志目录：`/data/hubei-ich/logs`

## 运行环境建议

- JDK 17
- MySQL 8.x
- Redis 6.x 或 7.x
- Nginx 1.20+

## 启动顺序

1. 导入 `sql/schema.sql`
2. 导入 `sql/data.sql`
3. 配置 Redis
4. 填写后端生产配置
5. 构建前后端产物
6. 先启动后端，再由 Nginx 提供前端静态资源并反向代理 API

## Nginx 约定

- `/` 指向前端静态目录
- `/api/` 反向代理到后端 `127.0.0.1:8080`
- `/uploads/` 指向文件上传目录

## 生产配置重点

- `spring.datasource.*`
- `spring.data.redis.*`
- `security.jwt.secret`
- `storage.upload-dir`
- `server.port`

## 备注

- 服务器阶段可再补 `systemd`、`nginx.conf`、自动构建脚本和真正的发布命令。
