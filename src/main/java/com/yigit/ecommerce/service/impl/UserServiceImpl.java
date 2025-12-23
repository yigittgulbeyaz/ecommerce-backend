package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.request.user.ChangePasswordRequest;
import com.yigit.ecommerce.dto.request.user.UpdateMeRequest;
import com.yigit.ecommerce.dto.response.user.UserResponse;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.exception.UnauthorizedException;
import com.yigit.ecommerce.mapper.UserMapper;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.UserRepository;
import com.yigit.ecommerce.security.context.AuthenticationContext;
import com.yigit.ecommerce.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           AuthenticationContext authenticationContext,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMe() {
        Long userId = authenticationContext.requireUserId();

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        return UserMapper.toResponse(u);
    }

    @Override
    public UserResponse updateMe(UpdateMeRequest request) {
        Long userId = authenticationContext.requireUserId();

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        u.setName(request.getName());
        return UserMapper.toResponse(u);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        Long userId = authenticationContext.requireUserId();

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), u.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        u.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }
}
