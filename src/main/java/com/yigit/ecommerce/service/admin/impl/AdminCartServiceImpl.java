package com.yigit.ecommerce.service.admin.impl;

import com.yigit.ecommerce.dto.response.cart.CartItemResponse;
import com.yigit.ecommerce.dto.response.cart.CartResponse;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.mapper.CartMapper;
import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.CartItemRepository;
import com.yigit.ecommerce.repository.CartRepository;
import com.yigit.ecommerce.repository.UserRepository;
import com.yigit.ecommerce.service.admin.AdminCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminCartServiceImpl implements AdminCartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public AdminCartServiceImpl(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CartResponse getCartByUserId(Long userId) {
        User user = getUserOrThrow(userId);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));

        return CartMapper.toResponse(cart);
    }

    @Override
    public CartResponse clearCartByUserId(Long userId) {
        User user = getUserOrThrow(userId);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));

        cartItemRepository.deleteAllByCart(cart);

        if (cart.getItems() != null) {
            cart.getItems().clear();
        }

        return CartMapper.toResponse(cart);
    }

    @Override
    public void deleteCartByUserId(Long userId) {
        User user = getUserOrThrow(userId);
        cartRepository.deleteByUser(user);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }
}
