package com.signaldesk.crawltask.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_tasks")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CrawlTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "trigger_type", nullable = false, length = 16)
    private TriggerType triggerType;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    @Builder.Default
    private Integer maxRetries = 3;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void markStarted() {
        this.status = TaskStatus.FETCHING;
        this.startedAt = LocalDateTime.now();
    }

    public void markCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = TaskStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    public boolean canRetry() {
        return retryCount < maxRetries;
    }
}
