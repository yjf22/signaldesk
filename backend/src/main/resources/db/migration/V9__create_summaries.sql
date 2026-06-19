-- ============================================================
-- V9: summaries table
-- AI-generated summaries
-- ============================================================

CREATE TABLE summaries (
    id                BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT        NOT NULL,
    source_id         BIGINT        COMMENT 'NULL means search-result summary',
    search_query      VARCHAR(512)  COMMENT 'Search condition payload or query JSON',
    title             VARCHAR(255),
    content_md        MEDIUMTEXT    COMMENT 'Summary content in Markdown',
    prompt_tokens     INT           COMMENT 'Input token count',
    completion_tokens INT           COMMENT 'Output token count',
    model_name        VARCHAR(64)   COMMENT 'AI model used',
    status            VARCHAR(16)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING,GATHERING,PROMPTING,COMPLETED,FAILED',
    error_message     TEXT,
    completed_at      DATETIME,
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_summary_user (user_id, created_at DESC),
    INDEX idx_summary_source (source_id, created_at DESC),
    INDEX idx_summary_status (status),

    CONSTRAINT fk_summary_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_summary_source FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
