package com.signaldesk.document.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_versions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "content_text", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String contentText;

    @Column(name = "content_hash", nullable = false, columnDefinition = "CHAR(64)")
    private String contentHash;

    @Column(name = "change_summary", length = 512)
    private String changeSummary;

    @Column(name = "crawl_task_id")
    private Long crawlTaskId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
