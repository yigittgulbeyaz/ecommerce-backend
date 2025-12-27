package com.yigit.ecommerce.mapper;

import com.yigit.ecommerce.dto.response.order.OrderItemResponse;
import com.yigit.ecommerce.dto.response.order.OrderResponse;
import com.yigit.ecommerce.model.order.Order;
import com.yigit.ecommerce.model.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderResponse toResponse(Order o) {
        if (o == null) return null;

        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setTotalPrice(o.getTotalPrice());
        r.setStatus(o.getStatus());
        r.setCreatedAt(o.getCreatedAt());

        List<OrderItemResponse> items = o.getItems() == null ? List.of()
                : o.getItems().stream().map(OrderMapper::toItemResponse).toList();

        r.setItems(items);
        return r;
    }

    private static OrderItemResponse toItemResponse(OrderItem it) {
        OrderItemResponse r = new OrderItemResponse();
        r.setId(it.getId());
        r.setProductId(it.getProduct().getId());
        r.setProductName(it.getProduct().getName());
        r.setUnitPrice(it.getPrice());
        r.setQuantity(it.getQuantity());

        BigDecimal lineTotal = it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
        r.setLineTotal(lineTotal);

        return r;
    }
}
