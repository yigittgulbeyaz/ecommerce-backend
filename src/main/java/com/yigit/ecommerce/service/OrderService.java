package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.order.CheckoutRequest;
import com.yigit.ecommerce.dto.response.order.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse checkout(CheckoutRequest request);

    List<OrderResponse> getMyOrders();

    OrderResponse getMyOrder(Long id);
}
