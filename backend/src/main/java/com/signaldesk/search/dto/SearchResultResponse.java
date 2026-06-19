package com.signaldesk.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SearchResultResponse {
    private Long id;
    private String title;
    private String sourceTitle;
    private Long sourceId;
    private String sourceType;
    private java.util.List<String> tags;
    private String snippet;
    private String fetchedAt;
}
