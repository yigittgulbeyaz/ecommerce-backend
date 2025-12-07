package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.auth.LoginRequest;
import com.yigit.ecommerce.dto.request.auth.RegisterRequest;
import com.yigit.ecommerce.dto.response.auth.LoginResponse;
import com.yigit.ecommerce.dto.response.auth.RefreshTokenResponse;
import com.yigit.ecommerce.dto.response.auth.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(String refreshToken);
}
