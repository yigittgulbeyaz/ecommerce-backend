package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.cart.AddCartItemRequest;
import com.yigit.ecommerce.dto.request.cart.UpdateCartItemQuantityRequest;
import com.yigit.ecommerce.dto.response.cart.CartResponse;

public interface CartService {

    CartResponse getMyCart();

    CartResponse addItem(AddCartItemRequest request);

    CartResponse updateQuantity(Long productId, UpdateCartItemQuantityRequest request);

    CartResponse removeItem(Long productId);

    CartResponse clear();
}
