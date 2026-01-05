package com.yigit.ecommerce.dto.request.user;

import com.yigit.ecommerce.model.user.Role;
import jakarta.validation.constraints.NotNull;

public record AdminUpdateRoleRequest(
        @NotNull Role role) {
}
