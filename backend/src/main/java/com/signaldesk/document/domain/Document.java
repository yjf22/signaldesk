package com.signaldesk.document.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(name = "content_text", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String contentText;

    @Column(name = "content_hash", nullable = false, columnDefinition = "CHAR(64)")
    private String contentHash;

    @Column(name = "source_url", length = 2048)
    private String sourceUrl;

    @Column(length = 255)
    private String author;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "is_current", nullable = false)
    @Builder.Default
    private Boolean isCurrent = true;

    @Column(name = "crawl_task_id")
    private Long crawlTaskId;

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
}
