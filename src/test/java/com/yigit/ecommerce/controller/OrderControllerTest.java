package com.yigit.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yigit.ecommerce.dto.request.order.CheckoutRequest;
import com.yigit.ecommerce.dto.request.payment.PaymentRequest;
import com.yigit.ecommerce.dto.response.order.OrderResponse;
import com.yigit.ecommerce.model.order.OrderStatus;
import com.yigit.ecommerce.service.OrderService;
import com.yigit.ecommerce.security.jwt.JwtService;
import com.yigit.ecommerce.security.user.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.yigit.ecommerce.controller.order.OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void checkout_success_shouldReturn200_andPaidOrderStatus() throws Exception {
        // Arrange
        OrderResponse response = new OrderResponse(
                1L,
                BigDecimal.valueOf(200),
                OrderStatus.PAID,
                LocalDateTime.now(),
                List.of()
        );

        when(orderService.checkout(any())).thenReturn(response);

        CheckoutRequest request = new CheckoutRequest(
                1L,
                new PaymentRequest(
                        BigDecimal.valueOf(200),
                        "4111111111111111",
                        "Yigit Gulbeyaz",
                        "123",
                        "12/30"
                )
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Order created"))
                .andExpect(jsonPath("$.data.status").value("PAID"));
    }

    @Test
    void myOrders_success_shouldReturn200_andOrderList() throws Exception {
        // Arrange
        when(orderService.getMyOrders()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void myOrder_byId_shouldReturn200_andOrderDetails() throws Exception {
        // Arrange
        OrderResponse response = new OrderResponse(
                5L,
                BigDecimal.valueOf(150),
                OrderStatus.PAID,
                LocalDateTime.now(),
                List.of()
        );

        when(orderService.getMyOrder(5L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(5));
    }
}