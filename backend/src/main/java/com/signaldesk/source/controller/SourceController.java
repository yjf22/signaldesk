package com.signaldesk.source.controller;

import com.signaldesk.crawltask.dto.CrawlTaskResponse;
import com.signaldesk.crawltask.service.CrawlTaskService;
import com.signaldesk.document.dto.DocumentResponse;
import com.signaldesk.document.service.DocumentService;
import com.signaldesk.infrastructure.dto.ApiResponse;
import com.signaldesk.source.domain.SourceStatus;
import com.signaldesk.source.dto.*;
import com.signaldesk.source.service.SourceService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sources")
public class SourceController {

    private final SourceService sourceService;
    private final CrawlTaskService crawlTaskService;
    private final DocumentService documentService;

    public SourceController(SourceService sourceService,
                            CrawlTaskService crawlTaskService,
                            DocumentService documentService) {
        this.sourceService = sourceService;
        this.crawlTaskService = crawlTaskService;
        this.documentService = documentService;
    }

    @GetMapping
    public ApiResponse<ApiResponse.PagedData<SourceResponse>> listSources(
            Authentication auth,
            @ModelAttribute SourceListFilter filter) {
        Long userId = (Long) auth.getPrincipal();
        if (filter == null) filter = new SourceListFilter();
        return ApiResponse.success(sourceService.listSources(userId, filter));
    }

    @GetMapping("/{id}")
    public ApiResponse<SourceResponse> getSource(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sourceService.getSource(userId, id));
    }

    @PostMapping
    public ApiResponse<SourceResponse> createSource(
            Authentication auth,
            @Valid @RequestBody SourceCreateRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sourceService.createSource(userId, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SourceResponse> updateSource(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody SourceUpdateRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sourceService.updateSource(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSource(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sourceService.deleteSource(userId, id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<SourceResponse> updateStatus(
            Authentication auth,
            @PathVariable Long id,
            @RequestParam SourceStatus status) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sourceService.updateStatus(userId, id, status));
    }

    @PostMapping("/{id}/fetch")
    public ApiResponse<CrawlTaskResponse> triggerFetch(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(crawlTaskService.triggerManualFetch(userId, id));
    }

    @GetMapping("/{id}/documents")
    public ApiResponse<ApiResponse.PagedData<DocumentResponse>> getSourceDocuments(
            Authentication auth,
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(documentService.getDocumentsBySource(userId, id, page, size));
    }

    @GetMapping("/{id}/fetch-history")
    public ApiResponse<ApiResponse.PagedData<CrawlTaskResponse>> getSourceFetchHistory(
            Authentication auth,
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(crawlTaskService.getTasksBySource(userId, id, page, size));
    }
}
