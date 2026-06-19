package com.signaldesk.summary.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SummaryGenerateRequest {
    private Long sourceId;          // Summarize a specific source

    @Size(max = 512)
    private String searchQuery;     // Or summarize search results

    @Size(max = 255)
    private String title;           // Optional custom title
}
