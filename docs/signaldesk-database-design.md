# SignalDesk 数据库设计

> 面向 `Java 17 + Spring Boot 3 + Maven + MySQL 8` 的数据库设计说明。

## 1. 核心表

- `users`
- `sources`
- `source_tags`
- `source_tag_relations`
- `crawl_tasks`
- `crawl_task_logs`
- `documents`
- `document_versions`
- `summaries`
- `summary_references`
- `saved_searches`
- `audit_logs`

## 2. 设计原则

- 默认使用 `utf8mb4`
- 业务主数据优先保存在 MySQL
- 列表查询和详情查询优先依赖结构化表与索引
- 搜索索引属于派生数据，不作为唯一数据来源
- 表结构变更通过 Flyway 管理

## 3. 存储职责

### MySQL

- 用户数据
- 信息源数据
- 抓取任务数据
- 文档与摘要主数据

### Redis

- 短期缓存
- 任务过程状态
- 可能的节流或防抖场景

### Elasticsearch / OpenSearch

- 文档全文检索
- 搜索结果高亮与相关性排序

## 4. 关键实体关系

### users

保存系统用户基础信息。

### sources

保存用户创建的信息源，包括：

- 标题
- URL
- 类型
- 状态
- 抓取间隔
- 最近抓取时间
- 下次抓取时间

### source_tags / source_tag_relations

用于标签与信息源的多对多关系管理。

### crawl_tasks

保存每一次抓取任务，包括：

- 来源信息源
- 触发方式
- 任务状态
- 开始时间
- 结束时间
- 结果说明
- 错误信息

### documents

保存某次抓取后的主文档内容，用于前端展示最新内容与最近变化。

### document_versions

用于后续支持版本对比、内容演进和差异展示。

### summaries

保存 AI 生成的摘要结果及状态。

### summary_references

保存摘要与原始文档之间的引用关系，提升结果可信度。

## 5. 索引建议

建议优先为以下字段建立索引：

- `sources.user_id`
- `sources.status`
- `sources.next_fetch_at`
- `crawl_tasks.source_id`
- `crawl_tasks.status`
- `crawl_tasks.created_at`
- `documents.source_id`
- `documents.created_at`
- `summaries.source_id`
- `summaries.created_at`

## 6. 迁移策略

- 使用 Flyway 管理数据库结构
- 只追加迁移脚本，不直接修改已执行脚本
- 本地与线上保持相同迁移逻辑

## 7. 本地开发说明

当前本地环境约定：

- MySQL 端口：`3307`
- 数据库名：`signaldesk`
- 用户名：`root`

数据库初始化可参考：

- [create_signaldesk_database.sql](C:\Users\28145\Documents\New project 3\create_signaldesk_database.sql)

## 8. 后续可扩展点

- 文档版本 diff 存储优化
- 失败任务重试记录表
- 更细粒度的用户偏好设置
- 团队空间与权限关联表
