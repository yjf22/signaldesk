package com.signaldesk.source.dto;

import com.signaldesk.source.domain.SourceStatus;
import com.signaldesk.source.domain.SourceType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class SourceUpdateRequest {
    @Size(max = 255)
    private String title;

    @Size(max = 2048)
    private String url;

    private SourceType sourceType;

    private String description;

    private SourceStatus status;

    private Integer fetchIntervalMin;

    private Boolean isPinned;

    private List<Long> tagIds;
}
