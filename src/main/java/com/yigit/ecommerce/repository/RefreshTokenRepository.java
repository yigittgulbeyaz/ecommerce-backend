package com.yigit.ecommerce.repository;

import com.yigit.ecommerce.model.token.RefreshToken;
import com.yigit.ecommerce.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
