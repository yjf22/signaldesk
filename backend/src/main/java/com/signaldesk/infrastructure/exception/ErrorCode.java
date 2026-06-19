package com.signaldesk.infrastructure.exception;

public enum ErrorCode {

    // Auth errors 10xxx
    INVALID_CREDENTIALS(10001, "Invalid username or password"),
    USERNAME_TAKEN(10002, "Username already taken"),
    EMAIL_TAKEN(10003, "Email already taken"),
    TOKEN_EXPIRED(10004, "Token expired"),
    TOKEN_INVALID(10005, "Token invalid"),
    UNAUTHORIZED(10006, "Authentication required"),

    // Resource errors 20xxx
    NOT_FOUND(20001, "Resource not found"),
    SOURCE_NOT_FOUND(20002, "Source not found"),
    DOCUMENT_NOT_FOUND(20003, "Document not found"),
    TASK_NOT_FOUND(20004, "Crawl task not found"),
    SUMMARY_NOT_FOUND(20005, "Summary not found"),
    TAG_NOT_FOUND(20006, "Tag not found"),
    USER_NOT_FOUND(20007, "User not found"),

    // Validation errors 30xxx
    VALIDATION_ERROR(30001, "Validation failed"),
    INVALID_SOURCE_TYPE(30002, "Invalid source type"),
    INVALID_STATUS_TRANSITION(30003, "Invalid status transition"),

    // Business errors 40xxx
    TASK_ALREADY_RUNNING(40001, "A task is already running for this source"),
    SUMMARY_ALREADY_GENERATING(40002, "A summary is already being generated"),
    NO_CONTENT_FOR_SUMMARY(40003, "Not enough content to generate summary"),
    SEARCH_SERVICE_UNAVAILABLE(40004, "Search service temporarily unavailable"),

    // Server errors 50xxx
    INTERNAL_ERROR(50001, "Internal server error"),
    AI_SERVICE_ERROR(50002, "AI service error"),
    CRAWL_EXECUTION_ERROR(50003, "Crawl execution error");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() { return code; }
    public String getDefaultMessage() { return defaultMessage; }
}
