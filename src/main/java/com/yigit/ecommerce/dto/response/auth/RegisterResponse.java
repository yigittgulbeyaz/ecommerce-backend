package com.yigit.ecommerce.dto.response.auth;

public record RegisterResponse(
        Long userId,
        String name,
        String email,
        String role,
        String message
) {}
