package com.yigit.ecommerce.controller.admin;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.request.order.AdminUpdateOrderStatusRequest;
import com.yigit.ecommerce.dto.response.order.OrderResponse;
import com.yigit.ecommerce.service.admin.AdminOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> all() {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.getAll(), "Orders listed"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.getById(id), "Order fetched"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long id,
                                                                   @Valid @RequestBody AdminUpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.updateStatus(id, request), "Status updated"));
    }
}
