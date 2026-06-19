-- ============================================================
-- SignalDesk full database bootstrap script
-- Version: 1.0.0
-- Target: MySQL 8 + InnoDB
-- Charset: utf8mb4 / utf8mb4_unicode_ci
--
-- Usage:
--   mysql -u root -p < create_signaldesk_database.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS signaldesk
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE signaldesk;

-- ============================================================
-- 1. users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL,
    email         VARCHAR(128) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name  VARCHAR(64),
    avatar_url    VARCHAR(512),
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 2. sources
-- ============================================================
CREATE TABLE IF NOT EXISTS sources (
    id                 BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT       NOT NULL,
    title              VARCHAR(255) NOT NULL,
    url                VARCHAR(2048),
    source_type        VARCHAR(32)  NOT NULL COMMENT 'URL,RSS,PDF,NOTE',
    description        TEXT,
    status             VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE,PAUSED,ARCHIVED',
    fetch_interval_min INT          NOT NULL DEFAULT 360 COMMENT 'Fetch interval in minutes',
    last_fetched_at    DATETIME,
    last_change_at     DATETIME     COMMENT 'Last detected content change time',
    next_fetch_at      DATETIME,
    is_pinned          TINYINT(1)   NOT NULL DEFAULT 0,
    created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_user_status (user_id, status),
    INDEX idx_user_next_fetch (user_id, next_fetch_at),
    INDEX idx_user_pinned (user_id, is_pinned),

    CONSTRAINT fk_source_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 3. source_tags
-- ============================================================
CREATE TABLE IF NOT EXISTS source_tags (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(64)  NOT NULL,
    color       VARCHAR(7)   COMMENT 'Hex color, e.g. #3B82F6',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_user_tag (user_id, name),

    CONSTRAINT fk_tag_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 4. source_tag_relations
-- ============================================================
CREATE TABLE IF NOT EXISTS source_tag_relations (
    id          BIGINT   AUTO_INCREMENT PRIMARY KEY,
    source_id   BIGINT   NOT NULL,
    tag_id      BIGINT   NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_source_tag (source_id, tag_id),
    INDEX idx_tag_source (tag_id, source_id),

    CONSTRAINT fk_str_source FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE CASCADE,
    CONSTRAINT fk_str_tag FOREIGN KEY (tag_id) REFERENCES source_tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 5. crawl_tasks
-- ============================================================
CREATE TABLE IF NOT EXISTS crawl_tasks (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    source_id     BIGINT       NOT NULL,
    user_id       BIGINT       NOT NULL,
    trigger_type  VARCHAR(16)  NOT NULL COMMENT 'MANUAL,SCHEDULED,RETRY',
    status        VARCHAR(16)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING,FETCHING,PARSING,COMPLETED,FAILED,CANCELLED',
    error_message TEXT,
    retry_count   INT          NOT NULL DEFAULT 0,
    max_retries   INT          NOT NULL DEFAULT 3,
    started_at    DATETIME,
    completed_at  DATETIME,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_task_source (source_id, created_at DESC),
    INDEX idx_task_user_status (user_id, status),
    INDEX idx_task_status_created (status, created_at),

    CONSTRAINT fk_task_source FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 6. crawl_task_logs
-- ============================================================
CREATE TABLE IF NOT EXISTS crawl_task_logs (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    task_id     BIGINT       NOT NULL,
    step        VARCHAR(32)  NOT NULL COMMENT 'FETCH,PARSE,DEDUP,INDEX,SUMMARIZE',
    status      VARCHAR(16)  NOT NULL COMMENT 'SUCCESS,FAILURE,SKIPPED',
    message     TEXT,
    duration_ms INT,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_log_task (task_id, created_at),

    CONSTRAINT fk_log_task FOREIGN KEY (task_id) REFERENCES crawl_tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 7. documents
-- ============================================================
CREATE TABLE IF NOT EXISTS documents (
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    source_id     BIGINT        NOT NULL,
    user_id       BIGINT        NOT NULL,
    title         VARCHAR(512)  NOT NULL,
    content_text  MEDIUMTEXT    NOT NULL,
    content_hash  CHAR(64)      NOT NULL COMMENT 'SHA-256 of content_text for dedup',
    source_url    VARCHAR(2048),
    author        VARCHAR(255),
    published_at  DATETIME,
    word_count    INT,
    is_current    TINYINT(1)    NOT NULL DEFAULT 1 COMMENT 'Marks the latest current version',
    crawl_task_id BIGINT,
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_doc_source_current (source_id, is_current, created_at DESC),
    INDEX idx_doc_user (user_id, created_at DESC),
    INDEX idx_doc_hash (source_id, content_hash),
    INDEX idx_doc_created (created_at),

    FULLTEXT INDEX ft_doc_content (title, content_text),

    CONSTRAINT fk_doc_source FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_task FOREIGN KEY (crawl_task_id) REFERENCES crawl_tasks(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 8. document_versions
-- ============================================================
CREATE TABLE IF NOT EXISTS document_versions (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    document_id    BIGINT       NOT NULL,
    version_number INT          NOT NULL,
    content_text   MEDIUMTEXT   NOT NULL,
    content_hash   CHAR(64)     NOT NULL,
    change_summary VARCHAR(512) COMMENT 'Short summary of the change',
    crawl_task_id  BIGINT,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_doc_version (document_id, version_number),
    INDEX idx_version_doc (document_id, created_at DESC),

    CONSTRAINT fk_ver_doc FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    CONSTRAINT fk_ver_task FOREIGN KEY (crawl_task_id) REFERENCES crawl_tasks(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 9. summaries
-- ============================================================
CREATE TABLE IF NOT EXISTS summaries (
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

-- ============================================================
-- 10. summary_references
-- ============================================================
CREATE TABLE IF NOT EXISTS summary_references (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    summary_id  BIGINT       NOT NULL,
    document_id BIGINT       NOT NULL,
    ref_index   INT          NOT NULL COMMENT 'Reference number in the summary',
    quote_text  TEXT         COMMENT 'Quoted original text snippet',

    INDEX idx_ref_summary (summary_id),

    CONSTRAINT fk_ref_summary FOREIGN KEY (summary_id) REFERENCES summaries(id) ON DELETE CASCADE,
    CONSTRAINT fk_ref_document FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 11. saved_searches
-- ============================================================
CREATE TABLE IF NOT EXISTS saved_searches (
    id         BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT        NOT NULL,
    name       VARCHAR(128)  NOT NULL,
    query_json JSON          NOT NULL COMMENT 'Search condition JSON',
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_saved_user (user_id),

    CONSTRAINT fk_saved_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 12. audit_logs
-- ============================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    action      VARCHAR(64)   NOT NULL COMMENT 'SOURCE_CREATE,SOURCE_DELETE,TASK_TRIGGER,SUMMARY_GENERATE,etc.',
    target_type VARCHAR(32)   COMMENT 'SOURCE,DOCUMENT,TASK,SUMMARY',
    target_id   BIGINT,
    detail_json JSON          COMMENT 'Additional detail payload',
    ip_address  VARCHAR(45),
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_audit_user (user_id, created_at DESC),
    INDEX idx_audit_target (target_type, target_id),
    INDEX idx_audit_action (action, created_at DESC),

    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SHOW TABLES;
