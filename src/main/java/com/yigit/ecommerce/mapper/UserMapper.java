package com.yigit.ecommerce.mapper;

import com.yigit.ecommerce.dto.response.user.UserResponse;
import com.yigit.ecommerce.model.user.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User u) {
        if (u == null)
            return null;

        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole());
    }
}
