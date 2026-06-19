package com.signaldesk.source.repository;

import com.signaldesk.source.domain.SourceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourceTagRepository extends JpaRepository<SourceTag, Long> {
    List<SourceTag> findByUserId(Long userId);
    Optional<SourceTag> findByUserIdAndName(Long userId, String name);
    boolean existsByUserIdAndName(Long userId, String name);
}
