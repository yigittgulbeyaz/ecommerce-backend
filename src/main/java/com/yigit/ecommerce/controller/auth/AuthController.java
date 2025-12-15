package com.yigit.ecommerce.controller.auth;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.request.auth.LoginRequest;
import com.yigit.ecommerce.dto.request.auth.RegisterRequest;
import com.yigit.ecommerce.dto.response.auth.LoginResponse;
import com.yigit.ecommerce.dto.response.auth.RefreshTokenResponse;
import com.yigit.ecommerce.dto.response.auth.RegisterResponse;
import com.yigit.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /* ---------------------- REGISTER ---------------------- */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "User registered successfully"));
    }

    /* ---------------------- LOGIN ---------------------- */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Login successful"));
    }

    /* ---------------------- REFRESH TOKEN ---------------------- */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @RequestParam("refreshToken") String refreshToken
    ) {
        RefreshTokenResponse response = authService.refreshToken(refreshToken);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Token refreshed"));
    }
}
