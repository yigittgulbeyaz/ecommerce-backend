package com.yigit.ecommerce.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
        @NotNull(message = "productId is required") Long productId,
        @Min(value = 1, message = "quantity must be at least 1") int quantity) {
}
