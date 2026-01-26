package com.yigit.ecommerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API")
                        .version("1.0.0")
                        .description("""
                                E-Commerce Backend API - A complete RESTful API for e-commerce applications.

                                ## Features
                                - 🔐 JWT Authentication (Access + Refresh tokens)
                                - 👤 User Management
                                - 📦 Product & Category Management
                                - 🛒 Shopping Cart
                                - 📍 Address Management
                                - 💳 Order Processing with Payment
                                - 👑 Admin Operations

                                ## Authentication
                                Most endpoints require a JWT token. Use the `/auth/login` endpoint to obtain tokens,
                                then click the **Authorize** button and enter: `Bearer <your-token>`
                                """)
                        .contact(new Contact()
                                .name("Yiğit Gülbeyaz")
                                .url("https://github.com/yigittgulbeyaz")
                                .email("yigit@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token. Example: eyJhbGciOiJIUzI1NiIs...")));
    }
}
