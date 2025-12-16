package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.request.auth.LoginRequest;
import com.yigit.ecommerce.dto.request.auth.RegisterRequest;
import com.yigit.ecommerce.dto.response.auth.LoginResponse;
import com.yigit.ecommerce.dto.response.auth.RefreshTokenResponse;
import com.yigit.ecommerce.dto.response.auth.RegisterResponse;
import com.yigit.ecommerce.exception.ConflictException;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.exception.UnauthorizedException;
import com.yigit.ecommerce.model.token.RefreshToken;
import com.yigit.ecommerce.model.user.Role;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.RefreshTokenRepository;
import com.yigit.ecommerce.repository.UserRepository;
import com.yigit.ecommerce.security.jwt.JwtService;
import com.yigit.ecommerce.security.token.TokenHasher;
import com.yigit.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long refreshTokenExpirationMs;

    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            TokenHasher tokenHasher,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpirationMs
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenHasher = tokenHasher;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
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

        String rawRefreshToken = UUID.randomUUID().toString();
        saveRefreshToken(user, rawRefreshToken);

        return new LoginResponse(
                accessToken,
                rawRefreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {

        String hash = tokenHasher.sha256(refreshToken);

        RefreshToken existing = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (existing.isRevoked()) {
            throw new UnauthorizedException("Refresh token revoked");
        }

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = existing.getUser();

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getRole().name());

        // rotation
        String newRawRefreshToken = UUID.randomUUID().toString();
        RefreshToken next = saveRefreshToken(user, newRawRefreshToken);

        existing.setRevoked(true);
        existing.setRevokedAt(Instant.now());
        existing.setReplacedBy(next.getId());

        return new RefreshTokenResponse(newAccessToken, newRawRefreshToken, "Bearer");
    }

    private RefreshToken saveRefreshToken(User user, String rawToken) {
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(tokenHasher.sha256(rawToken));
        rt.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));
        return refreshTokenRepository.save(rt);
    }
}
