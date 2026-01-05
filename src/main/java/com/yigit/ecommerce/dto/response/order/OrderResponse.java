package com.yigit.ecommerce.dto.response.order;

import com.yigit.ecommerce.model.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items) {
}
