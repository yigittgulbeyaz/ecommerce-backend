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

                        /* ---------- SWAGGER / OPENAPI (PUBLIC) ---------- */
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs.yaml").permitAll()

                        /* ---------- AUTH (PUBLIC) ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()

                        /* ---------- CATEGORY (PUBLIC) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/*").permitAll()

                        /* ---------- ADMIN CATEGORY ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/admin/categories/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/categories/*").hasRole("ADMIN")

                        /* ---------- PRODUCT (PUBLIC) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/*").permitAll()

                        /* ---------- ADMIN PRODUCT ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/admin/products/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/products/*").hasRole("ADMIN")

                        /* ---------- CART (AUTH REQUIRED) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/cart").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/cart/items").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/cart/items/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart/items/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart/clear").authenticated()

                        /* ---------- ADMIN CART ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/carts/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/carts/users/*/clear").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/carts/users/*").hasRole("ADMIN")

                        /* ---------- ADDRESS (AUTH REQUIRED) ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/v1/addresses").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/addresses").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/addresses/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/addresses/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/addresses/*").authenticated()

                        /* ---------- USER ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/me/change-password").authenticated()

                        /* ---------- ADMIN USER ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/admin/users/*/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/users/*").hasRole("ADMIN")

                        /* ---------- ORDER ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/*").authenticated()

                        /* ---------- ADMIN ORDER ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/orders/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/admin/orders/*/status").hasRole("ADMIN")

                        /* ---------- DEFAULT ---------- */
                        .anyRequest().denyAll())
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
