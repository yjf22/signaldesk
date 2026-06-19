package com.signaldesk.source.dto;

import com.signaldesk.source.domain.SourceStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SourceListFilter {
    private SourceStatus status;
    private String type;
    private String sort = "recent";  // recent | title | created
    private Integer page = 0;
    private Integer size = 12;
}
