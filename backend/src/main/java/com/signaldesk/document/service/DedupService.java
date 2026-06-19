package com.signaldesk.document.service;

import com.signaldesk.document.domain.Document;
import com.signaldesk.document.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class DedupService {

    private static final Logger log = LoggerFactory.getLogger(DedupService.class);
    private final DocumentRepository documentRepository;

    public DedupService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Compute SHA-256 hash of content text.
     */
    public String computeHash(String contentText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contentText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Check if content already exists for this source (by content_hash).
     * Returns the existing document if found, empty otherwise.
     */
    public Optional<Document> findDuplicate(Long sourceId, String contentHash) {
        return documentRepository.findBySourceIdAndContentHash(sourceId, contentHash);
    }

    /**
     * Determine if content has actually changed compared to the current version.
     */
    public boolean hasContentChanged(Long sourceId, String newContentHash) {
        return findDuplicate(sourceId, newContentHash).isEmpty();
    }
}
