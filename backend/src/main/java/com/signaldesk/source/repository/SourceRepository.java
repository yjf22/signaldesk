package com.signaldesk.source.repository;

import com.signaldesk.source.domain.Source;
import com.signaldesk.source.domain.SourceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {

    Page<Source> findByUserIdAndStatus(Long userId, SourceStatus status, Pageable pageable);

    Page<Source> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT s FROM Source s WHERE s.userId = :userId " +
           "AND (:status IS NULL OR s.status = :status) " +
           "AND (:type IS NULL OR s.sourceType = :type) " +
           "ORDER BY CASE WHEN :sort = 'title' THEN s.title END ASC, " +
           "CASE WHEN :sort = 'recent' THEN s.updatedAt END DESC, " +
           "CASE WHEN :sort = 'created' THEN s.createdAt END DESC")
    Page<Source> findByFilters(@Param("userId") Long userId,
                               @Param("status") SourceStatus status,
                               @Param("type") String type,
                               @Param("sort") String sort,
                               Pageable pageable);

    List<Source> findByUserIdAndIsPinnedTrue(Long userId);

    @Query("SELECT s FROM Source s WHERE s.status = 'ACTIVE' AND s.nextFetchAt <= :now")
    List<Source> findSourcesDueForFetch(@Param("now") LocalDateTime now);

    long countByUserIdAndStatus(Long userId, SourceStatus status);
}
