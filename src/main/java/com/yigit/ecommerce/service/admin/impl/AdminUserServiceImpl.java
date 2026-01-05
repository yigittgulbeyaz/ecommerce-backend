package com.yigit.ecommerce.service.admin.impl;

import com.yigit.ecommerce.dto.request.user.AdminUpdateRoleRequest;
import com.yigit.ecommerce.dto.response.user.UserResponse;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.mapper.UserMapper;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.UserRepository;
import com.yigit.ecommerce.service.admin.AdminUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    public AdminUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        return UserMapper.toResponse(u);
    }

    @Override
    public UserResponse updateRole(Long id, AdminUpdateRoleRequest request) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        u.setRole(request.role());
        return UserMapper.toResponse(u);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}
