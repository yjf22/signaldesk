package com.signaldesk.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EventListener {

    private static final Logger log = LoggerFactory.getLogger(EventListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDocumentCreated(DocumentCreatedEvent event) {
        log.debug("Document created: docId={}, sourceId={}", event.getDocumentId(), event.getSourceId());
        // IndexService.indexDocument(event) would be called here
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDocumentUpdated(DocumentUpdatedEvent event) {
        log.debug("Document updated: docId={}, sourceId={}", event.getDocumentId(), event.getSourceId());
        // IndexService.updateDocument(event) would be called here
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCrawlCompleted(CrawlCompletedEvent event) {
        log.debug("Crawl completed: taskId={}, sourceId={}, docsCreated={}",
                event.getTaskId(), event.getSourceId(), event.getDocumentsCreated());
        // Trigger auto-summary generation if configured
    }
}
