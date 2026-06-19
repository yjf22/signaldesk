package com.signaldesk.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RecentChangeResponse {
    private Long documentId;
    private Long sourceId;
    private String sourceTitle;
    private String changeType;  // NEW, UPDATED
    private String snippet;
    private String changedAt;
}
