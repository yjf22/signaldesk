package com.signaldesk.crawltask.domain;

public enum TaskStatus {
    PENDING,
    FETCHING,
    PARSING,
    COMPLETED,
    FAILED,
    CANCELLED
}
