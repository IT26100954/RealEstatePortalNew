package com.realestate.user.dto;

public class AuthResponse {
    private final UserResponse user;
    private final String csrfToken;
    public AuthResponse(UserResponse user,String csrfToken) { this.user=user; this.csrfToken=csrfToken; }
    public UserResponse getUser() { return user; }
    public String getCsrfToken() { return csrfToken; }
}
