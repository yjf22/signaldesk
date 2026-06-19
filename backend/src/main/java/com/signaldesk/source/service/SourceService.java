package com.signaldesk.source.service;

import com.signaldesk.infrastructure.exception.BusinessException;
import com.signaldesk.infrastructure.exception.ErrorCode;
import com.signaldesk.source.domain.*;
import com.signaldesk.source.dto.*;
import com.signaldesk.source.repository.SourceRepository;
import com.signaldesk.source.repository.SourceTagRelationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@Transactional
public class SourceService {

    private final SourceRepository sourceRepository;
    private final SourceTagRelationRepository relationRepository;
    private final TagService tagService;

    public SourceService(SourceRepository sourceRepository,
                         SourceTagRelationRepository relationRepository,
                         TagService tagService) {
        this.sourceRepository = sourceRepository;
        this.relationRepository = relationRepository;
        this.tagService = tagService;
    }

    @Transactional(readOnly = true)
    public com.signaldesk.infrastructure.dto.ApiResponse.PagedData<SourceResponse> listSources(
            Long userId, SourceListFilter filter) {

        int normalizedPage = Math.max((filter.getPage() != null ? filter.getPage() : 1) - 1, 0);
        int pageSize = filter.getSize() != null ? filter.getSize() : 12;

        Pageable pageable;
        if ("title".equals(filter.getSort())) {
            pageable = PageRequest.of(normalizedPage, pageSize, Sort.by("title").ascending());
        } else if ("created".equals(filter.getSort())) {
            pageable = PageRequest.of(normalizedPage, pageSize, Sort.by("createdAt").descending());
        } else {
            pageable = PageRequest.of(normalizedPage, pageSize, Sort.by("updatedAt").descending());
        }

        Page<Source> page;
        if (filter.getStatus() != null && filter.getType() != null) {
            page = sourceRepository.findByFilters(userId, filter.getStatus(), filter.getType(),
                    filter.getSort(), pageable);
        } else if (filter.getStatus() != null) {
            page = sourceRepository.findByUserIdAndStatus(userId, filter.getStatus(), pageable);
        } else {
            page = sourceRepository.findByUserId(userId, pageable);
        }

        List<SourceResponse> content = page.getContent().stream()
                .map(s -> SourceResponse.from(s, tagService.getTagsForSource(s.getId())))
                .toList();

        return new com.signaldesk.infrastructure.dto.ApiResponse.PagedData<>(
                content, page.getNumber() + 1, page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public SourceResponse getSource(Long userId, Long sourceId) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOURCE_NOT_FOUND));
        if (!source.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
        }
        return SourceResponse.from(source, tagService.getTagsForSource(source.getId()));
    }

    public SourceResponse createSource(Long userId, SourceCreateRequest request) {
        Source source = Source.builder()
                .userId(userId)
                .title(request.getTitle())
                .url(request.getUrl())
                .sourceType(request.getSourceType())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : SourceStatus.ACTIVE)
                .fetchIntervalMin(request.getFetchIntervalMin() != null ? request.getFetchIntervalMin() : 360)
                .isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
                .build();

        sourceRepository.save(source);

        // Handle tags
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            setTags(source.getId(), request.getTagIds());
        }

        return SourceResponse.from(source, tagService.getTagsForSource(source.getId()));
    }

    public SourceResponse updateSource(Long userId, Long sourceId, SourceUpdateRequest request) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOURCE_NOT_FOUND));
        if (!source.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
        }

        if (request.getTitle() != null) source.setTitle(request.getTitle());
        if (request.getUrl() != null) source.setUrl(request.getUrl());
        if (request.getSourceType() != null) source.setSourceType(request.getSourceType());
        if (request.getDescription() != null) source.setDescription(request.getDescription());
        if (request.getStatus() != null) source.setStatus(request.getStatus());
        if (request.getFetchIntervalMin() != null) source.setFetchIntervalMin(request.getFetchIntervalMin());
        if (request.getIsPinned() != null) source.setIsPinned(request.getIsPinned());

        sourceRepository.save(source);

        if (request.getTagIds() != null) {
            setTags(sourceId, request.getTagIds());
        }

        return SourceResponse.from(source, tagService.getTagsForSource(source.getId()));
    }

    public void deleteSource(Long userId, Long sourceId) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOURCE_NOT_FOUND));
        if (!source.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
        }
        // Clean up relations first
        relationRepository.deleteBySourceId(sourceId);
        sourceRepository.delete(source);
    }

    public SourceResponse updateStatus(Long userId, Long sourceId, SourceStatus status) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOURCE_NOT_FOUND));
        if (!source.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
        }
        source.setStatus(status);
        sourceRepository.save(source);
        return SourceResponse.from(source, tagService.getTagsForSource(source.getId()));
    }

    private void setTags(Long sourceId, List<Long> tagIds) {
        relationRepository.deleteBySourceId(sourceId);
        for (Long tagId : normalizeTagIds(tagIds)) {
            SourceTagRelation relation = SourceTagRelation.builder()
                    .sourceId(sourceId)
                    .tagId(tagId)
                    .build();
            relationRepository.save(relation);
        }
    }

    private List<Long> normalizeTagIds(List<Long> tagIds) {
        LinkedHashSet<Long> uniqueTagIds = new LinkedHashSet<>();
        for (Long tagId : tagIds) {
            if (tagId != null) {
                uniqueTagIds.add(tagId);
            }
        }
        return new ArrayList<>(uniqueTagIds);
    }
}
