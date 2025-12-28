package com.yigit.ecommerce.controller.order;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.response.order.OrderResponse;
import com.yigit.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService userOrderService) {
        this.orderService = userOrderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> checkout() {
        return ResponseEntity.ok(ApiResponse.success(orderService.checkout(), "Order created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> myOrders() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getMyOrders(), "Orders listed"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> myOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getMyOrder(id), "Order fetched"));
    }
}
