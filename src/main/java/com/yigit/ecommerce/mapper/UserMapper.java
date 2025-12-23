package com.yigit.ecommerce.mapper;

import com.yigit.ecommerce.dto.response.user.UserResponse;
import com.yigit.ecommerce.model.user.User;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User u) {
        if (u == null) return null;

        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setName(u.getName());
        r.setEmail(u.getEmail());
        r.setRole(u.getRole());
        return r;
    }
}
