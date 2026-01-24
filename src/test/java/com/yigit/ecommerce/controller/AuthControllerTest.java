package com.yigit.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yigit.ecommerce.dto.request.auth.LoginRequest;
import com.yigit.ecommerce.dto.request.auth.RegisterRequest;
import com.yigit.ecommerce.dto.response.auth.LoginResponse;
import com.yigit.ecommerce.dto.response.auth.RegisterResponse;
import com.yigit.ecommerce.service.AuthService;
import com.yigit.ecommerce.security.jwt.JwtService;
import com.yigit.ecommerce.security.user.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.yigit.ecommerce.controller.auth.AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_success_shouldReturn201_andRegisterResponse() throws Exception {
        // Arrange
        RegisterResponse response = new RegisterResponse(
                1L,
                "Yigit",
                "yigit@example.com",
                "ROLE_USER",
                "User registered successfully"
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        RegisterRequest request = new RegisterRequest(
                "Yigit",
                "yigit@example.com",
                "password123"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.message").value("User registered successfully"));
    }

    @Test
    void login_success_shouldReturn200_andLoginResponse() throws Exception {
        // Arrange
        LoginResponse response = new LoginResponse(
                "mock-access-token",
                "mock-refresh-token",
                "Bearer",
                1L,
                "yigit@example.com",
                "Yigit",
                "ROLE_USER"
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        LoginRequest request = new LoginRequest("yigit@example.com", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.data.userId").value(1));
    }
}