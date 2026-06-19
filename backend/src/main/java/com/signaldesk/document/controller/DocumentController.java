package com.signaldesk.document.controller;

import com.signaldesk.document.dto.DocumentResponse;
import com.signaldesk.document.dto.DocumentVersionResponse;
import com.signaldesk.document.service.DocumentService;
import com.signaldesk.infrastructure.dto.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ApiResponse<ApiResponse.PagedData<DocumentResponse>> getDocumentsBySource(
            Authentication auth,
            @RequestParam Long sourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(
                documentService.getDocumentsBySource(userId, sourceId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<DocumentResponse> getDocument(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(documentService.getDocument(userId, id));
    }

    @GetMapping("/{id}/versions")
    public ApiResponse<List<DocumentVersionResponse>> getVersions(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(documentService.getVersions(userId, id));
    }
}
