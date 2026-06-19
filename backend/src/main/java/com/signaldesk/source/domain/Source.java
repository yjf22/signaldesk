package com.signaldesk.source.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sources")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Source {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 2048)
    private String url;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "source_type", nullable = false, length = 32)
    private SourceType sourceType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private SourceStatus status = SourceStatus.ACTIVE;

    @Column(name = "fetch_interval_min", nullable = false)
    @Builder.Default
    private Integer fetchIntervalMin = 360;

    @Column(name = "last_fetched_at")
    private LocalDateTime lastFetchedAt;

    @Column(name = "last_change_at")
    private LocalDateTime lastChangeAt;

    @Column(name = "next_fetch_at")
    private LocalDateTime nextFetchAt;

    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private Boolean isPinned = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (nextFetchAt == null) {
            nextFetchAt = LocalDateTime.now().plusMinutes(fetchIntervalMin != null ? fetchIntervalMin : 360);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Convenience method: schedule next fetch after a fetch completes
    public void scheduleNextFetch() {
        this.nextFetchAt = LocalDateTime.now().plusMinutes(fetchIntervalMin);
    }

    public void recordFetch() {
        this.lastFetchedAt = LocalDateTime.now();
        scheduleNextFetch();
    }

    public void recordChange() {
        this.lastChangeAt = LocalDateTime.now();
    }
}
