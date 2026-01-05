package com.yigit.ecommerce.dto.common;

public record PaginationMeta(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious) {
}
