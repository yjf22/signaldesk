package com.signaldesk.crawltask.dto;

import com.signaldesk.crawltask.domain.CrawlTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CrawlTaskResponse {
    private Long id;
    private Long sourceId;
    private String triggerType;
    private String status;
    private String errorMessage;
    private Integer retryCount;
    private Integer maxRetries;
    private String startedAt;
    private String completedAt;
    private String createdAt;

    public static CrawlTaskResponse from(CrawlTask task) {
        return CrawlTaskResponse.builder()
                .id(task.getId())
                .sourceId(task.getSourceId())
                .triggerType(task.getTriggerType().name())
                .status(task.getStatus().name())
                .errorMessage(task.getErrorMessage())
                .retryCount(task.getRetryCount())
                .maxRetries(task.getMaxRetries())
                .startedAt(task.getStartedAt() != null ? task.getStartedAt().toString() : null)
                .completedAt(task.getCompletedAt() != null ? task.getCompletedAt().toString() : null)
                .createdAt(task.getCreatedAt() != null ? task.getCreatedAt().toString() : null)
                .build();
    }
}
