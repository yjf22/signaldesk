package com.signaldesk.search.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SearchRequest {
    private String keyword;
    private String sourceType;
    private Long sourceId;
    private String sort = "relevance";  // relevance | date
    private Integer page = 0;
    private Integer size = 20;
}
