package com.signaldesk.source.dto;

import com.signaldesk.source.domain.SourceTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TagResponse {
    private Long id;
    private String name;
    private String color;

    public static TagResponse from(SourceTag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .build();
    }
}
