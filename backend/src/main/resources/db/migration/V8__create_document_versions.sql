-- ============================================================
-- V8: document_versions table
-- Historical versions of documents
-- ============================================================

CREATE TABLE document_versions (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    document_id     BIGINT       NOT NULL,
    version_number  INT          NOT NULL,
    content_text    MEDIUMTEXT   NOT NULL,
    content_hash    CHAR(64)     NOT NULL,
    change_summary  VARCHAR(512) COMMENT 'Short summary of the change',
    crawl_task_id   BIGINT,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_doc_version (document_id, version_number),
    INDEX idx_version_doc (document_id, created_at DESC),

    CONSTRAINT fk_ver_doc FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    CONSTRAINT fk_ver_task FOREIGN KEY (crawl_task_id) REFERENCES crawl_tasks(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
