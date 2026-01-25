package com.yigit.ecommerce.integration;

import com.yigit.ecommerce.exception.ConflictException;
import com.yigit.ecommerce.repository.UserRepository;
import com.yigit.ecommerce.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional // fixed the test pollution with transactional annotation
class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TestDataFactory dataFactory;

    @Test
    void register_validRequest_shouldReturn201_andPersistUser() {
        var request = dataFactory.createRegisterRequest();
        authService.register(request);

        var user = userRepository.findByEmail(request.email()).orElseThrow();
        assertThat(user.getName()).isEqualTo(request.name());
    }

    @Test
    void register_duplicateEmail_shouldThrowException() {
        var request = dataFactory.createRegisterRequest();

        //Setup
        authService.register(request);

        //Test
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already in use");
    }
}