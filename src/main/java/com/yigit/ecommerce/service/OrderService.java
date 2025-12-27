package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.response.order.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse checkout();// cart -> order

    List<OrderResponse> getMyOrders();   // me orders

    OrderResponse getMyOrder(Long id);   // me order detail
}
