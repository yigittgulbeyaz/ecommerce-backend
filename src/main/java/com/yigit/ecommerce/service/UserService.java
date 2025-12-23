package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.user.ChangePasswordRequest;
import com.yigit.ecommerce.dto.request.user.UpdateMeRequest;
import com.yigit.ecommerce.dto.response.user.UserResponse;

public interface UserService {
    UserResponse getMe();
    UserResponse updateMe(UpdateMeRequest request);
    void changePassword(ChangePasswordRequest request);
}
