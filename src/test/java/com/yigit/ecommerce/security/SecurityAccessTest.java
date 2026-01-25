package com.yigit.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yigit.ecommerce.dto.request.auth.RegisterRequest;
import com.yigit.ecommerce.dto.response.auth.RegisterResponse;
import com.yigit.ecommerce.dto.response.cart.CartResponse; 
import com.yigit.ecommerce.security.jwt.JwtService;
import com.yigit.ecommerce.security.user.CustomUserDetailsService;
import com.yigit.ecommerce.service.AuthService;
import com.yigit.ecommerce.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    // --- BUCKET 1: PUBLIC ACCESS ---

    @Test
    @DisplayName("Public: Product listing should be accessible to everyone")
    void publicEndpoints_ShouldBeAccessible() throws Exception {

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Public: Registration should return 201 Created")
    void register_ShouldBeAccessible() throws Exception {

        RegisterResponse mockResponse = new RegisterResponse(1L, "Yigit", "yigit@example.com", "ROLE_USER", "Success");
        when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        RegisterRequest request = new RegisterRequest("Yigit", "yigit@example.com", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // --- BUCKET 2: AUTHENTICATED ACCESS ---

    @Test
    @WithMockUser(username = "yigit@example.com", roles = "USER")
    @DisplayName("Auth: Authenticated USER should access their Cart successfully")
    void authenticatedUser_ShouldAccessCart() throws Exception {

        CartResponse mockCart = new CartResponse(1L, new ArrayList<>(), 0.0);
        when(cartService.getMyCart()).thenReturn(mockCart);

        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk());
    }

    // --- BUCKET 3: ROLE-BASED ACCESS (ADMIN ONLY) ---

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Role: ADMIN should be authorized to create products")
    void adminCanAccessProductCreation() throws Exception {

        String validProductJson = "{\"name\":\"Gaming Mouse\",\"price\":1500,\"categoryId\":1,\"description\":\"RGB\",\"stock\":50}";

        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProductJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Deny: USER should receive 403 Forbidden for Admin endpoints")
    void userCannotAccessAdminUserList() throws Exception {

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());
    }

    // --- BUCKET 4: SECURITY FAIL-SAFE ---

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Fail-safe: Undefined random URLs must be denied")
    void undefinedUrl_ShouldBeDeniedForAll() throws Exception {

        mockMvc.perform(get("/api/v1/undefined-random-path"))
                .andExpect(status().isForbidden());
    }
}