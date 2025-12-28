package com.yigit.ecommerce.service.admin.impl;

import com.yigit.ecommerce.dto.request.order.AdminUpdateOrderStatusRequest;
import com.yigit.ecommerce.dto.response.order.OrderResponse;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.mapper.OrderMapper;
import com.yigit.ecommerce.model.order.Order;
import com.yigit.ecommerce.repository.OrderRepository;
import com.yigit.ecommerce.service.admin.AdminOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;

    public AdminOrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order o = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
        return OrderMapper.toResponse(o);
    }

    @Override
    public OrderResponse updateStatus(Long id, AdminUpdateOrderStatusRequest request) {
        Order o = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        o.setStatus(request.getStatus());
        return OrderMapper.toResponse(o);
    }
}
