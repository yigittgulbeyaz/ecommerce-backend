package com.yigit.ecommerce.mapper;

import com.yigit.ecommerce.dto.response.order.OrderItemResponse;
import com.yigit.ecommerce.dto.response.order.OrderResponse;
import com.yigit.ecommerce.model.order.Order;
import com.yigit.ecommerce.model.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order o) {
        if (o == null)
            return null;

        List<OrderItemResponse> items = o.getItems() == null ? List.of()
                : o.getItems().stream().map(OrderMapper::toItemResponse).toList();

        return new OrderResponse(
                o.getId(),
                o.getTotalPrice(),
                o.getStatus(),
                o.getCreatedAt(),
                items);
    }

    private static OrderItemResponse toItemResponse(OrderItem it) {
        BigDecimal lineTotal = it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity()));

        return new OrderItemResponse(
                it.getId(),
                it.getProduct().getId(),
                it.getProduct().getName(),
                it.getPrice(),
                it.getQuantity(),
                lineTotal);
    }
}
