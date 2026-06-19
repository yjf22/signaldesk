-- ============================================================
-- V4: source_tag_relations 表
-- 信息源与标签多对多关系
-- ============================================================

CREATE TABLE source_tag_relations (
    id          BIGINT   AUTO_INCREMENT PRIMARY KEY,
    source_id   BIGINT   NOT NULL,
    tag_id      BIGINT   NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_source_tag (source_id, tag_id),
    INDEX idx_tag_source (tag_id, source_id),

    CONSTRAINT fk_str_source FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE CASCADE,
    CONSTRAINT fk_str_tag    FOREIGN KEY (tag_id)    REFERENCES source_tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
