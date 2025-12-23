package com.yigit.ecommerce.dto.request.user;

import com.yigit.ecommerce.model.user.Role;
import jakarta.validation.constraints.NotNull;

public class AdminUpdateRoleRequest {

    @NotNull
    private Role role;

    public AdminUpdateRoleRequest() {}

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
