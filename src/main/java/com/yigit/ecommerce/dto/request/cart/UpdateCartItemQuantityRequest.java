package com.yigit.ecommerce.dto.request.cart;

import jakarta.validation.constraints.Min;

public record UpdateCartItemQuantityRequest(
        @Min(value = 1, message = "quantity must be at least 1") int quantity) {
}
