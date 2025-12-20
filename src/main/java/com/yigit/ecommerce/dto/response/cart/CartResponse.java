package com.yigit.ecommerce.dto.response.cart;

import java.util.ArrayList;
import java.util.List;

public class CartResponse {

    private Long cartId;
    private List<CartItemResponse> items = new ArrayList<>();
    private double total;

    public CartResponse() {
    }

    public CartResponse(Long cartId, List<CartItemResponse> items, double total) {
        this.cartId = cartId;
        this.items = (items != null) ? items : new ArrayList<>();
        this.total = total;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
