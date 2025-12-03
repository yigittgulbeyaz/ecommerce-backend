package com.yigit.ecommerce.dto.response.auth;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
