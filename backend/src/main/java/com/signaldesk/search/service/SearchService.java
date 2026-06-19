package com.signaldesk.search.service;

import com.signaldesk.document.domain.Document;
import com.signaldesk.document.repository.DocumentRepository;
import com.signaldesk.search.dto.SearchRequest;
import com.signaldesk.search.dto.SearchResultResponse;
import com.signaldesk.source.domain.Source;
import com.signaldesk.source.repository.SourceRepository;
import com.signaldesk.source.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);
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
        // Try Elasticsearch first
        List<SearchResultResponse> esResults = indexService.search(request, userId);

        if (!esResults.isEmpty()) {
            return esResults;
        }

        // Fallback to MySQL FULLTEXT search
        return mysqlFulltextSearch(request, userId);
    }

    private List<SearchResultResponse> mysqlFulltextSearch(SearchRequest request, Long userId) {
        if (request.getKeyword() == null || request.getKeyword().isBlank()) {
            // Without keyword, just return recent documents
            return documentRepository.findTop10ByUserIdAndIsCurrentTrueOrderByCreatedAtDesc(userId)
                    .stream()
                    .map(doc -> buildResult(doc))
                    .toList();
        }

        // MySQL FULLTEXT: we use the native query via repository
        // For now, use LIKE search as a simpler fallback
        var page = documentRepository.findByUserIdAndIsCurrentTrueOrderByCreatedAtDesc(
                userId, PageRequest.of(request.getPage(), request.getSize()));

        String keyword = "%" + request.getKeyword().toLowerCase() + "%";

        return page.getContent().stream()
                .filter(doc ->
                        (doc.getTitle() != null && doc.getTitle().toLowerCase().contains(keyword)) ||
                        (doc.getContentText() != null && doc.getContentText().toLowerCase().contains(keyword)))
                .map(doc -> buildResult(doc))
                .toList();
    }

    private SearchResultResponse buildResult(Document doc) {
        Source source = sourceRepository.findById(doc.getSourceId()).orElse(null);
        String sourceTitle = source != null ? source.getTitle() : "Unknown";
        String sourceType = source != null ? source.getSourceType().name() : "URL";
        List<String> tags = tagService.getTagsForSource(doc.getSourceId()).stream()
                .map(t -> t.getName())
                .toList();

        String snippet = doc.getContentText();
        if (snippet != null && snippet.length() > 300) {
            snippet = snippet.substring(0, 300) + "...";
        }

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
}
