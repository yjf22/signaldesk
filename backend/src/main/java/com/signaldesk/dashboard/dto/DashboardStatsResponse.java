package com.signaldesk.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private long todayNewDocuments;
    private long activeSourcesCount;
    private long recentFetchCount;
    private long pendingTaskCount;
}
