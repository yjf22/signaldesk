package com.signaldesk.summary.dto;

import com.signaldesk.summary.domain.Summary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SummaryResponse {
    private Long id;
    private Long sourceId;
    private String searchQuery;
    private String title;
    private String contentMd;
    private String status;
    private String errorMessage;
    private String modelName;
    private Integer promptTokens;
    private Integer completionTokens;
    private List<SummaryReferenceResponse> references;
    private String completedAt;
    private String createdAt;

    public static SummaryResponse from(Summary summary, List<SummaryReferenceResponse> refs) {
        return SummaryResponse.builder()
                .id(summary.getId())
                .sourceId(summary.getSourceId())
                .searchQuery(summary.getSearchQuery())
                .title(summary.getTitle())
                .contentMd(summary.getContentMd())
                .status(summary.getStatus().name())
                .errorMessage(summary.getErrorMessage())
                .modelName(summary.getModelName())
                .promptTokens(summary.getPromptTokens())
                .completionTokens(summary.getCompletionTokens())
                .references(refs)
                .completedAt(summary.getCompletedAt() != null ? summary.getCompletedAt().toString() : null)
                .createdAt(summary.getCreatedAt() != null ? summary.getCreatedAt().toString() : null)
                .build();
    }
}
