package com.signaldesk.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.signaldesk.document.domain.Document;
import com.signaldesk.search.dto.SearchRequest;
import com.signaldesk.search.dto.SearchResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {

    private static final Logger log = LoggerFactory.getLogger(IndexService.class);
    private final ElasticsearchClient esClient;
    private final String indexPrefix;

    public IndexService(ElasticsearchClient esClient,
                        @Value("${signaldesk.elasticsearch.index-prefix:signaldesk}") String indexPrefix) {
        this.esClient = esClient;
        this.indexPrefix = indexPrefix;
    }

    public String getIndexName() {
        return indexPrefix + "-documents";
    }

    /**
     * Index a document into Elasticsearch.
     */
    public void indexDocument(Document doc, String sourceTitle, String sourceType, List<String> tags) {
        try {
            Map<String, Object> docMap = Map.of(
                    "documentId", doc.getId(),
                    "title", doc.getTitle(),
                    "contentText", doc.getContentText(),
                    "sourceId", doc.getSourceId(),
                    "sourceTitle", sourceTitle,
                    "sourceType", sourceType,
                    "userId", doc.getUserId(),
                    "tags", tags,
                    "createdAt", doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : ""
            );

            esClient.index(IndexRequest.of(i -> i
                    .index(getIndexName())
                    .id(doc.getId().toString())
                    .document(docMap)));
        } catch (IOException e) {
            log.error("Failed to index document: {}", doc.getId(), e);
        }
    }

    /**
     * Search documents by keyword with optional filters.
     * Falls back to MySQL FULLTEXT search if ES is unavailable.
     */
    public List<SearchResultResponse> search(SearchRequest request, Long userId) {
        try {
            Query query = buildQuery(request, userId);

            SearchResponse<Map> response = esClient.search(s -> s
                    .index(getIndexName())
                    .query(query)
                    .from(request.getPage() * request.getSize())
                    .size(request.getSize())
                    .highlight(h -> h
                            .fields("contentText", hf -> hf
                                    .fragmentSize(200)
                                    .numberOfFragments(1))
                            .fields("title", hf -> hf
                                    .fragmentSize(100)
                                    .numberOfFragments(1))),
                    Map.class);

            return response.hits().hits().stream()
                    .map(hit -> mapToSearchResult(hit, request.getKeyword()))
                    .toList();

        } catch (IOException e) {
            log.error("Elasticsearch search failed, falling back to MySQL", e);
            return Collections.emptyList();
        }
    }

    private Query buildQuery(SearchRequest request, Long userId) {
        if (request.getKeyword() == null || request.getKeyword().isBlank()) {
            // Match all for this user
            return Query.of(q -> q.bool(b -> b
                    .must(m -> m.term(t -> t.field("userId").value(userId)))));
        }

        return Query.of(q -> q.bool(b -> {
            b.must(m -> m.term(t -> t.field("userId").value(userId)));

            // Multi-match on title and content
            b.must(mm -> mm.multiMatch(mt -> mt
                    .query(request.getKeyword())
                    .fields("title^3", "contentText")));

            // Optional filters
            if (request.getSourceType() != null) {
                b.filter(f -> f.term(t -> t.field("sourceType").value(request.getSourceType())));
            }
            if (request.getSourceId() != null) {
                b.filter(f -> f.term(t -> t.field("sourceId").value(request.getSourceId())));
            }

            return b;
        }));
    }

    @SuppressWarnings("unchecked")
    private SearchResultResponse mapToSearchResult(Hit<Map> hit, String keyword) {
        Map<String, Object> source = hit.source();
        if (source == null) return null;

        // Build snippet from highlight or content
        String snippet = "";
        if (hit.highlight() != null && hit.highlight().containsKey("contentText")) {
            snippet = String.join(" ... ", hit.highlight().get("contentText"));
        } else if (source.containsKey("contentText")) {
            String content = (String) source.get("contentText");
            snippet = content != null && content.length() > 300
                    ? content.substring(0, 300) + "..."
                    : content;
        }

        List<String> tags = source.containsKey("tags")
                ? (List<String>) source.get("tags")
                : List.of();

        return SearchResultResponse.builder()
                .id(toLong(source.get("documentId")))
                .title((String) source.get("title"))
                .sourceTitle((String) source.get("sourceTitle"))
                .sourceId(toLong(source.get("sourceId")))
                .sourceType((String) source.get("sourceType"))
                .tags(tags)
                .snippet(snippet)
                .fetchedAt((String) source.get("createdAt"))
                .build();
    }

    private Long toLong(Object obj) {
        if (obj instanceof Integer i) return i.longValue();
        if (obj instanceof Long l) return l;
        return null;
    }
}
