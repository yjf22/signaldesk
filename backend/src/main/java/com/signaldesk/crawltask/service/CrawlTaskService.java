package com.signaldesk.crawltask.service;

import com.signaldesk.crawltask.domain.*;
import com.signaldesk.crawltask.dto.CrawlTaskLogResponse;
import com.signaldesk.crawltask.dto.CrawlTaskResponse;
import com.signaldesk.crawltask.repository.CrawlTaskLogRepository;
import com.signaldesk.crawltask.repository.CrawlTaskRepository;
import com.signaldesk.document.domain.Document;
import com.signaldesk.document.service.DedupService;
import com.signaldesk.document.service.DocumentService;
import com.signaldesk.infrastructure.event.CrawlCompletedEvent;
import com.signaldesk.infrastructure.event.EventPublisher;
import com.signaldesk.infrastructure.exception.BusinessException;
import com.signaldesk.infrastructure.exception.ErrorCode;
import com.signaldesk.source.domain.Source;
import com.signaldesk.source.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CrawlTaskService {

    private static final Logger log = LoggerFactory.getLogger(CrawlTaskService.class);

    private final CrawlTaskRepository taskRepository;
    private final CrawlTaskLogRepository logRepository;
    private final SourceRepository sourceRepository;
    private final DocumentService documentService;
    private final DedupService dedupService;
    private final FetchExecutor fetchExecutor;
    private final ParseExecutor parseExecutor;
    private final EventPublisher eventPublisher;

    public CrawlTaskService(CrawlTaskRepository taskRepository,
                            CrawlTaskLogRepository logRepository,
                            SourceRepository sourceRepository,
                            DocumentService documentService,
                            DedupService dedupService,
                            FetchExecutor fetchExecutor,
                            ParseExecutor parseExecutor,
                            EventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.logRepository = logRepository;
        this.sourceRepository = sourceRepository;
        this.documentService = documentService;
        this.dedupService = dedupService;
        this.fetchExecutor = fetchExecutor;
        this.parseExecutor = parseExecutor;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create and trigger a manual crawl task for a source.
     */
    public CrawlTaskResponse triggerManualFetch(Long userId, Long sourceId) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOURCE_NOT_FOUND));
        if (!source.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
        }

        // Check if there's already a running task
        boolean running = taskRepository.existsBySourceIdAndStatusIn(sourceId,
                List.of(TaskStatus.PENDING, TaskStatus.FETCHING, TaskStatus.PARSING));
        if (running) {
            throw new BusinessException(ErrorCode.TASK_ALREADY_RUNNING);
        }

        CrawlTask task = CrawlTask.builder()
                .sourceId(sourceId)
                .userId(userId)
                .triggerType(TriggerType.MANUAL)
                .status(TaskStatus.PENDING)
                .build();
        taskRepository.save(task);

        // Execute asynchronously
        executeTask(task.getId());

        return CrawlTaskResponse.from(task);
    }

    /**
     * Trigger fetch for scheduled tasks.
     */
    public void triggerScheduledFetch(Long sourceId, Long userId) {
        boolean running = taskRepository.existsBySourceIdAndStatusIn(sourceId,
                List.of(TaskStatus.PENDING, TaskStatus.FETCHING, TaskStatus.PARSING));
        if (running) {
            log.debug("Skip scheduled fetch for source {} because a task is already running", sourceId);
            return;
        }

        CrawlTask task = CrawlTask.builder()
                .sourceId(sourceId)
                .userId(userId)
                .triggerType(TriggerType.SCHEDULED)
                .status(TaskStatus.PENDING)
                .build();
        taskRepository.save(task);
        executeTask(task.getId());
    }

    /**
     * Retry a failed task.
     */
    public CrawlTaskResponse retryTask(Long userId, Long taskId) {
        CrawlTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));
        if (!task.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        if (!task.canRetry()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Max retries exceeded");
        }

        task.setRetryCount(task.getRetryCount() + 1);
        task.setTriggerType(TriggerType.RETRY);
        task.setStatus(TaskStatus.PENDING);
        task.setErrorMessage(null);
        taskRepository.save(task);

        executeTask(task.getId());
        return CrawlTaskResponse.from(task);
    }

    @Transactional(readOnly = true)
    public CrawlTaskResponse getTaskStatus(Long userId, Long taskId) {
        CrawlTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));
        if (!task.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        return CrawlTaskResponse.from(task);
    }

    @Transactional(readOnly = true)
    public com.signaldesk.infrastructure.dto.ApiResponse.PagedData<CrawlTaskResponse> getTasksBySource(
            Long userId, Long sourceId, int page, int size) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOURCE_NOT_FOUND));
        if (!source.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
        }

        int normalizedPage = Math.max(page - 1, 0);
        Page<CrawlTask> tasks = taskRepository.findBySourceIdOrderByCreatedAtDesc(
                sourceId, PageRequest.of(normalizedPage, size));
        List<CrawlTaskResponse> content = tasks.getContent().stream()
                .map(CrawlTaskResponse::from)
                .toList();
        return new com.signaldesk.infrastructure.dto.ApiResponse.PagedData<>(
                content, tasks.getNumber() + 1, tasks.getSize(),
                tasks.getTotalElements(), tasks.getTotalPages());
    }

    @Transactional(readOnly = true)
    public List<CrawlTaskLogResponse> getTaskLogs(Long userId, Long taskId) {
        CrawlTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));
        if (!task.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        return logRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(CrawlTaskLogResponse::from)
                .toList();
    }

    // ---- Core execution logic ----

    @Async("crawlExecutor")
    public void executeTask(Long taskId) {
        CrawlTask task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return;

        try {
            // Step 1: Fetch
            task.markStarted();
            taskRepository.save(task);
            logStep(taskId, "FETCH", "SUCCESS", "Fetch started", 0);

            Source source = sourceRepository.findById(task.getSourceId()).orElse(null);
            if (source == null) {
                task.markFailed("Source not found");
                taskRepository.save(task);
                return;
            }

            long fetchStart = System.currentTimeMillis();
            FetchExecutor.FetchResult fetchResult = fetchExecutor.fetch(source);
            long fetchDuration = System.currentTimeMillis() - fetchStart;

            if (!fetchResult.isSuccess()) {
                logStep(taskId, "FETCH", "FAILURE",
                        "HTTP " + fetchResult.statusCode(), (int) fetchDuration);
                task.markFailed("Fetch failed with status: " + fetchResult.statusCode());
                taskRepository.save(task);
                return;
            }
            logStep(taskId, "FETCH", "SUCCESS",
                    "Fetched " + fetchResult.body().length + " bytes", (int) fetchDuration);

            // Step 2: Parse
            task.setStatus(TaskStatus.PARSING);
            taskRepository.save(task);

            long parseStart = System.currentTimeMillis();
            List<ParseExecutor.ParsedItem> items = parseExecutor.parse(
                    fetchResult.bodyAsString(), fetchResult.contentType(), source.getUrl());
            long parseDuration = System.currentTimeMillis() - parseStart;

            logStep(taskId, "PARSE", "SUCCESS",
                    "Parsed " + items.size() + " items", (int) parseDuration);

            // Step 3: Dedup + Store
            int docsCreated = 0;
            int docsUpdated = 0;
            for (ParseExecutor.ParsedItem item : items) {
                String contentHash = dedupService.computeHash(item.content());

                Document doc = Document.builder()
                        .sourceId(source.getId())
                        .userId(task.getUserId())
                        .title(item.title())
                        .contentText(item.content())
                        .contentHash(contentHash)
                        .sourceUrl(item.url() != null ? item.url() : source.getUrl())
                        .author(item.author())
                        .wordCount(item.wordCount())
                        .crawlTaskId(task.getId())
                        .build();

                DocumentService.UpsertResult upsertResult = documentService.upsertDocument(doc);
                if (upsertResult.changed()) {
                    if (upsertResult.created()) {
                        docsCreated++;
                    } else {
                        docsUpdated++;
                    }
                }
            }

            logStep(taskId, "DEDUP", "SUCCESS",
                    docsCreated + " new, " + docsUpdated + " updated, "
                            + (items.size() - docsCreated - docsUpdated) + " unchanged", 0);

            // Step 4: Update source
            source.recordFetch();
            if (docsCreated + docsUpdated > 0) {
                source.recordChange();
            }
            sourceRepository.save(source);

            // Mark task completed
            task.markCompleted();
            taskRepository.save(task);

            // Publish event
            eventPublisher.publish(new CrawlCompletedEvent(
                    task.getId(), source.getId(), task.getUserId(), docsCreated + docsUpdated));

            log.info("Crawl task {} completed: {} new documents, {} updated documents",
                    taskId, docsCreated, docsUpdated);

        } catch (Exception e) {
            log.error("Crawl task {} failed", taskId, e);
            logStep(taskId, "EXECUTE", "FAILURE", e.getMessage(), 0);
            task.markFailed(e.getMessage());
            taskRepository.save(task);
        }
    }

    private void logStep(Long taskId, String step, String status, String message, int durationMs) {
        CrawlTaskLog logEntry = CrawlTaskLog.builder()
                .taskId(taskId)
                .step(step)
                .status(status)
                .message(message)
                .durationMs(durationMs)
                .build();
        logRepository.save(logEntry);
    }
}
