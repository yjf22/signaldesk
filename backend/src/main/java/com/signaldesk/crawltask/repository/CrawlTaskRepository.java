package com.signaldesk.crawltask.repository;

import com.signaldesk.crawltask.domain.CrawlTask;
import com.signaldesk.crawltask.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlTaskRepository extends JpaRepository<CrawlTask, Long> {

    Page<CrawlTask> findBySourceIdOrderByCreatedAtDesc(Long sourceId, Pageable pageable);

    List<CrawlTask> findByUserIdAndStatusIn(Long userId, List<TaskStatus> statuses);

    long countByUserIdAndStatusIn(Long userId, List<TaskStatus> statuses);

    boolean existsBySourceIdAndStatusIn(Long sourceId, List<TaskStatus> statuses);

    List<CrawlTask> findByStatusAndRetryCountLessThan(TaskStatus status, int maxRetries);
}
