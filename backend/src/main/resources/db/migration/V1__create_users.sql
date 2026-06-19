-- ============================================================
-- V1: users 表
-- 用户账户与个人信息
-- ============================================================

CREATE TABLE users (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL,
    email       VARCHAR(128) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(64),
    avatar_url  VARCHAR(512),
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
