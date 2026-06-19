package com.signaldesk.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final int code;
    private final T data;
    private final String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, data, "ok");
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(0, null, "ok");
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, null, message);
    }

    // Paginated wrapper
    @Getter
    @AllArgsConstructor
    public static class PagedData<T> {
        private final java.util.List<T> content;
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;
    }

    public static <T> ApiResponse<PagedData<T>> paged(
            java.util.List<T> content, int page, int size, long total) {
        int totalPages = (int) Math.ceil((double) total / size);
        return success(new PagedData<>(content, page, size, total, totalPages));
    }
}
