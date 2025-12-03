package com.yigit.ecommerce.dto.request.auth;

public record RegisterRequest(
        String name,
        String email,
        String password
) {}
