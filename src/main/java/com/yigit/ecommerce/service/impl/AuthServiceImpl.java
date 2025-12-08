package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.request.auth.LoginRequest;
import com.yigit.ecommerce.dto.request.auth.RegisterRequest;
import com.yigit.ecommerce.dto.response.auth.LoginResponse;
import com.yigit.ecommerce.dto.response.auth.RefreshTokenResponse;
import com.yigit.ecommerce.dto.response.auth.RegisterResponse;
import com.yigit.ecommerce.exception.ConflictException;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.model.user.Role;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.UserRepository;
import com.yigit.ecommerce.security.jwt.JwtService;
import com.yigit.ecommerce.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already in use.");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                "User registered successfully"
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("User not found."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new NotFoundException("Invalid email or password.");
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getRole().name()
        );

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {

        jwtService.validateRefreshToken(refreshToken);

        Long userId = jwtService.extractUserIdFromToken(refreshToken);
        String role = jwtService.extractRoleFromToken(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        String newAccessToken = jwtService.generateAccessToken(user.getId(), role);
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), role);

        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer"
        );
    }
}

