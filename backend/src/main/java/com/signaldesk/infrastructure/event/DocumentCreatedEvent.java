package com.signaldesk.infrastructure.event;

import lombok.Getter;

@Getter
public class DocumentCreatedEvent extends DomainEvent {
    private final Long documentId;
    private final Long sourceId;
    private final Long userId;

    public DocumentCreatedEvent(Long documentId, Long sourceId, Long userId) {
        super("DOCUMENT_CREATED");
        this.documentId = documentId;
        this.sourceId = sourceId;
        this.userId = userId;
    }
}
