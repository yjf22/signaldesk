-- ============================================================
-- V6: crawl_task_logs 表
-- 抓取步骤日志
-- ============================================================

CREATE TABLE crawl_task_logs (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    task_id     BIGINT       NOT NULL,
    step        VARCHAR(32)  NOT NULL COMMENT 'FETCH, PARSE, DEDUP, INDEX, SUMMARIZE',
    status      VARCHAR(16)  NOT NULL COMMENT 'SUCCESS,FAILURE,SKIPPED',
    message     TEXT,
    duration_ms INT,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_log_task (task_id, created_at),

    CONSTRAINT fk_log_task FOREIGN KEY (task_id) REFERENCES crawl_tasks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
