package com.yigit.ecommerce.dto.response.cart;

public record CartItemResponse(
        Long productId,
        String productName,
        int quantity,
        double unitPrice,
        double lineTotal) {
}
