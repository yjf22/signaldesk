package com.signaldesk.document.dto;

import com.signaldesk.document.domain.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DocumentResponse {
    private Long id;
    private Long sourceId;
    private String title;
    private String contentText;
    private String contentHash;
    private String sourceUrl;
    private String author;
    private String publishedAt;
    private Integer wordCount;
    private Boolean isCurrent;
    private Long crawlTaskId;
    private String createdAt;

    public static DocumentResponse from(Document doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .sourceId(doc.getSourceId())
                .title(doc.getTitle())
                .contentText(doc.getContentText())
                .contentHash(doc.getContentHash())
                .sourceUrl(doc.getSourceUrl())
                .author(doc.getAuthor())
                .publishedAt(doc.getPublishedAt() != null ? doc.getPublishedAt().toString() : null)
                .wordCount(doc.getWordCount())
                .isCurrent(doc.getIsCurrent())
                .crawlTaskId(doc.getCrawlTaskId())
                .createdAt(doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : null)
                .build();
    }
}
