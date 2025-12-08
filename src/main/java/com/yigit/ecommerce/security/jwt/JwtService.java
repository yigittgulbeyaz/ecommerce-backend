package com.yigit.ecommerce.security.jwt;

import com.yigit.ecommerce.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /* ---------------------- TOKEN GENERATION ---------------------- */

    public String generateAccessToken(Long userId, String role) {
        return generateToken(userId, role, accessTokenExpiration);
    }

    public String generateRefreshToken(Long userId, String role) {
        return generateToken(userId, role, refreshTokenExpiration);
    }

    private String generateToken(Long userId, String role, long expiration) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(Map.of(
                        "userId", userId,
                        "role", role
                ))
                .setSubject(String.valueOf(userId)) // SUBJECT = USER ID
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* ---------------------- BASIC VALIDATION ---------------------- */

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /* ---------------------- REFRESH TOKEN METHODS ---------------------- */
    public void validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Refresh token expired.");
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token.");
        }
    }

    public Long extractUserIdFromToken(String token) {
        Claims claims = extractClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public String extractRoleFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }
}
