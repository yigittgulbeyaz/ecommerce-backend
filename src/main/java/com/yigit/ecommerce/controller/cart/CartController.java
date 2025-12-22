package com.yigit.ecommerce.controller.cart;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.request.cart.AddCartItemRequest;
import com.yigit.ecommerce.dto.request.cart.UpdateCartItemQuantityRequest;
import com.yigit.ecommerce.dto.response.cart.CartResponse;
import com.yigit.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        CartResponse cart = cartService.getMyCart();
        ApiResponse<CartResponse> res = ApiResponse.success(cart, "Cart fetched");
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody AddCartItemRequest request) {
        CartResponse cart = cartService.addItem(request);
        ApiResponse<CartResponse> res = ApiResponse.success(cart, "Item added to cart");
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(@PathVariable Long productId,
                                                                    @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        CartResponse cart = cartService.updateQuantity(productId, request);
        ApiResponse<CartResponse> res = ApiResponse.success(cart, "Cart item quantity updated");
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable Long productId) {
        CartResponse cart = cartService.removeItem(productId);
        ApiResponse<CartResponse> res = ApiResponse.success(cart, "Item removed from cart");
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<CartResponse>> clear() {
        CartResponse cart = cartService.clear();
        ApiResponse<CartResponse> res = ApiResponse.success(cart, "Cart cleared");
        return ResponseEntity.status(res.getStatus()).body(res);
    }
}
