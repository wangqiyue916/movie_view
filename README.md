# 电影点评系统

本项目采用前后端分离架构：

- `backend`：Spring Boot 后端基础框架。
- `frontend`：Vue 3 + Vite 前端基础框架。
- `sql`：数据库建表和初始化脚本。
- `docs`：需求、设计、规范和上手文档。

## 快速启动

### 后端

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:8080
```

健康检查：

```text
GET http://localhost:8080/api/health
```

### 前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

## 数据库

数据库脚本位于 `sql` 目录。

执行顺序：

1. `sql/schema.sql`
2. `sql/init-data.sql`

数据库密码请通过环境变量 `DB_PASSWORD` 配置，不要写入代码或文档。

云数据库连接环境变量示例：

```text
DB_HOST=dpshmy-ugp2kxl2hqqylbtj-pub.proxy.dms.aliyuncs.com
DB_PORT=3306
DB_NAME=movie
DB_USERNAME=nhH6IpERCnuCcjhV6h0okozZ
DB_PASSWORD=<数据库密码>
```

## 文档阅读

新成员先阅读：

- `docs/16-文档阅读引导与成员上手指南.md`
- `docs/06-分工与模块边界文档.md`
- `docs/14-后端模块详细设计与类设计文档.md`
- `docs/15-前端页面与组件设计文档.md`

