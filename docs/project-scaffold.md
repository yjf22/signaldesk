# SignalDesk 项目脚手架说明

## 1. 总体结构

```text
.
├─ backend/
├─ frontend/
├─ docs/
├─ docker/
├─ create_signaldesk_database.sql
└─ README.md
```

## 2. 后端结构

```text
backend/
├─ pom.xml
├─ Dockerfile
└─ src/
   └─ main/
      ├─ java/com/signaldesk/
      │  ├─ auth/
      │  ├─ user/
      │  ├─ source/
      │  ├─ document/
      │  ├─ crawltask/
      │  ├─ search/
      │  ├─ summary/
      │  ├─ dashboard/
      │  ├─ audit/
      │  └─ infrastructure/
      └─ resources/
         ├─ application.yml
         └─ db/migration/
```

说明：

- 后端以模块化单体方式组织
- 每个业务模块尽量包含自己的 controller、service、repository、dto
- 公共配置、异常处理、统一响应格式放在 `infrastructure`

## 3. 前端结构

```text
frontend/
├─ package.json
├─ vite.config.ts
├─ tsconfig.json
└─ src/
   ├─ api/
   ├─ components/
   ├─ pages/
   ├─ router/
   ├─ stores/
   └─ types/
```

说明：

- `pages/` 存放页面级组件
- `components/` 存放业务组件和通用组件
- `stores/` 存放 Pinia 状态管理
- `api/` 统一封装前后端请求
- `types/` 管理前端类型定义

## 4. 本地运行

### 后端

```bash
cd backend
mvn spring-boot:run
```

### 前端

```bash
cd frontend
npm install
npm run dev
```

### 依赖服务

建议准备以下服务：

- MySQL 8
- Redis
- Elasticsearch 或 OpenSearch

## 5. 数据库初始化

数据库脚本见：

- [create_signaldesk_database.sql](C:\Users\28145\Documents\New project 3\create_signaldesk_database.sql)

默认本地数据库配置：

- Host: `localhost`
- Port: `3307`
- Database: `signaldesk`
- Username: `root`

## 6. 建议补充内容

仓库继续打磨时，建议优先补：

- 根目录 `.gitignore`
- 项目截图
- 演示 GIF
- API 示例
- 部署说明
