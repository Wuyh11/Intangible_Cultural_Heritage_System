# Backend

## 技术栈

- Spring Boot 3.2.x
- Java 17
- MyBatis-Plus
- Spring Security + JWT
- MySQL
- Redis
- springdoc-openapi
- EasyExcel

## 配置

复制并重命名以下文件后再填写真实环境变量：

- `src/main/resources/application-dev.yml.example`
- `src/main/resources/application-prod.yml.example`

## 接口分层

- `auth`：管理员登录
- `heritage`：项目、传承人、分类、地区
- `system`：管理员账户、操作日志
- `stats`：统计分析、地图点位、导出

## 约定

- 公开端接口统一放在 `/api/public/**`
- 管理端接口统一放在 `/api/admin/**`
- 上传资源目录通过 `storage.upload-dir` 配置
- Swagger 文档地址：`/swagger-ui.html`
