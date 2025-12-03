package com.yigit.ecommerce.dto.request.auth;

public record LoginRequest(
        String email,
        String password
) {}
