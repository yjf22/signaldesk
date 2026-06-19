package com.signaldesk.crawltask.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_task_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CrawlTaskLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(nullable = false, length = 32)
    private String step;  // FETCH, PARSE, DEDUP, INDEX, SUMMARIZE

    @Column(nullable = false, length = 16)
    private String status;  // SUCCESS, FAILURE, SKIPPED

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
