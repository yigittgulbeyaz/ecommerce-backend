package com.yigit.ecommerce.dto.response.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long userId,
        String email,
        String name,
        String role
) {}
