package com.realestate.user;

import com.realestate.user.dto.UserResponse;

public final class UserViews {
    private UserViews() { }
    public static UserResponse response(User user) {
        UserResponse result=new UserResponse();
        result.setId(user.getId());
        result.setUsername(user.getUsername());   // Copy safe account fields; never return passwordHash.
        result.setEmail(user.getEmail());
        result.setPhone(user.getPhone());
        result.setRole(user.getRole());
        return result;
    }
}
