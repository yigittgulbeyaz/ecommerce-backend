package com.yigit.ecommerce.dto.request.cart;

import jakarta.validation.constraints.Min;

public class UpdateCartItemQuantityRequest {

    @Min(value = 1, message = "quantity must be at least 1")
    private int quantity;

    public UpdateCartItemQuantityRequest() {
    }

    public UpdateCartItemQuantityRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
