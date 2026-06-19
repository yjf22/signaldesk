package com.signaldesk.summary.repository;

import com.signaldesk.summary.domain.Summary;
import com.signaldesk.summary.domain.SummaryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Page<Summary> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Summary> findBySourceIdOrderByCreatedAtDesc(Long sourceId);
    boolean existsBySourceIdAndStatusIn(Long sourceId, List<SummaryStatus> statuses);
    boolean existsByUserIdAndStatusIn(Long userId, List<SummaryStatus> statuses);
}
