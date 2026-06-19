-- ============================================================
-- V11: saved_searches 表 (P1)
-- 保存的搜索条件
-- ============================================================

CREATE TABLE saved_searches (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT        NOT NULL,
    name        VARCHAR(128)  NOT NULL,
    query_json  JSON          NOT NULL COMMENT '搜索条件(JSON)',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_saved_user (user_id),

    CONSTRAINT fk_saved_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
