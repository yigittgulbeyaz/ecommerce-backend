package com.yigit.ecommerce.controller.user;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.request.user.ChangePasswordRequest;
import com.yigit.ecommerce.dto.request.user.UpdateMeRequest;
import com.yigit.ecommerce.dto.response.user.UserResponse;
import com.yigit.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        UserResponse me = userService.getMe();
        return ResponseEntity.ok(ApiResponse.success(me, "User profile fetched"));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(@Valid @RequestBody UpdateMeRequest request) {
        UserResponse updated = userService.updateMe(request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profile updated"));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Password changed"));
    }
}
