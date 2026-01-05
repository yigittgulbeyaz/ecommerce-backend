package com.yigit.ecommerce.dto.response.cart;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        double total) {
}
