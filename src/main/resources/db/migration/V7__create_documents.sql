-- ============================================================
-- V7: documents 表
-- 采集到的文档/内容
-- ============================================================

CREATE TABLE documents (
    id              BIGINT        AUTO_INCREMENT PRIMARY KEY,
    source_id       BIGINT        NOT NULL,
    user_id         BIGINT        NOT NULL,
    title           VARCHAR(512)  NOT NULL,
    content_text    MEDIUMTEXT    NOT NULL,
    content_hash    CHAR(64)      NOT NULL COMMENT 'SHA-256 of content_text for dedup',
    source_url      VARCHAR(2048),
    author          VARCHAR(255),
    published_at    DATETIME,
    word_count      INT,
    is_current      TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '标记为当前最新版本',
    crawl_task_id   BIGINT,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_doc_source_current (source_id, is_current, created_at DESC),
    INDEX idx_doc_user (user_id, created_at DESC),
    INDEX idx_doc_hash (source_id, content_hash),
    INDEX idx_doc_created (created_at),

    FULLTEXT INDEX ft_doc_content (title, content_text),

    CONSTRAINT fk_doc_source FOREIGN KEY (source_id)    REFERENCES sources(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_user   FOREIGN KEY (user_id)      REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_task   FOREIGN KEY (crawl_task_id) REFERENCES crawl_tasks(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
