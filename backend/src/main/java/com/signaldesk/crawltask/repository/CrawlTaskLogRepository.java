package com.signaldesk.crawltask.repository;

import com.signaldesk.crawltask.domain.CrawlTaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlTaskLogRepository extends JpaRepository<CrawlTaskLog, Long> {
    List<CrawlTaskLog> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}
