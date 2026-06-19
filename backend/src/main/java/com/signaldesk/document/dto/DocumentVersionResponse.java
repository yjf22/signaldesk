package com.signaldesk.document.dto;

import com.signaldesk.document.domain.DocumentVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DocumentVersionResponse {
    private Long id;
    private Long documentId;
    private Integer versionNumber;
    private String contentText;
    private String contentHash;
    private String changeSummary;
    private Long crawlTaskId;
    private String createdAt;

    public static DocumentVersionResponse from(DocumentVersion version) {
        return DocumentVersionResponse.builder()
                .id(version.getId())
                .documentId(version.getDocumentId())
                .versionNumber(version.getVersionNumber())
                .contentText(version.getContentText())
                .contentHash(version.getContentHash())
                .changeSummary(version.getChangeSummary())
                .crawlTaskId(version.getCrawlTaskId())
                .createdAt(version.getCreatedAt() != null ? version.getCreatedAt().toString() : null)
                .build();
    }
}
