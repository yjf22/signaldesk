package com.signaldesk.infrastructure.event;

import lombok.Getter;

@Getter
public class DocumentUpdatedEvent extends DomainEvent {
    private final Long documentId;
    private final Long sourceId;
    private final Long userId;
    private final Integer versionNumber;

    public DocumentUpdatedEvent(Long documentId, Long sourceId, Long userId, Integer versionNumber) {
        super("DOCUMENT_UPDATED");
        this.documentId = documentId;
        this.sourceId = sourceId;
        this.userId = userId;
        this.versionNumber = versionNumber;
    }
}
