package com.signaldesk.dashboard.service;

import com.signaldesk.crawltask.domain.TaskStatus;
import com.signaldesk.crawltask.repository.CrawlTaskRepository;
import com.signaldesk.dashboard.dto.DashboardStatsResponse;
import com.signaldesk.dashboard.dto.RecentChangeResponse;
import com.signaldesk.document.domain.Document;
import com.signaldesk.document.repository.DocumentRepository;
import com.signaldesk.source.domain.Source;
import com.signaldesk.source.domain.SourceStatus;
import com.signaldesk.source.repository.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final DocumentRepository documentRepository;
    private final SourceRepository sourceRepository;
    private final CrawlTaskRepository crawlTaskRepository;

    public DashboardService(DocumentRepository documentRepository,
                            SourceRepository sourceRepository,
                            CrawlTaskRepository crawlTaskRepository) {
        this.documentRepository = documentRepository;
        this.sourceRepository = sourceRepository;
        this.crawlTaskRepository = crawlTaskRepository;
    }

    public DashboardStatsResponse getStats(Long userId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        long todayNewDocs = documentRepository.countDocumentsByUserSince(userId, todayStart);
        long activeSources = sourceRepository.countByUserIdAndStatus(userId, SourceStatus.ACTIVE);

        // Recent fetches: count tasks completed today
        long recentFetches = crawlTaskRepository.countByUserIdAndStatusIn(userId,
                List.of(TaskStatus.COMPLETED));

        // Pending tasks: tasks in PENDING, FETCHING, PARSING states
        long pendingTasks = crawlTaskRepository.countByUserIdAndStatusIn(userId,
                List.of(TaskStatus.PENDING, TaskStatus.FETCHING, TaskStatus.PARSING));

        return DashboardStatsResponse.builder()
                .todayNewDocuments(todayNewDocs)
                .activeSourcesCount(activeSources)
                .recentFetchCount(recentFetches)
                .pendingTaskCount(pendingTasks)
                .build();
    }

    public List<RecentChangeResponse> getRecentChanges(Long userId) {
        List<Document> recentDocs = documentRepository
                .findTop10ByUserIdAndIsCurrentTrueOrderByCreatedAtDesc(userId);

        return recentDocs.stream()
                .map(doc -> {
                    Source source = sourceRepository.findById(doc.getSourceId()).orElse(null);
                    String sourceTitle = source != null ? source.getTitle() : "Unknown Source";
                    String changeType = "NEW"; // Simplified; in production check version count

                    String snippet = doc.getContentText();
                    if (snippet != null && snippet.length() > 200) {
                        snippet = snippet.substring(0, 200) + "...";
                    }

                    return RecentChangeResponse.builder()
                            .documentId(doc.getId())
                            .sourceId(doc.getSourceId())
                            .sourceTitle(sourceTitle)
                            .changeType(changeType)
                            .snippet(snippet)
                            .changedAt(doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : null)
                            .build();
                })
                .toList();
    }
}
