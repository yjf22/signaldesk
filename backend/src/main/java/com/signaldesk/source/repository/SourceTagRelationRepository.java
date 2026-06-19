package com.signaldesk.source.repository;

import com.signaldesk.source.domain.SourceTagRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourceTagRelationRepository extends JpaRepository<SourceTagRelation, Long> {
    List<SourceTagRelation> findBySourceId(Long sourceId);
    List<SourceTagRelation> findByTagId(Long tagId);
    Optional<SourceTagRelation> findBySourceIdAndTagId(Long sourceId, Long tagId);
    void deleteBySourceIdAndTagId(Long sourceId, Long tagId);
    void deleteBySourceId(Long sourceId);
}
