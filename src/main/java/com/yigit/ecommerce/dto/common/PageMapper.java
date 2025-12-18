package com.yigit.ecommerce.dto.common;

import org.springframework.data.domain.Page;

public final class PageMapper {
    private PageMapper() {}

    public static <T> PageResponse<T> toResponse(Page<T> page) {
        PaginationMeta meta = new PaginationMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );

        return new PageResponse<>(page.getContent(), meta);
    }
}
