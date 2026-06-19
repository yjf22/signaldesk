package com.signaldesk.search.controller;

import com.signaldesk.infrastructure.dto.ApiResponse;
import com.signaldesk.search.dto.SearchRequest;
import com.signaldesk.search.dto.SearchResultResponse;
import com.signaldesk.search.service.SearchService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ApiResponse<List<SearchResultResponse>> search(
            Authentication auth,
            @ModelAttribute SearchRequest request) {
        Long userId = (Long) auth.getPrincipal();
        if (request == null) request = new SearchRequest();
        return ApiResponse.success(searchService.search(request, userId));
    }
}
