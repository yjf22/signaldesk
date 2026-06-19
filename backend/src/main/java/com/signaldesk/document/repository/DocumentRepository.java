package com.signaldesk.document.repository;

import com.signaldesk.document.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findBySourceIdOrderByCreatedAtDesc(Long sourceId, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.sourceId = :sourceId AND d.isCurrent = true ORDER BY d.createdAt DESC")
    List<Document> findCurrentBySourceId(@Param("sourceId") Long sourceId);

    Optional<Document> findFirstBySourceIdAndSourceUrlAndIsCurrentTrue(Long sourceId, String sourceUrl);

    Optional<Document> findFirstBySourceIdAndTitleAndIsCurrentTrue(Long sourceId, String title);

    Page<Document> findByUserIdAndIsCurrentTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query(
            value = """
                    SELECT d.*
                    FROM documents d
                    LEFT JOIN sources s ON s.id = d.source_id
                    WHERE d.user_id = :userId
                      AND d.is_current = true
                      AND (
                            :keyword IS NULL
                            OR :keyword = ''
                            OR LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(d.content_text) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(COALESCE(d.source_url, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                          )
                      AND (
                            :sourceType IS NULL
                            OR :sourceType = ''
                            OR UPPER(s.source_type) = UPPER(:sourceType)
                          )
                      AND (
                            :sourceId IS NULL
                            OR d.source_id = :sourceId
                          )
                    ORDER BY d.created_at DESC
                    """,
            countQuery = """
                    SELECT COUNT(1)
                    FROM documents d
                    LEFT JOIN sources s ON s.id = d.source_id
                    WHERE d.user_id = :userId
                      AND d.is_current = true
                      AND (
                            :keyword IS NULL
                            OR :keyword = ''
                            OR LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(d.content_text) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(COALESCE(d.source_url, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                          )
                      AND (
                            :sourceType IS NULL
                            OR :sourceType = ''
                            OR UPPER(s.source_type) = UPPER(:sourceType)
                          )
                      AND (
                            :sourceId IS NULL
                            OR d.source_id = :sourceId
                          )
                    """,
            nativeQuery = true
    )
    Page<Document> searchCurrentDocuments(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("sourceType") String sourceType,
            @Param("sourceId") Long sourceId,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d WHERE d.sourceId = :sourceId AND d.contentHash = :hash AND d.isCurrent = true")
    Optional<Document> findBySourceIdAndContentHash(@Param("sourceId") Long sourceId, @Param("hash") String hash);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.createdAt >= :since")
    long countDocumentsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.userId = :userId AND d.createdAt >= :since")
    long countDocumentsByUserSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    List<Document> findTop10ByUserIdAndIsCurrentTrueOrderByCreatedAtDesc(Long userId);
}
