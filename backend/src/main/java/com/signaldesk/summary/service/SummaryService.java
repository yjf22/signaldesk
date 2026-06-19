package com.signaldesk.summary.service;

import com.signaldesk.document.domain.Document;
import com.signaldesk.document.repository.DocumentRepository;
import com.signaldesk.infrastructure.exception.BusinessException;
import com.signaldesk.infrastructure.exception.ErrorCode;
import com.signaldesk.source.domain.Source;
import com.signaldesk.source.repository.SourceRepository;
import com.signaldesk.summary.client.AIClient;
import com.signaldesk.summary.domain.Summary;
import com.signaldesk.summary.domain.SummaryReference;
import com.signaldesk.summary.domain.SummaryStatus;
import com.signaldesk.summary.dto.SummaryGenerateRequest;
import com.signaldesk.summary.dto.SummaryReferenceResponse;
import com.signaldesk.summary.dto.SummaryResponse;
import com.signaldesk.summary.repository.SummaryReferenceRepository;
import com.signaldesk.summary.repository.SummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SummaryService {

    private static final Logger log = LoggerFactory.getLogger(SummaryService.class);

    private final SummaryRepository summaryRepository;
    private final SummaryReferenceRepository referenceRepository;
    private final DocumentRepository documentRepository;
    private final SourceRepository sourceRepository;
    private final AIClient aiClient;

    public SummaryService(SummaryRepository summaryRepository,
                          SummaryReferenceRepository referenceRepository,
                          DocumentRepository documentRepository,
                          SourceRepository sourceRepository,
                          AIClient aiClient) {
        this.summaryRepository = summaryRepository;
        this.referenceRepository = referenceRepository;
        this.documentRepository = documentRepository;
        this.sourceRepository = sourceRepository;
        this.aiClient = aiClient;
    }

    /**
     * Trigger summary generation. Creates a PENDING summary and executes async.
     */
    public SummaryResponse triggerSummary(Long userId, SummaryGenerateRequest request) {
        // Check for existing in-progress summary
        if (request.getSourceId() != null) {
            boolean inProgress = summaryRepository.existsBySourceIdAndStatusIn(
                    request.getSourceId(),
                    List.of(SummaryStatus.PENDING, SummaryStatus.GATHERING, SummaryStatus.PROMPTING));
            if (inProgress) {
                throw new BusinessException(ErrorCode.SUMMARY_ALREADY_GENERATING);
            }
        }

        String title = request.getTitle();
        if (title == null && request.getSourceId() != null) {
            Source source = sourceRepository.findById(request.getSourceId()).orElse(null);
            if (source != null) {
                title = "Summary: " + source.getTitle();
            }
        }
        if (title == null) {
            title = "Summary";
        }

        Summary summary = Summary.builder()
                .userId(userId)
                .sourceId(request.getSourceId())
                .searchQuery(request.getSearchQuery())
                .title(title)
                .status(SummaryStatus.PENDING)
                .build();

        summaryRepository.save(summary);

        // Execute asynchronously
        executeSummary(summary.getId());

        return SummaryResponse.from(summary, List.of());
    }

    @Transactional(readOnly = true)
    public SummaryResponse getSummary(Long userId, Long summaryId) {
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUMMARY_NOT_FOUND));
        if (!summary.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SUMMARY_NOT_FOUND);
        }

        List<SummaryReferenceResponse> refs = getReferences(summaryId);
        return SummaryResponse.from(summary, refs);
    }

    @Transactional(readOnly = true)
    public com.signaldesk.infrastructure.dto.ApiResponse.PagedData<SummaryResponse> getHistory(
            Long userId, int page, int size) {
        int normalizedPage = Math.max(page - 1, 0);
        Page<Summary> summaries = summaryRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(normalizedPage, size));

        List<SummaryResponse> content = summaries.getContent().stream()
                .map(s -> SummaryResponse.from(s, getReferences(s.getId())))
                .toList();

        return new com.signaldesk.infrastructure.dto.ApiResponse.PagedData<>(
                content, summaries.getNumber() + 1, summaries.getSize(),
                summaries.getTotalElements(), summaries.getTotalPages());
    }

    @Transactional(readOnly = true)
    public List<SummaryResponse> getSummariesBySource(Long sourceId) {
        return summaryRepository.findBySourceIdOrderByCreatedAtDesc(sourceId).stream()
                .map(s -> SummaryResponse.from(s, getReferences(s.getId())))
                .toList();
    }

    // ---- Async execution ----

    @Async("summaryExecutor")
    public void executeSummary(Long summaryId) {
        Summary summary = summaryRepository.findById(summaryId).orElse(null);
        if (summary == null) return;

        try {
            // Step 1: Gather content
            summary.setStatus(SummaryStatus.GATHERING);
            summaryRepository.save(summary);

            List<Document> documents = gatherDocuments(summary);

            if (documents.isEmpty()) {
                summary.markFailed("No content available to summarize");
                summaryRepository.save(summary);
                return;
            }

            // Step 2: Build prompt
            summary.setStatus(SummaryStatus.PROMPTING);
            summaryRepository.save(summary);

            String systemPrompt = buildSystemPrompt();
            String userContent = buildUserContent(documents, summary.getSearchQuery());

            // Step 3: Call AI
            AIClient.AIResponse aiResponse = aiClient.generate(systemPrompt, userContent);

            // Step 4: Save result and references
            summary.markCompleted(aiResponse.content(), aiResponse.model(),
                    aiResponse.promptTokens(), aiResponse.completionTokens());
            summaryRepository.save(summary);

            // Save references
            for (int i = 0; i < documents.size(); i++) {
                Document doc = documents.get(i);
                String quoteText = doc.getContentText();
                if (quoteText != null && quoteText.length() > 500) {
                    quoteText = quoteText.substring(0, 500);
                }

                SummaryReference ref = SummaryReference.builder()
                        .summaryId(summary.getId())
                        .documentId(doc.getId())
                        .refIndex(i + 1)
                        .quoteText(quoteText)
                        .build();
                referenceRepository.save(ref);
            }

            log.info("Summary {} generated successfully", summaryId);

        } catch (Exception e) {
            log.error("Summary generation failed: {}", summaryId, e);
            summary.markFailed(e.getMessage());
            summaryRepository.save(summary);
        }
    }

    private List<Document> gatherDocuments(Summary summary) {
        if (summary.getSourceId() != null) {
            // Summarize a specific source
            return documentRepository.findCurrentBySourceId(summary.getSourceId());
        } else {
            // Summarize recent documents for this user
            return documentRepository.findTop10ByUserIdAndIsCurrentTrueOrderByCreatedAtDesc(
                    summary.getUserId());
        }
    }

    private String buildSystemPrompt() {
        return """
               You are a helpful assistant that summarizes content changes for a content monitoring tool.
               Produce a concise, structured summary in Markdown format:
               1. Start with a brief overview (2-3 sentences)
               2. List key points as bullet points
               3. Include a "Key Changes" section if comparing to previous versions
               4. End with a "Notable Items" section highlighting the most important changes
               Keep the summary informative but concise. Use Chinese if the source content is in Chinese.
               """;
    }

    private String buildUserContent(List<Document> documents, String searchQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please summarize the following collected content:\n\n");

        if (searchQuery != null && !searchQuery.isBlank()) {
            sb.append("Search context: ").append(searchQuery).append("\n\n");
        }

        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            sb.append("---\n");
            sb.append("Document ").append(i + 1).append(": ").append(doc.getTitle()).append("\n");
            sb.append("Source: ").append(doc.getSourceUrl() != null ? doc.getSourceUrl() : "N/A").append("\n");
            if (doc.getAuthor() != null) {
                sb.append("Author: ").append(doc.getAuthor()).append("\n");
            }
            sb.append("\n");

            // Truncate each document to avoid token limit issues
            String content = doc.getContentText();
            if (content != null && content.length() > 3000) {
                content = content.substring(0, 3000) + "\n... (truncated)";
            }
            sb.append(content).append("\n\n");
        }

        return sb.toString();
    }

    private List<SummaryReferenceResponse> getReferences(Long summaryId) {
        List<SummaryReference> refs = referenceRepository.findBySummaryIdOrderByRefIndexAsc(summaryId);
        List<SummaryReferenceResponse> result = new ArrayList<>();
        for (SummaryReference ref : refs) {
            String sourceTitle = "";
            Document doc = documentRepository.findById(ref.getDocumentId()).orElse(null);
            if (doc != null) {
                Source source = sourceRepository.findById(doc.getSourceId()).orElse(null);
                if (source != null) {
                    sourceTitle = source.getTitle();
                }
            }
            result.add(SummaryReferenceResponse.from(ref, sourceTitle));
        }
        return result;
    }
}
