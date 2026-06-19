package com.signaldesk.source.service;

import com.signaldesk.infrastructure.exception.BusinessException;
import com.signaldesk.infrastructure.exception.ErrorCode;
import com.signaldesk.source.domain.SourceTag;
import com.signaldesk.source.dto.TagCreateRequest;
import com.signaldesk.source.dto.TagResponse;
import com.signaldesk.source.repository.SourceTagRelationRepository;
import com.signaldesk.source.repository.SourceTagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagService {

    private final SourceTagRepository tagRepository;
    private final SourceTagRelationRepository relationRepository;

    public TagService(SourceTagRepository tagRepository,
                      SourceTagRelationRepository relationRepository) {
        this.tagRepository = tagRepository;
        this.relationRepository = relationRepository;
    }

    @Transactional(readOnly = true)
    public List<TagResponse> listTags(Long userId) {
        return tagRepository.findByUserId(userId).stream()
                .map(TagResponse::from)
                .toList();
    }

    public TagResponse createTag(Long userId, TagCreateRequest request) {
        if (tagRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Tag name already exists");
        }
        SourceTag tag = SourceTag.builder()
                .userId(userId)
                .name(request.getName())
                .color(request.getColor())
                .build();
        tagRepository.save(tag);
        return TagResponse.from(tag);
    }

    public void deleteTag(Long userId, Long tagId) {
        SourceTag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TAG_NOT_FOUND));
        if (!tag.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        // Remove all relations first
        relationRepository.deleteBySourceId(tagId);
        tagRepository.delete(tag);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTagsForSource(Long sourceId) {
        List<SourceTag> tags = relationRepository.findBySourceId(sourceId).stream()
                .map(r -> tagRepository.findById(r.getTagId()).orElse(null))
                .filter(t -> t != null)
                .toList();
        return tags.stream().map(TagResponse::from).toList();
    }
}
