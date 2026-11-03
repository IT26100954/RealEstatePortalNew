package com.realestate.user;

import java.util.List;

import com.realestate.user.dto.ChangePasswordRequest;
import com.realestate.user.dto.ChangeRoleRequest;
import com.realestate.user.dto.CreateUserRequest;
import com.realestate.user.dto.UpdateUserRequest;
import com.realestate.user.dto.UserResponse;

public interface UserService {

    // Public registration permits buyers and sellers
    UserResponse register(CreateUserRequest request);

    // Account creation through admin
    UserResponse createUser(
            String currentUserId,
            CreateUserRequest request);

    List<UserResponse> getAllUsers(String currentUserId);

    UserResponse getUserById(
            String currentUserId,
            String userId);

    // Edit profile fields separately from role and password
    UserResponse updateUser(
            String currentUserId,
            String userId,
            UpdateUserRequest request);

    void changePassword(
            String currentUserId,
            String userId,
            ChangePasswordRequest request);

    UserResponse changeRole(
            String currentUserId,
            String userId,
            ChangeRoleRequest request);

    void deleteUser(
            String currentUserId,
            String userId);
}