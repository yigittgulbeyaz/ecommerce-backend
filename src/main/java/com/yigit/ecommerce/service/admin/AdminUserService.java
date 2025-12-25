package com.yigit.ecommerce.service.admin;

import com.yigit.ecommerce.dto.request.user.AdminUpdateRoleRequest;
import com.yigit.ecommerce.dto.response.user.UserResponse;

import java.util.List;

public interface AdminUserService {
    List<UserResponse> getAll();
    UserResponse getById(Long id);
    UserResponse updateRole(Long id, AdminUpdateRoleRequest request);
    void delete(Long id);
}
