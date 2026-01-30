package com.yigit.ecommerce.dto.response.admin;

import java.math.BigDecimal;

public record AdminStatsResponse(
        long totalProducts,
        long totalOrders,
        long totalCategories,
        long totalUsers,
        BigDecimal revenue) {
}
