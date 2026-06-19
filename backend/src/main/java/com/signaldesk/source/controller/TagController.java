package com.signaldesk.source.controller;

import com.signaldesk.infrastructure.dto.ApiResponse;
import com.signaldesk.source.dto.TagCreateRequest;
import com.signaldesk.source.dto.TagResponse;
import com.signaldesk.source.service.TagService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ApiResponse<List<TagResponse>> listTags(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(tagService.listTags(userId));
    }

    @PostMapping
    public ApiResponse<TagResponse> createTag(
            Authentication auth,
            @Valid @RequestBody TagCreateRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(tagService.createTag(userId, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(
            Authentication auth,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        tagService.deleteTag(userId, id);
        return ApiResponse.success();
    }
}
