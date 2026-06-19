package com.signaldesk.search.service;

import com.signaldesk.document.domain.Document;
import com.signaldesk.document.repository.DocumentRepository;
import com.signaldesk.search.dto.SearchRequest;
import com.signaldesk.search.dto.SearchResultResponse;
import com.signaldesk.source.domain.Source;
import com.signaldesk.source.repository.SourceRepository;
import com.signaldesk.source.service.TagService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    private final IndexService indexService;
    private final DocumentRepository documentRepository;
    private final SourceRepository sourceRepository;
    private final TagService tagService;

    public SearchService(IndexService indexService,
                         DocumentRepository documentRepository,
                         SourceRepository sourceRepository,
                         TagService tagService) {
        this.indexService = indexService;
        this.documentRepository = documentRepository;
        this.sourceRepository = sourceRepository;
        this.tagService = tagService;
    }

    /**
     * Search with ES primary, MySQL FULLTEXT fallback.
     */
    public List<SearchResultResponse> search(SearchRequest request, Long userId) {
        List<SearchResultResponse> esResults = indexService.search(request, userId);

        if (!esResults.isEmpty()) {
            return esResults;
        }

        return mysqlFulltextSearch(request, userId);
    }

    private List<SearchResultResponse> mysqlFulltextSearch(SearchRequest request, Long userId) {
        int page = request.getPage() != null && request.getPage() >= 0 ? request.getPage() : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 20;

        return documentRepository.searchCurrentDocuments(
                        userId,
                        request.getKeyword(),
                        request.getSourceType(),
                        request.getSourceId(),
                        PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(doc -> buildResult(doc, request.getKeyword()))
                .toList();
    }

    private SearchResultResponse buildResult(Document doc, String keyword) {
        Source source = sourceRepository.findById(doc.getSourceId()).orElse(null);
        String sourceTitle = source != null ? source.getTitle() : "Unknown";
        String sourceType = source != null ? source.getSourceType().name() : "URL";
        List<String> tags = tagService.getTagsForSource(doc.getSourceId()).stream()
                .map(t -> t.getName())
                .toList();

        String snippet = buildSnippet(doc, keyword);

        return SearchResultResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .sourceTitle(sourceTitle)
                .sourceId(doc.getSourceId())
                .sourceType(sourceType)
                .tags(tags)
                .snippet(snippet)
                .fetchedAt(doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : null)
                .build();
    }

    private String buildSnippet(Document doc, String keyword) {
        String content = doc.getContentText();
        if (content == null || content.isBlank()) {
            return "";
        }

        String normalized = content.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 220) {
            return normalized;
        }

        if (keyword == null || keyword.isBlank()) {
            return normalized.substring(0, 220) + "...";
        }

        String lowerContent = normalized.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        int matchIndex = lowerContent.indexOf(lowerKeyword);
        if (matchIndex < 0) {
            return normalized.substring(0, 220) + "...";
        }

        int context = 90;
        int start = Math.max(0, matchIndex - context);
        int end = Math.min(normalized.length(), matchIndex + keyword.length() + context);
        String snippet = normalized.substring(start, end).trim();

        if (start > 0) {
            snippet = "..." + snippet;
        }
        if (end < normalized.length()) {
            snippet = snippet + "...";
        }

        return snippet;
    }
}
