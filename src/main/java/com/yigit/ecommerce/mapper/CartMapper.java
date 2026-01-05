package com.yigit.ecommerce.mapper;

import com.yigit.ecommerce.dto.response.cart.CartItemResponse;
import com.yigit.ecommerce.dto.response.cart.CartResponse;
import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.cart.CartItem;
import com.yigit.ecommerce.model.product.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartResponse toResponse(Cart cart) {
        if (cart == null)
            return null;

        List<CartItemResponse> itemResponses = new ArrayList<>();
        double total = 0.0;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemResponse itemResponse = toItemResponse(item);
                if (itemResponse != null) {
                    itemResponses.add(itemResponse);
                    total += itemResponse.lineTotal();
                }
            }
        }

        return new CartResponse(cart.getId(), itemResponses, total);
    }

    public static CartItemResponse toItemResponse(CartItem item) {
        if (item == null)
            return null;

        Long productId = null;
        String productName = null;
        double unitPrice = 0.0;

        Product p = item.getProduct();
        if (p != null) {
            productId = p.getId();
            productName = p.getName();
            BigDecimal price = p.getPrice();
            unitPrice = price != null ? price.doubleValue() : 0.0;
        }

        double lineTotal = unitPrice * item.getQuantity();

        return new CartItemResponse(
                productId,
                productName,
                item.getQuantity(),
                unitPrice,
                lineTotal);
    }
}
