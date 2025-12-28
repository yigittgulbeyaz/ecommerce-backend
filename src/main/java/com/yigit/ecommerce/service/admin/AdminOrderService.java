package com.yigit.ecommerce.service.admin;

import com.yigit.ecommerce.dto.request.order.AdminUpdateOrderStatusRequest;
import com.yigit.ecommerce.dto.response.order.OrderResponse;

import java.util.List;

public interface AdminOrderService {

    List<OrderResponse> getAll();

    OrderResponse getById(Long id);

    OrderResponse updateStatus(Long id, AdminUpdateOrderStatusRequest request);
}
