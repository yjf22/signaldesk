-- ============================================================
-- V2: sources table
-- Information source definitions (URL/RSS/PDF/NOTE)
-- ============================================================

CREATE TABLE sources (
    id                  BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT       NOT NULL,
    title               VARCHAR(255) NOT NULL,
    url                 VARCHAR(2048),
    source_type         VARCHAR(32)  NOT NULL COMMENT 'URL,RSS,PDF,NOTE',
    description         TEXT,
    status              VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE,PAUSED,ARCHIVED',
    fetch_interval_min  INT          NOT NULL DEFAULT 360 COMMENT 'Fetch interval in minutes',
    last_fetched_at     DATETIME,
    last_change_at      DATETIME     COMMENT 'Last detected content change time',
    next_fetch_at       DATETIME,
    is_pinned           TINYINT(1)   NOT NULL DEFAULT 0,
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_user_status (user_id, status),
    INDEX idx_user_next_fetch (user_id, next_fetch_at),
    INDEX idx_user_pinned (user_id, is_pinned),

    CONSTRAINT fk_source_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
