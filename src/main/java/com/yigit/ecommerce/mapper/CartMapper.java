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
        if (cart == null) return null;

        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());

        List<CartItemResponse> itemResponses = new ArrayList<>();

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemResponse itemResponse = toItemResponse(item);
                if (itemResponse != null) {
                    itemResponses.add(itemResponse);
                }
            }
        }

        response.setItems(itemResponses);

        response.setTotal(0.0);

        return response;
    }

    public static CartItemResponse toItemResponse(CartItem item) {
        if (item == null) return null;

        CartItemResponse r = new CartItemResponse();
        r.setQuantity(item.getQuantity());

        Product p = item.getProduct();
        if (p != null) {
            r.setProductId(p.getId());
            r.setProductName(p.getName());
            BigDecimal price = p.getPrice();
            r.setUnitPrice(price != null ? price.doubleValue() : 0.0);
        } else {
            r.setUnitPrice(0.0);
        }

        r.setLineTotal(0.0);

        return r;
    }
}
