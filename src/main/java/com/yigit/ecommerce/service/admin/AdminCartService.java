package com.yigit.ecommerce.service.admin;

import com.yigit.ecommerce.dto.response.cart.CartResponse;

public interface AdminCartService {

    CartResponse getCartByUserId(Long userId);

    CartResponse clearCartByUserId(Long userId);

    void deleteCartByUserId(Long userId);
}
