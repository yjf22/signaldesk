-- ============================================================
-- SignalDesk 种子数据 (开发环境)
-- 使用方式: Flyway callback 或手动执行
-- 演示密码: test123 (BCrypt hash)
-- ============================================================

-- 测试用户
INSERT INTO users (username, email, password_hash, display_name)
VALUES ('demo', 'demo@signaldesk.dev', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Demo User')
ON DUPLICATE KEY UPDATE username = username;

-- 示例标签
INSERT INTO source_tags (user_id, name, color) VALUES
(1, '招聘',   '#EF4444'),
(1, '技术博客', '#3B82F6'),
(1, '文档',   '#10B981'),
(1, '学习资料', '#F59E0B')
ON DUPLICATE KEY UPDATE name = name;

-- 示例信息源
INSERT INTO sources (user_id, title, url, source_type, status) VALUES
(1, '字节跳动校招',        'https://jobs.bytedance.com/campus',  'URL', 'ACTIVE'),
(1, '美团技术团队',        'https://tech.meituan.com/feed',       'RSS', 'ACTIVE'),
(1, 'Spring Boot 3 迁移指南', 'https://example.com/spring-boot3-guide', 'URL', 'ACTIVE')
ON DUPLICATE KEY UPDATE title = title;
