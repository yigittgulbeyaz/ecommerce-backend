package com.yigit.ecommerce.controller.admin;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.request.user.AdminUpdateRoleRequest;
import com.yigit.ecommerce.dto.response.user.UserResponse;
import com.yigit.ecommerce.service.admin.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> all() {
        List<UserResponse> users = adminUserService.getAll();
        return ResponseEntity.ok(ApiResponse.success(users, "Users listed"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> get(@PathVariable Long id) {
        UserResponse user = adminUserService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User fetched"));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(@PathVariable Long id,
                                                                @Valid @RequestBody AdminUpdateRoleRequest request) {
        UserResponse updated = adminUserService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Role updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        adminUserService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted"));
    }
}
