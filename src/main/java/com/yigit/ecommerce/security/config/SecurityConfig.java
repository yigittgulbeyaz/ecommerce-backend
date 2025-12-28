package com.yigit.ecommerce.security.config;

import com.yigit.ecommerce.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        /* ---------- AUTH (PUBLIC) ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()

                        /* ---------- CATEGORY (PUBLIC) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/*").permitAll()

                        /* ---------- ADMIN CATEGORY (ADMIN ONLY) ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/admin/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/categories/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/categories/*").hasRole("ADMIN")

                        /* ---------- PRODUCT (PUBLIC) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/*").permitAll()

                        /* ---------- ADMIN PRODUCT (ADMIN ONLY) ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/admin/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/products/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/products/*").hasRole("ADMIN")

                        /* ---------- CART (USER AUTH REQUIRED) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/cart").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/cart/items").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/cart/items/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart/items/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart/clear").authenticated()

                        /* ---------- ADMIN CART (ADMIN ONLY) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/carts/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/carts/users/*/clear").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/carts/users/*").hasRole("ADMIN")

                        /* ---------- USER (AUTH REQUIRED) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/me/change-password").authenticated()

                        /* ---------- ADMIN USER (ADMIN ONLY) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/admin/users/*/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/users/*").hasRole("ADMIN")

                        /* ---------- ORDER (USER AUTH REQUIRED) ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/*").authenticated()

                        /* ---------- ADMIN ORDER (ADMIN ONLY) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/orders/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/admin/orders/*/status").hasRole("ADMIN")

                                /* ---------- DEFAULT ---------- */
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
