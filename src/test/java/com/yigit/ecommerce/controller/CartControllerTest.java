package com.yigit.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yigit.ecommerce.dto.request.cart.AddCartItemRequest;
import com.yigit.ecommerce.dto.request.cart.UpdateCartItemQuantityRequest;
import com.yigit.ecommerce.dto.response.cart.CartResponse;
import com.yigit.ecommerce.service.CartService;
import com.yigit.ecommerce.security.jwt.JwtService;
import com.yigit.ecommerce.security.user.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.yigit.ecommerce.controller.cart.CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMyCart_success_shouldReturn200_andCartDetails() throws Exception {
        // Arrange
        CartResponse response = new CartResponse(1L, List.of(), 0.0);
        when(cartService.getMyCart()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cart fetched"));
    }

    @Test
    void addItem_success_shouldReturn200_andUpdatedCart() throws Exception {
        // Arrange
        AddCartItemRequest request = new AddCartItemRequest(1L, 2);
        CartResponse response = new CartResponse(1L, List.of(), 100.0);
        when(cartService.addItem(any(AddCartItemRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item added to cart"));
    }

    @Test
    void updateQuantity_success_shouldReturn200_andUpdatedMessage() throws Exception {
        // Arrange
        Long productId = 1L;
        UpdateCartItemQuantityRequest request = new UpdateCartItemQuantityRequest(5);
        CartResponse response = new CartResponse(1L, List.of(), 250.0);
        when(cartService.updateQuantity(eq(productId), any(UpdateCartItemQuantityRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/cart/items/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cart item quantity updated"));
    }

    @Test
    void removeItem_success_shouldReturn200_andDeletedMessage() throws Exception {
        // Arrange
        Long productId = 1L;
        CartResponse response = new CartResponse(1L, List.of(), 0.0);
        when(cartService.removeItem(productId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/cart/items/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item removed from cart"));
    }

    @Test
    void clear_success_shouldReturn200_andClearedMessage() throws Exception {
        // Arrange
        CartResponse response = new CartResponse(1L, List.of(), 0.0);
        when(cartService.clear()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/cart/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cart cleared"));
    }
}