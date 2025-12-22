package com.yigit.ecommerce.controller.admin;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.response.cart.CartResponse;
import com.yigit.ecommerce.service.admin.AdminCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/carts")
public class AdminCartController {

    private final AdminCartService adminCartService;

    public AdminCartController(AdminCartService adminCartService) {
        this.adminCartService = adminCartService;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByUser(@PathVariable Long userId) {
        CartResponse cart = adminCartService.getCartByUserId(userId);
        ApiResponse<CartResponse> res = ApiResponse.success(cart, "Cart fetched");
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/users/{userId}/clear")
    public ResponseEntity<ApiResponse<CartResponse>> clearCartByUser(@PathVariable Long userId) {
        CartResponse cart = adminCartService.clearCartByUserId(userId);
        ApiResponse<CartResponse> res = ApiResponse.success(cart, "Cart cleared");
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartByUser(@PathVariable Long userId) {
        adminCartService.deleteCartByUserId(userId);
        ApiResponse<Void> res = ApiResponse.success("Cart deleted");
        return ResponseEntity.status(res.getStatus()).body(res);
    }
}
