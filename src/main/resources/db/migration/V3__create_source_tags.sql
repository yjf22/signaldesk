-- ============================================================
-- V3: source_tags 表
-- 标签字典
-- ============================================================

CREATE TABLE source_tags (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(64)  NOT NULL,
    color       VARCHAR(7)   COMMENT 'Hex color, e.g. #3B82F6',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_user_tag (user_id, name),

    CONSTRAINT fk_tag_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
