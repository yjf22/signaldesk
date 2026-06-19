package com.signaldesk.summary.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "summary_references")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SummaryReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summary_id", nullable = false)
    private Long summaryId;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "ref_index", nullable = false)
    private Integer refIndex;

    @Column(name = "quote_text", columnDefinition = "TEXT")
    private String quoteText;
}
