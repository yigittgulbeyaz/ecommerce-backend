package com.yigit.ecommerce.dto.response.user;

import com.yigit.ecommerce.model.user.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role) {
}
