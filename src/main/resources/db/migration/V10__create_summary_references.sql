-- ============================================================
-- V10: summary_references 表
-- 摘要引用的来源
-- ============================================================

CREATE TABLE summary_references (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    summary_id  BIGINT       NOT NULL,
    document_id BIGINT       NOT NULL,
    ref_index   INT          NOT NULL COMMENT '摘要中的引用编号',
    quote_text  TEXT         COMMENT '引用的原文片段',

    INDEX idx_ref_summary (summary_id),

    CONSTRAINT fk_ref_summary  FOREIGN KEY (summary_id)  REFERENCES summaries(id) ON DELETE CASCADE,
    CONSTRAINT fk_ref_document FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
