-- ============================================================
-- SignalDesk seed data for local development
-- Demo password: test123 (BCrypt hash)
-- ============================================================

INSERT INTO users (username, email, password_hash, display_name)
VALUES ('demo', 'demo@signaldesk.dev', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Demo User')
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO source_tags (user_id, name, color) VALUES
(1, 'Recruiting', '#EF4444'),
(1, 'Tech Blog', '#3B82F6'),
(1, 'Docs', '#10B981'),
(1, 'Study', '#F59E0B')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO sources (user_id, title, url, source_type, status) VALUES
(1, 'Bytedance Campus Jobs', 'https://jobs.bytedance.com/campus', 'URL', 'ACTIVE'),
(1, 'Meituan Tech Feed', 'https://tech.meituan.com/feed', 'RSS', 'ACTIVE'),
(1, 'Spring Boot 3 Migration Guide', 'https://example.com/spring-boot3-guide', 'URL', 'ACTIVE')
ON DUPLICATE KEY UPDATE title = title;
