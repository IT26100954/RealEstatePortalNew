package com.realestate.user;

import jakarta.servlet.http.HttpServletRequest;
import com.realestate.user.dto.AuthResponse;
import com.realestate.user.dto.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request,HttpServletRequest httpRequest);
}
