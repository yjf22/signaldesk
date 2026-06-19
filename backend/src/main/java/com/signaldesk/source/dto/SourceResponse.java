package com.signaldesk.source.dto;

import com.signaldesk.source.domain.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SourceResponse {
    private Long id;
    private String title;
    private String url;
    private String sourceType;
    private String description;
    private String status;
    private Integer fetchIntervalMin;
    private String lastFetchedAt;
    private String lastChangeAt;
    private String nextFetchAt;
    private Boolean isPinned;
    private List<TagResponse> tags;
    private String createdAt;

    public static SourceResponse from(Source source, List<TagResponse> tags) {
        return SourceResponse.builder()
                .id(source.getId())
                .title(source.getTitle())
                .url(source.getUrl())
                .sourceType(source.getSourceType().name())
                .description(source.getDescription())
                .status(source.getStatus().name())
                .fetchIntervalMin(source.getFetchIntervalMin())
                .lastFetchedAt(source.getLastFetchedAt() != null ? source.getLastFetchedAt().toString() : null)
                .lastChangeAt(source.getLastChangeAt() != null ? source.getLastChangeAt().toString() : null)
                .nextFetchAt(source.getNextFetchAt() != null ? source.getNextFetchAt().toString() : null)
                .isPinned(source.getIsPinned())
                .tags(tags)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toString() : null)
                .build();
    }
}
