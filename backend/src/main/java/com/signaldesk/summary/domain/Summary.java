package com.signaldesk.summary.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "summaries")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "search_query", length = 512)
    private String searchQuery;

    @Column(length = 255)
    private String title;

    @Column(name = "content_md", columnDefinition = "MEDIUMTEXT")
    private String contentMd;

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @Column(name = "model_name", length = 64)
    private String modelName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private SummaryStatus status = SummaryStatus.PENDING;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

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

    public void markCompleted(String contentMd, String modelName,
                              Integer promptTokens, Integer completionTokens) {
        this.status = SummaryStatus.COMPLETED;
        this.contentMd = contentMd;
        this.modelName = modelName;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = SummaryStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }
}
