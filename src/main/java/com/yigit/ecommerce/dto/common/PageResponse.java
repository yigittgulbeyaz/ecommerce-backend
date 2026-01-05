package com.yigit.ecommerce.dto.common;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        PaginationMeta meta) {
}
