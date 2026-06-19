package com.signaldesk.infrastructure.event;

import lombok.Getter;

@Getter
public class CrawlCompletedEvent extends DomainEvent {
    private final Long taskId;
    private final Long sourceId;
    private final Long userId;
    private final int documentsCreated;

    public CrawlCompletedEvent(Long taskId, Long sourceId, Long userId, int documentsCreated) {
        super("CRAWL_COMPLETED");
        this.taskId = taskId;
        this.sourceId = sourceId;
        this.userId = userId;
        this.documentsCreated = documentsCreated;
    }
}
