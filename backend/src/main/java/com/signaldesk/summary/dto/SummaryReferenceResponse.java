package com.signaldesk.summary.dto;

import com.signaldesk.summary.domain.SummaryReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SummaryReferenceResponse {
    private Long id;
    private Integer refIndex;
    private Long documentId;
    private String sourceTitle;
    private String quoteText;

    public static SummaryReferenceResponse from(SummaryReference ref, String sourceTitle) {
        return SummaryReferenceResponse.builder()
                .id(ref.getId())
                .refIndex(ref.getRefIndex())
                .documentId(ref.getDocumentId())
                .sourceTitle(sourceTitle)
                .quoteText(ref.getQuoteText())
                .build();
    }
}
