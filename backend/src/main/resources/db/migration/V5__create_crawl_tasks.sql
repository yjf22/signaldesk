-- ============================================================
-- V5: crawl_tasks 表
-- 抓取任务记录
-- ============================================================

CREATE TABLE crawl_tasks (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    source_id       BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    trigger_type    VARCHAR(16)  NOT NULL COMMENT 'MANUAL,SCHEDULED,RETRY',
    status          VARCHAR(16)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING,FETCHING,PARSING,COMPLETED,FAILED,CANCELLED',
    error_message   TEXT,
    retry_count     INT          NOT NULL DEFAULT 0,
    max_retries     INT          NOT NULL DEFAULT 3,
    started_at      DATETIME,
    completed_at    DATETIME,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_task_source (source_id, created_at DESC),
    INDEX idx_task_user_status (user_id, status),
    INDEX idx_task_status_created (status, created_at),

    CONSTRAINT fk_task_source FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_user   FOREIGN KEY (user_id)   REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
