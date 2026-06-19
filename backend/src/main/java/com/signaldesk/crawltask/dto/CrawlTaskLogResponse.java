package com.signaldesk.crawltask.dto;

import com.signaldesk.crawltask.domain.CrawlTaskLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CrawlTaskLogResponse {
    private Long id;
    private Long taskId;
    private String step;
    private String status;
    private String message;
    private Integer durationMs;
    private String createdAt;

    public static CrawlTaskLogResponse from(CrawlTaskLog log) {
        return CrawlTaskLogResponse.builder()
                .id(log.getId())
                .taskId(log.getTaskId())
                .step(log.getStep())
                .status(log.getStatus())
                .message(log.getMessage())
                .durationMs(log.getDurationMs())
                .createdAt(log.getCreatedAt() != null ? log.getCreatedAt().toString() : null)
                .build();
    }
}
