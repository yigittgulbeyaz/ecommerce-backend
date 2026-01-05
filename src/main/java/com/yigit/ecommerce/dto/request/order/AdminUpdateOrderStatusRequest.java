package com.yigit.ecommerce.dto.request.order;

import com.yigit.ecommerce.model.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record AdminUpdateOrderStatusRequest(
        @NotNull OrderStatus status) {
}
