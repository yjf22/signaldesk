package com.signaldesk.summary.repository;

import com.signaldesk.summary.domain.SummaryReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryReferenceRepository extends JpaRepository<SummaryReference, Long> {
    List<SummaryReference> findBySummaryIdOrderByRefIndexAsc(Long summaryId);
}
