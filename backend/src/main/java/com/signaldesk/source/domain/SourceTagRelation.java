package com.signaldesk.source.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "source_tag_relations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SourceTagRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
