package com.yigit.ecommerce.dto.request.order;

import com.yigit.ecommerce.model.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class AdminUpdateOrderStatusRequest {

    @NotNull
    private OrderStatus status;

    public AdminUpdateOrderStatusRequest() {}

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
