-- ============================================================
-- V9: summaries 表
-- AI 摘要记录
-- ============================================================

CREATE TABLE summaries (
    id              BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    source_id       BIGINT        COMMENT 'NULL 表示对搜索结果摘要',
    search_query    VARCHAR(512)  COMMENT '触发摘要的搜索条件(JSON)',
    title           VARCHAR(255),
    content_md      MEDIUMTEXT    COMMENT 'Markdown 格式摘要结果',
    prompt_tokens   INT           COMMENT '输入 token 数',
    completion_tokens INT         COMMENT '输出 token 数',
    model_name      VARCHAR(64)   COMMENT '使用的 AI 模型',
    status          VARCHAR(16)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING,GATHERING,PROMPTING,COMPLETED,FAILED',
    error_message   TEXT,
    completed_at    DATETIME,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_summary_user (user_id, created_at DESC),
    INDEX idx_summary_source (source_id, created_at DESC),
    INDEX idx_summary_status (status),

    CONSTRAINT fk_summary_user   FOREIGN KEY (user_id)   REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_summary_source FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
