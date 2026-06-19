package com.signaldesk.summary.controller;

import com.signaldesk.infrastructure.dto.ApiResponse;
import com.signaldesk.summary.dto.SummaryGenerateRequest;
import com.signaldesk.summary.dto.SummaryResponse;
import com.signaldesk.summary.service.SummaryService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/summaries")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @PostMapping
    public ApiResponse<SummaryResponse> generateSummary(
            Authentication auth,
            @Valid @RequestBody SummaryGenerateRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(summaryService.triggerSummary(userId, request));
    }

    @PostMapping("/generate")
    public ApiResponse<SummaryResponse> generateSummaryCompat(
            Authentication auth,
            @Valid @RequestBody SummaryGenerateRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(summaryService.triggerSummary(userId, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<SummaryResponse> getSummary(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(summaryService.getSummary(userId, id));
    }

    @GetMapping
    public ApiResponse<ApiResponse.PagedData<SummaryResponse>> getHistory(
            Authentication auth,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(summaryService.getHistory(userId, page, size));
    }

    @GetMapping("/by-source")
    public ApiResponse<List<SummaryResponse>> getSummariesBySource(
            @RequestParam Long sourceId) {
        return ApiResponse.success(summaryService.getSummariesBySource(sourceId));
    }
}
