package com.signaldesk.document.service;

import com.signaldesk.document.domain.Document;
import com.signaldesk.document.domain.DocumentVersion;
import com.signaldesk.document.dto.DocumentResponse;
import com.signaldesk.document.dto.DocumentVersionResponse;
import com.signaldesk.document.repository.DocumentRepository;
import com.signaldesk.document.repository.DocumentVersionRepository;
import com.signaldesk.infrastructure.exception.BusinessException;
import com.signaldesk.infrastructure.exception.ErrorCode;
import com.signaldesk.source.domain.Source;
import com.signaldesk.source.repository.SourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final SourceRepository sourceRepository;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentVersionRepository versionRepository,
                           SourceRepository sourceRepository) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.sourceRepository = sourceRepository;
    }

    public com.signaldesk.infrastructure.dto.ApiResponse.PagedData<DocumentResponse> getDocumentsBySource(
            Long userId, Long sourceId, int page, int size) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOURCE_NOT_FOUND));
        if (!source.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
        }

        int normalizedPage = Math.max(page - 1, 0);
        Page<Document> docs = documentRepository.findBySourceIdOrderByCreatedAtDesc(
                sourceId, PageRequest.of(normalizedPage, size));
        List<DocumentResponse> content = docs.getContent().stream()
                .map(DocumentResponse::from)
                .toList();
        return new com.signaldesk.infrastructure.dto.ApiResponse.PagedData<>(
                content, docs.getNumber() + 1, docs.getSize(),
                docs.getTotalElements(), docs.getTotalPages());
    }

    public DocumentResponse getDocument(Long userId, Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND));
        if (!doc.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
        return DocumentResponse.from(doc);
    }

    public List<DocumentVersionResponse> getVersions(Long userId, Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND));
        if (!doc.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
        return versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId).stream()
                .map(DocumentVersionResponse::from)
                .toList();
    }

    /**
     * Upsert document: if content hash matches an existing current document, skip.
     * If content changed, mark old current as not-current, create a new version record,
     * and update the document with new content.
     */
    @Transactional
    public UpsertResult upsertDocument(Document newDoc) {
        Optional<Document> currentDocOpt = findCurrentDocument(newDoc);

        if (currentDocOpt.isPresent()) {
            Document currentDoc = currentDocOpt.get();
            if (currentDoc.getContentHash().equals(newDoc.getContentHash())) {
                return new UpsertResult(currentDoc, false, false);
            }

            long versionCount = versionRepository.countByDocumentId(currentDoc.getId());
            DocumentVersion version = DocumentVersion.builder()
                    .documentId(currentDoc.getId())
                    .versionNumber((int) versionCount + 1)
                    .contentText(currentDoc.getContentText())
                    .contentHash(currentDoc.getContentHash())
                    .changeSummary("Content updated via crawl")
                    .crawlTaskId(newDoc.getCrawlTaskId())
                    .build();
            versionRepository.save(version);

            currentDoc.setIsCurrent(false);
            documentRepository.save(currentDoc);

            newDoc.setIsCurrent(true);
            documentRepository.save(newDoc);
            return new UpsertResult(newDoc, true, false);
        }

        newDoc.setIsCurrent(true);
        documentRepository.save(newDoc);
        return new UpsertResult(newDoc, true, true);
    }

    private Optional<Document> findCurrentDocument(Document newDoc) {
        if (newDoc.getSourceUrl() != null && !newDoc.getSourceUrl().isBlank()) {
            Optional<Document> byUrl = documentRepository.findFirstBySourceIdAndSourceUrlAndIsCurrentTrue(
                    newDoc.getSourceId(), newDoc.getSourceUrl());
            if (byUrl.isPresent()) {
                return byUrl;
            }
        }

        if (newDoc.getTitle() != null && !newDoc.getTitle().isBlank()) {
            return documentRepository.findFirstBySourceIdAndTitleAndIsCurrentTrue(
                    newDoc.getSourceId(), newDoc.getTitle());
        }

        return Optional.empty();
    }

    public record UpsertResult(Document document, boolean changed, boolean created) {
    }
}
