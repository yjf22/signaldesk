package com.signaldesk.crawltask.controller;

import com.signaldesk.crawltask.dto.CrawlTaskLogResponse;
import com.signaldesk.crawltask.dto.CrawlTaskResponse;
import com.signaldesk.crawltask.service.CrawlTaskService;
import com.signaldesk.infrastructure.dto.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class CrawlTaskController {

    private final CrawlTaskService crawlTaskService;

    public CrawlTaskController(CrawlTaskService crawlTaskService) {
        this.crawlTaskService = crawlTaskService;
    }

    @GetMapping
    public ApiResponse<ApiResponse.PagedData<CrawlTaskResponse>> getTasksBySource(
            Authentication auth,
            @RequestParam Long sourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(
                crawlTaskService.getTasksBySource(userId, sourceId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<CrawlTaskResponse> getTaskStatus(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(crawlTaskService.getTaskStatus(userId, id));
    }

    @GetMapping("/{id}/logs")
    public ApiResponse<List<CrawlTaskLogResponse>> getTaskLogs(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(crawlTaskService.getTaskLogs(userId, id));
    }

    @PostMapping("/{id}/retry")
    public ApiResponse<CrawlTaskResponse> retryTask(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(crawlTaskService.retryTask(userId, id));
    }
}
