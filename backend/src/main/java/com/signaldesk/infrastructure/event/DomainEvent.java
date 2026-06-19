package com.signaldesk.infrastructure.event;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public abstract class DomainEvent {

    private final String eventType;
    private final Instant occurredAt;

    protected DomainEvent(String eventType) {
        this.eventType = eventType;
        this.occurredAt = Instant.now();
    }
}
