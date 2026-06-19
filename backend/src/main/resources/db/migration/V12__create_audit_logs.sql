-- ============================================================
-- V12: audit_logs 表
-- 操作审计日志
-- ============================================================

CREATE TABLE audit_logs (
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT,
    action        VARCHAR(64)   NOT NULL COMMENT 'SOURCE_CREATE,SOURCE_DELETE,TASK_TRIGGER,SUMMARY_GENERATE,etc.',
    target_type   VARCHAR(32)   COMMENT 'SOURCE,DOCUMENT,TASK,SUMMARY',
    target_id     BIGINT,
    detail_json   JSON          COMMENT '补充详情',
    ip_address    VARCHAR(45),
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_audit_user (user_id, created_at DESC),
    INDEX idx_audit_target (target_type, target_id),
    INDEX idx_audit_action (action, created_at DESC),

    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
