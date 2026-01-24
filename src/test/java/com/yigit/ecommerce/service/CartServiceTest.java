package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.cart.AddCartItemRequest;
import com.yigit.ecommerce.dto.request.cart.UpdateCartItemQuantityRequest;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.cart.CartItem;
import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.CartItemRepository;
import com.yigit.ecommerce.repository.CartRepository;
import com.yigit.ecommerce.repository.ProductRepository;
import com.yigit.ecommerce.repository.UserRepository;
import com.yigit.ecommerce.security.context.AuthenticationContext;
import com.yigit.ecommerce.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    CartRepository cartRepository;

    @Mock
    CartItemRepository cartItemRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticationContext authenticationContext;

    @InjectMocks
    CartServiceImpl cartService;

    @Test
    void addItem_success_shouldCreateNewCartItem() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        AddCartItemRequest request = new AddCartItemRequest(10L, 2);
        Product product = new Product();
        product.setId(10L);
        product.setPrice(BigDecimal.TEN);

        when(authenticationContext.requireUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(request.productId())).thenReturn(Optional.of(product));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartAndProduct(any(), any())).thenReturn(Optional.empty());

        var response = cartService.addItem(request);

        assertThat(response).isNotNull();
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void getMyCart_success_shouldReturnUserCart() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(authenticationContext.requireUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Cart cart = new Cart();
        cart.setUser(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        var response = cartService.getMyCart();

        assertThat(response).isNotNull();
        verify(cartRepository).findByUser(user);
    }

    @Test
    void updateQuantity_success_shouldUpdateItemQuantity() {
        Long userId = 1L;
        Long productId = 10L;
        User user = new User();
        user.setId(userId);
        UpdateCartItemQuantityRequest request = new UpdateCartItemQuantityRequest(5);

        when(authenticationContext.requireUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Cart cart = new Cart();
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        Product product = new Product();
        product.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        CartItem existingItem = new CartItem();
        existingItem.setQuantity(1);
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingItem));

        cartService.updateQuantity(productId, request);

        assertThat(existingItem.getQuantity()).isEqualTo(5);
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void removeItem_success_shouldDeleteFromRepository() {
        Long userId = 1L;
        Long productId = 10L;
        User user = new User();
        when(authenticationContext.requireUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Cart cart = new Cart();
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        Product product = new Product();
        product.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        CartItem itemToRemove = new CartItem();
        itemToRemove.setProduct(product);
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(itemToRemove));

        cartService.removeItem(productId);

        verify(cartItemRepository).delete(itemToRemove);
    }

    @Test
    void addItem_shouldThrowNotFoundException_whenProductDoesNotExist() {
        Long userId = 1L;
        User user = new User();
        when(authenticationContext.requireUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        AddCartItemRequest request = new AddCartItemRequest(99L, 1);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItem(request))
                .isInstanceOf(NotFoundException.class);

        verify(cartItemRepository, never()).save(any());
    }
}